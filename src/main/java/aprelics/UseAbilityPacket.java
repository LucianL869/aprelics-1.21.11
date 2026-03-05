package aprelics;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record UseAbilityPacket() implements CustomPacketPayload {

    public static final Type<UseAbilityPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath("aprelics", "use_ability"));
    public static final StreamCodec<FriendlyByteBuf, UseAbilityPacket> CODEC = StreamCodec.unit(new UseAbilityPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}