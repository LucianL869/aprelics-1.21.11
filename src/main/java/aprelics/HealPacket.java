package aprelics;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record HealPacket() implements CustomPacketPayload {

    // In 1.21.11, we use .id() to create the Type
    public static final Type<HealPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath("aprelics", "heal_packet"));

    // If the line above is STILL red, try this specific 1.21.11 variant:
    // public static final Id<HealPacket> ID = new Id<>(ResourceLocation.fromNamespaceAndPath("aprelics", "heal_packet"));

    public static final StreamCodec<FriendlyByteBuf, HealPacket> CODEC = StreamCodec.unit(new HealPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}