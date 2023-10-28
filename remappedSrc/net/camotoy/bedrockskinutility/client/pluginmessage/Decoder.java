package net.camotoy.bedrockskinutility.client.pluginmessage;

import net.camotoy.bedrockskinutility.client.SkinManager;
import net.camotoy.bedrockskinutility.client.SkinUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.ColorHelper;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;

public abstract class Decoder {
    protected final Logger logger;
    protected final SkinManager skinManager;

    public Decoder(Logger logger, SkinManager skinManager) {
        this.logger = logger;
        this.skinManager = skinManager;
    }

    public abstract void decode(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf);

    /**
     * Read a string in a platform-friendly way - Mojang's way uses VarInts which aren't easily implemented on our end.
     */
    protected String readString(PacketByteBuf buf) {
        int length = buf.readInt();
        String result = buf.toString(buf.readerIndex(), length, StandardCharsets.UTF_8);
        buf.readerIndex(buf.readerIndex() + length);
        return result;
    }

    protected NativeImage toNativeImage(byte[] data, int width, int height) {
        BufferedImage bufferedImage = SkinUtils.toBufferedImage(data, width, height);

        NativeImage nativeImage = new NativeImage(width, height, true);
        for (int currentWidth = 0; currentWidth < width; currentWidth++) {
            for (int currentHeight = 0; currentHeight < height; currentHeight++) {
                int rgba = bufferedImage.getRGB(currentWidth, currentHeight);
                nativeImage.setColor(currentWidth, currentHeight, ColorHelper.Argb.getArgb(
                        (rgba >> 24) & 0xFF, rgba & 0xFF, (rgba >> 8) & 0xFF, (rgba >> 16) & 0xFF));
            }
        }
        return nativeImage;
    }
}
