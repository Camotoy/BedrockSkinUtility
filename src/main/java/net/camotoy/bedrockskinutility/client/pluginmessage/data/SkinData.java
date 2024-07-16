package net.camotoy.bedrockskinutility.client.pluginmessage.data;

import net.camotoy.bedrockskinutility.client.pluginmessage.BedrockMessageHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamDecoder;

import java.util.UUID;

public record SkinData(UUID playerUuid, int chunkPosition, int available, byte[] skinData) implements BedrockData {
    public static final StreamDecoder<FriendlyByteBuf, SkinData> STREAM_DECODER = buf -> {
        UUID playerUuid = buf.readUUID();
        int chunkPosition = buf.readInt();
        int available = buf.readableBytes();
        byte[] skinData = new byte[available];
        buf.readBytes(skinData);
        return new SkinData(playerUuid, chunkPosition, available, skinData);
    };

    @Override
    public void handle(ClientPlayNetworking.Context context, BedrockMessageHandler handler) {
        handler.handle(this, context);
    }
}
