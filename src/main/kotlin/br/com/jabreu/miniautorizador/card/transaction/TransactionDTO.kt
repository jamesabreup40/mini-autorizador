package br.com.jabreu.miniautorizador.card.transaction

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class TransactionDTO(
    @JsonProperty("numeroCartao") val cardNumber: String,
    @JsonProperty("senhaCartao") val cardPassword: String,
    @JsonProperty("valor") val amount: BigDecimal
)