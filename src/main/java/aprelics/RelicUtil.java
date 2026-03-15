package aprelics;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.List;

public class RelicUtil {

    private static final List<Item> RELICS = new ArrayList<>();

    public static void registerRelic(Item relic) {
        RELICS.add(relic);
    }

    public static List<Item> getRelics() {
        return RELICS;
    }

    public static int countRelicsInInventory(Player player) {
        int count = 0;

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && RELICS.contains(stack.getItem())) {
                count++;
            }
        }
        return count;
    }


    public static void punish(Player player) {
        player.hurt(player.damageSources().magic(), 2.0f);
        player.displayClientMessage(Component.literal("§0[§4!§0] §cA Mere Mortal Cannot Control The Power Of More Than One Relic!"), true);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 0.5f, 2.0f);
    }


    public static boolean canUseRelic(Player player) {
        if (countRelicsInInventory(player) > 1) {
            punish(player);
            return false;
        }
        return true;
    }
}