package br.com.jabreu.miniautorizador

import br.com.jabreu.miniautorizador.card.Card
import br.com.jabreu.miniautorizador.card.CardDTO
import br.com.jabreu.miniautorizador.card.CardRepository
import br.com.jabreu.miniautorizador.card.CardResource
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono.just

@SpringBootTest
class MiniAutorizadorApplicationTests {

    @Autowired
    lateinit var cardResource: CardResource

    @MockBean
    lateinit var cardRepository: CardRepository

    lateinit var client: WebTestClient

    @BeforeEach
    fun setup() {
        client = WebTestClient
            .bindToRouterFunction(cardResource.endpoints())
            .build();
    }

    @Disabled("Until this is a non flaky test")
    @Test
    fun whenCreateCard_thenStatusShouldBeOkWithExpectResponseBody() {
        val requestedCardDTO = CardDTO(number = "6549873025634501", password = "1234")
        val requestedCard = Card(number = "6549873025634501", password = "1234")

        `when`(cardRepository.existsByNumber(requestedCardDTO.number)).thenReturn(just(false))
        `when`(cardRepository.save(requestedCard)).thenReturn(just(requestedCard))

        client.post()
            .uri("/cartoes")
            .contentType(APPLICATION_JSON)
            .bodyValue(
                """
                {   
                    "numeroCartao": "6549873025634501",
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
                    "numeroCartao": "6549873025634501"
                } 
            """
            )
    }

}
