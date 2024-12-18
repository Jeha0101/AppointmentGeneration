pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        // 네이버 지도 리포지토리 주석 처리
        //maven {
        //    url = uri("https://naver.jfrog.io/artifactory/maven/")
       // }
    }
}

rootProject.name = "AppointmentGeneration"
include(":app")