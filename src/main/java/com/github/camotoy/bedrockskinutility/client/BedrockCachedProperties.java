package com.github.camotoy.bedrockskinutility.client;

import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * All cached properties of a player, if their PlayerListPacket has not sent or is being reloaded
 */
public class BedrockCachedProperties {
    public ResourceLocation cape;
    public PlayerRenderer model;
    public ResourceLocation skin;
}
