package fr.gouv.impots.entreprises.domain.api

import fr.gouv.impots.entreprises.domain.model.Entreprise
import reactor.core.publisher.Mono
import reactor.util.function.Tuple2

interface ImpotsService {

    fun calculerImpotEntreprise(siret: String, annee: Int) : Mono<Tuple2<out Entreprise, Int>>
}
