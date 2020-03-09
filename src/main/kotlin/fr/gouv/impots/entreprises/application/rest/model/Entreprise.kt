package fr.gouv.impots.entreprises.application.rest.model

import com.fasterxml.jackson.annotation.JsonInclude

data class Entreprise (
    val siret: String,
    val denomination: String,
    val type: String,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val adresse: Adresse? = null
)
