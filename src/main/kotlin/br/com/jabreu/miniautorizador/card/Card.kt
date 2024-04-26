package br.com.jabreu.miniautorizador.card

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
class Card(@Id val number: String, val password: String)
