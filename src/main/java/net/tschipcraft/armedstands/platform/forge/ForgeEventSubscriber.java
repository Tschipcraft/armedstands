package net.tschipcraft.armedstands.platform.forge;

//? forge {

/*import net.minecraft.world.InteractionResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.tschipcraft.armedstands.ArmedStands;
import net.tschipcraft.armedstands.event.ArmorStandInteract;

@Mod.EventBusSubscriber(modid = ArmedStands.MOD_ID)
public class ForgeEventSubscriber {

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
