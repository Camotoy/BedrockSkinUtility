package net.camotoy.bedrockskinutility.client.mixin;

import net.camotoy.bedrockskinutility.client.interfaces.BedrockPlayerListEntry;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.Entity;
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
        PlayerInfo playerListEntry = ((BedrockAbstractClientPlayerEntity) entity).bedrockskinutility$getPlayerListEntry();
        if (playerListEntry != null) {
            PlayerRenderer renderer = ((BedrockPlayerListEntry) playerListEntry).bedrockskinutility$getModel();
            if (renderer != null) {
                cir.setReturnValue(renderer);
            }
        }
    }
}
