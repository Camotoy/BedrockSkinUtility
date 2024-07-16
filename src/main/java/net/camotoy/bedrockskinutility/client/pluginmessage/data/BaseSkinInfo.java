package net.camotoy.bedrockskinutility.client.pluginmessage.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.camotoy.bedrockskinutility.client.pluginmessage.BedrockMessageHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamDecoder;

import java.util.UUID;

public record BaseSkinInfo(UUID playerUuid, int skinWidth, int skinHeight, String geometry, JsonObject jsonGeometry,
                           JsonObject jsonGeometryName, int chunkCount) implements BedrockData {
    public static final StreamDecoder<FriendlyByteBuf, BaseSkinInfo> STREAM_DECODER = buf -> {
        int version = buf.readInt();
        if (version != 1) { // Version 2 is probably going to be reserved for persona skins
            throw new RuntimeException("Could not load skin info! Is the mod and plugin updated?");
        }

        UUID playerUuid = new UUID(buf.readLong(), buf.readLong());

        int skinWidth = buf.readInt();
        int skinHeight = buf.readInt();

        String geometry = null;
        JsonObject jsonGeometry = null;
        JsonObject jsonGeometryName = null;

        if (buf.readBoolean()) { // is geometry present
            try {
                geometry = BedrockData.readString(buf);
                jsonGeometry = JsonParser.parseString(geometry).getAsJsonObject();
                jsonGeometryName = JsonParser.parseString(BedrockData.readString(buf)).getAsJsonObject();
            } catch (Exception e) {
                throw new RuntimeException("Error while trying to decode geometry!", e);
            }
        }

        int chunkCount = buf.readInt();

        return new BaseSkinInfo(playerUuid, skinWidth, skinHeight, geometry, jsonGeometry, jsonGeometryName, chunkCount);
    };

    @Override
    public void handle(ClientPlayNetworking.Context context, BedrockMessageHandler handler) {
        handler.handle(this);
    }
}
