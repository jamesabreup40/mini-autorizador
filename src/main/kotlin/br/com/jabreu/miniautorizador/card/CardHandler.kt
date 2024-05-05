package br.com.jabreu.miniautorizador.card

import br.com.jabreu.miniautorizador.card.transaction.TransactionDTO
import br.com.jabreu.miniautorizador.card.transaction.TransactionResult.CARTAO_INEXISTENTE
import br.com.jabreu.miniautorizador.card.transaction.TransactionResult.SALDO_INSUFICIENTE
import br.com.jabreu.miniautorizador.card.transaction.TransactionResult.SENHA_INVALIDA
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.OK
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
            repository.findByNumber(requestedCardNumber)
                .let { foundCard -> foundCard?.balance?.let { ok().bodyValueAndAwait(it) } }
        } ?: notFound().buildAndAwait()


    suspend fun create(request: ServerRequest): ServerResponse =
        cardFromCreateRequest(request).let { requestedCardForCreate ->
            validateRequestedCardExists(requestedCardForCreate.number)?.let {
                unprocessableEntity().bodyValueAndAwait(requestedCardForCreate)
            } ?: repository.save(aNewCard(requestedCardForCreate))
                .let { createdCard -> status(CREATED).bodyValueAndAwait(aNewCardDTO(createdCard)) }
        }

    suspend fun transaction(request: ServerRequest): ServerResponse =
        transactionFromCreateRequest(request).let { requestedTransaction ->
            repository.findByNumber(requestedTransaction.cardNumber)
                ?.let { foundCardForTransaction ->
                    foundCardForTransaction.isAValidPassword(requestedTransaction.cardPassword)
                        .takeUnless { it }
                        ?.let { unprocessableEntity().bodyValueAndAwait(SENHA_INVALIDA) }
                        ?: foundCardForTransaction.haveBalance(requestedTransaction.amount)
                            .takeUnless { it }
                            ?.let { unprocessableEntity().bodyValueAndAwait(SALDO_INSUFICIENTE) }
                        ?: foundCardForTransaction.run {
                            this.withdraw(requestedTransaction.amount)
                            repository.save(foundCardForTransaction)
                            status(CREATED).bodyValueAndAwait(OK)
                        }
                }
        } ?: unprocessableEntity().bodyValueAndAwait(CARTAO_INEXISTENTE)

    private suspend fun cardFromCreateRequest(request: ServerRequest) =
        request.awaitBody<CardDTO>()

    private suspend fun validateRequestedCardExists(cardNumber: String) =
        repository.existsByNumber(cardNumber).takeIf { it }

    private fun cardNumberFromBalanceRequest(request: ServerRequest) =
        request.pathVariable("cardNumber")

    private fun aNewCard(requestedCardForCreate: CardDTO) =
        Card(number = requestedCardForCreate.number, password = requestedCardForCreate.password)

    private suspend fun aNewCardDTO(createdCard: Card): CardDTO =
        CardDTO(number = createdCard.number, password = createdCard.password)

    private suspend fun transactionFromCreateRequest(request: ServerRequest) =
        request.awaitBody<TransactionDTO>()
}
