package com.github.camotoy.bedrockskinutility.client.mixin;

import com.github.camotoy.bedrockskinutility.client.interfaces.EntityRendererDispatcherModelModify;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRendererDispatcherMixin implements EntityRendererDispatcherModelModify {

    @Shadow
    private Map<String, PlayerEntityRenderer> modelRenderers;

    @Override
    public void addPlayerModel(String id, PlayerEntityRenderer renderer) {
        // TODO don't remake the immutable map every time?
        ImmutableMap.Builder<String, PlayerEntityRenderer> builder = ImmutableMap.builder();
        for (Map.Entry<String, PlayerEntityRenderer> entry : this.modelRenderers.entrySet()) {
            if (!entry.getKey().equals(id)) {
                builder.put(entry.getKey(), entry.getValue());
            }
        }
        builder.put(id, renderer);
        this.modelRenderers = builder.build();
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
