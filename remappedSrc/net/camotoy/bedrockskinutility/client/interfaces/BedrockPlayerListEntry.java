package net.camotoy.bedrockskinutility.client.interfaces;

import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.util.Identifier;

public interface BedrockPlayerListEntry {
    Identifier bedrockskinutility$getCape();

    PlayerEntityRenderer bedrockskinutility$getModel();

    Identifier bedrockskinutility$getSkin();

    void bedrockskinutility$setCape(Identifier identifier);

    void bedrockskinutility$setSkinProperties(Identifier identifier, PlayerEntityRenderer model);
}
