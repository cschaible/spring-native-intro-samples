package io.nvtc.csc.jpa

import org.flywaydb.core.Flyway
import org.flywaydb.core.internal.logging.buffered.BufferedLogCreator
import org.flywaydb.core.internal.logging.slf4j.Slf4jLogCreator
import org.flywaydb.core.internal.util.FeatureDetector
import org.hibernate.type.UUIDCharType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.nativex.hint.InitializationHint
import org.springframework.nativex.hint.InitializationTime
import org.springframework.nativex.hint.NativeHint
import org.springframework.nativex.hint.NativeHints
import org.springframework.nativex.hint.ResourceHint
import org.springframework.nativex.hint.TypeHint

@NativeHints(
    NativeHint(types = [TypeHint(types = [UUIDCharType::class])]),
    NativeHint(
        trigger = Flyway::class,
        initialization =
            [
                InitializationHint(
                    types =
                        [
                            FeatureDetector::class,
                            NativePathLocationScanner::class,
                            BufferedLogCreator::class],
                    initTime = InitializationTime.BUILD)],
        types = [TypeHint(types = [Slf4jLogCreator::class])],
        resources = [ResourceHint(patterns = ["org/flywaydb/core/internal/version.txt"])]))
@SpringBootApplication
class Application

fun main(args: Array<String>) {
  runApplication<Application>(*args)
}
