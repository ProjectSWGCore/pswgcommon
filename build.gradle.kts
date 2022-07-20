plugins {
	idea
	`java-library`
	kotlin("jvm")
}

val javaVersion: String by ext
val javaMajorVersion: String by ext
val kotlinTargetJdk: String by ext

java {
	modularity.inferModulePath.set(true)
}

idea {
	targetVersion = javaVersion
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
			api(group="org.jetbrains", name="annotations", version="20.1.0")
			api(group="me.joshlarson", name="jlcommon", version="1.10.1")
			api(group="org.bouncycastle", name="bcprov-jdk18on", version="1.71")
			implementation(kotlin("stdlib"))
			implementation(group="org.mongodb", name="mongodb-driver-sync", version="3.12.2")
		}
	}
	test {
		dependencies {
			testImplementation(kotlin("stdlib"))
			testImplementation(group="junit", name="junit", version="4.12")
		}
	}
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
	kotlinOptions {
		jvmTarget = kotlinTargetJdk
	}
	destinationDirectory.set(File(destinationDirectory.get().asFile.path.replace("kotlin", "java")))
}
