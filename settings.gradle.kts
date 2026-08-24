plugins {
    id("com.gradleup.nmcp.settings") version "1.6.1"
}

rootProject.name = "funxtion"

nmcpSettings {
    centralPortal {
        username = providers.environmentVariable("MAVEN_CENTRAL_USERNAME").orNull ?: ""
        password = providers.environmentVariable("MAVEN_CENTRAL_PASSWORD").orNull ?: ""
        publicationName = "io.github.nanielito:funxtion"
    }
}
