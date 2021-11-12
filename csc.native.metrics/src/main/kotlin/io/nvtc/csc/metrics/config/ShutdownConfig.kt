package io.nvtc.csc.metrics.config

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.context.event.ContextClosedEvent

@Configuration
@Profile("!test")
class ShutdownConfig(
    @Value("\${shutdown.timeout-seconds:10}") private val timeoutSeconds: Long
) : ApplicationListener<ContextClosedEvent> {

  override fun onApplicationEvent(event: ContextClosedEvent) {
    LOGGER.info("Delay shutdown {} seconds to finish ongoing requests ...", timeoutSeconds)

    try {
      Thread.sleep(timeoutSeconds * 1000)
    } catch (e: InterruptedException) {
      LOGGER.error("The shutdown timeout has been interrupted")
    }
  }

  companion object {
    private val LOGGER = LoggerFactory.getLogger(ShutdownConfig::class.java)
  }
}
