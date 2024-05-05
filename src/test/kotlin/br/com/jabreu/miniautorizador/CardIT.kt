package br.com.jabreu.miniautorizador

import br.com.jabreu.miniautorizador.card.Card
import br.com.jabreu.miniautorizador.card.CardRepository
import br.com.jabreu.miniautorizador.card.CardResource
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.UUID.randomUUID

@SpringBootTest
@ActiveProfiles("test")
class CardIT {

    @Autowired
    lateinit var cardResource: CardResource

    @Autowired
    lateinit var repository: CardRepository
    lateinit var client: WebTestClient
    val randomCardNumber = randomUUID().toString()

    @BeforeEach
    fun setup() {
        client = WebTestClient
            .bindToRouterFunction(cardResource.endpoints())
            .build();
    }

    @Test
    fun whenCreateCard_thenStatusShouldBeCreatedWithExpectResponseBody() {
        client.post()
            .uri("/cartoes")
            .contentType(APPLICATION_JSON)
            .bodyValue(
                """
                {   
                    "numeroCartao": "$randomCardNumber",
                    "senha": "1234"
                }
                """
            )
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .json(
                """
                {
                    "senha": "1234",
                    "numeroCartao": "$randomCardNumber"
                } 
            """
            )
    }

    @Test
    fun whenCreateAExistentCard_thenStatusShouldBeUnprocessableEntityWithExpectResponseBody() {
        runBlocking {
            launch { repository.save(Card(number = randomCardNumber, password = "1234")) }
        }
        client.post()
            .uri("/cartoes")
            .contentType(APPLICATION_JSON)
            .bodyValue(
                """
                {   
                    "numeroCartao": "$randomCardNumber",
                    "senha": "1234"
                }
                """
            )
            .exchange()
            .expectStatus().is4xxClientError
            .expectBody()
            .json(
                """
                {
                    "senha": "1234",
                    "numeroCartao": "$randomCardNumber"
                } 
            """
            )
    }

    @Test
    fun whenTransactionACardWithInvalidPassword_thenStatusShouldBeUnprocessableEntityWithExpectResponseBody() {
        runBlocking {
            launch { repository.save(Card(number = randomCardNumber, password = "1234")) }
        }
        client.post()
            .uri("/transacoes")
            .contentType(APPLICATION_JSON)
            .bodyValue(
                """
                {
                    "numeroCartao": "$randomCardNumber",
                    "senhaCartao": "12298483",
                    "valor": 10.00
                }
                """
            )
            .exchange()
            .expectStatus().is4xxClientError
            .expectBody(String::class.java)
            .isEqualTo(""""SENHA_INVALIDA"""")
    }

    @Test
    fun whenTransactionACardWithoutBalance_thenStatusShouldBeUnprocessableEntityWithExpectResponseBody() {
        runBlocking {
            launch { repository.save(Card(number = randomCardNumber, password = "1234")) }
        }
        client.post()
            .uri("/transacoes")
            .contentType(APPLICATION_JSON)
            .bodyValue(
                """
                {
                    "numeroCartao": "$randomCardNumber",
                    "senhaCartao": "1234",
                    "valor": 500.01
                }
                """
            )
            .exchange()
            .expectStatus().is4xxClientError
            .expectBody(String::class.java)
            .isEqualTo(""""SALDO_INSUFICIENTE"""")
    }
}
