package com.github.camotoy.bedrockskinutility.client.mixin;

import com.github.camotoy.bedrockskinutility.client.interfaces.BedrockPlayerListEntry;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(PlayerListEntry.class)
public abstract class PlayerListEntryMixin implements BedrockPlayerListEntry {

    @Shadow @Final private Map<MinecraftProfileTexture.Type, Identifier> textures;

    /**
     * The identifier pointing to the Bedrock cape sent from the server.
     */
    private Identifier bedrockCape;

    private Identifier bedrockSkin;

    private PlayerEntityRenderer bedrockModel;

    @Override
    public Identifier bedrockskinutility$getCape() {
        return bedrockCape;
    }

    @Override
    public PlayerEntityRenderer bedrockskinutility$getModel() {
        return bedrockModel;
    }

    @Override
    public Identifier bedrockskinutility$getSkin() {
        return bedrockSkin;
    }

    @Override
    public void bedrockskinutility$setCape(Identifier identifier) {
        this.bedrockCape = identifier;
        // We don't need to set the ELYTRA texture - that appears to work as a first check, and then falls back to the cape
        this.textures.put(MinecraftProfileTexture.Type.CAPE, identifier);
    }

    @Inject(method = "getCapeTexture", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/PlayerListEntry;loadTextures()V"))
    public void bedrockskinutility$getCapeTexture(CallbackInfoReturnable<@Nullable Identifier> cir) {
        // Don't overwrite existing capes
        if (this.bedrockCape != null && this.textures.get(MinecraftProfileTexture.Type.CAPE) == null) {
            this.textures.put(MinecraftProfileTexture.Type.CAPE, this.bedrockCape);
        }
    }

    @Inject(method = "getSkinTexture", at = @At("RETURN"), cancellable = true)
    public void bedrockskinutility$getSkinTexture(CallbackInfoReturnable<Identifier> cir) {
        if (bedrockSkin != null) {
            cir.setReturnValue(bedrockSkin);
        }
    }

    @Override
    public void bedrockskinutility$setSkinProperties(Identifier identifier, PlayerEntityRenderer model) {
        this.textures.put(MinecraftProfileTexture.Type.SKIN, identifier);
        this.bedrockModel = model;
        this.bedrockSkin = identifier;
    }
}
