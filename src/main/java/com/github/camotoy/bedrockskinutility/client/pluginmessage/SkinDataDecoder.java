package com.github.camotoy.bedrockskinutility.client.pluginmessage;

import com.github.camotoy.bedrockskinutility.client.*;
import com.github.camotoy.bedrockskinutility.client.interfaces.BedrockPlayerListEntry;
import com.github.camotoy.bedrockskinutility.client.mixin.PlayerEntityRendererChangeModel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.entity.EntityRendererFactory;
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
        buf.readBytes(skinData);

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
                EntityRendererFactory.Context context = new EntityRendererFactory.Context(client.getEntityRenderDispatcher(),
                        client.getItemRenderer(), client.getResourceManager(), client.getEntityModelLoader(), client.textRenderer);
                renderer = new PlayerEntityRenderer(context, false);
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
            applySkinTexture(handler, playerUuid, identifier, renderer);
        });
    }

    /**
     * Should be run from the main thread
     */
    private void applySkinTexture(ClientPlayNetworkHandler handler, UUID playerUuid, Identifier identifier, PlayerEntityRenderer renderer) {
        PlayerListEntry entry = handler.getPlayerListEntry(playerUuid);
        if (entry == null) {
            // Save in the cache for later
            BedrockCachedProperties properties = skinManager.getCachedPlayers().getIfPresent(playerUuid);
            if (properties == null) {
                properties = new BedrockCachedProperties();
                skinManager.getCachedPlayers().put(playerUuid, properties);
            }
            properties.model = renderer;
            properties.skin = identifier;
        } else {
            ((BedrockPlayerListEntry) entry).bedrockskinutility$setSkinProperties(identifier, renderer);
        }
    }
}
