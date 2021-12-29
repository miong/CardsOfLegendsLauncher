rm -rf build/*
./gradlew build
./gradlew shadowJar
./gradlew launch4j
./gradlew msi
