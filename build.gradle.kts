plugins {
	idea
	java
	id("org.jetbrains.kotlin.jvm")
	id("org.javamodularity.moduleplugin")
}

idea {
	targetVersion = "12.0.1"
    module {
        inheritOutputDirs = true
    }
}

repositories {
	jcenter()
}

sourceSets {
	main {
		dependencies {
			api(group="me.joshlarson", name="jlcommon", version="1.9.1")
			api(group="org.bouncycastle", name="bcprov-jdk15on", version="1.60")
			implementation(kotlin("stdlib"))
			implementation(group="org.mongodb", name="mongodb-driver-sync", version="3.12.2")
		}
	}
	test {
		dependencies {
			implementation(group="junit", name="junit", version="4.12")
		}
	}
}
