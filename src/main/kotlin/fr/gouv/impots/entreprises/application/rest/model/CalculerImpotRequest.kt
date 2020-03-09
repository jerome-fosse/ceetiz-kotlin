package fr.gouv.impots.entreprises.application.rest.model

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class CalculerImpotRequest(
    @NotBlank
    val siret: String,
    @NotNull
    val annee: Int
)
