package fr.gouv.impots.entreprises

import fr.gouv.impots.entreprises.application.rest.model.CalculerImpotResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CalculImpotIntegrationTest(@LocalServerPort private val port: Int) {

    private lateinit var webTestClient: WebTestClient

    @BeforeEach
    fun beforeEach() {
        webTestClient = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:$port")
            .defaultHeader("Content-Type", "application/json")
            .defaultHeader("Accept", "application/json")
            .build()
    }

    @Test
    fun `L'impot pour les societes individuelles est de 25% du CA annuel`() {
        // Quand je lance le calucl de l'impot pour une société individuelle
        val request = webTestClient.post().uri("/impot/calculer").body(BodyInserters.fromValue("{\"siret\":\"12345\", \"annee\":2019}"))
        val response = request.exchange()

        // Le code retour HTTP est OK
        response.expectStatus().isOk

        // le body est OK
        val body = response.expectBody(CalculerImpotResponse::class.java).returnResult().responseBody;
        assertThat(body!!.entreprise.siret).isEqualTo("12345");
        assertThat(body.entreprise.denomination).isEqualTo("World Company");
        assertThat(body.entreprise.type).isEqualTo("INDIVIDUELLE");
        assertThat(body.entreprise.adresse).isNull();
        assertThat(body.annee).isEqualTo(2019);
        assertThat(body.montant).isEqualTo(25_000);
    }

    @Test
    fun `L'impot pour les societes SAS est de 33% du CA annuel`() {
        // Quand je lance le calucl de l'impot pour une société SAS
        val request = webTestClient.post().uri("/impot/calculer").body(BodyInserters.fromValue("{\"siret\":\"56789\", \"annee\":2019}"))
        val response = request.exchange()

        // Le code retour HTTP est OK
        response.expectStatus().isOk

        // le body est OK
        val body = response.expectBody(CalculerImpotResponse::class.java).returnResult().responseBody
        assertThat(body!!.entreprise.siret).isEqualTo("56789")
        assertThat(body.entreprise.denomination).isEqualTo("World Company")
        assertThat(body.entreprise.type).isEqualTo("SAS")
        assertThat(body.entreprise.adresse!!.rue).isEqualTo("35 rue Victor Hugo")
        assertThat(body.entreprise.adresse!!.codePostal).isEqualTo("75001")
        assertThat(body.entreprise.adresse!!.ville).isEqualTo("Paris")
        assertThat(body.annee).isEqualTo(2019)
        assertThat(body.montant).isEqualTo(33000)
    }

    @Test
    fun `Lorsque la societe n'existe pas alors le systeme retourne une erreur`() {
        // Quand je lance le calucl de l'impot pour uve société qui n'existe pas
        val request = webTestClient.post().uri("/impot/calculer").body(BodyInserters.fromValue("{\"siret\":\"00000\", \"annee\":2019}"))
        val response = request.exchange()

        // Le code retour HTTP est KO
        response.expectStatus().isBadRequest

        // Le message d'erreur indique que la société n'existe pas
        response.expectBody().jsonPath("$.message").isEqualTo("L'entreprise 00000 est inconnue")
    }
}
