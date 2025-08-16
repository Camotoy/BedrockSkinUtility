package net.camotoy.bedrockskinutility.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class BedrockPlayerRenderer extends PlayerRenderer {
    private final ResourceLocation texture;

    public BedrockPlayerRenderer(EntityRendererProvider.Context context, boolean bl, ResourceLocation texture) {
        super(context, bl);
        this.texture = texture;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(PlayerRenderState playerRenderState) {
        return this.texture;
    }
}