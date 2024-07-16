package net.camotoy.bedrockskinutility.client;

import net.camotoy.bedrockskinutility.client.pluginmessage.BedrockMessageHandler;
import net.camotoy.bedrockskinutility.client.pluginmessage.GeyserSkinManagerListener;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class BedrockSkinUtilityClient implements ClientModInitializer {
    private final Logger logger = LogManager.getLogger("BedrockSkinUtility");

    @Override
    public void onInitializeClient() {
        logger.info("Hello from BedrockClientSkinUtility!");

        var handler = new BedrockMessageHandler(logger, new SkinManager());
        PayloadTypeRegistry.playS2C().register(GeyserSkinManagerListener.TYPE, GeyserSkinManagerListener.STREAM_CODEC);
        ClientPlayNetworking.registerGlobalReceiver(GeyserSkinManagerListener.TYPE, (payload, context) -> payload.handle(context, handler));
    }
}
