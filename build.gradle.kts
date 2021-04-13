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
	jcenter()
}

sourceSets {
	main {
        java.outputDir = File(java.outputDir.toString().replace("\\${File.separatorChar}java", ""))
		
		dependencies {
			implementation(group="org.jetbrains", name="annotations", version="20.1.0")
			api(group="me.joshlarson", name="jlcommon", version="1.9.2")
			api(group="org.bouncycastle", name="bcprov-jdk15on", version="1.60")
			api(group="org.mongodb", name="mongodb-driver-sync", version="3.10.2")
		}
	}
	test {
		dependencies {
			implementation(kotlin("stdlib"))
			implementation(group="junit", name="junit", version="4.12")
		}
	}
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
	kotlinOptions {
		jvmTarget = kotlinTargetJdk
	}
	destinationDir = sourceSets.main.get().java.outputDir
}
