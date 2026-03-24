package aprelics;

import aprelics.AnkletLogic;
import aprelics.HaloLogic;
import aprelics.ScytheLogic;
import aprelics.items.BookStaffItem;
import aprelics.items.GrowHornItem;
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
        else if (headItem.is(ModItems.AMBRIA_CROWN)) {
            CrownLogic.tryCrownAbility(player);
        }
        else if (mainHandItem.is(ModItems.BOOK_STAFF)) {
            ((BookStaffItem)mainHandItem.getItem()).cycleMode(player, mainHandItem);
        }
        else if (mainHandItem.is(ModItems.GROW_HORN)) {
            ((GrowHornItem)mainHandItem.getItem()).useAbility(player);
        }
    }
}