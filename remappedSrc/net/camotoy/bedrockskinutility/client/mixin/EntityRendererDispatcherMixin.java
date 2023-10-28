package net.camotoy.bedrockskinutility.client.mixin;

import net.camotoy.bedrockskinutility.client.interfaces.BedrockPlayerListEntry;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRendererDispatcherMixin {

    @Inject(
            method = "getRenderer",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/player/AbstractClientPlayer;getModelName()Ljava/lang/String;"),
            cancellable = true
    )
    public void getRenderer(Entity entity, CallbackInfoReturnable<EntityRenderer<?>> cir) {
        PlayerListEntry playerListEntry = ((BedrockAbstractClientPlayerEntity) entity).bedrockskinutility$getPlayerListEntry();
        if (playerListEntry != null) {
            PlayerEntityRenderer renderer = ((BedrockPlayerListEntry) playerListEntry).bedrockskinutility$getModel();
            if (renderer != null) {
                cir.setReturnValue(renderer);
            }
        }
    }
}
