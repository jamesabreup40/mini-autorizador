package br.com.jabreu.miniautorizador.card

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.http.HttpStatus.CREATED
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.*
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Service
class CardHandler(private val repository: CardRepository) {

    suspend fun getBalance(request: ServerRequest): ServerResponse =
        repository.findByNumber(request.pathVariable("cardNumber")).awaitSingle().let {
            ok().bodyValueAndAwait(it.balance)
        }

    suspend fun create(request: ServerRequest): ServerResponse =
        request.awaitBody<CardDTO>().let { requestedCardForCreate ->
            validateRequestedCardExists(requestedCardForCreate)
                ?: repository.save(Card(requestedCardForCreate.number, requestedCardForCreate.password))
                    .awaitSingle()
                    .let { createdCard -> status(CREATED).bodyValueAndAwait(entityToDto(createdCard)) }
        }

    private suspend fun validateRequestedCardExists(requestedCardDTO: CardDTO) =
        repository.existsByNumber(requestedCardDTO.number).awaitSingle().takeIf { it }
            ?.let { unprocessableEntity().bodyValueAndAwait(requestedCardDTO) }

    private suspend fun entityToDto(createdCard: Card): CardDTO =
        CardDTO(createdCard.number, createdCard.password)
}
