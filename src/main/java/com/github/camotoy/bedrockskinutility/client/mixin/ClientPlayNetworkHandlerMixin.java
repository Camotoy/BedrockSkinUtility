package com.github.camotoy.bedrockskinutility.client.mixin;

import com.github.camotoy.bedrockskinutility.client.BedrockCachedProperties;
import com.github.camotoy.bedrockskinutility.client.interfaces.BedrockPlayerListEntry;
import com.github.camotoy.bedrockskinutility.client.SkinManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin implements ClientPlayPacketListener {

    @Shadow @Final private Map<UUID, PlayerListEntry> playerListEntries;

    /**
     * @reason check and see if we already have this player's information
     */
    @Inject(method = "onPlayerList", at = @At("RETURN"))
    public void bedrockskinutility$onPlayerAdd(PlayerListS2CPacket packet, CallbackInfo ci) {
        if (packet.getAction() == PlayerListS2CPacket.Action.ADD_PLAYER) {
            for (PlayerListS2CPacket.Entry entry : packet.getEntries()) {
                BedrockCachedProperties properties = SkinManager.getInstance().getCachedPlayers().getIfPresent(entry.getProfile().getId());
                if (properties != null) {
                    BedrockPlayerListEntry bedrockEntry = ((BedrockPlayerListEntry) this.playerListEntries.get(entry.getProfile().getId()));
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
    @Inject(method = "onPlayerList", at = @At("HEAD"))
    public void bedrockskinutility$onPlayerRemove(PlayerListS2CPacket packet, CallbackInfo ci) {
        if (packet.getAction() == PlayerListS2CPacket.Action.REMOVE_PLAYER) {
            for (PlayerListS2CPacket.Entry entry : packet.getEntries()) {
                PlayerListEntry playerListEntry = this.playerListEntries.get(entry.getProfile().getId());
                if (playerListEntry != null) {
                    Identifier skinIdentifier = ((BedrockPlayerListEntry) playerListEntry).bedrockskinutility$getSkin();
                    Identifier capeIdentifier = ((BedrockPlayerListEntry) playerListEntry).bedrockskinutility$getCape();
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
