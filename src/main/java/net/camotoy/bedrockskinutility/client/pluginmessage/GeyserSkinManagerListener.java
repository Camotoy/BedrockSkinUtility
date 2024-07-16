package net.camotoy.bedrockskinutility.client.pluginmessage;

import net.camotoy.bedrockskinutility.client.BedrockSkinPluginMessageType;
import net.camotoy.bedrockskinutility.client.pluginmessage.data.BaseSkinInfo;
import net.camotoy.bedrockskinutility.client.pluginmessage.data.BedrockData;
import net.camotoy.bedrockskinutility.client.pluginmessage.data.CapeData;
import net.camotoy.bedrockskinutility.client.pluginmessage.data.SkinData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public final class GeyserSkinManagerListener {
    public static final CustomPacketPayload.Type<BedrockData> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("bedrockskin", "data"));
    public static final StreamCodec<FriendlyByteBuf, BedrockData> STREAM_CODEC = StreamCodec.of(null, buf -> {
        int type = buf.readInt();
        BedrockSkinPluginMessageType[] values = BedrockSkinPluginMessageType.values();
        if (values.length < type) {
            throw new RuntimeException("Unknown plugin message type received! Is the mod and plugin updated? Type: " + type);
        }
        BedrockSkinPluginMessageType pluginMessageType = values[type];
        return switch (pluginMessageType) {
            case CAPE -> CapeData.STREAM_DECODER.decode(buf);
            case SKIN_INFORMATION -> BaseSkinInfo.STREAM_DECODER.decode(buf);
            case SKIN_DATA -> SkinData.STREAM_DECODER.decode(buf);
        };
    });

    private GeyserSkinManagerListener() {
    }
}
