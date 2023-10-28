package net.camotoy.bedrockskinutility.client.mixin;

import net.camotoy.bedrockskinutility.client.interfaces.BedrockPlayerListEntry;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListEntry.class)
public abstract class PlayerListEntryMixin implements BedrockPlayerListEntry {
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
    }

    @Inject(method = "getCapeLocation", at = @At("RETURN"), cancellable = true)
    public void bedrockskinutility$getCapeTexture(CallbackInfoReturnable<@Nullable Identifier> cir) {
        // Don't overwrite existing capes
        if (cir.getReturnValue() == null && this.bedrockCape != null) {
            cir.setReturnValue(this.bedrockCape);
        }
    }

    @Inject(method = "getSkinLocation", at = @At("HEAD"), cancellable = true)
    public void bedrockskinutility$getSkinTexture(CallbackInfoReturnable<Identifier> cir) {
        if (bedrockSkin != null) {
            cir.setReturnValue(bedrockSkin);
        }
    }

    @Override
    public void bedrockskinutility$setSkinProperties(Identifier identifier, PlayerEntityRenderer model) {
        this.bedrockModel = model;
        this.bedrockSkin = identifier;
    }
}
