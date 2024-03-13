package net.camotoy.bedrockskinutility.client.mixin;

import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.PlayerSkin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Supplier;

@Mixin(PlayerInfo.class)
public interface PlayerSkinFieldAccessor {
    @Accessor("skinLookup")
    @Mutable
    void setPlayerSkin(Supplier<PlayerSkin> playerSkin);
}
