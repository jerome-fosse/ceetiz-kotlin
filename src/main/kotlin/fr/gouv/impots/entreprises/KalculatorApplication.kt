package fr.gouv.impots.entreprises

import fr.gouv.impots.entreprises.domain.DefaultImpotsService
import fr.gouv.impots.entreprises.domain.spi.EntrepriseRepository
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class KalculatorApplication {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<KalculatorApplication>(*args)
        }
    }

    @Bean
    fun impotService(entrepriseRepository: EntrepriseRepository) = DefaultImpotsService(entrepriseRepository)
}
