package fr.gouv.impots.entreprises.domain

import fr.gouv.impots.entreprises.domain.model.Entreprise

class ChiffreAffaireInconnuException(entreprise: Entreprise, annee: Int)
    : RuntimeException("le Chiffre d'affaire $annee pour l'entreprise $entreprise n'est pas disponible")
