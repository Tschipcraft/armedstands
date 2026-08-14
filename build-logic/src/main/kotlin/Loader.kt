@file:Suppress("unused")

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import dev.eav.tomlkt.Toml
import org.gradle.api.NamedDomainObjectContainer
import java.util.*

// kotlinx defaults to a four-space indent; two matches the hand-written fabric.mod.json
// convention, so a generated manifest still diffs cleanly against one checked in before.
private val JSON = Json {
	prettyPrint = true
	prettyPrintIndent = "  "
	encodeDefaults = true
	explicitNulls = false
}
private val TOML = Toml { explicitNulls = false }

/** Store-only dependencies are skipped - see [Dependency.declareInManifest]. */
private fun NamedDomainObjectContainer<Dependency>.manifestEntries(): List<Dependency> =
	filter { it.declareInManifest.get() }

sealed class Loader(val id: String) {
	abstract val modManifestPath: String
	abstract val excludedResources: List<String>

	open val isFabricLike: Boolean = false

	abstract fun generateManifest(ctx: Context): String

	object Fabric : Loader("fabric") {
		override val isFabricLike = true
		override val modManifestPath = "fabric.mod.json"
		override val excludedResources = listOf(
			"META-INF/mods.toml", "META-INF/neoforge.mods.toml", "aw/*.cfg", ".cache", "pack.mcmeta"
		)

		/** Mod Menu link entries plus the Catalogue banner, driven by the `[mod.links]` table. */
		private fun customBlock(ctx: Context): JsonObject? = buildJsonObject {
			if (ctx.links.isNotEmpty()) putJsonObject("modmenu") {
				putJsonObject("links") {
					ctx.links.forEach { (key, url) -> put(key, url) }
				}
			}
			putJsonObject("catalogue") { put("banner", ctx.icon) }
		}.takeIf { it.isNotEmpty() }

		override fun generateManifest(ctx: Context): String {
			val manifest = FabricManifest(
				id = ctx.modId,
				name = ctx.modName,
				version = ctx.baseVersion,
				authors = ctx.authors,
				contributors = ctx.contributors,
				contact = mapOf(
					"sources" to ctx.sourcesUrl, "issues" to ctx.issuesUrl, "homepage" to ctx.homepageUrl
				),
				custom = customBlock(ctx),
				description = ctx.description,
				icon = ctx.icon,
				license = ctx.licenseName,
				environment = ctx.fabricEnvironment,
				accessWidener = ctx.accessWidenerPath,
				entrypoints = ctx.entrypoints,
				mixins = listOf("${ctx.modId}.mixins.json"),
				depends = ctx.extension.dependencies.required.manifestEntries()
					.associate { it.modid.get() to it.fabricLikeVersionRange.get() },
				recommends = ctx.extension.dependencies.optional.manifestEntries()
					.associate { it.modid.get() to it.fabricLikeVersionRange.get() },
				breaks = ctx.extension.dependencies.incompatible.manifestEntries()
					.associate { it.modid.get() to it.fabricLikeVersionRange.get() },
				provides = ctx.extension.dependencies.embeds.manifestEntries().map { it.modid.get() }
			)
			return JSON.encodeToString(manifest)
		}
	}

	sealed class ForgeLike(id: String) : Loader(id) {
		override val excludedResources = listOf(
			"fabric.mod.json", "aw/*.accesswidener", ".cache"
		)

		/** Query parameter Modrinth's update-check endpoint expects for this loader. */
		protected open val updateCheckLoader: String get() = id

		/**
		 * NeoForge retired `displayTest` - it is no longer part of the default
		 * `neoforge.mods.toml` - so only legacy MinecraftForge still gets the key.
		 */
		protected open val supportsDisplayTest: Boolean get() = true

		override fun generateManifest(ctx: Context): String {
			val forgeDeps = mutableListOf<ForgeDependency>()

			fun addDeps(container: NamedDomainObjectContainer<Dependency>, type: String) {
				container.manifestEntries().forEach {
					forgeDeps.add(
						ForgeDependency(
							modId = it.modid.get(),
							side = it.environment.orNull?.uppercase(Locale.getDefault()) ?: ctx.forgeSide,
							versionRange = it.forgeLikeVersionRange.get(),
							mandatory = type == "required",
							type = type
						)
					)
				}
			}

			addDeps(ctx.extension.dependencies.required, "required")
			addDeps(ctx.extension.dependencies.optional, "optional")
			addDeps(ctx.extension.dependencies.incompatible, "incompatible")

			val manifest = ForgeManifest(
				license = ctx.licenseName,
				issueTrackerURL = ctx.issuesUrl,
				mods = listOf(
					ForgeMod(
						modId = ctx.modId,
						displayName = ctx.modName,
						version = ctx.baseVersion,
						displayURL = ctx.homepageUrl,
						modUrl = ctx.homepageUrl,
						updateJSONURL = ctx.modrinthId.takeIf { it.isNotEmpty() }?.let {
							"https://api.modrinth.com/updates/$it/forge_updates.json?$updateCheckLoader=only"
						},
						logoFile = ctx.icon,
						authors = ctx.authors.joinToString(", "),
						credits = ctx.contributors.joinToString(", "),
						description = ctx.description,
						displayTest = if (supportsDisplayTest) ctx.forgeDisplayTest else null
					)
				),
				dependencies = mapOf(ctx.modId to forgeDeps),
				mixins = listOf(ForgeMixin("${ctx.modId}.mixins.json")),
				accessTransformers = ctx.accessTransformerPath?.let { listOf(ForgeAccessTransformer(it)) },
				modproperties = mapOf(ctx.modId to mapOf("catalogueImageIcon" to ctx.icon))
			)

			return TOML.encodeToString(manifest)
		}
	}

	object NeoForge : ForgeLike("neoforge") {
		override val modManifestPath = "META-INF/neoforge.mods.toml"
		override val excludedResources = (super.excludedResources + "META-INF/mods.toml") + "pack.mcmeta"
		override val supportsDisplayTest = false
	}

	object Forge : ForgeLike("forge") {
		override val modManifestPath = "META-INF/mods.toml"
		override val excludedResources = super.excludedResources + "META-INF/neoforge.mods.toml"
		val mixinConfigAttribute = "MixinConfigs"
	}

	companion object {
		fun of(id: String): Loader = when (id) {
			"fabric" -> Fabric
			"neoforge" -> NeoForge
			"forge" -> Forge
			else -> error("Unknown loader: '$id'")
		}
	}
}
