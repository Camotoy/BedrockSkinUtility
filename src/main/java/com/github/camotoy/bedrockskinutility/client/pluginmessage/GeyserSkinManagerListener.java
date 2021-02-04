package com.github.camotoy.bedrockskinutility.client.pluginmessage;

import com.github.camotoy.bedrockskinutility.client.BedrockPlayerListEntry;
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
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class GeyserSkinManagerListener implements ClientPlayNetworking.PlayChannelHandler {
    private static final Identifier CHANNEL = new Identifier("geyserskinmanager", "cape");
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
        int version = buf.readInt();
        if (version != 1) {
            this.logger.info("Could not load cape data! Is the mod and plugin updated?");
            return;
        }
        UUID playerUuid = new UUID(buf.readLong(), buf.readLong());
        int width = buf.readInt();
        int height = buf.readInt();
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
            if (client.world == null) {
                this.logger.info("Client world was null; not applying texture for " + playerUuid);
                return;
            }
            if (client.getNetworkHandler() == null) {
                // ???
                this.logger.info("Network handler was null; not applying texture for " + playerUuid);
                return;
            }
            Identifier identifier = client.getTextureManager().registerDynamicTexture("geyserskinmanager", new NativeImageBackedTexture(capeImage));
            PlayerListEntry entry = client.getNetworkHandler().getPlayerListEntry(playerUuid);
            if (entry == null) {
                // Save in the cache for later
                CACHED_PLAYERS.put(playerUuid, identifier);
            } else {
                ((BedrockPlayerListEntry) entry).bedrockskinutility$setCape(identifier);
            }
        });
    }
}
