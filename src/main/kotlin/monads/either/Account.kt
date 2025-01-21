package monads.either

import monads.either.AccountError.AccountNotFound
import monads.either.AccountError.NegativeAmount
import monads.either.AccountError.NotEnoughFunds
import monads.either.Either.Left
import monads.either.Either.Right
import java.io.IOException
import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import java.util.UUID

sealed class AccountError {
    object NegativeAmount : AccountError()
    object NotEnoughFunds : AccountError()
    object AccountNotFound : AccountError()
    data class TransactionFailed(val exception: Exception?) : AccountError()
}

@ConsistentCopyVisibility
data class Account private constructor(val balance: BigDecimal) {
    companion object { // functions within the companion are statics
        operator fun invoke(initialBalance: BigDecimal): Either<NegativeAmount, Account> =
            applyAmount(initialBalance) { Account(it) } // smart constructor

        fun create(initialBalance: BigDecimal): Either<NegativeAmount, Account> =
            applyAmount(initialBalance) { Account(it) }

        private fun applyAmount(amount: BigDecimal, fn: (BigDecimal) -> Account) =
            if (amount < ZERO) Left(NegativeAmount) else Right(fn(amount))

        /**
         * Convenience function to create a new Account or throw IllegalArgumentException.
         * Not recommended for production use, use Constructor instead.
         */
        fun createOrThrow(initialBalance: BigDecimal): Account {
            var accountResult = applyAmount(initialBalance) { Account(it) }
            return when(accountResult) {
                is Right<Account> -> accountResult.value
                is Left<*> -> throw IllegalArgumentException()
            }
        }

        /**
         * @return Updated debtor and creditor Accounts or an AccountError if the transfer fails
         */
        fun transferMoney(debtor: Account, creditor: Account, amount: BigDecimal): Either<AccountError, Pair<Account, Account>> {
            return try {
                // In the real-world, this might be a flaky network operation than can fail
                debtor
                    .withdraw(amount)
                    .flatMap { d -> creditor.deposit(amount).map { Pair(d, it) } }
            } catch(ex: IOException){
                Left(AccountError.TransactionFailed(ex))
            }
        }
    }

    fun copyAccount(balance: BigDecimal): Account = this.copy(balance = balance)
    //fun copyAccount(): Account = this.copy(balance = this.balance)

    fun deposit(amount: BigDecimal): Either<NegativeAmount, Account> =
        applyAmount(amount) { this.copyAccount(balance = this.balance + it) }

    fun withdraw(amount: BigDecimal): Either<AccountError, Account> =
        applyAmount(amount) { this.copyAccount(balance = this.balance - it) }
            .flatMap {
                if ((balance - amount) < ZERO) Left(NotEnoughFunds) else Right(Account(balance - amount))
            }
}


interface AccountRepository {
    fun findBy(userId: UUID): Either<AccountNotFound, Account>
    fun save(account: Account)
}
