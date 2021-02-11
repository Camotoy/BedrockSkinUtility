package com.github.camotoy.bedrockskinutility.client.pluginmessage;

import com.github.camotoy.bedrockskinutility.client.*;
import com.github.camotoy.bedrockskinutility.client.interfaces.BedrockPlayerListEntry;
import com.github.camotoy.bedrockskinutility.client.interfaces.EntityRendererDispatcherModelModify;
import com.github.camotoy.bedrockskinutility.client.interfaces.PlayerEntityRendererChangeModel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class SkinDataDecoder extends Decoder {
    private final GeometryUtil geometryUtil;

    public SkinDataDecoder(Logger logger, SkinManager skinManager) {
        super(logger, skinManager);
        this.geometryUtil = new GeometryUtil(logger);
    }

    @Override
    public void decode(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf) {
        UUID playerUuid = buf.readUuid();
        int chunkPosition = buf.readInt();
        int available = buf.readableBytes();
        byte[] skinData = new byte[available];
        for (int i = 0; i < skinData.length; i++) {
            skinData[i] = buf.readByte();
        }

        SkinInfo info = skinManager.getSkinInfo().get(playerUuid);
        if (info == null) {
            this.logger.error("Skin info was null!!!");
            return;
        }
        info.setData(skinData, chunkPosition);
        this.logger.info("Skin chunk " + chunkPosition + " received for " + playerUuid.toString());

        if (info.isComplete()) {
            // All skin data has been received
            skinManager.getSkinInfo().remove(playerUuid);
        } else {
            return;
        }

        NativeImage skinImage = toNativeImage(info.getData(), info.getWidth(), info.getHeight());
        PlayerEntityRenderer renderer;
        boolean setModel = info.getGeometry() != null;

        if (setModel) {
            // Convert Bedrock JSON geometry into a class format that Java understands
            BedrockPlayerEntityModel<AbstractClientPlayerEntity> model = geometryUtil.bedrockGeoToJava(info);
            if (model != null) {
                renderer = new PlayerEntityRenderer(client.getEntityRenderDispatcher());
                ((PlayerEntityRendererChangeModel) renderer).bedrockskinutility$setModel(model);
            } else {
                renderer = null;
            }
        } else {
            renderer = null;
        }

        Identifier identifier = new Identifier("geyserskinmanager", playerUuid.toString());
        client.submit(() -> {
            client.getTextureManager().registerTexture(identifier, new NativeImageBackedTexture(skinImage));
            if (setModel) { // If IntelliJ yells at you here, it's because it sees the "CastCastExceptions" above and yells at you.
                ((EntityRendererDispatcherModelModify) client.getEntityRenderDispatcher()).addPlayerModel(playerUuid.toString(), renderer);
            }
            applySkinTexture(handler, playerUuid, identifier, setModel);
        });
    }

    /**
     * Should be run from the main thread
     */
    private void applySkinTexture(ClientPlayNetworkHandler handler, UUID playerUuid, Identifier identifier, boolean setModel) {
        PlayerListEntry entry = handler.getPlayerListEntry(playerUuid);
        if (entry == null) {
            // Save in the cache for later
            BedrockCachedProperties properties = skinManager.getCachedPlayers().getIfPresent(playerUuid);
            if (properties == null) {
                properties = new BedrockCachedProperties();
                skinManager.getCachedPlayers().put(playerUuid, properties);
            }
            if (setModel) {
                properties.model = playerUuid.toString();
            }
            properties.skin = identifier;
        } else {
            ((BedrockPlayerListEntry) entry).bedrockskinutility$setSkinProperties(identifier, setModel ? playerUuid.toString() : null);
        }
    }
}
