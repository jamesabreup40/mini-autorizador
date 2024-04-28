package br.com.jabreu.miniautorizador.card

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.http.HttpStatus.CREATED
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.notFound
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.ServerResponse.status
import org.springframework.web.reactive.function.server.ServerResponse.unprocessableEntity
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait

@Service
class CardHandler(private val repository: CardRepository) {

    suspend fun getBalance(request: ServerRequest): ServerResponse =
        cardNumberFromBalanceRequest(request).let { requestedCardNumber ->
            validateRequestedCardExists(requestedCardNumber)?.let {
                repository.findByNumber(requestedCardNumber).awaitSingle()
                    .let { foundCard -> ok().bodyValueAndAwait(foundCard.balance) }
            }
                ?: notFound().buildAndAwait()
        }

    suspend fun create(request: ServerRequest): ServerResponse =
        cardFromCreateRequest(request).let { requestedCardForCreate ->
            validateRequestedCardExists(requestedCardForCreate.number)?.let {
                unprocessableEntity().bodyValueAndAwait(requestedCardForCreate)
            }
                ?: repository.save(aNewCard(requestedCardForCreate)).awaitSingle()
                    .let { createdCard -> status(CREATED).bodyValueAndAwait(aNewDTO(createdCard)) }
        }

    private fun cardNumberFromBalanceRequest(request: ServerRequest) =
        request.pathVariable("cardNumber")

    private suspend fun cardFromCreateRequest(request: ServerRequest) =
        request.awaitBody<CardDTO>()

    private suspend fun validateRequestedCardExists(cardNumber: String) =
        repository.existsByNumber(cardNumber).awaitSingle().takeIf { it }

    private fun aNewCard(requestedCardForCreate: CardDTO) =
        Card(number = requestedCardForCreate.number, password = requestedCardForCreate.password)

    private suspend fun aNewDTO(createdCard: Card): CardDTO =
        CardDTO(createdCard.number, createdCard.password)
}
