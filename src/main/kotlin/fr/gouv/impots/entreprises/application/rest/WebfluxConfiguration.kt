package fr.gouv.impots.entreprises.application.rest

import fr.gouv.impots.entreprises.application.rest.handlers.CalculerImpotHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.function.server.router

@Configuration
@EnableWebFlux
class WebfluxConfiguration {
    @Bean
    fun calculerImpotRouter(handler: CalculerImpotHandler) = router {
        ("/impot/calculer" and contentType(MediaType.APPLICATION_JSON)).nest {
            POST("", handler::handleRequest )
        }
    }
}
