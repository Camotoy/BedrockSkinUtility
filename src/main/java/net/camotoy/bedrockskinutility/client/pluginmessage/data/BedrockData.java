package net.camotoy.bedrockskinutility.client.pluginmessage.data;

import net.camotoy.bedrockskinutility.client.pluginmessage.BedrockMessageHandler;
import net.camotoy.bedrockskinutility.client.pluginmessage.GeyserSkinManagerListener;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.nio.charset.StandardCharsets;

public interface BedrockData extends CustomPacketPayload {

    /**
     * Read a string in a platform-friendly way - Mojang's way uses VarInts which aren't easily implemented on our end.
     */
    static String readString(FriendlyByteBuf buf) {
        int length = buf.readInt();
        String result = buf.toString(buf.readerIndex(), length, StandardCharsets.UTF_8);
        buf.readerIndex(buf.readerIndex() + length);
        return result;
    }

    void handle(ClientPlayNetworking.Context context, BedrockMessageHandler handler);

    @Override
    default Type<? extends CustomPacketPayload> type() {
        return GeyserSkinManagerListener.TYPE;
    }
}
