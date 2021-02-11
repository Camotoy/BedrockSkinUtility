package com.github.camotoy.bedrockskinutility.client.interfaces;

import net.minecraft.util.Identifier;

public interface BedrockPlayerListEntry {
    Identifier bedrockskinutility$getCape();

    String bedrockskinutility$getModel();

    Identifier bedrockskinutility$getSkin();

    void bedrockskinutility$setCape(Identifier identifier);

    void bedrockskinutility$setSkinProperties(Identifier identifier, String model);
}
