package br.com.jabreu.miniautorizador

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MiniAutorizadorApplication

fun main(args: Array<String>) {
	runApplication<MiniAutorizadorApplication>(*args)
}
