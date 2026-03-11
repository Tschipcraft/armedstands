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

	@SubscribeEvent
	public static void onEntityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
		InteractionResult result = ArmorStandInteract.onClick(event.getEntity(), event.getLevel(), event.getHand(), event.getTarget(), null);
		if (result.consumesAction()) {
			event.setCanceled(true);
			event.setCancellationResult(result);
		}
	}

}
*///?}
