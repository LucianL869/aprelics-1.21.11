package aprelics;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.UUID;

public class ModEvents {

    private static final HashMap<UUID, Boolean> NOTIFIED_READY = new HashMap<>();
    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                IPlayerData data = (IPlayerData) player;


                int currentCooldown = data.aprelics_getCooldown();
                if (currentCooldown > 0) {
                    data.aprelics_setCooldown(currentCooldown - 1);

                    if (currentCooldown - 1 == 0) {
                        player.displayClientMessage(Component.literal("✔ Reaper's Vengeance has Recharged!")
                                .withStyle(ChatFormatting.DARK_PURPLE), true);

                        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                                SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5f, 0.5f);
                    }
                }


                int currentDuration = data.aprelics_getRevengeDuration();
                if (data.aprelics_getIsRevengeArmed() && currentDuration > 0) {
                    data.aprelics_setRevengeDuration(currentDuration - 1);


                    if (currentDuration - 1 == 0) {
                        data.aprelics_setIsRevengeArmed(false);
                        player.displayClientMessage(Component.literal("Vengeance window closed.")
                                .withStyle(ChatFormatting.GRAY), true);
                    }
                }
            }
        });
    }
}