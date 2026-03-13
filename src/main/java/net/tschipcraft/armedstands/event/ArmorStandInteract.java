package net.tschipcraft.armedstands.event;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class ArmorStandInteract {

    public static InteractionResult onClick(Player player, Level level, InteractionHand hand, Entity target, EntityHitResult hitResult) {
        // Only target armor stands
        if (!(target instanceof ArmorStand armorStand)) {
            return InteractionResult.PASS;
        }

        // Ensure armor stand does not have marker or invisible flags
        if (armorStand.isMarker() || armorStand.isInvisible()) {
            return InteractionResult.PASS;
        }

        // Ensure empty hand & is sneaking
        ItemStack handStack = player.getItemInHand(hand);
        if (!handStack.getItem().equals(Items.AIR)) {
            return InteractionResult.PASS;
        }

        if (!player.isCrouching()) {
            return InteractionResult.PASS;
        }

        // Toggle arms server-side
		if (!level.isClientSide()) {
        	if (/*? >= 1.21.3 >>*//*armorStand.showArms()*/ /*? < 1.21.3 >>*/armorStand.isShowArms() ) {
            	// Drop items from armor stand if arms are being removed
            	((ArmorStandAccessor) armorStand).armedStands$dropArmItems(level, armorStand.blockPosition());

				armorStand.setShowArms(false);
			} else {
				armorStand.setShowArms(true);
			}
		}

        return InteractionResult.SUCCESS;
    }

}
