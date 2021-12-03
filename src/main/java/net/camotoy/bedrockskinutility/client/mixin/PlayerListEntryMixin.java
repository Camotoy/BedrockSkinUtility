package net.camotoy.bedrockskinutility.client.mixin;

import net.camotoy.bedrockskinutility.client.interfaces.BedrockPlayerListEntry;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(PlayerInfo.class)
public abstract class PlayerListEntryMixin implements BedrockPlayerListEntry {

    @Shadow @Final private Map<MinecraftProfileTexture.Type, ResourceLocation> textureLocations;

    /**
     * The identifier pointing to the Bedrock cape sent from the server.
     */
    private ResourceLocation bedrockCape;

    private ResourceLocation bedrockSkin;

    private PlayerRenderer bedrockModel;

    @Override
    public ResourceLocation bedrockskinutility$getCape() {
        return bedrockCape;
    }

    @Override
    public PlayerRenderer bedrockskinutility$getModel() {
        return bedrockModel;
    }

    @Override
    public ResourceLocation bedrockskinutility$getSkin() {
        return bedrockSkin;
    }

    @Override
    public void bedrockskinutility$setCape(ResourceLocation identifier) {
        this.bedrockCape = identifier;
        // We don't need to set the ELYTRA texture - that appears to work as a first check, and then falls back to the cape
        this.textureLocations.put(MinecraftProfileTexture.Type.CAPE, identifier);
    }

    @Inject(method = "getCapeLocation", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/PlayerInfo;registerTextures()V"))
    public void bedrockskinutility$getCapeTexture(CallbackInfoReturnable<@Nullable ResourceLocation> cir) {
        // Don't overwrite existing capes
        if (this.bedrockCape != null && this.textureLocations.get(MinecraftProfileTexture.Type.CAPE) == null) {
            this.textureLocations.put(MinecraftProfileTexture.Type.CAPE, this.bedrockCape);
        }
    }

    @Inject(method = "getSkinLocation", at = @At("RETURN"), cancellable = true)
    public void bedrockskinutility$getSkinTexture(CallbackInfoReturnable<ResourceLocation> cir) {
        if (bedrockSkin != null) {
            cir.setReturnValue(bedrockSkin);
        }
    }

    @Override
    public void bedrockskinutility$setSkinProperties(ResourceLocation identifier, PlayerRenderer model) {
        this.textureLocations.put(MinecraftProfileTexture.Type.SKIN, identifier);
        this.bedrockModel = model;
        this.bedrockSkin = identifier;
    }
}
