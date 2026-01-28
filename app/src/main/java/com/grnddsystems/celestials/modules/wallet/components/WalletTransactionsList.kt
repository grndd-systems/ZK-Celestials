package com.grnddsystems.celestials.modules.wallet.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.grnddsystems.celestials.R
import com.grnddsystems.celestials.data.tokens.Erc20Token
import com.grnddsystems.celestials.data.tokens.TokenType
import com.grnddsystems.celestials.manager.WalletAsset
import com.grnddsystems.celestials.modules.wallet.models.Transaction
import com.grnddsystems.celestials.modules.wallet.models.TransactionState
import com.grnddsystems.celestials.modules.wallet.models.TransactionType
import com.grnddsystems.celestials.ui.theme.RarimeTheme
import com.grnddsystems.celestials.util.Constants
import java.util.Date

@Composable
fun WalletTransactionsList(
    modifier: Modifier = Modifier, walletAsset: WalletAsset, transactions: List<Transaction>
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.transactions_title),
            style = RarimeTheme.typography.subtitle5,
            color = RarimeTheme.colors.textPrimary
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (transactions.isEmpty()) {
            Text(
                text = stringResource(R.string.no_transactions_msg),
                style = RarimeTheme.typography.body4,
                color = RarimeTheme.colors.textSecondary
            )
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                transactions.forEach {
                    WalletTransactionCard(it, walletAsset)
                }
            }
        }
    }
}

@Preview
@Composable
fun WalletTransactionsListPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RarimeTheme.colors.backgroundPrimary)
            .padding(20.dp)
    ) {
        WalletTransactionsList(
            walletAsset = WalletAsset("0x000000", Erc20Token("0x00000000")), transactions = listOf(
                Transaction(
                    id = "1",
                    amount = Constants.AIRDROP_REWARD,
                    date = Date(),
                    state = TransactionState.INCOMING,
                    from = "",
                    to = "",
                    tokenType = TokenType.DEFAULT,
                    operationType = TransactionType.TRANSFER
                ),
                Transaction(
                    id = "2",
                    amount = Constants.AIRDROP_REWARD,
                    date = Date(),
                    state = TransactionState.INCOMING,
                    from = "",
                    to = "",
                    tokenType = TokenType.DEFAULT,
                    operationType = TransactionType.TRANSFER
                ),
                Transaction(
                    id = "3",
                    amount = Constants.AIRDROP_REWARD,
                    date = Date(),
                    state = TransactionState.INCOMING,
                    from = "",
                    to = "",
                    tokenType = TokenType.DEFAULT,
                    operationType = TransactionType.TRANSFER
                ),
                Transaction(
                    id = "4",
                    amount = Constants.AIRDROP_REWARD,
                    date = Date(),
                    state = TransactionState.INCOMING,
                    from = "",
                    to = "",
                    tokenType = TokenType.DEFAULT,
                    operationType = TransactionType.TRANSFER
                ),
            )
        )
    }
}