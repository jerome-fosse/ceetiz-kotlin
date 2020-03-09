package fr.gouv.impots.entreprises.application.rest

import fr.gouv.impots.entreprises.domain.api.ImpotsService
import fr.gouv.impots.entreprises.domain.model.Adresse
import fr.gouv.impots.entreprises.domain.model.Entreprise
import fr.gouv.impots.entreprises.domain.model.EntrepriseIndividuelle
import fr.gouv.impots.entreprises.domain.model.EntrepriseSAS
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.util.function.Tuple2
import reactor.util.function.Tuples


class ImpotServiceAdapterTest {

    private lateinit var adapter: ImpotServiceAdapter

    @BeforeEach
    fun beforeEach() {
        val service = object :ImpotsService {
            override fun calculerImpotEntreprise(siret: String, annee: Int): Mono<Tuple2<out Entreprise, Int>> =
                when (siret) {
                    "12345" -> Mono.just(Tuples.of(EntrepriseIndividuelle(siret = "12345", denomination = "World Company", chiffresAffaire = mapOf(2018 to 150_000, 2019 to 100_000)), 25_000))
                    "56789" -> Mono.just(Tuples.of(EntrepriseSAS(siret = "56789", denomination = "World Company", adresse = Adresse(rue = "35 rue Victor Hugo", codePostal = "75001", ville = "Paris"), chiffresAffaire = mapOf(2018 to 150_000, 2019 to 100_000)), 33_000))
                    else -> Mono.empty()
                }
        }

        adapter = ImpotServiceAdapter(service)
    }

    @Test
    fun `Une entreprise individuelle est bien convertie dans le modele de l'API REST`() {
        // Quand je calule l'impot d'une société
        val resp = adapter.calculerImpotEntreprise("12345", 2019).block()

        // Alors, la réponse récue utilise le modele de l'API Rest
        assertThat(resp).isNotNull
        assertThat(resp!!.t1).isInstanceOf(fr.gouv.impots.entreprises.application.rest.model.Entreprise::class.java)
        assertThat(resp.t1.siret).isEqualTo("12345")
        assertThat(resp.t1.denomination).isEqualTo("World Company")
        assertThat(resp.t1.type).isEqualTo("INDIVIDUELLE")
        assertThat(resp.t1.adresse).isNull()
    }

    @Test
    fun `Une entreprise SAS est bien convertie dans le modele de l'API REST`() {
        // Quand je calule l'impot d'une société
        val resp = adapter.calculerImpotEntreprise("56789", 2019).block()

        // Alors, la réponse récue utilise le modele de l'API Rest
        assertThat(resp).isNotNull
        assertThat(resp!!.t1).isInstanceOf(fr.gouv.impots.entreprises.application.rest.model.Entreprise::class.java)
        assertThat(resp.t1.siret).isEqualTo("56789")
        assertThat(resp.t1.denomination).isEqualTo("World Company")
        assertThat(resp.t1.type).isEqualTo("SAS")
        assertThat(resp.t1.adresse).isNotNull
        assertThat(resp.t1.adresse!!.rue).isEqualTo("35 rue Victor Hugo")
        assertThat(resp.t1.adresse!!.codePostal).isEqualTo("75001")
        assertThat(resp.t1.adresse!!.ville).isEqualTo("Paris")
    }
}
