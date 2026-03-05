package aprelics;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class APRelics implements ModInitializer {
    public static final String MOD_ID = "aprelics";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing APRelics...");

        // 1. Register Items
        ModItems.register();

        // 2. Register Global Logic (Tick events)
        HaloLogic.register();

        // 3. Register the Packet for Networking (1.21.4 Style)
        // This tells the game that "HealPacket" is a valid thing to send over the air
        PayloadTypeRegistry.playC2S().register(HealPacket.TYPE, HealPacket.CODEC);

        // 4. Listen for the "Phone Call" from the Client
        ServerPlayNetworking.registerGlobalReceiver(HealPacket.TYPE, (payload, context) -> {
            // Move the logic to the main Server Thread to avoid crashes
            context.server().execute(() -> {
                // Call our updated HaloLogic method
                HaloLogic.tryHeal(context.player());
            });
        });
    }
}