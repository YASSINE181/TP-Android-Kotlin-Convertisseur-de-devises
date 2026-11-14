package com.example.currencyconverter

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.currencyconverter.databinding.ActivityMainBinding
import java.util.Locale

/**
 * Activité principale de l'application « Currency Converter ».
 * Permet la conversion d'un montant d'une devise source vers une devise cible
 * à partir de taux de change fixes avec l'Euro (EUR) comme devise pivot.
 */
class MainActivity : AppCompatActivity() {

    // Liaison des vues avec View Binding
    private lateinit var binding: ActivityMainBinding

    // Taux de change fixes par rapport à 1 EUR
    private val taux = mapOf(
        "EUR" to 1.0,
        "USD" to 1.17,
        "TND" to 3.40,
        "GBP" to 0.87,
        "JPY" to 172.50,
        "CAD" to 1.62
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialisation du View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuration des Spinners pour les devises
        configurerSpinners()

        // Configuration des écouteurs du clavier numérique personnalisé
        configurerClavier()

        // Configuration des boutons de contrôle (Convertir et Inverser)
        configurerActions()

        // Restauration de l'état si sauvegarde existante
        if (savedInstanceState != null) {
            val montantSauvegarde = savedInstanceState.getString(KEY_MONTANT, getString(R.string.montant_initial))
            val resultatSauvegarde = savedInstanceState.getString(KEY_RESULTAT, getString(R.string.resultat_initial))
            val sourcePos = savedInstanceState.getInt(KEY_SOURCE_POS, 0)
            val ciblePos = savedInstanceState.getInt(KEY_CIBLE_POS, 1)

            binding.tvMontant.text = montantSauvegarde
            binding.tvResultat.text = resultatSauvegarde
            binding.spDeviseSource.setSelection(sourcePos)
            binding.spDeviseCible.setSelection(ciblePos)
        }
    }

