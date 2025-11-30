package com.example.itplaneta.domain.validation

import androidx.annotation.StringRes
import com.example.itplaneta.R
import com.example.itplaneta.core.otp.parser.Base32
import com.example.itplaneta.domain.AccountInputDto
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Constants for account validation
 */
object AccountConstraints {
    const val MIN_DIGITS = 1
    const val MAX_DIGITS = 9
    const val MIN_SECRET_LENGTH = 16
    const val MIN_PERIOD = 1
    const val MAX_PERIOD = 2_147_483
    const val DEFAULT_PERIOD = 30
    const val DEFAULT_DIGITS = 6
}

/**
 * Sealed class for validation results
 */
sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val error: AccountFieldError) : ValidationResult()
}

/**
 * Sealed class for field-specific errors
 */

sealed class AccountFieldError(@param:StringRes val resId: Int) {
    object SecretEmpty : AccountFieldError(R.string.error_secret_empty)
    object SecretInvalid : AccountFieldError(R.string.error_secret_invalid)
    object LabelEmpty : AccountFieldError(R.string.error_label_empty)
    object CounterInvalid : AccountFieldError(R.string.error_counter_invalid)
    object PeriodInvalid : AccountFieldError(R.string.error_period_invalid)
    object DigitsInvalid : AccountFieldError(R.string.error_digits_invalid)
}

/**
 * Validator for account input
 * Separates validation logic from ViewModel
 */
@Singleton
class AccountValidator @Inject constructor(private val base32: Base32) {

    fun validateField(field: FieldType, account: AccountInputDto): AccountFieldError? =
        when (field) {
            FieldType.LABEL -> when {
                account.label.isBlank() -> AccountFieldError.LabelEmpty
                else -> null
            }

            FieldType.SECRET -> when {
                account.secret.isBlank() -> AccountFieldError.SecretEmpty
                !isSecretValid(account.secret) -> AccountFieldError.SecretInvalid
                else -> null
            }

            FieldType.COUNTER -> when {
                !isCounterValid(account.counter) -> AccountFieldError.CounterInvalid
                else -> null
            }

            FieldType.PERIOD -> when {
                !isPeriodValid(account.period) -> AccountFieldError.PeriodInvalid
                else -> null
            }

            FieldType.DIGITS -> when {
                !isDigitsValid(account.digits) -> AccountFieldError.DigitsInvalid
                else -> null
            }

            FieldType.ISSUER -> null
        }

    fun validate(account: AccountInputDto): Map<FieldType, AccountFieldError?> {
        val errors = mutableMapOf<FieldType, AccountFieldError?>()

        errors[FieldType.LABEL] = when {
            account.label.isBlank() -> AccountFieldError.LabelEmpty
            else -> null
        }

        errors[FieldType.SECRET] = when {
            account.secret.isBlank() -> AccountFieldError.SecretEmpty
            !isSecretValid(account.secret) -> AccountFieldError.SecretInvalid
            else -> null
        }

        errors[FieldType.COUNTER] = when {
            !isCounterValid(account.counter) -> AccountFieldError.CounterInvalid
            else -> null
        }

        errors[FieldType.PERIOD] = when {
            !isPeriodValid(account.period) -> AccountFieldError.PeriodInvalid
            else -> null
        }

        // DIGITS (TOTP/HOTP digits)
        errors[FieldType.DIGITS] = when {
            !isDigitsValid(account.digits) -> AccountFieldError.DigitsInvalid
            else -> null
        }

        return errors

    }

    private fun isSecretValid(secret: String): Boolean {
        val cleanSecret = secret.replace(" ", "")
        if (cleanSecret.length < AccountConstraints.MIN_SECRET_LENGTH) {
            return false
        }
        return try {
            base32.decodeBase32(cleanSecret.uppercase())
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun isCounterValid(counter: String): Boolean {
        val value = counter.toLongOrNull() ?: return false
        return value >= 0
    }

    private fun isPeriodValid(period: String): Boolean {
        val value = period.toIntOrNull() ?: return false
        return value in AccountConstraints.MIN_PERIOD..AccountConstraints.MAX_PERIOD
    }

    private fun isDigitsValid(digits: String): Boolean {
        val value = digits.toIntOrNull() ?: return false
        return value in AccountConstraints.MIN_DIGITS..AccountConstraints.MAX_DIGITS
    }
}


enum class FieldType {
    LABEL, ISSUER, SECRET, COUNTER, DIGITS, PERIOD
}