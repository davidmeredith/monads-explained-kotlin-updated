package monads.either

import monads.either.AccountError.NegativeAmount
import monads.either.AccountError.NotEnoughFunds
import monads.either.Either.Left
import monads.either.Either.Right
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.fail

class AccountTest {

    @Nested
    inner class CreateAnAccount {

        @Test
        fun `should create an account via Account smart constructor`() {
            val knownValidAccount = buildAccountViaReflectionForTestsOnly(100.toBigDecimal())
            val accountResult: Either<NegativeAmount, Account> = Account(100.toBigDecimal())
            assert(accountResult == Right(knownValidAccount))
            assert(accountResult.isRight())

            val account : Account
            when (accountResult) {
                is Left<NegativeAmount> -> fail("unexpected left error")
                is Right<Account> -> account = accountResult.value
            }
            assert(account.balance == 100.toBigDecimal())
        }


        @Test
        fun `should create an account via Account_createOrThrow method`() {
            val knownValidAccount = buildAccountViaReflectionForTestsOnly(100.toBigDecimal())
            val account: Account = Account.createOrThrow(100.toBigDecimal())
            assert(knownValidAccount == account)
            assert(account.balance == 100.toBigDecimal())
        }

        @Test
        fun `should fail creating an account with an Exception`() {
            try {
                Account.createOrThrow((-100).toBigDecimal())
                fail("Should not get here")
            } catch(_ : IllegalArgumentException) {
              // success
            }
        }

        @Test
        fun `should fail creating an account with a negative amount`() {
            assert(Account.create((-100).toBigDecimal()) == Left(NegativeAmount))
        }
    }


    @Nested
    inner class Deposit {
        @Test
        fun `should deposit money to an account`() {
            val account = Account.createOrThrow(100.toBigDecimal())
            val updatedAccount = account.deposit(100.toBigDecimal())
            assert(updatedAccount == Right(buildAccountViaReflectionForTestsOnly(200.toBigDecimal())))
        }

        @Test
        fun `should fail depositing a negative amount to an account`() {
            val account = Account.createOrThrow(100.toBigDecimal())
            val fail = account.deposit((-100).toBigDecimal())
            assert(fail == Left(NegativeAmount))
        }
    }

    @Nested
    inner class Withdraw {

        @Test
        fun `should withdraw money from an account`() {
            val account = Account.createOrThrow(100.toBigDecimal())
            val updatedAccount = account.withdraw(50.toBigDecimal())
            assert(updatedAccount == Right(buildAccountViaReflectionForTestsOnly(50.toBigDecimal())))
        }

        @Test
        fun `should fail withdrawing a negative amount to an account`() {
            val account = Account.createOrThrow(100.toBigDecimal())
            val fail = account.withdraw((-50).toBigDecimal())
            assert(fail == Left(NegativeAmount))
        }

        @Test
        fun `should fail withdrawing when there is not enough funds`() {
            val account = Account.createOrThrow(100.toBigDecimal())
            val fail = account.withdraw(200.toBigDecimal())
            assert(fail == Left(NotEnoughFunds))
        }
    }

    @Nested
    inner class TransactionTesting {
        @Test
        fun `should transfer money across two different accounts`() {
            val debtor = Account.createOrThrow(100.toBigDecimal())
            val creditor = Account.createOrThrow(100.toBigDecimal())
            val result = Account.transferMoney(debtor, creditor, 50.toBigDecimal())
            assert(result == Right(Pair(buildAccountViaReflectionForTestsOnly(50.toBigDecimal()), buildAccountViaReflectionForTestsOnly(150.toBigDecimal())) ))
        }

    }


}