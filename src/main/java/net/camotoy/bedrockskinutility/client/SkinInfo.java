package net.camotoy.bedrockskinutility.client;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

public class SkinInfo {
    private final int width;
    private final int height;
    private final JsonObject geometry;
    private final JsonObject geometryName;
    private final byte[][] skinData;

    public SkinInfo(int width, int height, JsonObject geometry, JsonObject geometryName, int chunkCount) {
        this.width = width;
        this.height = height;
        this.geometry = geometry;
        this.geometryName = geometryName;
        this.skinData = new byte[chunkCount][];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Nullable
    public JsonObject getGeometry() {
        return geometry;
    }

    @Nullable
    public JsonObject getGeometryName() {
        return geometryName;
    }

    /**
     * Should the skin data be sent to us through multiple plugin messages, assemble it.
     */
    public byte[] getData() {
        if (skinData.length == 1) {
            // No concatenation needed
            return skinData[0];
        }

        int totalLength = 0;
        for (byte[] data : skinData) {
            totalLength += data.length;
        }
        byte[] totalData = new byte[totalLength];
        int currentIndex = 0;
        for (byte[] currentData : skinData) {
            // Copy all arrays to one array
            System.arraycopy(currentData, 0, totalData, currentIndex, currentData.length);
            currentIndex += currentData.length;
        }
        return totalData;
    }

    public void setData(byte[] data, int chunk) {
        this.skinData[chunk] = data;
    }

    public boolean isComplete() {
        for (byte[] data : skinData) {
            if (data == null) {
                return false;
            }
        }
        return true;
    }
}
