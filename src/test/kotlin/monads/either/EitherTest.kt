package monads.either

import monads.either.Either.Left
import monads.either.Either.Right
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class EitherTest {
    @Nested
    inner class CreateTests {
       @Test
       fun `simple creation`() {
           assert( Right(1).value == 1 )
           assert( Left("some error").value == "some error")
       }
    }

    @Nested
    inner class MapTests {

        @Test
        fun `should apply a fn when either is right`() {
            assert(Right(1).map { it + 1 } == Right(2))
        }

        @Test
        fun `should not apply a fn when either is left`() {
            val either: Either<String, Int> = Left("Some Error")
            assert(either.map { it + 1 } == Left("Some Error"))
        }
    }

    @Nested
    inner class FlatMapTests {

        @Test
        fun `should apply a fn when either is right`() {
            val justInc = { n:Int -> Right(n + 1) }
            assert(Right(1).flatMap(justInc) == Right(2))
        }

        @Test
        fun `should not apply a fn when either is left`() {
            val justInc = { n:Int -> Right(n + 1) }
            val either: Either<String, Int> = Left("Some Error")
            assert(either.flatMap(justInc) == Left("Some Error"))
        }
    }
}
