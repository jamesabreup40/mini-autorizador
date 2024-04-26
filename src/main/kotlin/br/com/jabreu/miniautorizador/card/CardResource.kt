package br.com.jabreu.miniautorizador.card

import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.coRouter

@RestController
class CardResource(private val cardHandler: CardHandler) {

    @Bean
    fun cardEndpoints() = coRouter {
        accept(APPLICATION_JSON).nest {
            GET("/cartoes/{cardNumber}", cardHandler::getBalance)
            POST("/cartoes", cardHandler::createCard)
        }
    }
}