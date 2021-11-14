package io.nvtc.csc.jpa.db

import java.io.FileWriter
import java.io.PrintWriter
import java.sql.Driver
import java.util.*
import javax.persistence.spi.PersistenceUnitInfo
import org.assertj.core.api.Assertions.fail
import org.hibernate.boot.Metadata
import org.hibernate.boot.model.relational.Namespace
import org.hibernate.boot.model.relational.QualifiedNameImpl
import org.hibernate.boot.spi.MetadataImplementor
import org.hibernate.dialect.Dialect
import org.hibernate.jpa.HibernatePersistenceProvider
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl
import org.hibernate.mapping.Column
import org.hibernate.mapping.ForeignKey
import org.hibernate.mapping.Table
import org.hibernate.tool.hbm2ddl.SchemaExport
import org.hibernate.tool.schema.TargetType
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean

class SchemaExporter {

  /**
   * Exports database schema to file for the given dialect and database driver.
   *
   * @param factoryBean [LocalContainerEntityManagerFactoryBean] where to take hibernate information
   * / object references from.
   * @param dialect the dialect to export the schema for
   * @param driver the driver to use for schema export
   * @param outputPath the path where to write the export, relative to project root directory
   */
  fun exportSchema(
      factoryBean: LocalContainerEntityManagerFactoryBean,
      dialect: Class<out Dialect>,
      driver: Class<out Driver>,
      outputPath: String
  ) {

    try {

      // Specify properties how to export
      val jpaPropertyMap = factoryBean.jpaPropertyMap
      jpaPropertyMap["hibernate.hbm2ddl.auto"] = "create"
      jpaPropertyMap["hibernate.show_sql"] = "false"
      jpaPropertyMap["hibernate.format_sql"] = "true"
      jpaPropertyMap["hibernate.dialect"] = dialect.name
      jpaPropertyMap["datasource.driverClassName"] = driver.name

      // Get hibernate persistence provider
      val persistenceProvider = factoryBean.persistenceProvider as HibernatePersistenceProvider

      // Get class method of entity manager factory builder using
      // reflection api in hibernate persistence provider
      val methodEntityManagerFactoryBuilder =
          HibernatePersistenceProvider::class.java.getDeclaredMethod(
              "getEntityManagerFactoryBuilder",
              PersistenceUnitInfo::class.java,
              MutableMap::class.java)
      methodEntityManagerFactoryBuilder.isAccessible = true

      // Get persistence info from factory bean
      val persistenceUnitInfo = factoryBean.persistenceUnitInfo

      // Get entity manager factory builder from persistence provider
      val entityManagerFactoryBuilder =
          methodEntityManagerFactoryBuilder.invoke(
              persistenceProvider, persistenceUnitInfo, jpaPropertyMap) as
              EntityManagerFactoryBuilderImpl

      // Close output file. Hibernate 5.2.0 appends to it automatically.
      PrintWriter(outputPath).close()

      // Get metadata implementor
      val methodMetadataImplementor =
          EntityManagerFactoryBuilderImpl::class.java.getDeclaredMethod("metadata")
      methodMetadataImplementor.isAccessible = true
      val metadataImplementor =
          methodMetadataImplementor.invoke(entityManagerFactoryBuilder) as MetadataImplementor

      // Export schema
      val schemaExport = SchemaExport()
      schemaExport.setFormat(true)
      schemaExport.setDelimiter(";")
      schemaExport.setOutputFile(outputPath)
      schemaExport.execute(
          EnumSet.of(TargetType.SCRIPT), SchemaExport.Action.CREATE, metadataImplementor)

      // Add indices for foreign keys
      exportIndicesForForeignKeys(metadataImplementor, outputPath)
    } catch (e: Exception) {
      fail("Database schema export failed", e)
    }
  }

