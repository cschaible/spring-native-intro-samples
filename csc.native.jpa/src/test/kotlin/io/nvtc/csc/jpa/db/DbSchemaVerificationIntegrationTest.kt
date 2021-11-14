package io.nvtc.csc.jpa.db

import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets.UTF_8
import java.sql.Driver
import org.assertj.core.api.Assertions.assertThat
import org.hibernate.dialect.Dialect
import org.hibernate.dialect.H2Dialect
import org.hibernate.dialect.MariaDB103Dialect
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.core.io.Resource
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource

@ActiveProfiles("test")
@DataJpaTest
@TestPropertySource(
    properties = ["spring.flyway.enabled=false", "spring.jpa.hibernate.ddl-auto=update"])
class DbSchemaVerificationIntegrationTest {

  private val schemaExporter = SchemaExporter()

  @Value("classpath:db/schema-h2.sql") private lateinit var schemaH2: Resource

  @Value("classpath:db/schema-postgresql.sql") private lateinit var schemaPostgresql: Resource

  @Value("classpath:db/schema-mariadb.sql") private lateinit var schemaMariadb: Resource

  @Autowired private lateinit var entityManagerFactoryBean: LocalContainerEntityManagerFactoryBean

  @Test
  @Throws(IOException::class)
  fun verifyH2SchemaUpToDate() {
    verifySchema(schemaH2, H2Dialect::class.java, org.h2.Driver::class.java)
  }

  @Test
  @Throws(IOException::class)
  fun verifyPostgresqlSchemaUpToDate() {
    verifySchema(
        schemaPostgresql, PostgreSQLExportDialect::class.java, org.postgresql.Driver::class.java)
  }

  @Test
  @Throws(IOException::class)
  fun verifyMariaDbSchemaUpToDate() {
    verifySchema(schemaMariadb, MariaDB103Dialect::class.java, org.mariadb.jdbc.Driver::class.java)
  }

  @Throws(IOException::class)
  private fun verifySchema(
      referenceFile: Resource,
      dialect: Class<out Dialect>,
      driver: Class<out Driver>
  ) {
    val tempFile =
        File.createTempFile("db-schema-verification", referenceFile.filename).apply {
          this.deleteOnExit()
        }

    schemaExporter.exportSchema(entityManagerFactoryBean, dialect, driver, tempFile.absolutePath)

    var generatedSchema = tempFile.readText(UTF_8)
    generatedSchema = generatedSchema.replace("\r\n", "\n")

    var referenceSchema = referenceFile.file.readText(UTF_8)
    referenceSchema = referenceSchema.replace("\r\n", "\n")

    assertThat(generatedSchema).isEqualTo(referenceSchema)
  }
}
