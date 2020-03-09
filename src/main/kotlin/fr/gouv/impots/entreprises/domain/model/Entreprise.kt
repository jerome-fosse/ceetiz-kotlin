package fr.gouv.impots.entreprises.domain.model

import arrow.core.Either
import fr.gouv.impots.entreprises.domain.ChiffreAffaireInconnuException

sealed class Entreprise {
    abstract val siret: String
    abstract val denomination: String
    abstract val chiffresAffaire: Map<Int, Int>

    abstract fun calculerImpot(annee: Int) : Either<ChiffreAffaireInconnuException, Int>

}

data class EntrepriseIndividuelle(override val siret: String, override val denomination: String, override val chiffresAffaire: Map<Int, Int>) : Entreprise() {
    override fun calculerImpot(annee: Int): Either<ChiffreAffaireInconnuException, Int> =
        when (val ca = chiffresAffaire[annee]) {
            null -> Either.Left(ChiffreAffaireInconnuException(this, annee))
            else -> Either.right((ca * 0.25).toInt())
        }

    override fun toString(): String = "EntrepriseIndividuelle(siret=$siret, denomination=$denomination)"
}

data class EntrepriseSAS(override val siret: String, override val denomination: String, val adresse: Adresse, override val chiffresAffaire: Map<Int, Int>): Entreprise() {
    override fun calculerImpot(annee: Int): Either<ChiffreAffaireInconnuException, Int> =
        when (val ca = chiffresAffaire[annee]) {
            null -> Either.Left(ChiffreAffaireInconnuException(this, annee))
            else -> Either.right((ca * 0.33).toInt())
        }

    override fun toString(): String = "EntrepriseSAS(siret=$siret, denomination=$denomination)"
}
