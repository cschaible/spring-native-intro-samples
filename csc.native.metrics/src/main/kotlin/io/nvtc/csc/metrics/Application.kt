package io.nvtc.csc.metrics

import java.util.*
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.nativex.hint.TypeHint

@TypeHint(types = [UUID::class])
@SpringBootApplication(exclude = [DataSourceAutoConfiguration::class])
class Application

fun main(args: Array<String>) {
  runApplication<Application>(*args)
}
