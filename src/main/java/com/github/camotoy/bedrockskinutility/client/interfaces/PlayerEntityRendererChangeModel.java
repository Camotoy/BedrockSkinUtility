package com.github.camotoy.bedrockskinutility.client.interfaces;

import com.github.camotoy.bedrockskinutility.client.BedrockPlayerEntityModel;
import net.minecraft.client.network.AbstractClientPlayerEntity;

public interface PlayerEntityRendererChangeModel {
    void bedrockskinutility$setModel(BedrockPlayerEntityModel<AbstractClientPlayerEntity> model);
}
