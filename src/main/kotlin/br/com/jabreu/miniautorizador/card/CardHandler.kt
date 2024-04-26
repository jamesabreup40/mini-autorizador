package br.com.jabreu.miniautorizador.card

import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Service
class CardHandler(private val cardRepository: CardRepository) {

    suspend fun getBalance(request: ServerRequest): ServerResponse =
        cardRepository.findById(request.pathVariable("cardNumber")).let {
            ok().bodyValueAndAwait(it)
        }

    suspend fun createCard(request: ServerRequest): ServerResponse =
        cardRepository.save(request.awaitBody<Card>()).let {
            ok().bodyValueAndAwait(it)
        }
}

