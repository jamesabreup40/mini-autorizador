package br.com.jabreu.miniautorizador.card

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface CardRepository : ReactiveMongoRepository<Card, String> {

    fun findByNumber(cardNumber: String): Mono<Card>
    fun existsByNumber(number: String): Mono<Boolean>
}
