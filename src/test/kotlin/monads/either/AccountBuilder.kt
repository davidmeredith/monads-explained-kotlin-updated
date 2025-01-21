package monads.either

import java.math.BigDecimal

// needed because constructor is private. Do not use in production code, it is just for assertions in tests
fun buildAccountViaReflectionForTestsOnly(initialAmount: BigDecimal): Account =
    Account::class.java.getDeclaredConstructor(BigDecimal::class.java)
        .also { it.isAccessible = true }
        .newInstance(initialAmount)