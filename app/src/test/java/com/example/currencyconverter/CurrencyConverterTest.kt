package com.example.currencyconverter

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

/**
 * Tests unitaires validant la logique de conversion selon le barème du TP :
 * 1. 120 USD vers EUR = 102.56
 * 2. 100 EUR vers TND = 340.00
 * 3. 50 GBP vers USD = 67.24
 * 4. 10 EUR vers EUR = 10.00
 */
class CurrencyConverterTest {

    private val taux = mapOf(
        "EUR" to 1.0,
        "USD" to 1.17,
        "TND" to 3.40,
        "GBP" to 0.87,
        "JPY" to 172.50,
        "CAD" to 1.62
    )

    private fun convertir(montant: Double, source: String, cible: String): Double {
        if (source == cible) {
            return montant
        }
        val tauxSource = taux[source] ?: 1.0
        val tauxCible = taux[cible] ?: 1.0
        val montantEnEur = montant / tauxSource
        return montantEnEur * tauxCible
    }

    private fun formater(valeur: Double): String {
        return String.format(Locale.US, "%.2f", valeur)
    }

    @Test
    fun testCas1_USD_vers_EUR() {
        val resultat = convertir(120.0, "USD", "EUR")
        val affichage = formater(resultat)
        assertEquals("102.56", affichage)
    }

    @Test
    fun testCas2_EUR_vers_TND() {
        val resultat = convertir(100.0, "EUR", "TND")
        val affichage = formater(resultat)
        assertEquals("340.00", affichage)
    }

    @Test
    fun testCas3_GBP_vers_USD() {
        val resultat = convertir(50.0, "GBP", "USD")
        val affichage = formater(resultat)
        assertEquals("67.24", affichage)
    }

    @Test
    fun testCas4_EUR_vers_EUR() {
        val resultat = convertir(10.0, "EUR", "EUR")
        val affichage = formater(resultat)
        assertEquals("10.00", affichage)
    }

    @Test
    fun testDevisesIdentiques_TND_vers_TND() {
        val resultat = convertir(55.5, "TND", "TND")
        val affichage = formater(resultat)
        assertEquals("55.50", affichage)
    }
}
