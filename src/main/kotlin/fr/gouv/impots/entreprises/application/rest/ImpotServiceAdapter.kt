package fr.gouv.impots.entreprises.application.rest

import fr.gouv.impots.entreprises.application.rest.model.Adresse
import fr.gouv.impots.entreprises.application.rest.model.Entreprise
import fr.gouv.impots.entreprises.domain.api.ImpotsService
import fr.gouv.impots.entreprises.domain.model.EntrepriseIndividuelle
import fr.gouv.impots.entreprises.domain.model.EntrepriseSAS
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.util.function.Tuple2
import reactor.util.function.Tuples

@Component
class ImpotServiceAdapter(private val impotsService: ImpotsService) {

    fun calculerImpotEntreprise(siret: String, annee: Int) : Mono<Tuple2<Entreprise, Int>> {
        return impotsService.calculerImpotEntreprise(siret, annee)
            .map { tuple -> Tuples.of(mapDomainEntrepriseToRestEntreprise(tuple.t1), tuple.t2) }
    }

    private fun mapDomainEntrepriseToRestEntreprise(entreprise: fr.gouv.impots.entreprises.domain.model.Entreprise) : Entreprise =
        when (entreprise) {
            is EntrepriseIndividuelle -> Entreprise(siret = entreprise.siret, denomination = entreprise.denomination, type = "INDIVIDUELLE")
            is EntrepriseSAS -> Entreprise(siret = entreprise.siret, denomination = entreprise.denomination, type = "SAS", adresse = Adresse(entreprise.adresse.rue,entreprise.adresse.codePostal, entreprise.adresse.ville))
        }
}
