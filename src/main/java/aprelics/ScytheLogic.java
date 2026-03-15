package aprelics;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.ItemStack;

public class ScytheLogic {

    public static void useRevenge(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            handleScytheAbility(serverPlayer);
        }
    }

    public static void handleScytheAbility(ServerPlayer player) {
        ItemStack stack = player.getMainHandItem();
        if (!stack.is(ModItems.REAPERS_SCYTHE)) return;

        IPlayerData data = (IPlayerData) player;

        boolean isItemOnCooldown = player.getCooldowns().isOnCooldown(stack);
        boolean isDataOnCooldown = data.aprelics_getCooldown() > 0;

        if (isItemOnCooldown || isDataOnCooldown) {
            int secondsRemaining = (int) Math.ceil((data.aprelics_getCooldown() / 20.0));

            player.displayClientMessage(Component.literal("⏳ Scythe is recharging... (" + secondsRemaining + "s)")
                    .withStyle(ChatFormatting.DARK_PURPLE), true);

            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.WOODEN_BUTTON_CLICK_ON, SoundSource.PLAYERS, 0.8f, 0.5f);
            return;
        }


        data.aprelics_setIsRevengeArmed(true);
        data.aprelics_setCooldown(800);        // 40s Cooldown
        data.aprelics_setRevengeDuration(200); // 10s Window of Opportunity

        player.getCooldowns().addCooldown(stack, 800);

        player.displayClientMessage(Component.literal("Reaper's Vengance Activated!").withStyle(ChatFormatting.DARK_PURPLE), true);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ILLUSIONER_PREPARE_MIRROR, SoundSource.PLAYERS, 1.0f, 0.5f);

        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.MYCELIUM, player.getX(), player.getY() + 1, player.getZ(), 20, 0.5, 0.5, 0.5, 0.1);
        }
    }
}