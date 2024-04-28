package br.com.jabreu.miniautorizador.card

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.UUID.randomUUID

@Document("cards")
class Card(
    @Id val id: String = randomUUID().toString(),
    val number: String,
    val password: String,
    val balance: Double = 500.00
)
