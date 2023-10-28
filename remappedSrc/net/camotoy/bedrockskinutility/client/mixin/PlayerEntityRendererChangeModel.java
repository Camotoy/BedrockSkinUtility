package net.camotoy.bedrockskinutility.client.mixin;

import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntityRenderer.class)
public interface PlayerEntityRendererChangeModel {
    @Accessor("model")
    void bedrockskinutility$setModel(EntityModel<?> model);
}
