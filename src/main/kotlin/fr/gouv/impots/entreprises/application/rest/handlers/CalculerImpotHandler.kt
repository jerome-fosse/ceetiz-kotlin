package fr.gouv.impots.entreprises.application.rest.handlers

import fr.gouv.impots.entreprises.application.rest.ImpotServiceAdapter
import fr.gouv.impots.entreprises.application.rest.model.CalculerImpotRequest
import fr.gouv.impots.entreprises.application.rest.model.CalculerImpotResponse
import fr.gouv.impots.entreprises.domain.ChiffreAffaireInconnuException
import fr.gouv.impots.entreprises.domain.EntrepriseInconnueException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.validation.Validator
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono

@Component
class CalculerImpotHandler(private val impotServiceAdapter: ImpotServiceAdapter, @Qualifier("defaultValidator") validator: Validator) : AbstractValidationHandler<CalculerImpotRequest>(validator) {

    companion object {
        val LOG = LoggerFactory.getLogger(CalculerImpotHandler::class.java)!!
    }

    override fun processBody(body: CalculerImpotRequest, request: ServerRequest): Mono<ServerResponse> {
        LOG.info("Demande de calcul de l'impot {} pour l'entreprise {}", body.annee, body.siret)

        return impotServiceAdapter.calculerImpotEntreprise(body.siret, body.annee)
            .flatMap { tuple -> ServerResponse.ok().body(BodyInserters.fromValue(CalculerImpotResponse(entreprise = tuple.t1, annee = body.annee, montant = tuple.t2))) }
            .doOnSuccess { LOG.info("L'impot {} pour la société {} a été calculé avec succes", body.annee, body.siret)}
            .doOnError { thrown -> LOG.error(thrown.message) }
            .onErrorResume(ChiffreAffaireInconnuException::class.java) { throwable -> Mono.error(ResponseStatusException(HttpStatus.BAD_REQUEST, throwable.message, throwable)) }
            .onErrorResume(EntrepriseInconnueException::class.java) { throwable -> Mono.error(ResponseStatusException(HttpStatus.BAD_REQUEST, throwable.message, throwable)) }
    }
}
