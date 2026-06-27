package com.example.itplaneta.domain.validation

object PinValidator {
    const val PIN_LENGTH = 6

    fun isValid(pin: String): Boolean {
        return pin.length == PIN_LENGTH && pin.all(Char::isDigit)
    }
}
