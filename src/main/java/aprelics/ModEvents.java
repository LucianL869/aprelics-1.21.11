package aprelics;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

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
    public static void triggerRelicEvent(Level level, Player activator, ItemStack relic) {
        String message = activator.getName().getString() + " has claimed the " + relic.getHoverName().getString() + "!";


        Component titleText = Component.literal("RELIC CLAIMED").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
        Component subtitleText = Component.literal(message).withStyle(ChatFormatting.YELLOW);


        if (level.getServer() != null) {
            for (ServerPlayer serverPlayer : level.getServer().getPlayerList().getPlayers()) {

                serverPlayer.sendSystemMessage(subtitleText);


                serverPlayer.connection.send(new ClientboundSetTitleTextPacket(titleText));
                serverPlayer.connection.send(new ClientboundSetSubtitleTextPacket(subtitleText));
                serverPlayer.connection.send(new ClientboundSetTitlesAnimationPacket(10, 70, 20)); // Ticks


                serverPlayer.playSound(
                        SoundEvents.UI_TOAST_CHALLENGE_COMPLETE,
                        1.0f,
                        1.0f
                );
            }
        }
    }
}