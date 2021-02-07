package com.github.camotoy.bedrockskinutility.client.pluginmessage;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Logger;

public class GeyserSkinManagerInitListener implements ClientPlayNetworking.PlayChannelHandler {
    private static final Identifier CHANNEL = new Identifier("geyserskin", "init");

    public static final Identifier INIT = new Identifier("bedrockskin", "init");
    public static final int VERSION = 1;

    private final Logger logger;

    public GeyserSkinManagerInitListener(Logger logger) {
        this.logger = logger;
    }

    public void register() {
        ClientPlayNetworking.registerGlobalReceiver(CHANNEL, this);
    }

    public void unregister() {
        ClientPlayNetworking.unregisterGlobalReceiver(CHANNEL);
    }

    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        this.logger.info("Initial plugin message received from server!");

        client.submit(() -> {
            if (client.player != null && client.player.getUuid() != null) {
                sendInitPacket(client);
            } else {
                SEND_INIT_PACKET = true;
            }
        });
    }

    /**
     * Whether we need to send the init packet later, because the UUID is currently null
     */
    public static boolean SEND_INIT_PACKET = false;

    public static void sendInitPacket(MinecraftClient client) {
        PacketByteBuf responseBuf = new PacketByteBuf(Unpooled.buffer());
        responseBuf.writeInt(VERSION);
        // Needed for BungeeCord
        responseBuf.writeUuid(client.player.getUuid());
        client.getNetworkHandler().getConnection().send(new CustomPayloadC2SPacket(INIT, responseBuf));
    }
}
