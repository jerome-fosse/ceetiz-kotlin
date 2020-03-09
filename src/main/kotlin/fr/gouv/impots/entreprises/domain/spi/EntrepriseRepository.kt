package fr.gouv.impots.entreprises.domain.spi

import fr.gouv.impots.entreprises.domain.model.Entreprise
import reactor.core.publisher.Mono

interface EntrepriseRepository {

    fun chargerEntreprise(siret: String) : Mono<Entreprise>
}
