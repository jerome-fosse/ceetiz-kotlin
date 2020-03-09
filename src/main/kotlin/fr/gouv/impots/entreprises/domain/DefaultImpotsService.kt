package fr.gouv.impots.entreprises.domain

import arrow.core.None
import arrow.core.Some
import fr.gouv.impots.entreprises.domain.api.ImpotsService
import fr.gouv.impots.entreprises.domain.model.Entreprise
import fr.gouv.impots.entreprises.domain.spi.EntrepriseRepository
import org.springframework.beans.factory.annotation.Autowired
import reactor.core.publisher.Mono
import reactor.util.function.Tuple2
import reactor.util.function.Tuples

class DefaultImpotsService(@Autowired private val repository: EntrepriseRepository): ImpotsService {

    override fun calculerImpotEntreprise(siret: String, annee: Int): Mono<Tuple2<out Entreprise, Int>> {
        return repository.chargerEntreprise(siret)
            .switchIfEmpty(Mono.error(EntrepriseInconnueException(siret)))
            .map { entreprise -> when (val opt = entreprise.calculerImpot(annee)) {
                is None -> throw ChiffreAffaireInconnuException(entreprise, annee)
                is Some -> Tuples.of(entreprise, opt.t)
            }}
    }
}
