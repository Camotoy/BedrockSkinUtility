package net.camotoy.bedrockskinutility.client.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractClientPlayerEntity.class)
public interface BedrockAbstractClientPlayerEntity {
    /**
     * Expose the player list entry for retrieving the model of this player
     */
    @Invoker("getPlayerInfo")
    PlayerListEntry bedrockskinutility$getPlayerListEntry();
}
