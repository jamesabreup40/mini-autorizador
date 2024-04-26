package br.com.jabreu.miniautorizador

import br.com.jabreu.miniautorizador.card.CardRepository
import br.com.jabreu.miniautorizador.card.CardResource
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.reactive.server.WebTestClient

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
            .bindToRouterFunction(cardResource.cardEndpoints())
            .build();
    }

    @Test
    fun whenCreateCard_thenStatusShouldBeOkWithExpectResponseBody() {
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
