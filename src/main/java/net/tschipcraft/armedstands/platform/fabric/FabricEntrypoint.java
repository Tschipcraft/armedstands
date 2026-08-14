package net.tschipcraft.armedstands.platform.fabric;

//? fabric {

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.tschipcraft.armedstands.ArmedStands;
import net.fabricmc.api.ModInitializer;
import net.tschipcraft.armedstands.event.ArmorStandInteract;

public class FabricEntrypoint implements ModInitializer {

	@Override
	public void onInitialize() {
		UseEntityCallback.EVENT.register(ArmorStandInteract::onClick);
		ArmedStands.onInitialize();
	}

}
//?}
