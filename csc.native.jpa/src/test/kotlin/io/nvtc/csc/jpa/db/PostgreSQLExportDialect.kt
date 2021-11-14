package io.nvtc.csc.jpa.db

import org.hibernate.dialect.PostgreSQL10Dialect
import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorNoOpImpl
import org.hibernate.tool.schema.extract.spi.SequenceInformationExtractor

/**
 * Custom implementation of [PostgreSQL10Dialect] to prevent "Could not fetch the
 * SequenceInformation from the database" error.
 */
class PostgreSQLExportDialect : PostgreSQL10Dialect() {

  override fun getSequenceInformationExtractor(): SequenceInformationExtractor =
      SequenceInformationExtractorNoOpImpl.INSTANCE
}
