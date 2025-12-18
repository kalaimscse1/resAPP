package com.warriortech.resb.util

import kotlin.math.roundToInt

object CurrencyUtil {
    
    private var currencySymbol: String = "₹"
    private var decimalPlaces: Int = 2
    
    fun setCurrencySymbol(symbol: String) {
        currencySymbol = symbol
    }
    
    fun setDecimalPlaces(places: Int) {
        decimalPlaces = places
    }
    
    fun formatCurrency(amount: Double): String {
        val formatted = amount.roundToDecimalPlaces(decimalPlaces)
        return "$currencySymbol$formatted"
    }
    
    fun formatCurrencyWithoutSymbol(amount: Double): String {
        return amount.roundToDecimalPlaces(decimalPlaces)
    }
    
    fun parseCurrency(value: String): Double {
        return value.replace(currencySymbol, "")
            .replace(",", "")
            .trim()
            .toDoubleOrNull() ?: 0.0
    }
    
    fun calculateTax(amount: Double, taxPercentage: Double): Double {
        return (amount * taxPercentage / 100.0)
    }
    
    fun calculateGrandTotal(amount: Double, taxAmount: Double, discount: Double = 0.0): Double {
        return amount + taxAmount - discount
    }
    
    private fun Double.roundToDecimalPlaces(places: Int): String {
        val factor = Math.pow(10.0, places.toDouble())
        val rounded = (this * factor).roundToInt() / factor
        return "%.${places}f".format(rounded)
    }
    
    private fun Math.pow(base: Double, exponent: Double): Double {
        var result = 1.0
        repeat(exponent.toInt()) { result *= base }
        return result
    }
}
