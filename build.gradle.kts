import org.jetbrains.kotlin.gradle.dsl.JvmTarget

/***********************************************************************************
 * Copyright (c) 2023 /// Project SWG /// www.projectswg.com                       *
 *                                                                                 *
 * ProjectSWG is the first NGE emulator for Star Wars Galaxies founded on          *
 * July 7th, 2011 after SOE announced the official shutdown of Star Wars Galaxies. *
 * Our goal is to create an emulator which will provide a server for players to    *
 * continue playing a game similar to the one they used to play. We are basing     *
 * it on the final publish of the game prior to end-game events.                   *
 *                                                                                 *
 * This file is part of Holocore.                                                  *
 *                                                                                 *
 * --------------------------------------------------------------------------------*
 *                                                                                 *
 * Holocore is free software: you can redistribute it and/or modify                *
 * it under the terms of the GNU Affero General Public License as                  *
 * published by the Free Software Foundation, either version 3 of the              *
 * License, or (at your option) any later version.                                 *
 *                                                                                 *
 * Holocore is distributed in the hope that it will be useful,                     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                  *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                   *
 * GNU Affero General Public License for more details.                             *
 *                                                                                 *
 * You should have received a copy of the GNU Affero General Public License        *
 * along with Holocore.  If not, see <http://www.gnu.org/licenses/>.               *
 ***********************************************************************************/

plugins {
	idea
	`java-library`
	kotlin("jvm")
}

val javaVersion = JavaVersion.current()
val kotlinTargetJdk = JvmTarget.fromTarget(javaVersion.majorVersion)
val junit5Version: String by ext

java {
	modularity.inferModulePath.set(true)
}

idea {
	module {
		inheritOutputDirs = true
	}
}

repositories {
	maven("https://dev.joshlarson.me/maven2")
	mavenCentral()
}

dependencies {
	api(group="org.jetbrains", name="annotations", version="26.0.1")
	api(group="me.joshlarson", name="jlcommon", version="1.10.1")
	api(group="org.bouncycastle", name="bcprov-jdk18on", version="1.79")
	implementation(kotlin("stdlib"))
	implementation(group="org.mongodb", name="mongodb-driver-sync", version="5.2.1")

	testImplementation(group="org.junit.jupiter", name="junit-jupiter-api", version=junit5Version)
	testImplementation(group="org.junit.jupiter", name="junit-jupiter-params", version=junit5Version)
	testRuntimeOnly(group="org.junit.jupiter", name="junit-jupiter-engine", version=junit5Version)
}

tasks.withType<Jar> {
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
	compilerOptions {
		jvmTarget.set(kotlinTargetJdk)
	}
	destinationDirectory.set(File(destinationDirectory.get().asFile.path.replace("kotlin", "java")))
}

tasks.withType<Test>().configureEach {
	useJUnitPlatform()
}
