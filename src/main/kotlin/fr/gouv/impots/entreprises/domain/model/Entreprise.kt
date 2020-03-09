package fr.gouv.impots.entreprises.domain.model

import arrow.core.None
import arrow.core.Option
import arrow.core.Some

sealed class Entreprise {
    abstract val siret: String
    abstract val denomination: String
    abstract val chiffresAffaire: Map<Int, Int>

    protected abstract val tauxImposition : Double

    fun calculerImpot(annee: Int): Option<Int> =
        when (val ca = chiffresAffaire[annee]) {
            null -> None
            else -> Some((ca * tauxImposition).toInt())
        }

}

data class EntrepriseIndividuelle(override val siret: String, override val denomination: String, override val chiffresAffaire: Map<Int, Int>) : Entreprise() {
    override val tauxImposition: Double = 0.25
    override fun toString(): String = "EntrepriseIndividuelle(siret=$siret, denomination=$denomination)"
}

data class EntrepriseSAS(override val siret: String, override val denomination: String, val adresse: Adresse, override val chiffresAffaire: Map<Int, Int>): Entreprise() {
    override val tauxImposition: Double = 0.33
    override fun toString(): String = "EntrepriseSAS(siret=$siret, denomination=$denomination)"
}
