package flsh.time.keeper

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AxonTimeKeeperApplication

fun main(args: Array<String>) {
	runApplication<AxonTimeKeeperApplication>(*args)
}
