package com.github.camotoy.bedrockskinutility.client.interfaces;

import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;

public interface BedrockPlayerListEntry {
    ResourceLocation bedrockskinutility$getCape();

    PlayerRenderer bedrockskinutility$getModel();

    ResourceLocation bedrockskinutility$getSkin();

    void bedrockskinutility$setCape(ResourceLocation identifier);

    void bedrockskinutility$setSkinProperties(ResourceLocation identifier, PlayerRenderer model);
}
