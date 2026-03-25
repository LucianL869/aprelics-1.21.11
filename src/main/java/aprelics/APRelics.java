package aprelics;

import aprelics.blocks.entity.ModBlockEntities;
import aprelics.client.renderer.projectile.BookProjectileRenderer;
import aprelics.effects.ModStatusEffects;
import aprelics.items.BookStaffItem;
import aprelics.items.GrowHornItem;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.GeckoLib;

public class APRelics implements ModInitializer {
    public static final String MOD_ID = "aprelics";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);



    @Override
    public void onInitialize() {
        LOGGER.info("Initializing APRelics for Minecraft 1.21.1...");

        ModComponents.register();

        ModItems.register();
        ModStatusEffects.register();
        ModBlocks.initialize();
        ModBlockEntities.registerBlockEntities();
        ModEntities.registerModEntities();

        HaloLogic.register();
        AnkletLogic.register();
        CrownLogic.register();
        BookStaffItem.register();
        GrowHornItem.register();


        PayloadTypeRegistry.playC2S().register(AbilityPacket.ID, AbilityPacket.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(AbilityPacket.ID, (payload, context) -> {
            AbilityUtil.useAbility(context.player());

        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            StaffCommands.register(dispatcher);
        });

        net.fabricmc.fabric.api.event.player.AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.getItem() instanceof BookStaffItem && !world.isClientSide()) {

                return InteractionResult.PASS;
            }
            return InteractionResult.PASS;
        });
    }
}