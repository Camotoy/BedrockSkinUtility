package net.camotoy.bedrockskinutility.client.mixin;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CapeFeatureRenderer.class)
public class CapeFeatureRendererMixin {
    @Redirect(
            method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/player/AbstractClientPlayer;FFFFFF)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/RenderType;entitySolid(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"),
            require = 0 // Fail safely if other mods overwrite this
    )
    public RenderLayer solidToTranslucent(Identifier texture) {
        if (texture.getNamespace().equals("geyserskinmanager")) {
            // Capes can be translucent in Bedrock
            return RenderLayer.getEntityTranslucent(texture, true);
        }
        return RenderLayer.getEntitySolid(texture);
    }
}
