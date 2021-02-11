package com.github.camotoy.bedrockskinutility.client.mixin;

import com.github.camotoy.bedrockskinutility.client.BedrockPlayerEntityModel;
import com.github.camotoy.bedrockskinutility.client.interfaces.PlayerEntityRendererChangeModel;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> implements PlayerEntityRendererChangeModel {
    public PlayerEntityRendererMixin(EntityRenderDispatcher dispatcher, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(dispatcher, model, shadowRadius);
    }

    @Override
    public void bedrockskinutility$setModel(BedrockPlayerEntityModel<AbstractClientPlayerEntity> model) {
        this.model = model;
    }
}
