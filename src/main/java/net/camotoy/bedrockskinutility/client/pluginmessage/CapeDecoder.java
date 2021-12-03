package net.camotoy.bedrockskinutility.client.pluginmessage;

import net.camotoy.bedrockskinutility.client.BedrockCachedProperties;
import net.camotoy.bedrockskinutility.client.SkinManager;
import net.camotoy.bedrockskinutility.client.interfaces.BedrockPlayerListEntry;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class CapeDecoder extends Decoder {

    public CapeDecoder(Logger logger, SkinManager skinManager) {
        super(logger, skinManager);
    }

    @Override
    public void decode(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf) {
        int version = buf.readInt();
        if (version != 1) {
            this.logger.error("Could not load cape data! Is the mod and plugin updated?");
            return;
        }

        UUID playerUuid = new UUID(buf.readLong(), buf.readLong());
        int width = buf.readInt();
        int height = buf.readInt();

        String capeId = readString(buf);
        ResourceLocation identifier = new ResourceLocation("geyserskinmanager", capeId);

        byte[] capeData = new byte[buf.readInt()];
        buf.readBytes(capeData);
        /* Read plugin message end */

        NativeImage capeImage = toNativeImage(capeData, width, height);

        client.submit(() -> {
            // As of 1.17.1, identical identifiers do not result in multiple objects of the same type being registered
            client.getTextureManager().register(identifier, new DynamicTexture(capeImage));
            applyCapeTexture(handler, playerUuid, identifier);
        });
    }

    /**
     * Should be run from the main thread
     */
    private void applyCapeTexture(ClientPacketListener handler, UUID playerUuid, ResourceLocation identifier) {
        PlayerInfo entry = handler.getPlayerInfo(playerUuid);
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
