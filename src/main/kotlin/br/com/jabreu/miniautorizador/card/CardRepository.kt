package br.com.jabreu.miniautorizador.card

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository


@Repository
interface CardRepository : CoroutineCrudRepository<Card, String> {

    suspend fun findByNumber(number: String): Card?
    suspend fun existsByNumber(number: String): Boolean
}
