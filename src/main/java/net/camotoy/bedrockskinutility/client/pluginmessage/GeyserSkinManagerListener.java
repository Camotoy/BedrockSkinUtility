package net.camotoy.bedrockskinutility.client.pluginmessage;

import net.camotoy.bedrockskinutility.client.BedrockSkinPluginMessageType;
import net.camotoy.bedrockskinutility.client.SkinManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.Logger;

public class GeyserSkinManagerListener implements ClientPlayNetworking.PlayChannelHandler {
    private static final ResourceLocation CHANNEL = new ResourceLocation("bedrockskin", "data");

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
    public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        this.logger.info("Plugin message received from server!");
        /* Read plugin message start */
        int type = buf.readInt();
        BedrockSkinPluginMessageType[] values = BedrockSkinPluginMessageType.values();
        if (values.length < type) {
            this.logger.error("Unknown plugin message type received! Is the mod and plugin updated? Type: " + type);
            return;
        }
        BedrockSkinPluginMessageType pluginMessageType = values[type];
        if (pluginMessageType == BedrockSkinPluginMessageType.CAPE) {
            skinManager.getCapeDecoder().decode(client, handler, buf);
        } else if (pluginMessageType == BedrockSkinPluginMessageType.SKIN_INFORMATION) {
            skinManager.getSkinInfoDecoder().decode(client, handler, buf);
        } else if (pluginMessageType == BedrockSkinPluginMessageType.SKIN_DATA) {
            skinManager.getSkinDataDecoder().decode(client, handler, buf);
        }
    }
}
