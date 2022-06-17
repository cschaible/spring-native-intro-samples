package io.nvtc.csc.jpa

import org.flywaydb.core.Flyway
import org.flywaydb.core.internal.logging.slf4j.Slf4jLogCreator
import org.flywaydb.core.internal.util.FeatureDetector
import org.flywaydb.database.MySQLDatabaseExtension
import org.hibernate.type.UUIDCharType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.domain.AbstractPersistable
import org.springframework.data.repository.query.FluentQuery
import org.springframework.nativex.hint.*

@NativeHints(
    NativeHint(
        types =
            [
                TypeHint(types = [UUIDCharType::class]),
                TypeHint(types = [AbstractPersistable::class], fields = [FieldHint(name = "id")]),
                TypeHint(types = [FluentQuery.FetchableFluentQuery::class])]),
    NativeHint(
        trigger = Flyway::class,
        initialization =
            [
                InitializationHint(
                    types = [FeatureDetector::class], initTime = InitializationTime.BUILD)],
        types = [TypeHint(types = [Slf4jLogCreator::class])],
        resources = [ResourceHint(patterns = ["org/flywaydb/core/internal/version.txt"])]),
    NativeHint(
        trigger = MySQLDatabaseExtension::class,
        resources = [ResourceHint(patterns = ["org/flywaydb/database/version.txt"])]))
@SpringBootApplication
class Application

fun main(args: Array<String>) {
  runApplication<Application>(*args)
}
