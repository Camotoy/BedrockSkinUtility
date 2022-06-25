package net.camotoy.bedrockskinutility.client.pluginmessage;

import com.mojang.blaze3d.platform.NativeImage;
import net.camotoy.bedrockskinutility.client.*;
import net.camotoy.bedrockskinutility.client.interfaces.BedrockPlayerListEntry;
import net.camotoy.bedrockskinutility.client.mixin.PlayerEntityRendererChangeModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class SkinDataDecoder extends Decoder {
    private final GeometryUtil geometryUtil;

    public SkinDataDecoder(Logger logger, SkinManager skinManager) {
        super(logger, skinManager);
        this.geometryUtil = new GeometryUtil(logger);
    }

    @Override
    public void decode(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf) {
        UUID playerUuid = buf.readUUID();
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
        PlayerRenderer renderer;
        boolean setModel = info.getGeometry() != null;

        if (setModel) {
            // Convert Bedrock JSON geometry into a class format that Java understands
            BedrockPlayerEntityModel<AbstractClientPlayer> model = geometryUtil.bedrockGeoToJava(info);
            if (model != null) {
                EntityRendererProvider.Context context = new EntityRendererProvider.Context(client.getEntityRenderDispatcher(),
                        client.getItemRenderer(), client.getBlockRenderer(), client.getEntityRenderDispatcher().getItemInHandRenderer(),
                        client.getResourceManager(), client.getEntityModels(), client.font);
                renderer = new PlayerRenderer(context, false);
                ((PlayerEntityRendererChangeModel) renderer).bedrockskinutility$setModel(model);
            } else {
                renderer = null;
            }
        } else {
            renderer = null;
        }

        ResourceLocation identifier = new ResourceLocation("geyserskinmanager", playerUuid.toString());
        client.submit(() -> {
            client.getTextureManager().register(identifier, new DynamicTexture(skinImage));
            applySkinTexture(handler, playerUuid, identifier, renderer);
        });
    }

    /**
     * Should be run from the main thread
     */
    private void applySkinTexture(ClientPacketListener handler, UUID playerUuid, ResourceLocation identifier, PlayerRenderer renderer) {
        PlayerInfo entry = handler.getPlayerInfo(playerUuid);
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
