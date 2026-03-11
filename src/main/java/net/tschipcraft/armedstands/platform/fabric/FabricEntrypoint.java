package net.tschipcraft.armedstands.platform.fabric;

//? fabric {

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.tschipcraft.armedstands.ArmedStands;
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import net.fabricmc.api.ModInitializer;
import net.tschipcraft.armedstands.event.ArmorStandInteract;

@Entrypoint("main")
public class FabricEntrypoint implements ModInitializer {

	@Override
	public void onInitialize() {
		UseEntityCallback.EVENT.register(ArmorStandInteract::onClick);
		ArmedStands.onInitialize();
	}

}
//?}
