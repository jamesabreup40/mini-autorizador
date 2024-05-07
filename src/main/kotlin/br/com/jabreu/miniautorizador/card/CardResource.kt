package br.com.jabreu.miniautorizador.card

import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.TEXT_PLAIN
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.coRouter

@RestController
class CardResource(private val handler: CardHandler) {

    @Bean
    fun endpoints() = coRouter {
        accept(APPLICATION_JSON).nest {
            GET("/cartoes/{cardNumber}", handler::getBalance)
            POST("/cartoes", handler::create)
        }
        accept(TEXT_PLAIN).let {
            POST("/transacoes", handler::transaction)
        }
    }
}