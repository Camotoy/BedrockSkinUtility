package com.github.camotoy.bedrockskinutility.client.mixin;

import com.github.camotoy.bedrockskinutility.client.interfaces.EntityRendererDispatcherModelModify;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRendererDispatcherMixin implements EntityRendererDispatcherModelModify {

    @Shadow @Final private Map<String, PlayerEntityRenderer> modelRenderers;

    @Override
    public void addPlayerModel(String id, PlayerEntityRenderer renderer) {
        this.modelRenderers.put(id, renderer);
    }

    // currently doesn't trigger - fix this
    @Override
    public void removePlayerModel(String id) {
        if ("alex".equals(id) || "steve".equals(id)) {
            // Sanity check
            return;
        }
        this.modelRenderers.remove(id);
    }
}
