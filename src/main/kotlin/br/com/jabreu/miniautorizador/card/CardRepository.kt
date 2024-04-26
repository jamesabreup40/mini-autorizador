package br.com.jabreu.miniautorizador.card

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface CardRepository : ReactiveMongoRepository<Card, String>
