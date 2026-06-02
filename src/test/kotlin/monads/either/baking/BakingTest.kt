package monads.either.baking

import monads.either.Either
import monads.either.flatMap
import org.junit.jupiter.api.Test
import kotlin.test.fail

/**
 * Tests demonstrate how Monads can be used to do functional composition and type transformation (of wrapped types).
 * The tests also show how Monads used as return types facilitate both interleaved result/error checking and/or short-circuiting of errors so that the happy path is not polluted with interleaved error checks.
 */
class BakingTest {

    @Test
    fun `demo interleaved result checking`() {
        val ingredients = listOf("sugar", "water", "flower")
        val pie = "baked cherry pie"

        val bakePrepped = validateIngredients(ingredients)
        when (bakePrepped) {
            is Either.Left<BakingServiceError> -> fail("unexpected left error")
            is Either.Right<OkVal> -> assert(bakePrepped.value.message == "Ingredients ok")
        }

        val cookResult = bakePrepped.flatMap { cook(ingredients, temperature = 180) }
        when (cookResult) {
            is Either.Left<BakingServiceError> -> fail("unexpected left error")
            is Either.Right<OkVal> -> assert(cookResult.value.message == "Cooked $ingredients ok")
        }

        val packResult = cookResult.flatMap { pack(pie, isFragile = false) }
        when (packResult) {
            is Either.Left<BakingServiceError> -> fail("unexpected left error")
            is Either.Right<OkVal> -> assert(packResult.value.message == "Packed $pie ok")
        }

        // Note that the wrapped success value in deliverResult is of a different type to above - a Boolean
        val deliverResult: Either<BakingServiceError, Boolean> = packResult.map { deliver(pie) }
        when (deliverResult) {
            is Either.Left<BakingServiceError> -> fail("unexpected left error")
            is Either.Right<Boolean> -> assert(deliverResult.value)
        }
    }

    @Test
    fun `demo happy path and check last result`() {
        val pie = "baked cherry pie"
        val ingredients = listOf("sugar", "water", "flower", "cherries")

        val bakePrepped = validateIngredients(ingredients)
        val cookResult = bakePrepped.flatMap { cook(ingredients, temperature = 180) }
        val packResult = cookResult.flatMap { pack(pie, isFragile = false) }
        val deliverResult = packResult.map { deliver(pie) }

        when (deliverResult) {
            is Either.Left<BakingServiceError> -> fail("unexpected left error")
            is Either.Right<Boolean> -> assert(deliverResult.value)
        }
    }

    @Test
    fun `demo happy path and check last result shortened`() {
        val pie = "baked cherry pie"
        val ingredients = listOf("sugar", "water", "flower", "cherries")

        val deliverResult = validateIngredients(ingredients)
            .flatMap { cook(ingredients, temperature = 180) }
            .flatMap { pack(pie, isFragile = false) }
            .map { deliver(pie) }

        when (deliverResult) {
            is Either.Left<BakingServiceError> -> fail("unexpected left error")
            is Either.Right<Boolean> -> assert(deliverResult.value)
        }
    }

    @Test
    fun `demo happy path append rating step`() {
        val pie = "baked cherry pie"
        val ingredients = listOf("sugar", "water", "flower", "cherries")

        val result: Either<BakingServiceError, OkVal> = validateIngredients(ingredients)
            // note type of PARAMETERSIED type 'it' below is OkVal and we are using 
            // trailing lambda syntax to print the message before proceeding (the parenthesis 
            // aren't needed but are shown for demonstration - we also show 
            // the implicit 'it', although its redundant in this example) 
            .flatMap() { it -> println(it.message); cook(ingredients, temperature = 180) }
            .flatMap { pack(pie, isFragile = false) }
            .map { deliver(pie) }
            // note type of PARAMETERISED type 'it' below is Boolean
            .flatMap { it -> rate(score = 5) }

        when (result) {
            is Either.Left<BakingServiceError> -> fail("unexpected left error")
            is Either.Right<OkVal> -> assert(result.value.message == "Rated Ok")
        }
    }


    @Test
    fun `should short circuit on first BakingError_BadIngredients`() {
        val pie = "baked cherry pie"
        val ingredients = listOf("sugar", "water", "flower", "poison")

        val deliverResult = validateIngredients(ingredients)
            .flatMap { cook(ingredients, temperature = 180) }
            .flatMap { pack(pie, isFragile = false) }
            .map { deliver(pie) }

        when (deliverResult) {
            is Either.Left<BakingServiceError> -> assert(deliverResult.value == BakingServiceError.BadIngredients)
            is Either.Right<Boolean> -> fail("unexpected left error")
        }
    }

