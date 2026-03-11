package net.tschipcraft.armedstands;

import net.tschipcraft.armedstands.platform.Platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//? fabric {
import net.tschipcraft.armedstands.platform.fabric.FabricPlatform;
//?} neoforge {
/*import net.tschipcraft.armedstands.platform.neoforge.NeoforgePlatform;
 *///?} forge {
/*import net.tschipcraft.armedstands.platform.forge.ForgePlatform;
*///?}

@SuppressWarnings("LoggingSimilarMessage")
public class ArmedStands {

	public static final String MOD_ID = /*$ mod_id*/ "armedstands";
	public static final String MOD_VERSION = /*$ mod_version*/ "1.0";
	public static final String MOD_FRIENDLY_NAME = /*$ mod_name*/ "Armed Stands";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final Platform PLATFORM = createPlatformInstance();

	public static void onInitialize() {
		LOGGER.info("Initialized {} by Tschipcraft on {}", MOD_FRIENDLY_NAME, ArmedStands.xplat().loader());
		LOGGER.debug("{}: { version: {}; friendly_name: {} }", MOD_ID, MOD_VERSION, MOD_FRIENDLY_NAME);
	}

	static Platform xplat() {
		return PLATFORM;
	}

	private static Platform createPlatformInstance() {
		//? fabric {
		return new FabricPlatform();
		//?} neoforge {
		/*return new NeoforgePlatform();
		 *///?} forge {
		/*return new ForgePlatform();
		*///?}
	}

}
