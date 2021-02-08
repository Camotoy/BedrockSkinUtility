package com.github.camotoy.bedrockskinutility.client.pluginmessage;

import com.github.camotoy.bedrockskinutility.client.BedrockPlayerListEntry;
import com.github.camotoy.bedrockskinutility.client.BedrockSkinPluginMessageType;
import com.github.camotoy.bedrockskinutility.client.SkinUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class GeyserSkinManagerListener implements ClientPlayNetworking.PlayChannelHandler {
    private static final Identifier CHANNEL = new Identifier("bedrockskin", "data");
    /**
     * If a player cannot be found, then stuff the UUID in here until they spawn.
     */
    public static final Cache<UUID, Identifier> CACHED_PLAYERS = CacheBuilder.newBuilder()
            .expireAfterAccess(15, TimeUnit.SECONDS)
            .build();

    private final Logger logger;

    public GeyserSkinManagerListener(Logger logger) {
        this.logger = logger;
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
        if (pluginMessageType == BedrockSkinPluginMessageType.SEND_CAPE) {
            int version = buf.readInt();
            if (version != 1) {
                this.logger.error("Could not load cape data! Is the mod and plugin updated?");
                return;
            }

            UUID playerUuid = new UUID(buf.readLong(), buf.readLong());
            int width = buf.readInt();
            int height = buf.readInt();

            byte[] capeIdBytes = new byte[buf.readInt()];
            for (int i = 0; i < capeIdBytes.length; i++) {
                capeIdBytes[i] = buf.readByte();
            }
            String capeId = new String(capeIdBytes, StandardCharsets.UTF_8);
            Identifier identifier = new Identifier("geyserskinmanager", capeId);
            if (client.getTextureManager().getTexture(identifier) != null) {
                // Texture is already registered, so we don't need to apply it again
                client.submit(() -> applyCapeTexture(client, playerUuid, identifier));
                return;
            }

            byte[] capeData = new byte[buf.readInt()];
            for (int i = 0; i < capeData.length; i++) {
                capeData[i] = buf.readByte();
            }
            /* Read plugin message end */

            BufferedImage bufferedImage = SkinUtils.toBufferedImage(capeData, width, height);

            NativeImage capeImage = new NativeImage(width, height, true);
            for (int currentWidth = 0; currentWidth < width; currentWidth++) {
                for (int currentHeight = 0; currentHeight < height; currentHeight++) {
                    int rgba = bufferedImage.getRGB(currentWidth, currentHeight);
                    capeImage.setPixelColor(currentWidth, currentHeight, NativeImage.getAbgrColor(
                            (rgba >> 24) & 0xFF, rgba & 0xFF, (rgba >> 8) & 0xFF, (rgba >> 16) & 0xFF));
                }
            }

            client.submit(() -> {
                client.getTextureManager().registerTexture(identifier, new NativeImageBackedTexture(capeImage));
                applyCapeTexture(client, playerUuid, identifier);
            });
        }
    }

    /**
     * Should be run from the main thread
     */
    private void applyCapeTexture(MinecraftClient client, UUID playerUuid, Identifier identifier) {
        if (client.getNetworkHandler() == null) {
            // ???
            this.logger.info("Network handler was null; not applying texture for " + playerUuid);
            return;
        }
        PlayerListEntry entry = client.getNetworkHandler().getPlayerListEntry(playerUuid);
        if (entry == null) {
            // Save in the cache for later
            CACHED_PLAYERS.put(playerUuid, identifier);
        } else {
            ((BedrockPlayerListEntry) entry).bedrockskinutility$setCape(identifier);
        }
    }
}
