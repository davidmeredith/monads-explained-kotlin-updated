package monads.either.baking

import monads.either.Either

sealed class BakingServiceError {
    object BadIngredients : BakingServiceError()
    object TemperatureTooLow : BakingServiceError()
    object PackingFailed : BakingServiceError()

    // If we encounter a PoorRating, we can optionally provide the minimum required score
    data class PoorRating(val minRequiredScore: Int = 3) : BakingServiceError() {
        companion object
    }
}

data class OkVal(val message: String)

fun validateIngredients(ingredients: List<String>): Either<BakingServiceError, OkVal> {
    if (ingredients.contains("poison")) {
        return Either.Left(BakingServiceError.BadIngredients)
    }
    return Either.Right(OkVal("Ingredients ok"))
}

fun validateIngredientsPlain(ingredients: List<String>): Boolean {
    if (ingredients.contains("poison")) {
        return false
    }
    return true
}

fun cook(ingredients: List<String>, temperature: Int): Either<BakingServiceError, OkVal> {
    if (temperature < 180) {
        return Either.Left(BakingServiceError.TemperatureTooLow)
    }
    return Either.Right(OkVal("Cooked $ingredients ok"))
}

fun cookPlain(ingredients: List<String>, temperature: Int): Boolean {
    if (temperature < 180) {
        return false
    }
    return true
}

fun pack(pie: String, isFragile: Boolean): Either<BakingServiceError, OkVal> {
    if (isFragile) {
        // we can't pack fragile cakes yet
        return Either.Left(BakingServiceError.PackingFailed)
    }
    return Either.Right(OkVal("Packed $pie ok"))
}

fun packPlain(pie: String, isFragile: Boolean): String {
    if (isFragile) {
        return "error, can't pack fragile cakes"
    }
    return "packed OK"
}

fun deliver(pie: String): Boolean {
    println("Packing $pie")
    return true
}

fun rate(score: Int): Either<BakingServiceError, OkVal> {
    val minRatingScore = 2
    if (score < minRatingScore) {
        return Either.Left(BakingServiceError.PoorRating(minRatingScore))
    }
    return Either.Right(OkVal("Rated Ok"))
}
