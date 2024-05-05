package br.com.jabreu.miniautorizador.card

import com.fasterxml.jackson.annotation.JsonProperty

data class CardDTO(
    @JsonProperty("senha") val password: String,
    @JsonProperty("numeroCartao") val number: String
)
