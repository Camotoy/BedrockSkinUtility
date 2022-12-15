package net.camotoy.bedrockskinutility.client.mixin;

import net.camotoy.bedrockskinutility.client.BedrockCachedProperties;
import net.camotoy.bedrockskinutility.client.SkinManager;
import net.camotoy.bedrockskinutility.client.interfaces.BedrockPlayerListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
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
    @Inject(method = "handlePlayerInfoUpdate", at = @At("RETURN"))
    public void bedrockskinutility$onPlayerAdd(ClientboundPlayerInfoUpdatePacket packet, CallbackInfo ci) {
        if (packet.actions().contains(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER)) {
            for (ClientboundPlayerInfoUpdatePacket.Entry entry : packet.entries()) {
                BedrockCachedProperties properties = SkinManager.getInstance().getCachedPlayers().getIfPresent(entry.profileId()); // TODO test
                if (properties != null) {
                    BedrockPlayerListEntry bedrockEntry = ((BedrockPlayerListEntry) this.playerInfoMap.get(entry.profileId()));
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
    @Inject(method = "handlePlayerInfoRemove", at = @At("HEAD"))
    public void bedrockskinutility$onPlayerRemove(ClientboundPlayerInfoRemovePacket packet, CallbackInfo ci) {
        for (UUID uuid : packet.profileIds()) {
            PlayerInfo playerListEntry = this.playerInfoMap.get(uuid);
            if (playerListEntry != null) {
                ResourceLocation skinIdentifier = ((BedrockPlayerListEntry) playerListEntry).bedrockskinutility$getSkin();
                ResourceLocation capeIdentifier = ((BedrockPlayerListEntry) playerListEntry).bedrockskinutility$getCape();
                if (skinIdentifier != null || capeIdentifier != null) {
                    BedrockCachedProperties properties = new BedrockCachedProperties();
                    properties.skin = skinIdentifier;
                    properties.model = ((BedrockPlayerListEntry) playerListEntry).bedrockskinutility$getModel();
                    properties.cape = capeIdentifier;
                    SkinManager.getInstance().getCachedPlayers().put(uuid, properties);
                }
            }
        }
    }
}
