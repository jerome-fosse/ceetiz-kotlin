package fr.gouv.impots.entreprises.application.rest.model

data class CalculerImpotResponse(val entreprise: Entreprise, val annee: Int, val montant: Int)
