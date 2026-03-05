package aprelics;

import aprelics.AnkletLogic;
import aprelics.HaloLogic;
import aprelics.ScytheLogic;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class AbilityUtil {

    public static void useAbility(ServerPlayer player) {
        // 1. Check for relic clashing and handle punishment
        if (!RelicUtil.canUseRelic(player)) {
            // RelicUtil.canUseRelic should already be calling punishPlayer internally
            return;
        }

        ItemStack headItem = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack feetItem = player.getItemBySlot(EquipmentSlot.FEET);
        ItemStack mainHandItem = player.getMainHandItem();

        // --- Routing logic: Calls the specific Logic files ---

        // 1. Verdant Halo (Head)
        if (headItem.is(ModItems.VERDANT_HALO)) {
            HaloLogic.tryHeal(player);
        }

        // 2. Tyrant's Anklet (Feet)
        else if (feetItem.is(ModItems.TYRANTS_ANKLET)) {
            AnkletLogic.useVolcanicSlam(player);
        }

        // 3. Reaper's Scythe (Main Hand)
        else if (mainHandItem.is(ModItems.REAPERS_SCYTHE)) {
            ScytheLogic.useRevenge(player);
        }
    }
}