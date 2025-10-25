import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	idea
	`java-library`
	kotlin("jvm") version "2.2.21"
}

group = "com.projectswg"
version = "1.0.0"
description = "ProjectSWG's SWG Emulator Common Library"

val javaVersion = JavaVersion.current()
val kotlinTargetJdk: JvmTarget = JvmTarget.fromTarget(javaVersion.majorVersion)

java {
	modularity.inferModulePath.set(true)
	
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(javaVersion.majorVersion))
	}
}

kotlin {
	compilerOptions {
		jvmTarget.set(kotlinTargetJdk)
	}
}

idea {
	targetVersion = javaVersion.majorVersion
	module {
		inheritOutputDirs = true
	}
}

repositories {
	maven("https://dev.joshlarson.me/maven2")
	mavenCentral()
}

sourceSets {
	main {
		dependencies {
			api("org.jetbrains:annotations:20.1.0")
			api("me.joshlarson:jlcommon:1.10.1")
			api("org.bouncycastle:bcprov-jdk18on:1.71")
			implementation(kotlin("stdlib"))
			implementation("org.mongodb:mongodb-driver-sync:3.12.2")
		}
		kotlin.destinationDirectory = java.destinationDirectory
	}
	test {
		dependencies {
			testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
			testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.1")
			testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
		}
		kotlin.destinationDirectory = java.destinationDirectory
	}
}

tasks.withType<Test>().configureEach {
	useJUnitPlatform()
}
