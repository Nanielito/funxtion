import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("java")
    id("java-library")
    id("maven-publish")
    id("signing")
}

group = "com.nan"
version = project.property("version") as String
val buildJavaVersion = providers.gradleProperty("buildJavaVersion")
    .map(String::toInt)
    .orElse(25)

fun environmentVariableOrNull(name: String): String? =
    providers.environmentVariable(name).orNull?.takeIf(String::isNotBlank)

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(buildJavaVersion.get()))
    }
    withSourcesJar()
    withJavadocJar()
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(21)
}

repositories {
    mavenCentral()
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/nanielito/maven-packages")
        credentials {
            username = environmentVariableOrNull("GITHUB_PACKAGES_USER")
                ?: environmentVariableOrNull("GITHUB_ACTOR")
            password = environmentVariableOrNull("GITHUB_PACKAGES_TOKEN")
                ?: environmentVariableOrNull("GITHUB_TOKEN")
        }
        content {
            includeGroup("com.nan")
        }
    }
}

dependencies {
    implementation("com.nan:tuplex:1.0.0")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()

    testLogging {
        events = setOf(
            TestLogEvent.PASSED,
            TestLogEvent.SKIPPED,
            TestLogEvent.FAILED
        )
        exceptionFormat = TestExceptionFormat.FULL
        showCauses = true
        showExceptions = true
        showStackTraces = true
    }

    reports {
        html.required.set(true)
        junitXml.required.set(true)
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            pom {
                name.set("Funxtion")
                description.set("A small, dependency-free functional library for Java")
                url.set("https://github.com/nanielito/funxtion")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("nanielito")
                        name.set("Daniel Ramirez")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/nanielito/funxtion.git")
                    developerConnection.set("scm:git:ssh://github.com/nanielito/funxtion.git")
                    url.set("https://github.com/nanielito/funxtion")
                }
            }
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/nanielito/funxtion")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
