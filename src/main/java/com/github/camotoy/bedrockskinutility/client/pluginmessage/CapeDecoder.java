package com.github.camotoy.bedrockskinutility.client.pluginmessage;

import com.github.camotoy.bedrockskinutility.client.BedrockCachedProperties;
import com.github.camotoy.bedrockskinutility.client.interfaces.BedrockPlayerListEntry;
import com.github.camotoy.bedrockskinutility.client.SkinManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class CapeDecoder extends Decoder {

    public CapeDecoder(Logger logger, SkinManager skinManager) {
        super(logger, skinManager);
    }

    @Override
    public void decode(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf) {
        int version = buf.readInt();
        if (version != 1) {
            this.logger.error("Could not load cape data! Is the mod and plugin updated?");
            return;
        }

        UUID playerUuid = new UUID(buf.readLong(), buf.readLong());
        int width = buf.readInt();
        int height = buf.readInt();

        String capeId = readString(buf);
        Identifier identifier = new Identifier("geyserskinmanager", capeId);

        byte[] capeData = new byte[buf.readInt()];
        for (int i = 0; i < capeData.length; i++) {
            capeData[i] = buf.readByte();
        }
        /* Read plugin message end */

        NativeImage capeImage = toNativeImage(capeData, width, height);

        client.submit(() -> {
            // As of 1.17.1, identical identifiers do not result in multiple objects of the same type being registered
            client.getTextureManager().registerTexture(identifier, new NativeImageBackedTexture(capeImage));
            applyCapeTexture(handler, playerUuid, identifier);
        });
    }

    /**
     * Should be run from the main thread
     */
    private void applyCapeTexture(ClientPlayNetworkHandler handler, UUID playerUuid, Identifier identifier) {
        PlayerListEntry entry = handler.getPlayerListEntry(playerUuid);
        if (entry == null) {
            // Save in the cache for later
            BedrockCachedProperties properties = skinManager.getCachedPlayers().getIfPresent(playerUuid);
            if (properties == null) {
                properties = new BedrockCachedProperties();
                skinManager.getCachedPlayers().put(playerUuid, properties);
            }
            properties.cape = identifier;
        } else {
            ((BedrockPlayerListEntry) entry).bedrockskinutility$setCape(identifier);
        }
    }
}
