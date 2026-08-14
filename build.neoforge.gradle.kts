plugins {
	id("mod-platform")
	id("net.neoforged.moddev")
}

stonecutter {
	val (version, loader) = current.project.split('-', limit = 2)
	properties.tags(version, loader)

	replacements.string(current.parsed >= "1.21.11") {
		replace("ResourceLocation", "Identifier")
		replace("location()", "identifier()")
	}
}

platform {
	loader = "neoforge"
	dependencies {
		required("minecraft") {
			forgeLikeVersionRange = minecraftForgeRange()
		}
		required("neoforge") {
			// `[1,)` by default (divergence 14). A target whose source needs an API that only
			// exists from a given NeoForge build onward sets `deps.neoforge_range` instead —
			// two builds of the same Minecraft version can differ, which the Minecraft range
			// has no way to express.
			forgeLikeVersionRange = propOr("deps.neoforge_range", "[1,)")
		}
		incompatible("poses") {
			slug("armor-stand-poses")
			// Store tag only - see build.fabric.gradle.kts.
			declareInManifest = false
		}
	}
}

neoForge {
	version = prop("deps.neoforge")

	rootProject.file("src/main/resources/aw/${sc.current.version}.cfg")
		.takeIf { it.exists() }
		?.let {
			accessTransformers.from(it)
			validateAccessTransformers = true
		}

	if (hasProperty("deps.parchment")) parchment {
		val (mc, ver) = prop("deps.parchment").split(':')
		mappingsVersion = ver
		minecraftVersion = mc
	}

	runs {
		register("client") {
			client()
			gameDirectory = file("run/")
			ideName = "NeoForge Client (${sc.current.version})"
			programArgument("--username=Dev")
		}
		register("server") {
			server()
			gameDirectory = file("run/")
			ideName = "NeoForge Server (${sc.current.version})"
		}
	}

	mods {
		register(prop("mod.id")) {
			sourceSet(sourceSets["main"])
		}
	}
	sourceSets["main"].resources.srcDir("${rootDir}/versions/datagen/${sc.current.version.split("-")[0]}/src/main/generated")
}

repositories {
	mavenCentral()
	strictMaven("https://api.modrinth.com/maven", "maven.modrinth") { name = "Modrinth" }
}

dependencies {
	//implementation(libs.moulberry.mixinconstraints)
	//jarJar(libs.moulberry.mixinconstraints)
}

tasks.named("createMinecraftArtifacts") {
	dependsOn(tasks.named("stonecutterGenerate"))
}
