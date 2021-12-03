package net.camotoy.bedrockskinutility.client.pluginmessage;

import net.camotoy.bedrockskinutility.client.SkinInfo;
import net.camotoy.bedrockskinutility.client.SkinManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class SkinInfoDecoder extends Decoder {

    public SkinInfoDecoder(Logger logger, SkinManager skinManager) {
        super(logger, skinManager);
    }

    @Override
    public void decode(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf) {
        try { // We wouldn't want to crash now, would we? :)
            int version = buf.readInt();
            if (version != 1) { // Version 2 is probably going to be reserved for persona skins
                this.logger.error("Could not load skin info! Is the mod and plugin updated?");
                return;
            }

            UUID playerUuid = new UUID(buf.readLong(), buf.readLong());

            int skinWidth = buf.readInt();
            int skinHeight = buf.readInt();

            String geometry = null;
            JsonObject jsonGeometry = null;
            JsonObject jsonGeometryName = null;

            if (buf.readBoolean()) { // is geometry present
                try {
                    geometry = readString(buf);
                    jsonGeometry = JsonParser.parseString(geometry).getAsJsonObject();
                    jsonGeometryName = JsonParser.parseString(readString(buf)).getAsJsonObject();
                } catch (Exception e) {
                    this.logger.error("Error while trying to decode geometry!", e);
                    return;
                }
            }

            this.logger.debug(geometry);

            int chunkCount = buf.readInt();

            skinManager.getSkinInfo().put(playerUuid, new SkinInfo(skinWidth, skinHeight, jsonGeometry,
                    jsonGeometryName, chunkCount));
        } catch (Exception e) {
            this.logger.error("Error while trying to load in skin data!");
            e.printStackTrace();
        }
    }
}
