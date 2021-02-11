package com.github.camotoy.bedrockskinutility.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeometryUtil {
    private final Logger logger;

    public GeometryUtil(Logger logger) {
        this.logger = logger;
    }

    public BedrockPlayerEntityModel<AbstractClientPlayerEntity> bedrockGeoToJava(SkinInfo info) {
        // Construct a list of all bones we need to translate
        List<JsonObject> bones = new ArrayList<>();
        try {
            String geometryName;
            if (info.getGeometry().get("format_version").getAsString().equals("1.8.0")) {
                geometryName = info.getGeometryName().getAsJsonObject("geometry").get("default").getAsString();
                for (JsonElement node : info.getGeometry().getAsJsonObject(geometryName).getAsJsonArray("bones")) {
                    bones.add(node.getAsJsonObject());
                }
            } else { // Seen with format_version 1.12.0
                geometryName = "minecraft:geometry";
                for (JsonElement node : info.getGeometry().getAsJsonArray(geometryName)) {
                    for (JsonElement subNode : node.getAsJsonObject().get("bones").getAsJsonArray()) {
                        bones.add(subNode.getAsJsonObject());
                    }
                }
            }
        } catch (Exception e) {
            this.logger.error("Error while parsing geometry!");
            e.printStackTrace();
            return null;
        }

        Map<String, PartInfo> stringToPart = new HashMap<>();
        try {
            // Create base model
            BedrockPlayerEntityModel<AbstractClientPlayerEntity> model = new BedrockPlayerEntityModel<>();
            model.textureHeight = info.getHeight();
            model.textureWidth = info.getWidth();
            for (JsonObject bone : bones) {
                // Iterate through all bones
                String name = bone.get("name").getAsString();

                JsonElement jsonParent = bone.get("parent");
                JsonObject parentPart = null;
                String parent = null;
                if (jsonParent != null) {
                    // Search through all bones to find the parent part
                    parent = jsonParent.getAsString();
                    for (JsonObject otherBone : bones) {
                        if (parent.equals(otherBone.get("name").getAsString())) {
                            parentPart = otherBone;
                            break;
                        }
                    }
                }

                ModelPart part = new ModelPart(model);
                JsonArray pivot = bone.getAsJsonArray("pivot");
                float pivotX = pivot.get(0).getAsFloat();
                float pivotY = pivot.get(1).getAsFloat();
                float pivotZ = pivot.get(2).getAsFloat();
                if (parentPart != null) {
                    // This appears to be a difference between Bedrock and Java - pivots are carried over for us
                    JsonArray parentPivot = parentPart.getAsJsonArray("pivot");
                    part.setPivot(pivotX - parentPivot.get(0).getAsFloat(),
                            pivotY - parentPivot.get(1).getAsFloat(),
                            pivotZ - parentPivot.get(2).getAsFloat());
                } else {
                    part.setPivot(pivotX, pivotY, pivotZ);
                }

                JsonArray cubes = bone.getAsJsonArray("cubes");
                if (cubes != null) {
                    for (JsonElement node : cubes) {
                        JsonObject cube = node.getAsJsonObject();
                        boolean mirrored = cube.get("mirror").getAsBoolean();
                        JsonArray origin = cube.getAsJsonArray("origin");
                        float originX = origin.get(0).getAsFloat();
                        float originY = origin.get(1).getAsFloat();
                        float originZ = origin.get(2).getAsFloat();
                        JsonArray size = cube.getAsJsonArray("size");
                        float sizeX = size.get(0).getAsFloat();
                        float sizeY = size.get(1).getAsFloat();
                        float sizeZ = size.get(2).getAsFloat();
                        JsonArray uv = cube.getAsJsonArray("uv");
                        float inflate = cube.get("inflate").getAsFloat();
                        // I didn't use the below, but it may be a helpful reference in the future
                        // The Y needs to be inverted, for whatever reason
                        // https://github.com/JannisX11/blockbench/blob/8529c0adee8565f8dac4b4583c3473b60679966d/js/transform.js#L148https://github.com/JannisX11/blockbench/blob/8529c0adee8565f8dac4b4583c3473b60679966d/js/transform.js#L148
                        part.setTextureOffset((int) uv.get(0).getAsFloat(), (int) uv.get(1).getAsFloat())
                                .addCuboid((originX - pivotX), (((originY + sizeY) * -1) + pivotY), (originZ - pivotZ), // probably Z too
                                        sizeX, sizeY, sizeZ, inflate, mirrored);
                    }
                }

                boolean needsParent = false;

                switch (name) { // Also do this with the overlays? Those are final, though.
                    case "head":
                        model.head = part;
                        break;
                    case "hat":
                        model.helmet = part;
                        break;
                    case "body":
                        model.torso = part;
                        break;
                    case "leftArm":
                        model.leftArm = part;
                        break;
                    case "rightArm":
                        model.rightArm = part;
                        break;
                    case "leftLeg":
                        model.leftLeg = part;
                        break;
                    case "rightLeg":
                        model.rightLeg = part;
                        break;
                    default:
                        needsParent = true;
                        break;
                }

                stringToPart.put(name, new PartInfo(needsParent, parent, part));
            }

            for (Map.Entry<String, PartInfo> entry : stringToPart.entrySet()) {
                if (entry.getValue().needsParent) {
                    if (entry.getValue().parent != null) {
                        PartInfo parentPart = stringToPart.get(entry.getValue().parent);
                        if (parentPart != null) {
                            parentPart.part.addChild(entry.getValue().part);
                        }
                    }
                }
            }

            return model;
        } catch (Exception e) {
            this.logger.error("Error while parsing geometry into model!");
            e.printStackTrace();
            return null;
        }
    }

    private static class PartInfo {
        public final boolean needsParent;
        public final String parent;
        public final ModelPart part;

        public PartInfo(boolean needsParent, String parent, ModelPart part) {
            this.needsParent = needsParent;
            this.parent = parent;
            this.part = part;
        }

        @Override
        public String toString() {
            return "PartInfo{" +
                    "parent='" + parent + '\'' +
                    ", part=" + part +
                    '}';
        }
    }
}
