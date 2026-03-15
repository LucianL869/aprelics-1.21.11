package aprelics;

import aprelics.effects.ModStatusEffects;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class APRelics implements ModInitializer {
    public static final String MOD_ID = "aprelics";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing APRelics for Minecraft 1.21.1...");


        ModItems.register();
        ModStatusEffects.register();

        HaloLogic.register();
        AnkletLogic.register();

        PayloadTypeRegistry.playC2S().register(AbilityPacket.ID, AbilityPacket.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(AbilityPacket.ID, (payload, context) -> {
            context.server().execute(() -> {

                ServerPlayer player = (ServerPlayer) context.player();
                AbilityUtil.useAbility(player);
            });
        });
    }
}