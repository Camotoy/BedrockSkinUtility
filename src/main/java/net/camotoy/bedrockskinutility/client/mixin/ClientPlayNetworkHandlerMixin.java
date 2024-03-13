package net.camotoy.bedrockskinutility.client.mixin;

import net.camotoy.bedrockskinutility.client.BedrockCachedProperties;
import net.camotoy.bedrockskinutility.client.PlayerSkinBuilder;
import net.camotoy.bedrockskinutility.client.SkinManager;
import net.camotoy.bedrockskinutility.client.interfaces.BedrockPlayerInfo;
import net.camotoy.bedrockskinutility.client.interfaces.BedrockPlayerSkin;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.PlayerSkin;
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
                BedrockCachedProperties properties = SkinManager.getInstance().getCachedPlayers().getIfPresent(entry.profileId());
                if (properties != null) {
                    final PlayerInfo playerInfo = this.playerInfoMap.get(entry.profileId());
                    final PlayerSkinBuilder builder = new PlayerSkinBuilder(playerInfo.getSkin());
                    if (properties.skin != null) {
                        builder.texture = properties.skin;
                        builder.bedrockSkin = true;
                        ((BedrockPlayerInfo) playerInfo).bedrockskinutility$setModel(properties.model);
                    }
                    if (properties.cape != null && builder.capeTexture == null) {
                        // Do not overwrite existing capes
                        builder.capeTexture = properties.cape;
                        builder.bedrockCape = true;
                    }
                    final PlayerSkin playerSkin = builder.build();
                    ((PlayerSkinFieldAccessor) playerInfo).setPlayerSkin(() -> playerSkin);
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
                final PlayerSkin playerSkin = playerListEntry.getSkin();
                BedrockPlayerSkin bedrockSkin = (BedrockPlayerSkin) (Object) playerSkin;
                ResourceLocation skinIdentifier = bedrockSkin.bedrockskinutility$bedrockSkin() ? playerSkin.texture() : null;
                ResourceLocation capeIdentifier = bedrockSkin.bedrockskinutility$bedrockCape() ? playerSkin.capeTexture() : null;
                if (skinIdentifier != null || capeIdentifier != null) {
                    BedrockCachedProperties properties = new BedrockCachedProperties();
                    properties.skin = skinIdentifier;
                    properties.model = ((BedrockPlayerInfo) playerListEntry).bedrockskinutility$getModel();
                    properties.cape = capeIdentifier;
                    SkinManager.getInstance().getCachedPlayers().put(uuid, properties);
                }
            }
        }
    }
}
