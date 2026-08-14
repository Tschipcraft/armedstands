package net.tschipcraft.armedstands.platform.neoforge;

//? neoforge {

/*import net.minecraft.world.InteractionResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.tschipcraft.armedstands.ArmedStands;
import net.tschipcraft.armedstands.event.ArmorStandInteract;

@EventBusSubscriber(modid = ArmedStands.MOD_ID)
public class NeoforgeEventSubscriber {

	// NeoForge 26.2 combined EntityInteractSpecific into EntityInteract
	// (neoforged/NeoForge#3339, 26.2.0.43-beta)
	@SubscribeEvent
	//? if >=26.2 {
	/^public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
	^///?} else {
	public static void onEntityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
	//?}
		InteractionResult result = ArmorStandInteract.onClick(event.getEntity(), event.getLevel(), event.getHand(), event.getTarget(), null);
		if (result.consumesAction()) {
			event.setCanceled(true);
			event.setCancellationResult(result);
		}
	}

}
*///?}
