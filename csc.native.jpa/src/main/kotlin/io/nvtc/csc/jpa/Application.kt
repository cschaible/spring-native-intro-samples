package io.nvtc.csc.jpa

import org.hibernate.type.UUIDCharType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.nativex.hint.TypeHint

@TypeHint(types = [UUIDCharType::class]) @SpringBootApplication class Application

fun main(args: Array<String>) {
  runApplication<Application>(*args)
}
