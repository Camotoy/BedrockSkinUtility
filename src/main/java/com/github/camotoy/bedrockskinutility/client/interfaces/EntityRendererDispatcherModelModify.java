package com.github.camotoy.bedrockskinutility.client.interfaces;

import net.minecraft.client.render.entity.PlayerEntityRenderer;

public interface EntityRendererDispatcherModelModify {
    void addPlayerModel(String id, PlayerEntityRenderer renderer);

    void removePlayerModel(String id);
}
