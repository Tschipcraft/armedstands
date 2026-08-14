plugins {
	id("mod-platform")
	id("net.neoforged.moddev.legacyforge")
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
	loader = "forge"
	dependencies {
		required("minecraft") {
			forgeLikeVersionRange = minecraftForgeRange()
		}
		required("forge") {
			forgeLikeVersionRange.set("[1,)")
		}
		incompatible("poses") {
			slug("armor-stand-poses")
			// Store tag only - see build.fabric.gradle.kts. The published Forge files are the
			// only ones missing this tag; Armor Stand Poses does ship a Forge build.
			declareInManifest = false
		}
	}
}

legacyForge {
	version = "${prop("deps.minecraft")}-${prop("deps.forge")}"

	rootProject.file("src/main/resources/aw/${sc.current.version}.cfg")
		.takeIf { it.exists() }
		?.let {
			accessTransformers.from(it)
			validateAccessTransformers = true
		}

	runs {
		register("client") {
			client()
			gameDirectory = file("run/")
			ideName = "Forge Client (${sc.current.version})"
			programArgument("--username=Dev")
			jvmArguments.addAll("-Dmixin.debug.verbose=true", "-Dmixin.debug.export=true")
		}
		register("server") {
			server()
			gameDirectory = file("run/")
			ideName = "Forge Server (${sc.current.version})"
		}
	}

	mods {
		register(prop("mod.id")) {
			sourceSet(sourceSets["main"])
		}
	}
}

mixin {
	add(sourceSets.main.get(), "${prop("mod.id")}.mixins.refmap.json")
	config("${prop("mod.id")}.mixins.json")
}

repositories {
	mavenCentral()
	strictMaven("https://api.modrinth.com/maven", "maven.modrinth") { name = "Modrinth" }
}

dependencies {
	// Not using mixin constraints currently
	//implementation(libs.moulberry.mixinconstraints)
	//jarJar(libs.moulberry.mixinconstraints)

	// Add the Mixin Annotation Processor explicitly
	annotationProcessor("org.spongepowered:mixin:${libs.versions.mixin.get()}:processor")
}

sourceSets {
	main {
		resources.srcDir(
			"${rootDir}/versions/datagen/${sc.current.version.split("-")[0]}/src/main/generated"
		)
	}
}

tasks.named("createMinecraftArtifacts") {
	dependsOn(tasks.named("stonecutterGenerate"))
}
