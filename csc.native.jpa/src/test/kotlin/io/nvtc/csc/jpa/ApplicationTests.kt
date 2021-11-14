package io.nvtc.csc.jpa

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

@TestPropertySource(
    properties =
        ["spring.flyway.enabled=true", "spring.flyway.locations: classpath:db/migration/h2"])
@SpringBootTest
class ApplicationTests {

  @Test fun contextLoads() {}
}
