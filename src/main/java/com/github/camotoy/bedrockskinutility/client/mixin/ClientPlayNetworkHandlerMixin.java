package com.github.camotoy.bedrockskinutility.client.mixin;

import com.github.camotoy.bedrockskinutility.client.BedrockCachedProperties;
import com.github.camotoy.bedrockskinutility.client.interfaces.BedrockPlayerListEntry;
import com.github.camotoy.bedrockskinutility.client.SkinManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;

@Environment(EnvType.CLIENT)
@Mixin(ClientPacketListener.class)
public abstract class ClientPlayNetworkHandlerMixin implements ClientGamePacketListener {

    @Shadow @Final private Map<UUID, PlayerInfo> playerInfoMap;

    /**
     * @reason check and see if we already have this player's information
     */
    @Inject(method = "handlePlayerInfo", at = @At("RETURN"))
    public void bedrockskinutility$onPlayerAdd(ClientboundPlayerInfoPacket packet, CallbackInfo ci) {
        if (packet.getAction() == ClientboundPlayerInfoPacket.Action.ADD_PLAYER) {
            for (ClientboundPlayerInfoPacket.PlayerUpdate entry : packet.getEntries()) {
                BedrockCachedProperties properties = SkinManager.getInstance().getCachedPlayers().getIfPresent(entry.getProfile().getId());
                if (properties != null) {
                    BedrockPlayerListEntry bedrockEntry = ((BedrockPlayerListEntry) this.playerInfoMap.get(entry.getProfile().getId()));
                    if (properties.skin != null) {
                        bedrockEntry.bedrockskinutility$setSkinProperties(properties.skin, properties.model);
                    }
                    if (properties.cape != null) {
                        bedrockEntry.bedrockskinutility$setCape(properties.cape);
                    }
                }
            }
        }
    }

    /**
     * @reason sometimes the player will be removed and then instantly re-added (skin refresh). Let's check for that.
     */
    @Inject(method = "handlePlayerInfo", at = @At("HEAD"))
    public void bedrockskinutility$onPlayerRemove(ClientboundPlayerInfoPacket packet, CallbackInfo ci) {
        if (packet.getAction() == ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER) {
            for (ClientboundPlayerInfoPacket.PlayerUpdate entry : packet.getEntries()) {
                PlayerInfo playerListEntry = this.playerInfoMap.get(entry.getProfile().getId());
                if (playerListEntry != null) {
                    ResourceLocation skinIdentifier = ((BedrockPlayerListEntry) playerListEntry).bedrockskinutility$getSkin();
                    ResourceLocation capeIdentifier = ((BedrockPlayerListEntry) playerListEntry).bedrockskinutility$getCape();
                    if (skinIdentifier != null || capeIdentifier != null) {
                        BedrockCachedProperties properties = new BedrockCachedProperties();
                        properties.skin = skinIdentifier;
                        properties.model = ((BedrockPlayerListEntry) playerListEntry).bedrockskinutility$getModel();
                        properties.cape = capeIdentifier;
                        SkinManager.getInstance().getCachedPlayers().put(entry.getProfile().getId(), properties);
                    }
                }
            }
        }
    }
}
