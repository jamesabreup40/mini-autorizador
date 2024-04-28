package br.com.jabreu.miniautorizador.card

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("cards")
class Card(val number: String, val password: String) {

    val balance: Double = 500.00

    @Id
    val id: String = UUID.randomUUID().toString()
}
