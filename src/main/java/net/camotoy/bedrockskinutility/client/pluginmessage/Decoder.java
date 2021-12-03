package net.camotoy.bedrockskinutility.client.pluginmessage;

import net.camotoy.bedrockskinutility.client.SkinManager;
import net.camotoy.bedrockskinutility.client.SkinUtils;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
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

    public abstract void decode(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf);

    /**
     * Read a string in a platform-friendly way - Mojang's way uses VarInts which aren't easily implemented on our end.
     */
    protected String readString(FriendlyByteBuf buf) {
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
                nativeImage.setPixelRGBA(currentWidth, currentHeight, NativeImage.combine(
                        (rgba >> 24) & 0xFF, rgba & 0xFF, (rgba >> 8) & 0xFF, (rgba >> 16) & 0xFF));
            }
        }
        return nativeImage;
    }
}
