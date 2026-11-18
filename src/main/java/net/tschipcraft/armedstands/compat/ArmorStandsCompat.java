package net.tschipcraft.armedstands.compat;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.tschipcraft.armedstands.ArmedStands;

/**
 * Soft dependency on Roundaround's Armor Stands. Its editor has an arms toggle of its own.
 */
public final class ArmorStandsCompat {

	public static final String MOD_ID = "armorstands";

	private static final String USERS_CLASS = "me.roundaround.armorstands.server.ArmorStandUsers";
	private static final String CAN_EDIT_METHOD = "canEditArmorStands";

	// (ServerPlayer)boolean; null while Armor Stands is absent or its check could not be found
	private static volatile MethodHandle canEditArmorStands;
	private static boolean warnedCheckFailed;

	private ArmorStandsCompat() {
	}

	// Call once during init
	public static void init() {
		try {
			// Load without initializing
			Class<?> users = Class.forName(USERS_CLASS, false, ArmorStandsCompat.class.getClassLoader());
			canEditArmorStands = findCanEdit(users);
			ArmedStands.LOGGER.info("[Armed Stands] Mod Armor Stands detected. Disabled the shift-right-click arms toggle from Armed Stands for players who can use Armor Stands' editor");
		} catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
			ArmedStands.LOGGER.warn("[Armed Stands] Mod Armor Stands detected, but its permission check could not be found ({})", e.toString());
		}
	}

	private static MethodHandle findCanEdit(Class<?> users) throws ReflectiveOperationException {
		MethodType serverPlayerCheck = MethodType.methodType(boolean.class, ServerPlayer.class);
		try {
			// Armor Stands 2.x and later
			return MethodHandles.publicLookup().findStatic(users, CAN_EDIT_METHOD, serverPlayerCheck);
		} catch (NoSuchMethodException e) {
			// Armor Stands 1.x (Minecraft 1.19 to 1.20.4)
			return MethodHandles.publicLookup()
					.findStatic(users, CAN_EDIT_METHOD, MethodType.methodType(boolean.class, Player.class))
					.asType(serverPlayerCheck);
		}
	}

	/**
	 * Whether the mod Armor Stands lets this player use its editor.
	 *
	 * @return true if Armor Stands is present and the player can use its editor, false otherwise
	 */
	public static boolean canUseEditor(ServerPlayer player) {
		MethodHandle canEdit = canEditArmorStands;
		if (canEdit == null) {
			return false;
		}

		try {
			return (boolean) canEdit.invokeExact(player);
		} catch (Throwable t) {
			if (!warnedCheckFailed) {
				warnedCheckFailed = true;
				ArmedStands.LOGGER.warn("[Armed Stands] Permission check from mod Armor Stands failed. The shift-right-click arms toggle from Armed Stands stays enabled. Further failures are not logged", t);
			}
			return false;
		}
	}

}