  /**
   * Export indices for foreign keys defined in the database schema.
   *
   * @param metadataImplementor metadata implementor where to take information from what to export
   * @param outputPath the export file where to export
   */
  private fun exportIndicesForForeignKeys(
      metadataImplementor: MetadataImplementor,
      outputPath: String
  ) {

    val statements: MutableList<String> = ArrayList()
    metadataImplementor.database.namespaces.forEach { namespace: Namespace ->
      namespace.tables.forEach { table: Table ->
        table.foreignKeys.values.forEach foreignKeysSkipLabel@{ foreignKey: ForeignKey ->
          // Collect all unique key columns to skip them if the unique keys are foreign keys, too.
          // This is required because some databases don't allow duplicate indices
          val uniqueKeyColumns =
              table
                  .uniqueKeyIterator
                  .asSequence()
                  .map { it.columns }
                  .filter { it.size == 1 }
                  .flatMap { it }
                  .toList()

          // Remove unique key columns
          val columns = foreignKey.columns
          columns.removeAll(uniqueKeyColumns)

          // Skip column if it's already indexed
          if (columns.isEmpty()) {
            return@foreignKeysSkipLabel
          }

          // Create index name
          val columnName = columns.joinToString(separator = "") { shorten(it.name) }
          val indexName = "IX_" + shorten(table.name) + "_" + columnName

          // Create index statement
          statements.add(createIndexStatement(table, columns, indexName, metadataImplementor))
          statements.add("\n\n")
        }
      }
    }
    if (statements.isNotEmpty()) {
      // If statements are not empty then add a new line to separate index
      // statements from previous statements
      statements.add(0, "\n")
      FileWriter(outputPath, true).use { it.write(java.lang.String.join("", statements)) }
    }
  }

  /**
   * Returns a shortened version of the given name.
   *
   * @param value the name to shorten
   * @return the shortened name
   */
  private fun shorten(value: String): String {
    var inputString = value

    // If column ends with _id then shorten it
    val withId = inputString.endsWith("_id")
    if (withId) {
      inputString = inputString.substring(0, inputString.length - 3)
    }

    if (inputString.contains("_")) {

      // If name is compound of multiple parts then shorten each part
      val parts = inputString.split("_")
      inputString =
          parts.joinToString(separator = "") {
            it.lowercase().substring(0, Math.min(3, it.length)).capitalizeWords()
          }
    } else {

      // Else if name is not compound then shorten the name directly
      val rawString = inputString
      inputString = inputString.substring(0, Math.min(3, inputString.length))

      if (rawString.length > inputString.length + 3) {
        inputString += rawString.substring(rawString.length - 3)
      } else if (rawString.length > inputString.length + 2) {
        inputString += rawString.substring(rawString.length - 2)
      } else if (rawString.length > inputString.length + 1) {
        inputString += rawString.substring(rawString.length - 1)
      }
    }

    return inputString.capitalizeWords()
  }

  /**
   * Create index sql statement. Uses code from [ ].
   *
   * @param table the table to create an index for
   * @param indexColumns the columns to create index for
   * @param indexName the index name
   * @param metadata metadata to verify and qualify object names
   * @return the index statement
   */
  private fun createIndexStatement(
      table: Table,
      indexColumns: List<Column>,
      indexName: String,
      metadata: Metadata
  ): String {

    val jdbcEnvironment = metadata.database.jdbcEnvironment
    val dialect = metadata.database.dialect
    val tableName =
        jdbcEnvironment.qualifiedObjectNameFormatter.format(table.qualifiedTableName, dialect)

    val indexNameForCreation: String =
        if (dialect.qualifyIndexName()) {
          jdbcEnvironment.qualifiedObjectNameFormatter.format(
              QualifiedNameImpl(
                  table.qualifiedTableName.catalogName,
                  table.qualifiedTableName.schemaName,
                  jdbcEnvironment.identifierHelper.toIdentifier(indexName)),
              jdbcEnvironment.dialect)
        } else {
          indexName
        }

    val statement = indexColumns.joinToString(separator = ", ") { it.getQuotedName(dialect) }
    return "    create index $indexNameForCreation on $tableName ($statement);"
  }

  private fun String.capitalizeWords() =
      split(" ").joinToString(" ") { it.lowercase().replaceFirstChar { it.titlecase() } }
}
