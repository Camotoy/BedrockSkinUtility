package com.github.camotoy.bedrockskinutility.client.mixin;

import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractClientPlayer.class)
public interface BedrockAbstractClientPlayerEntity {
    /**
     * Expose the player list entry for retrieving the model of this player
     */
    @Invoker("getPlayerInfo")
    PlayerInfo bedrockskinutility$getPlayerListEntry();
}
