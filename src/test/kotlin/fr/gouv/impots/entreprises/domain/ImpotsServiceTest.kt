package fr.gouv.impots.entreprises.domain

import fr.gouv.impots.entreprises.domain.api.ImpotsService
import fr.gouv.impots.entreprises.domain.model.Adresse
import fr.gouv.impots.entreprises.domain.model.Entreprise
import fr.gouv.impots.entreprises.domain.model.EntrepriseIndividuelle
import fr.gouv.impots.entreprises.domain.model.EntrepriseSAS
import fr.gouv.impots.entreprises.domain.spi.EntrepriseRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono


class ImpotsServiceTest {

    private lateinit var service: ImpotsService

    @BeforeEach
    fun beforeEach() {
        val repository = object : EntrepriseRepository {
            override fun chargerEntreprise(siret: String): Mono<Entreprise> =
                when (siret) {
                    "12345" -> Mono.just(EntrepriseIndividuelle(siret = "12345", denomination = "World Company", chiffresAffaire = mapOf(2018 to 150_000, 2019 to 100_000)))
                    "56789" -> Mono.just(EntrepriseSAS(siret = "56789", denomination = "World Company", adresse = Adresse(rue = "35 rue Victor Hugo", codePostal = "75001", ville = "Paris"), chiffresAffaire = mapOf(2018 to 150_000, 2019 to 100_000)))
                    "00000" -> Mono.just(EntrepriseIndividuelle(siret = "12345", denomination = "World Company", chiffresAffaire = mapOf(2018 to 150_000, 2019 to 0)))
                    else -> Mono.empty()
                }
        }

        service = DefaultImpotsService(repository);
    }

    @Test
    fun `L'impot des entreprises individuelles doit etre egal a 25% du chiffre d'affaire annuel`() {
        // Etant donné une entreprise individuelle dont le chiffre d'affaire 2019 est égal à 100 000
        val siret = "12345"

        // Quand je calcule son impot
        val resp = service.calculerImpotEntreprise(siret, 2019).block()

        // Alors l'impot est égal à 25 000
        assertThat(resp?.t2).isEqualTo(25_000)
    }

    @Test
    fun `L'impot des entreprises SAS doit etre egal a 33% du chiffre d'affaire annuel`() {
        // Etant donné une entreprise SAS dont le chiffre d'affaire 2019 est égal à 100 000
        val siret = "56789"

        // Quand je calcule son impot
        val resp = service.calculerImpotEntreprise(siret, 2019).block()

        // Alors l'impot est égal à 33 000
        assertThat(resp?.t2).isEqualTo(33_000)
    }

    @Test
    fun `Quand le chiffre d'affaire d'une entreprise est inconnu alors le calcul de l'impot est impossible est renvoie une erreur`() {
        // Etant donné une entreprise dont le chiffre d'affaire 2017 n'est pas connu
        val siret = "12345"

        // Quand je calcule son impot
        val thrown = catchThrowable { service.calculerImpotEntreprise(siret, 2017).block() }

        // Alors j'ai une erreur
        assertThat(thrown).isNotNull()
        assertThat(thrown).isInstanceOf(ChiffreAffaireInconnuException::class.java)
        assertThat(thrown.message).isEqualTo("le Chiffre d'affaire 2017 pour l'entreprise EntrepriseIndividuelle(siret=12345, denomination=World Company) n'est pas disponible")
    }

    @Test
    fun `Quand l'entreprise est inconnu alors le calcul de l'impot est impossible et renvoie une erreur`() {
        // Etant donné une entreprise dont le chiffre d'affaire 2017 n'est pas connu
        val siret = "99999"

        // Quand je calcule son impot
        val thrown = catchThrowable { service.calculerImpotEntreprise(siret, 2017).block()}

        // Alors j'ai une erreur
        assertThat(thrown).isNotNull()
        assertThat(thrown).isInstanceOf(EntrepriseInconnueException::class.java)
        assertThat(thrown.message).isEqualTo("L'entreprise 99999 est inconnue")
    }

    @Test
    fun `Quand le chiffre d'affaire d'une entreprise est egal a zero alors l'impot est egal a zero`() {
        // Etant donné une entreprise dont le chiffre d'affaire 2019 est égal à 0
        val siret = "00000"

        // Quand je calcule son impot
        val resp = service.calculerImpotEntreprise(siret, 2019).block()

        // Alors l'impot est égal à 0
        assertThat(resp?.t2).isEqualTo(0)
    }
}
