# Running Main Functions

## Using Gradle

### Default Main Class
To run the default main class (Main.kt):
```bash
./gradlew run
```

### Running Specific Main Functions
1. Lift Example:
```bash
./gradlew runLiftExample
```

2. Lift and Lift2 Example:
```bash
./gradlew runLiftAndLift2Example
```

## Alternative Methods

### Using Kotlin Compiler
If you prefer using the Kotlin compiler directly:
```bash
kotlinc -include-runtime -d app.jar src/main/kotlin/Main.kt
java -jar app.jar
```

### Running Specific Main Classes
```bash
kotlinc -include-runtime -d app.jar src/main/kotlin/monads/either/LiftExample.kt
java -cp app.jar monads.either.LiftExampleKt
```

## IntelliJ IDEA
1. Open the project in IntelliJ IDEA
2. Right-click on the main function
3. Select "Run" or use the green play button

