package monads.either

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import monads.either.AccountError.AccountNotFound
import monads.either.AccountError.NegativeAmount
import monads.either.AccountError.NotEnoughFunds
import monads.either.Either.Left
import monads.either.Either.Right
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.UUID


class ServicesTest {

    @Nested
    inner class TransferMoneyTest {

        private val transferMoney = TransferMoney()

        @Test
        fun `should transfer money within two different accounts`() {
            val debtor = Account.createOrThrow(100.toBigDecimal())
            val creditor = Account.createOrThrow(100.toBigDecimal())
            val amount = 50.toBigDecimal()

            val result = transferMoney(debtor, creditor, amount)

            assert(result == Right(Pair(buildAccountViaReflectionForTestsOnly(50.toBigDecimal()), buildAccountViaReflectionForTestsOnly(150.toBigDecimal())) ))
        }

        @Test
        fun `should fail transferring money within two different accounts when debtor has not enough funds`() {
            val debtor = buildAccountViaReflectionForTestsOnly(100.toBigDecimal())
            val creditor = buildAccountViaReflectionForTestsOnly(100.toBigDecimal())
            val amount = 500.toBigDecimal()

            val result = transferMoney(debtor, creditor, amount)

            assert(result == Left(NotEnoughFunds))
        }
    }


    @Nested
    inner class DepositCashTest {

        private val accountRepository = mockk<AccountRepository>(relaxed = true)

        private val depositCash = DepositCash(accountRepository)

        @Test
        fun `should deposit cash`() {
            val account = buildAccountViaReflectionForTestsOnly(100.toBigDecimal())
            val amount = 50.toBigDecimal()
            val userId = UUID.randomUUID()
            every { accountRepository.findBy(userId) } returns Right(account)

            val result = depositCash(userId, amount)
            assert(result == Right(Unit)) // just side effects

            // Verify that the logic above (ie depositCash(...)) calls the specified functions on our mockk
            // with the specified args
            verify(exactly = 1) { accountRepository.save(account.copyAccount(balance = 150.toBigDecimal())) }
        }

        @Test
        fun `should fail depositing cash when account is not found`() {
            val amount = 50.toBigDecimal()
            val userId = UUID.randomUUID()
            every { accountRepository.findBy(userId) } returns Left(AccountNotFound)

            val result = depositCash(userId, amount)

            assert(result == Left(AccountNotFound))
        }

        @Test
        fun `should fail depositing cash when amount is invalid`() {
            val account = buildAccountViaReflectionForTestsOnly(100.toBigDecimal())
            val negativeAmount = (-50).toBigDecimal()
            val userId = UUID.randomUUID()
            every { accountRepository.findBy(userId) } returns Right(account)

            val result = depositCash(userId, negativeAmount)

            assert(result == Left(NegativeAmount))
        }
    }


}