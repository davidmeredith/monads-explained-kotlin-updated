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
