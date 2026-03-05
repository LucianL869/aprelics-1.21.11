package aprelics;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record AbilityPacket() implements CustomPacketPayload {

    // Using the 'Id' class which is standard for 1.21.1 CustomPacketPayloads
    public static final Type<AbilityPacket> ID = new Type<>(Identifier.fromNamespaceAndPath("aprelics", "ability_packet"));

    // StreamCodec.unit is perfect here because we aren't sending any extra data (like coordinates),
    // we are just sending the notification that the key was pressed.
    public static final StreamCodec<FriendlyByteBuf, AbilityPacket> CODEC = StreamCodec.unit(new AbilityPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}