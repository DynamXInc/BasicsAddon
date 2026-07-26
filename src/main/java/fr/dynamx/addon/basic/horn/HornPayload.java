package fr.dynamx.addon.basic.horn;

import fr.dynamx.addon.basic.BasicAddonMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record HornPayload() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<HornPayload> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(BasicAddonMod.MOD_ID, "horn"));

    public static final StreamCodec<FriendlyByteBuf, HornPayload> STREAM_CODEC =
            StreamCodec.unit(new HornPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
