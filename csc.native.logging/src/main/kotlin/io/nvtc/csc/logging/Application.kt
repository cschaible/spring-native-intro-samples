package io.nvtc.csc.logging

import java.util.*
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.nativex.hint.TypeHint

@TypeHint(types = [UUID::class]) @SpringBootApplication class Application

fun main(args: Array<String>) {
  runApplication<Application>(*args)
}
