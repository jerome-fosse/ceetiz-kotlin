package fr.gouv.impots.entreprises.domain

class EntrepriseInconnueException(siret: String) : RuntimeException("L'entreprise $siret est inconnue")
