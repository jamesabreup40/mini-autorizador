package br.com.jabreu.miniautorizador.card

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.math.RoundingMode.HALF_EVEN
import java.util.UUID.randomUUID

@Document("cards")
class Card(
    @Id val id: String = randomUUID().toString(),
    @Indexed(unique = true) val number: String,
    val password: String,
    var balance: BigDecimal = BigDecimal(500).setScale(2, HALF_EVEN),
    @Version
    var version: Long? = null
) {

    fun withdraw(amount: BigDecimal) {
        balance = balance.subtract(amount)
    }

    fun haveBalance(incomingWithdrawAmount: BigDecimal): Boolean =
        balance >= incomingWithdrawAmount

    fun isAValidPassword(incomingCardPassword: String): Boolean =
        password == incomingCardPassword
}
