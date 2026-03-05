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
        // getContainerSize() includes everything: Hotbar, Main Inventory, Armor, and Offhand.
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && RELICS.contains(stack.getItem())) {
                count++;
            }
        }
        return count;
    }

    /**
     * Applies a punishment effect to the player for carrying multiple relics.
     */
    public static void punish(Player player) {
        player.hurt(player.damageSources().magic(), 2.0f);
        player.displayClientMessage(Component.literal("§0[§4!§0] §cA Mere Mortal Cannot Control The Power Of More Than One Relic!"), true);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 0.5f, 2.0f);
    }

    /**
     * Checks if a player is in a valid state to use a relic's active ability.
     * @param player The player attempting to use a relic.
     * @return True if the player has exactly one relic, otherwise punishes the player and returns false.
     */
    public static boolean canUseRelic(Player player) {
        if (countRelicsInInventory(player) > 1) {
            punish(player); // Apply Wither/Slowness/Lightning sound
            return false;
        }
        return true;
    }
}