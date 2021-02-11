package com.github.camotoy.bedrockskinutility.client;

import com.github.camotoy.bedrockskinutility.client.pluginmessage.GeyserSkinManagerListener;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Environment(EnvType.CLIENT)
public class BedrockSkinUtilityClient implements ClientModInitializer {
    private final Logger logger = LogManager.getLogger("BedrockSkinUtility");

    private GeyserSkinManagerListener pluginMessageListener;

    @Override
    public void onInitializeClient() {
        logger.info("Hello from BedrockClientSkinUtility!");

        this.pluginMessageListener = new GeyserSkinManagerListener(this.logger, new SkinManager(this.logger));
        this.pluginMessageListener.register();

        ClientLifecycleEvents.CLIENT_STOPPING.register((client -> this.pluginMessageListener.unregister()));
    }
}
