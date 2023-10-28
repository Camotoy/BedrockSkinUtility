package net.camotoy.bedrockskinutility.client;

import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.util.Identifier;

/**
 * All cached properties of a player, if their PlayerListPacket has not sent or is being reloaded
 */
public class BedrockCachedProperties {
    public Identifier cape;
    public PlayerEntityRenderer model;
    public Identifier skin;
}
