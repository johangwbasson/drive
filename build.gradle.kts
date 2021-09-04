import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.0"
}
group = "net.johanbasson"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

val jbcrypt_version = "0.4"
val jsonwebtoken_version = "0.10.7"
val flyway_version = "6.2.4"
val hikaricp_version = "3.4.2"
val arrow_version = "0.11.0"

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.8")

    implementation("io.arrow-kt:arrow-core:$arrow_version")
    implementation("io.arrow-kt:arrow-fx:$arrow_version")
//    implementation("io.arrow-kt:arrow-mtl:$arrow_version")



    implementation("org.http4k", "http4k-core", "3.260.0")
    implementation("org.http4k", "http4k-server-apache", "3.260.0")
    implementation ("org.http4k:http4k-format-jackson:3.260.0")
    implementation ("org.http4k:http4k-multipart:3.260.0")

    implementation("com.fasterxml.jackson.datatype:jackson-datatype-joda:2.10.3")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.11.2")

    implementation("mysql", "mysql-connector-java", "8.0.21")
    implementation("com.zaxxer:HikariCP:$hikaricp_version")
    implementation("org.sql2o", "sql2o", "1.6.0")


    implementation ("org.mindrot:jbcrypt:$jbcrypt_version")

    implementation ("io.jsonwebtoken:jjwt-impl:$jsonwebtoken_version")
    implementation ("io.jsonwebtoken:jjwt-jackson:$jsonwebtoken_version")
    implementation ("io.jsonwebtoken:jjwt-api:$jsonwebtoken_version")

//    implementation ("io.github.microutils:kotlin-logging:1.7.7")
    implementation ("ch.qos.logback:logback-classic:1.2.3")
    implementation("com.michael-bull.kotlin-inline-logger:kotlin-inline-logger:1.0.2")

    implementation("org.flywaydb:flyway-core:$flyway_version")

    implementation("com.viartemev:the-white-rabbit:0.0.6")

    implementation ("commons-io:commons-io:2.6")
    implementation ("org.apache.lucene:lucene-core:8.4.1")
    implementation ("org.apache.lucene:lucene-highlighter:8.4.1")
    implementation ("org.apache.lucene:lucene-queries:8.4.1")
    implementation ("org.apache.lucene:lucene-queryparser:8.4.1")
    implementation ("org.apache.tika:tika-core:1.23")
    implementation ("org.apache.tika:tika-parsers:1.23")
    implementation ("org.apache.lucene:lucene-analyzers-common:7.6.0")
    implementation ("org.apache.lucene:lucene-queryparser:7.6.0")
    implementation ("org.apache.lucene:lucene-highlighter:7.6.0")
    implementation ("org.apache.pdfbox:jbig2-imageio:3.0.1")
    implementation ("com.github.jai-imageio:jai-imageio-jpeg2000:1.3.0")
    implementation ("org.xerial:sqlite-jdbc:3.27.2.1")


    implementation ("org.http4k", "http4k-testing-kotest", "3.260.0")

}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}