    /**
     * Initialise les adaptateurs des Spinners depuis la ressource strings.xml (R.array.devises)
     * et définit les sélections par défaut : USD en source, EUR en cible.
     */
    private fun configurerSpinners() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.devises,
            R.layout.spinner_item
        ).also { spinAdapter ->
            spinAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        }

        binding.spDeviseSource.adapter = adapter
        binding.spDeviseCible.adapter = adapter

        // Sélection par défaut : source = USD (index 0), cible = EUR (index 1)
        binding.spDeviseSource.setSelection(0)
        binding.spDeviseCible.setSelection(1)
    }

    /**
     * Associe chaque touche numérique et le point à la fonction ajouterCaractere,
     * et la touche « C » à la remise à zéro.
     */
    private fun configurerClavier() {
        val touchesNumeriques = listOf(
            binding.btn0 to "0",
            binding.btn1 to "1",
            binding.btn2 to "2",
            binding.btn3 to "3",
            binding.btn4 to "4",
            binding.btn5 to "5",
            binding.btn6 to "6",
            binding.btn7 to "7",
            binding.btn8 to "8",
            binding.btn9 to "9",
            binding.btnPoint to "."
        )

        for ((bouton, caractere) in touchesNumeriques) {
            bouton.setOnClickListener {
                ajouterCaractere(caractere)
            }
        }

        // Touche « C » : remet le montant à « 0 » et vide le résultat
        binding.btnC.setOnClickListener {
            binding.tvMontant.text = getString(R.string.montant_initial)
            binding.tvResultat.text = getString(R.string.resultat_initial)
        }
    }

    /**
     * Configure les écouteurs de clics pour le bouton Convertir et le bouton Inverser.
     */
    private fun configurerActions() {
        // Clic sur le bouton « CONVERTIR »
        binding.btnConvertir.setOnClickListener {
            executerConversion()
        }

        // Clic sur le bouton « Inverser » (btnInverser)
        binding.btnInverser.setOnClickListener {
            inverserDevises()
        }
    }

    /**
     * Ajoute un caractère à la saisie du montant en respectant les règles :
     * 1. Longueur maximale de 10 caractères.
     * 2. Un seul point décimal autorisé.
     * 3. Si le montant actuel est « 0 », le premier chiffre tapé le remplace.
     */
    fun ajouterCaractere(c: String) {
        val texteActuel = binding.tvMontant.text.toString()

        // Règle 1 : Limite de 10 caractères maximum
        if (texteActuel.length >= 10) {
            return
        }

        // Règle 2 : Un seul point décimal autorisé (un deuxième point est ignoré)
        if (c == "." && texteActuel.contains(".")) {
            return
        }

        // Règle 3 : Le montant initial est « 0 », le premier chiffre le remplace
        if (texteActuel == "0") {
            if (c == ".") {
                binding.tvMontant.text = "0."
            } else {
                binding.tvMontant.text = c
            }
        } else {
            binding.tvMontant.append(c)
        }
    }

    /**
     * Effectue la conversion monétaire via l'Euro (EUR) comme devise pivot :
     * montant en EUR = montant / taux(source)
     * résultat = montant en EUR * taux(cible)
     * Si la source et la cible sont identiques, le résultat est égal au montant.
     */
    fun convertir(montant: Double, source: String, cible: String): Double {
        if (source == cible) {
            return montant
        }
        val tauxSource = taux[source] ?: 1.0
        val tauxCible = taux[cible] ?: 1.0
        val montantEnEur = montant / tauxSource
        return montantEnEur * tauxCible
    }

    /**
     * Valide la saisie, applique la conversion et formate le résultat à 2 décimales.
     * Affiche un Toast d'erreur si le montant vaut 0.
     */
    private fun executerConversion() {
        val texteMontant = binding.tvMontant.text.toString().trim()
        val montant = texteMontant.toDoubleOrNull() ?: 0.0

        // Si le montant vaut 0, afficher le Toast d'avertissement
        if (montant == 0.0) {
            Toast.makeText(this, getString(R.string.toast_montant_zero), Toast.LENGTH_SHORT).show()
            return
        }

        val deviseSource = binding.spDeviseSource.selectedItem.toString()
        val deviseCible = binding.spDeviseCible.selectedItem.toString()

        val resultat = convertir(montant, deviseSource, deviseCible)

        // Affichage du résultat avec arrondi à 2 décimales
        binding.tvResultat.text = String.format(Locale.US, "%.2f", resultat)
    }

    /**
     * Échange les sélections des deux Spinners (source et cible)
     * et relance automatiquement le calcul si un résultat est déjà affiché.
     */
    private fun inverserDevises() {
        val positionSource = binding.spDeviseSource.selectedItemPosition
        val positionCible = binding.spDeviseCible.selectedItemPosition

        // Échange des sélections
        binding.spDeviseSource.setSelection(positionCible)
        binding.spDeviseCible.setSelection(positionSource)

        // Si un résultat est déjà affiché, relancer la conversion immédiatement
        if (binding.tvResultat.text.isNotEmpty()) {
            val texteMontant = binding.tvMontant.text.toString().trim()
            val montant = texteMontant.toDoubleOrNull() ?: 0.0

            if (montant > 0.0) {
                val nouvelleSource = binding.spDeviseSource.adapter.getItem(positionCible).toString()
                val nouvelleCible = binding.spDeviseCible.adapter.getItem(positionSource).toString()
                val resultat = convertir(montant, nouvelleSource, nouvelleCible)
                binding.tvResultat.text = String.format(Locale.US, "%.2f", resultat)
            }
        }
    }

    /**
     * Sauvegarde l'état lors d'un changement de configuration (orientation, etc.)
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_MONTANT, binding.tvMontant.text.toString())
        outState.putString(KEY_RESULTAT, binding.tvResultat.text.toString())
        outState.putInt(KEY_SOURCE_POS, binding.spDeviseSource.selectedItemPosition)
        outState.putInt(KEY_CIBLE_POS, binding.spDeviseCible.selectedItemPosition)
    }

    companion object {
        private const val KEY_MONTANT = "key_montant"
        private const val KEY_RESULTAT = "key_resultat"
        private const val KEY_SOURCE_POS = "key_source_pos"
        private const val KEY_CIBLE_POS = "key_cible_pos"
    }
}
