plugins {
    application
    checkstyle
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.jspecify:jspecify:1.0.0")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

application {
    mainClass = "za.co.sww.game.wumpus.App"
}

checkstyle {
    toolVersion = "10.26.1"
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
