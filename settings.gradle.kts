pluginManagement {
	repositories {
		mavenLocal()
		mavenCentral()
		gradlePluginPortal()
		maven("https://maven.fabricmc.net/") { name = "Fabric" }
		maven("https://maven.neoforged.net/releases/") { name = "NeoForged" }
		maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
		maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
		maven("https://maven.parchmentmc.org") { name = "ParchmentMC" }
		maven("https://maven.terraformersmc.com/") { name = "TerraformersMC" }
		exclusiveContent {
			forRepository { maven("https://api.modrinth.com/maven") { name = "Modrinth" } }
			filter { includeGroup("maven.modrinth") }
		}
	}
	includeBuild("build-logic")
}

plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
	id("dev.kikugie.stonecutter") version "0.9.2"
	id("dev.kikugie.loom-back-compat") version "0.4.1"
}

stonecutter {
	create(rootProject) {
		fun match(version: String, vararg loaders: String) =
			loaders.forEach { version("$version-$it", version).buildscript = "build.$it.gradle.kts" }

		// NeoForge 26.2 needs its own target: the interaction event it hooks was replaced
		// mid-26.2-beta, so it cannot share a source tree with the older NeoForge builds.
		// The 1.21.5 NeoForge build covers everything from 1.21.5 through 26.1.2 unchanged,
		// so 26.1.2 only needs a Fabric target.
		match("26.2", "neoforge")
		match("26.1.2", "fabric")
		match("1.21.5", "fabric", "neoforge")
		match("1.21.3", "fabric", "neoforge")
		match("1.21.1", "neoforge")
		match("1.20.1", "fabric", "forge")
		//match("1.19.2", "fabric", "forge") requires fixing: method setShowArms is private in 1.19.2 or below

		vcsVersion = "1.20.1-fabric"
	}
}
