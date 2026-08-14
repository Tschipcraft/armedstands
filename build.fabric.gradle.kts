plugins {
	id("mod-platform")
	id("dev.kikugie.loom-back-compat")
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
	loader = "fabric"
	// Armed Stands is server-side logic only, so there is no client or datagen entrypoint.
	entrypoint("main", "${prop("mod.group")}.${prop("mod.id")}.platform.fabric.FabricEntrypoint")
	dependencies {
		required("minecraft") {
			fabricLikeVersionRange = minecraftFabricRange()
		}
		required("fabric-api") {
			slug("fabric-api")
			fabricLikeVersionRange = prop("deps.fabric_api_range")
		}
		required("fabricloader") {
			fabricLikeVersionRange = prop("deps.fabric_loader_range")
		}
		optional("modmenu") {
			slug("modmenu")
		}
		incompatible("poses") {
			slug("armor-stand-poses")
			// Store tag only. Armor Stand Poses claims the same shift-right-click on armor
			// stands, which is worth warning about, but not worth a `breaks` entry that would
			// refuse to launch outright.
			declareInManifest = false
		}
	}
}

loom {
	accessWidenerPath = rootProject.file("src/main/resources/aw/${sc.current.version}.accesswidener")
	runs.named("client") {
		client()
		ideConfigGenerated(true)
		runDir = "run/"
		environment = "client"
		programArgs("--username=Dev")
		configName = "Fabric Client"
	}
	runs.named("server") {
		server()
		ideConfigGenerated(true)
		runDir = "run/"
		environment = "server"
		configName = "Fabric Server"
	}
}

repositories {
	mavenCentral()
	strictMaven("https://maven.terraformersmc.com/", "com.terraformersmc") { name = "TerraformersMC" }
	strictMaven("https://api.modrinth.com/maven", "maven.modrinth") { name = "Modrinth" }
}

configurations.all {
	resolutionStrategy {
		force("net.fabricmc:fabric-loader:${prop("deps.fabric-loader")}")
	}
}

dependencies {
	minecraft("com.mojang:minecraft:${prop("deps.minecraft")}")
	// 26.1+ ships unobfuscated, so no mappings are layered in there.
	if (sc.current.parsed < "26") {
		mappings(loom.layered {
			officialMojangMappings()
			if (hasProperty("deps.parchment"))
				parchment("org.parchmentmc.data:parchment-${prop("deps.parchment")}@zip")
		})
	}
	modImplementation("net.fabricmc:fabric-loader:${prop("deps.fabric-loader")}")
	//implementation(libs.moulberry.mixinconstraints)
	//include(libs.moulberry.mixinconstraints)
	modImplementation("net.fabricmc.fabric-api:fabric-api:${prop("deps.fabric-api")}")
	//modLocalRuntime("com.terraformersmc:modmenu:${prop("deps.modmenu")}")
}
