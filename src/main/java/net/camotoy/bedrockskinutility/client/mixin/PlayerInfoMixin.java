package net.camotoy.bedrockskinutility.client.mixin;

import net.camotoy.bedrockskinutility.client.interfaces.BedrockPlayerInfo;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerInfo.class)
public class PlayerInfoMixin implements BedrockPlayerInfo {
    private PlayerRenderer bedrockModel;

    @Override
    public PlayerRenderer bedrockskinutility$getModel() {
        return this.bedrockModel;
    }

    @Override
    public void bedrockskinutility$setModel(PlayerRenderer renderer) {
        this.bedrockModel = renderer;
    }
}
