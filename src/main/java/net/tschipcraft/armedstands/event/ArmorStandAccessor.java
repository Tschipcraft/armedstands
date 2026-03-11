package net.tschipcraft.armedstands.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface ArmorStandAccessor {

    void armedStands$dropArmItems(Level level, BlockPos blockPosition);

}
