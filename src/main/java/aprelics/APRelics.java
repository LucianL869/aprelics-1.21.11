package aprelics;

import aprelics.effects.ModStatusEffects;
import aprelics.items.BookStaffItem;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.ModInitializer;
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

import java.awt.print.Book;

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
        CrownLogic.register();

        PayloadTypeRegistry.playC2S().register(AbilityPacket.ID, AbilityPacket.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(AbilityPacket.ID, (payload, context) -> {
            context.server().execute(() -> {

                ServerPlayer player = (ServerPlayer) context.player();
                AbilityUtil.useAbility(player);
            });
        });

//        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
//            ItemStack stack = player.getItemInHand(hand);
//            if (stack.getItem() instanceof BookStaffItem staff) {
//                if (staff.getMode(stack) != 0) { // If NOT in "Off" mode
//                    if (!world.isClientSide()) {
//                        staff.useSelectedAbility(player, stack);
//                    }
//                    return InteractionResult.SUCCESS; // Stop the player from punching the block
//                }
//            }
//            return InteractionResult.PASS;
//        });
//
//// Intercept Left-Click in Air
//        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
//            ItemStack stack = player.getItemInHand(hand);
//            if (stack.getItem() instanceof BookStaffItem staff) {
//                if (staff.getMode(stack) != 0) {
//                    if (!world.isClientSide()) {
//                        staff.useSelectedAbility(player, stack);
//                    }
//                    return InteractionResult.SUCCESS; // Stop the player from hitting the entity
//                }
//            }
//            return InteractionResult.PASS;
//        });
    }
}