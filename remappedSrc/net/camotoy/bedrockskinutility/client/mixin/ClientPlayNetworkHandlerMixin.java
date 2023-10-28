package net.camotoy.bedrockskinutility.client.mixin;

import net.camotoy.bedrockskinutility.client.BedrockCachedProperties;
import net.camotoy.bedrockskinutility.client.SkinManager;
import net.camotoy.bedrockskinutility.client.interfaces.BedrockPlayerListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket;
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

    @Shadow @Final private Map<UUID, PlayerListEntry> playerInfoMap;

    /**
     * @reason check and see if we already have this player's information
     */
    @Inject(method = "handlePlayerInfoUpdate", at = @At("RETURN"))
    public void bedrockskinutility$onPlayerAdd(PlayerListS2CPacket packet, CallbackInfo ci) {
        if (packet.getActions().contains(PlayerListS2CPacket.Action.ADD_PLAYER)) {
            for (PlayerListS2CPacket.Entry entry : packet.getEntries()) {
                BedrockCachedProperties properties = SkinManager.getInstance().getCachedPlayers().getIfPresent(entry.profileId());
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
    public void bedrockskinutility$onPlayerRemove(PlayerRemoveS2CPacket packet, CallbackInfo ci) {
        for (UUID uuid : packet.profileIds()) {
            PlayerListEntry playerListEntry = this.playerInfoMap.get(uuid);
            if (playerListEntry != null) {
                Identifier skinIdentifier = ((BedrockPlayerListEntry) playerListEntry).bedrockskinutility$getSkin();
                Identifier capeIdentifier = ((BedrockPlayerListEntry) playerListEntry).bedrockskinutility$getCape();
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
