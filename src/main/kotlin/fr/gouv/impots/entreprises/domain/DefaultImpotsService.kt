package fr.gouv.impots.entreprises.domain

import arrow.core.Either
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
            .map { entreprise -> when (val either = entreprise.calculerImpot(annee)) {
                is Either.Left -> throw either.a
                is Either.Right -> Tuples.of(entreprise, either.b)
            }}
    }
}
