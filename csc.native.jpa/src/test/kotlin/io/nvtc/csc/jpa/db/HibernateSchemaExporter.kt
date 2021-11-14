package io.nvtc.csc.jpa.db

import org.hibernate.dialect.H2Dialect
import org.hibernate.dialect.MariaDB103Dialect
import org.hibernate.dialect.MariaDB10Dialect
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.test.annotation.IfProfileValue
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource

@IfProfileValue(name = "spring.profiles.active", value = "schema-export")
@ActiveProfiles("test")
@DataJpaTest
@TestPropertySource(
    properties = ["spring.flyway.enabled=false", "spring.jpa.hibernate.ddl-auto=update"])
class HibernateSchemaExporter {

  private val schemaExporter = SchemaExporter()

  @Autowired private lateinit var entityManagerFactoryBean: LocalContainerEntityManagerFactoryBean

  @Test
  fun exportH2Schema() {
    schemaExporter.exportSchema(
        entityManagerFactoryBean,
        H2Dialect::class.java,
        org.h2.Driver::class.java,
        "src/main/resources/db/schema-h2.sql")
  }

  @Test
  fun exportPostgresqlSchema() {
    schemaExporter.exportSchema(
        entityManagerFactoryBean,
        PostgreSQLExportDialect::class.java,
        org.postgresql.Driver::class.java,
        "src/main/resources/db/schema-postgresql.sql")
  }

  @Test
  fun exportMariadbSchema() {
    schemaExporter.exportSchema(
        entityManagerFactoryBean,
        MariaDB103Dialect::class.java,
        org.mariadb.jdbc.Driver::class.java,
        "src/main/resources/db/schema-mariadb.sql")
  }
}
