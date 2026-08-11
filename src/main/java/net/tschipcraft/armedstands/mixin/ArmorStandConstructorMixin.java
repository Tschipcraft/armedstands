package net.tschipcraft.armedstands.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.tschipcraft.armedstands.event.ArmorStandAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? >= 1.21.5 {
/*import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionHand;
*///?} else {
import net.minecraft.core.NonNullList;
import org.spongepowered.asm.mixin.Final;
//?}

@Mixin(ArmorStand.class)
public abstract class ArmorStandConstructorMixin implements ArmorStandAccessor {

	@Shadow
	public abstract void setShowArms(boolean bl);

	//? < 1.21.5 {
	@Shadow
	@Final
	private NonNullList<ItemStack> handItems;

	@Shadow
	public abstract void setItemSlot(EquipmentSlot equipmentSlot, ItemStack itemStack);
	//?}

	@Shadow
	private int disabledSlots;

	// Modify new armor stands server-side to show arms by default
	@Inject(
			method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V",
			at = @At("TAIL")
	)
	private void onArmorStandInit(EntityType<? extends ArmorStand> entityType, Level level, CallbackInfo ci) {
		if (!level.isClientSide()) {
			this.setShowArms(true);
		}
	}

	// Drop main and offhand items if slots are not disabled; called from ArmorStandInteract
	@Unique
	@Override
	public void armedStands$dropArmItems(Level level, BlockPos blockPosition) {
		if (!this.armedStands$isRemoveDisabled(EquipmentSlot.MAINHAND)) {
			//? >= 1.21.5 {
			/*ItemStack itemStack = ((LivingEntity)(Object)this).getItemInHand(InteractionHand.MAIN_HAND);
			*///?} else {
			ItemStack itemStack = (ItemStack)this.handItems.get(0);
			//?}
			if (!itemStack.isEmpty()) {
				Block.popResource(level, blockPosition.above(), itemStack);
				//? >= 1.21.5 {
				/*((LivingEntity)(Object)this).setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
				*///?} else {
				this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
				//?}
			}
		}
		if (!this.armedStands$isRemoveDisabled(EquipmentSlot.OFFHAND)) {
			//? >= 1.21.5 {
			/*ItemStack itemStack = ((LivingEntity)(Object)this).getItemInHand(InteractionHand.OFF_HAND);
			*///?} else {
			ItemStack itemStack = (ItemStack)this.handItems.get(1);
			//?}
			if (!itemStack.isEmpty()) {
				Block.popResource(level, blockPosition.above(), itemStack);
				//? >= 1.21.5 {
				/*((LivingEntity)(Object)this).setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
				*///?} else {
				this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
				//?}
			}
		}

		// Technically, Mojang has implemented logic to drop more than two hand items.
		// We'll ignore this since armor stands normally only have two arms
	}

	@Unique
	private boolean armedStands$isRemoveDisabled(EquipmentSlot slot) {
		int flag;
		//? >= 1.21.3 {
		/*flag = slot.getFilterBit(0);
		*///?} else {
		flag = slot.getFilterFlag();
		//?}
		return (this.disabledSlots & (1 << flag)) != 0 || (this.disabledSlots & (1 << (flag + 8))) != 0;
	}

}
