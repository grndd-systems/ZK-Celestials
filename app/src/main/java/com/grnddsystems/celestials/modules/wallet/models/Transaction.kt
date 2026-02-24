package com.grnddsystems.celestials.modules.wallet.models

import com.grnddsystems.celestials.R
import com.grnddsystems.celestials.data.tokens.TokenType
import java.util.Date

enum class TransactionState {
    INCOMING, OUTGOING
}

enum class TransactionType(val value: Int) {
    TRANSFER(0)
}

data class Transaction(
    val id: String,
    val tokenType: TokenType,
    val operationType: TransactionType,
    val from: String,
    val to: String,
    val amount: Double,
    val date: Date,
    val state: TransactionState
) {
    fun getIconId(): Int {
        return when (operationType) {
            TransactionType.TRANSFER -> {
                when (state) {
                    TransactionState.INCOMING -> R.drawable.ic_arrow_down
                    TransactionState.OUTGOING -> R.drawable.ic_arrow_up
                }
            }
        }
    }

    fun getStringId(): Int {
        return when (operationType) {
            TransactionType.TRANSFER -> {
                when (state) {
                    TransactionState.INCOMING -> R.string.wallet_incoming_title
                    TransactionState.OUTGOING -> R.string.wallet_outgoing_title
                }
            }
        }
    }

}