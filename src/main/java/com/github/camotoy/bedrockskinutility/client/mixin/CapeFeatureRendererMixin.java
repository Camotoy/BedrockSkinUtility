package com.github.camotoy.bedrockskinutility.client.mixin;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CapeFeatureRenderer.class)
public class CapeFeatureRendererMixin {
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getEntitySolid(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"))
    public RenderLayer solidToTranslucent(Identifier texture) {
        if (texture.getNamespace().equals("geyserskinmanager")) {
            // Capes can be translucent in Bedrock
            return RenderLayer.getEntityTranslucent(texture, true);
        }
        return RenderLayer.getEntitySolid(texture);
    }
}
