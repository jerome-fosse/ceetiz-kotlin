package fr.gouv.impots.entreprises.infra.data

import fr.gouv.impots.entreprises.domain.model.Adresse
import fr.gouv.impots.entreprises.domain.model.Entreprise
import fr.gouv.impots.entreprises.domain.model.EntrepriseIndividuelle
import fr.gouv.impots.entreprises.domain.model.EntrepriseSAS
import fr.gouv.impots.entreprises.domain.spi.EntrepriseRepository
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class DatabaseAdapter : EntrepriseRepository {
    override fun chargerEntreprise(siret: String): Mono<Entreprise> {
        return when (siret) {
            "12345" -> Mono.just(EntrepriseIndividuelle(siret = "12345", denomination = "World Company", chiffresAffaire = mapOf(2018 to 150_000, 2019 to 100_000)))
            "56789" -> Mono.just(EntrepriseSAS(siret = "56789", denomination = "World Company", adresse = Adresse(rue = "35 rue Victor Hugo", codePostal = "75001", ville = "Paris"), chiffresAffaire = mapOf(2018 to 150_000, 2019 to 100_000)))
            else -> Mono.empty()
        }
    }
}