    @Test
    fun `should short circuit on BakingError_TemperatureTooLow`() {
        val pie = "baked cherry pie"
        val ingredients = listOf("sugar", "water", "flower", "cherries")

        val deliverResult = validateIngredients(ingredients)
            .flatMap { cook(ingredients, temperature = 130) }
            .flatMap { pack(pie, isFragile = false) }
            .map { deliver(pie) }

        when (deliverResult) {
            is Either.Left<BakingServiceError> -> assert(deliverResult.value == BakingServiceError.TemperatureTooLow)
            is Either.Right<Boolean> -> fail("unexpected left error")
        }
    }

    @Test
    fun `demo to show chained value transformations using map - Right bias does not compose boolean errors`() {
        val pie = "baked cherry pie"
        val ingredients = listOf("sugar", "water", "flower", "cherries")

        // Notice that in the transformation call chain below, cookPlain() returns a plain boolea (false) 
        // to indicatean error in that the temperature is not high enough and not a monad 
        // (horrid code, but this is just for illustration). This means we do not short-circuit - BEWARE.
        //
        // In the end, the 'deliverResult' value is a Right<String> 
        // with value "Blue Blah", so the chain does not short-circuit!
        //
        // We say that map() call chains are for transformations only; they have a 'Right bias' 
        // (i.e. because an Either.Left is never returned by cookPlain and pack Plain which return 
        // raw values and not monads, the raw value is always assumed to be a success value, 
        // and short-circuiting does not take place!)
        //
        val deliverResult = validateIngredients(ingredients) // returns Either<BakingServiceError, OkVal>
            .map { cookPlain(ingredients, temperature = 130) } // map returns Right<false> !BEWARE_ISSUE_HERE!
            .map { packPlain(pie, isFragile = false) } // map returns Right<String>
            .map { deliver(pie) } // map returns Right<true>
            .map { it -> "Blue Blah" } // map Returns Right<String>

        when (deliverResult) {
            is Either.Left<*> -> fail("unexpected Left")
            is Either.Right<String> -> assert("Blue Blah" == deliverResult.value)
        }
        //fail("forced fail")
    }

    @Test
    fun `demo to show 'lifting' of a plain boolean error return type into an Either Left`() {
        val pie = "baked cherry pie"
        val ingredients = listOf("sugar", "water", "flower", "cherries")

        val endResult = validateIngredients(ingredients) // returns Either<BakingServiceError, OkVal>
            // cooKPlain returns false on error, so we need to 'lift' 
            // this plain error into an Either.Left using flatMap:
            .flatMap { u ->
                if (cookPlain(ingredients, temperature = 130)) Either.Right(true) else Either.Left("temp too low")
            }
            .map { packPlain(pie, isFragile = false) } // We don't get here, above lift causes short-circuiting
            .map { deliver(pie) }
            .map { it -> "Blue Blah" }

        when (endResult) {
            is Either.Left<*> -> assert("temp too low" == endResult.value)
            is Either.Right<String> -> fail("unexpected right")
        }
    }

    @Test
    fun `should short circuit on BakingError_PackingFailed`() {
        val pie = "baked cherry pie"
        val ingredients = listOf("sugar", "water", "flower", "cherries")

        val deliverResult = validateIngredients(ingredients)
            .flatMap { cook(ingredients, temperature = 180) }
            .flatMap { pack(pie, isFragile = true) }
            .map { deliver(pie) }

        when (deliverResult) {
            is Either.Left<BakingServiceError> -> assert(deliverResult.value == BakingServiceError.PackingFailed)
            is Either.Right<Boolean> -> fail("unexpected left error")
        }
    }


    @Test
    fun `should short circuit on PoorRating then extract the minimum required score from the returned error`() {
        val pie = "baked cherry pie"
        val ingredients = listOf("sugar", "water", "flower", "cherries")

        val result: Either<BakingServiceError, OkVal> = validateIngredients(ingredients)
            .flatMap { cook(ingredients, temperature = 180) }
            .flatMap { pack(pie, isFragile = false) }
            .map { deliver(pie) }
            .flatMap { rate(score = 1) }

        // Show how to exhaustively extract the returned
        // BakingServiceError.PoorRating data object
        when (result) {
            is Either.Right<OkVal> -> fail("unexpected Right")
            is Either.Left<BakingServiceError> -> {
                val expectedMinScore = 2
                assert(result.value == BakingServiceError.PoorRating(expectedMinScore))
                when (result.value) {
                    BakingServiceError.BadIngredients -> fail("unexpected BakingServiceError.BadIngredients")
                    BakingServiceError.PackingFailed -> fail("unexpected BakingServiceError.PackingFailed")
                    is BakingServiceError.PoorRating -> assert(result.value.minRequiredScore == expectedMinScore)
                    BakingServiceError.TemperatureTooLow -> fail("unexpected BakingServiceError.TemperatureTooLow")
                }
            }
        }
    }

}
