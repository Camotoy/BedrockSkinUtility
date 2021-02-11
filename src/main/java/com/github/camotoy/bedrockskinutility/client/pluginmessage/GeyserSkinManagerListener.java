package com.github.camotoy.bedrockskinutility.client.pluginmessage;

import com.github.camotoy.bedrockskinutility.client.BedrockSkinPluginMessageType;
import com.github.camotoy.bedrockskinutility.client.SkinManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Logger;

public class GeyserSkinManagerListener implements ClientPlayNetworking.PlayChannelHandler {
    private static final Identifier CHANNEL = new Identifier("bedrockskin", "data");

    private final Logger logger;
    private final SkinManager skinManager;

    public GeyserSkinManagerListener(Logger logger, SkinManager skinManager) {
        this.logger = logger;
        this.skinManager = skinManager;
    }

    public void register() {
        ClientPlayNetworking.registerGlobalReceiver(CHANNEL, this);
    }

    public void unregister() {
        ClientPlayNetworking.unregisterGlobalReceiver(CHANNEL);
    }

    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        this.logger.info("Plugin message received from server!");
        /* Read plugin message start */
        int type = buf.readInt();
        if (BedrockSkinPluginMessageType.values().length < type) {
            this.logger.error("Unknown plugin message type received! Is the mod and plugin updated? Type: " + type);
            return;
        }
        BedrockSkinPluginMessageType pluginMessageType = BedrockSkinPluginMessageType.values()[type];
        if (pluginMessageType == BedrockSkinPluginMessageType.CAPE) {
            skinManager.getCapeDecoder().decode(client, handler, buf);
        } else if (pluginMessageType == BedrockSkinPluginMessageType.SKIN_INFORMATION) {
            skinManager.getSkinInfoDecoder().decode(client, handler, buf);
        } else if (pluginMessageType == BedrockSkinPluginMessageType.SKIN_DATA) {
            skinManager.getSkinDataDecoder().decode(client, handler, buf);
        }
    }
}
