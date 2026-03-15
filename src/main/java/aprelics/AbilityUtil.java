package aprelics;

import aprelics.AnkletLogic;
import aprelics.HaloLogic;
import aprelics.ScytheLogic;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class AbilityUtil {

    public static void useAbility(ServerPlayer player) {

        if (!RelicUtil.canUseRelic(player)) {

            return;
        }

        ItemStack headItem = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack feetItem = player.getItemBySlot(EquipmentSlot.FEET);
        ItemStack mainHandItem = player.getMainHandItem();

        if (headItem.is(ModItems.VERDANT_HALO)) {
            HaloLogic.tryHeal(player);
        }
        else if (feetItem.is(ModItems.TYRANTS_ANKLET)) {
            AnkletLogic.useVolcanicSlam(player);
        }
        else if (mainHandItem.is(ModItems.REAPERS_SCYTHE)) {
            ScytheLogic.useRevenge(player);
        }
    }
}