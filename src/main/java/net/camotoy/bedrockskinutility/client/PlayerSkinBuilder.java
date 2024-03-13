package net.camotoy.bedrockskinutility.client;

import net.camotoy.bedrockskinutility.client.interfaces.BedrockPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;

public final class PlayerSkinBuilder {
    public ResourceLocation texture;
    public String textureUrl;
    public ResourceLocation capeTexture;
    public ResourceLocation elytraTexture;
    public PlayerSkin.Model model;
    public boolean secure;
    public boolean bedrockSkin;
    public boolean bedrockCape;

    public PlayerSkinBuilder(final PlayerSkin base) {
        this.texture = base.texture();
        this.textureUrl = base.textureUrl();
        this.capeTexture = base.capeTexture();
        this.elytraTexture = base.elytraTexture();
        this.model = base.model();
        this.secure = base.secure();
        this.bedrockSkin = ((BedrockPlayerSkin) (Object) base).bedrockskinutility$bedrockSkin();
        this.bedrockCape = ((BedrockPlayerSkin) (Object) base).bedrockskinutility$bedrockCape();
    }

    public PlayerSkin build() {
        final PlayerSkin playerSkin = new PlayerSkin(
                texture,
                textureUrl,
                capeTexture,
                elytraTexture,
                model,
                secure
        );
        ((BedrockPlayerSkin) (Object) playerSkin).bedrockskinutility$bedrockSkin(bedrockSkin);
        ((BedrockPlayerSkin) (Object) playerSkin).bedrockskinutility$bedrockCape(bedrockCape);
        return playerSkin;
    }
}
