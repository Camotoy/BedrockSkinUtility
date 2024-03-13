package net.camotoy.bedrockskinutility.client.mixin;

import net.camotoy.bedrockskinutility.client.interfaces.BedrockPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerSkin.class)
public class PlayerSkinMixin implements BedrockPlayerSkin {
    private boolean bedrockSkin;
    private boolean bedrockCape;

    @Override
    public boolean bedrockskinutility$bedrockSkin() {
        return bedrockSkin;
    }

    @Override
    public boolean bedrockskinutility$bedrockCape() {
        return bedrockCape;
    }

    @Override
    public void bedrockskinutility$bedrockSkin(boolean bedrockSkin) {
        this.bedrockSkin = bedrockSkin;
    }

    @Override
    public void bedrockskinutility$bedrockCape(boolean bedrockCape) {
        this.bedrockCape = bedrockCape;
    }
}
