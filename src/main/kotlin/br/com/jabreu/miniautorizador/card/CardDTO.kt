package br.com.jabreu.miniautorizador.card

import com.fasterxml.jackson.annotation.JsonProperty

class CardDTO(
    @JsonProperty("numeroCartao") val number: String,
    @JsonProperty("senha") val password: String
)
