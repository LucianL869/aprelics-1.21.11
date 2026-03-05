package aprelics;

import aprelics.effects.ModStatusEffects;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class APRelics implements ModInitializer {
    public static final String MOD_ID = "aprelics";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing APRelics for Minecraft 1.21.1...");

        // 1. Register Items and Effects
        ModItems.register();
        ModStatusEffects.register();

        // 2. Register Passive Logic Handlers
        // These handle things like the Lush Cave speed boost or Slam logic
        HaloLogic.register();
        AnkletLogic.register();

        // 3. Register the Universal Network Packet
        // We only need one packet because AbilityUtil routes the logic based on equipment
        PayloadTypeRegistry.playC2S().register(AbilityPacket.ID, AbilityPacket.CODEC);

        // 4. Register the Packet Receiver
        ServerPlayNetworking.registerGlobalReceiver(AbilityPacket.ID, (payload, context) -> {
            context.server().execute(() -> {
                // We cast the player to ServerPlayer to ensure full access to server-side methods
                ServerPlayer player = (ServerPlayer) context.player();

                // The Router: Checks if you're wearing Halo, Anklet, or Scythe and triggers the right one
                AbilityUtil.useAbility(player);
            });
        });
    }
}