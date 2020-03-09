package fr.gouv.impots.entreprises.application.rest.handlers

import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

interface RequestHandler {
    fun handleRequest(request: ServerRequest) : Mono<ServerResponse>
}
