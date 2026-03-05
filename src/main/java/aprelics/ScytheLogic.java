package aprelics;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Item;

// You likely need to import your interface:
// import aprelics.util.IPlayerData;

public class ScytheLogic {

    public static void useRevenge(Player player) {
        // 1. Arm the ability via your interface
        ((IPlayerData) player).aprelics_setIsRevengeArmed(true);

        player.displayClientMessage(Component.literal("Revenge is Armed...").withStyle(ChatFormatting.DARK_PURPLE), true);

        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ILLUSIONER_PREPARE_MIRROR, SoundSource.PLAYERS, 1.0f, 0.5f);

        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.WITCH, player.getX(), player.getY() + 1, player.getZ(), 20, 0.5, 0.5, 0.5, 0.1);
        }

        // 2. FIX: Pass the ItemStack instead of the Item
        // This matches the (ItemStack, int) signature you found!
        player.getCooldowns().addCooldown(player.getMainHandItem(), 600);
    }
}