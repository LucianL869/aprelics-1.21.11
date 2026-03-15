package aprelics;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import static aprelics.AnkletLogic.useVolcanicSlam;
import static aprelics.HaloLogic.tryHeal;
import static aprelics.RelicUtil.countRelicsInInventory;
import static aprelics.RelicUtil.punish;
import static aprelics.ScytheLogic.handleScytheAbility;

public record AbilityPacket() implements CustomPacketPayload {


    public static final Type<AbilityPacket> ID = new Type<>(Identifier.fromNamespaceAndPath("aprelics", "ability_packet"));


    public static final StreamCodec<FriendlyByteBuf, AbilityPacket> CODEC = StreamCodec.unit(new AbilityPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handle(ServerPlayer player) {
        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack feet = player.getItemBySlot(EquipmentSlot.FEET);
        ItemStack mainHand = player.getMainHandItem();

        int relicsInInventory = countRelicsInInventory(player);


        if (relicsInInventory > 1) {
            punish(player);
            return;
        }


        if (head.is(ModItems.VERDANT_HALO)) {
            tryHeal(player);
        } else if (feet.is(ModItems.TYRANTS_ANKLET)) {
            useVolcanicSlam(player);
        } else if (mainHand.is(ModItems.REAPERS_SCYTHE)) {
            handleScytheAbility(player);
        }
    }
}