package net.camotoy.bedrockskinutility.client;

import com.google.common.collect.Maps;
import net.camotoy.bedrockskinutility.client.interfaces.BedrockModelPart;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.Direction;
import org.cube.converter.model.element.Cube;
import org.cube.converter.model.element.Parent;
import org.cube.converter.model.impl.bedrock.BedrockGeometryModel;
import org.cube.converter.util.element.Position3V;
import org.cube.converter.util.element.UVMap;
import org.joml.Vector3f;

import java.util.*;

public class GeometryUtil {
    private static final List<String> LEG_RELATED = List.of("leftleg", "rightleg");

    public static BedrockPlayerEntityModel<AbstractClientPlayer> bedrockGeoToJava(BedrockGeometryModel geometry) {
        // There are some times when the skin image file is larger than the geometry UV points.
        // In this case, we need to scale UV calls
        // https://github.com/Camotoy/BedrockSkinUtility/issues/9
        final float uvWidth = geometry.getTextureSize().getX();
        final float uvHeight = geometry.getTextureSize().getY();

        final Map<String, PartInfo> stringToPart = new HashMap<>();
        for (final Parent bone : geometry.getParents()) {
            final Map<String, ModelPart> children = Maps.newHashMap();
            final ModelPart part = new ModelPart(List.of(), children);
            // Arm and leg
            boolean neededOffset = switch (bone.getName().toLowerCase(Locale.ROOT)) {
                case "rightarm", "leftarm" -> true;
                default -> false;
            };

            ((BedrockModelPart)((Object)part)).bedrockskinutility$setBedrockModel();
            ((BedrockModelPart)((Object)part)).bedrockskinutility$setNeededOffset(neededOffset);
            ((BedrockModelPart)((Object)part)).bedrockskinutility$setAngles(new Vector3f(bone.getRotation().getX() , bone.getRotation().getY(), bone.getRotation().getZ()));

            boolean leg = LEG_RELATED.contains(bone.getName().toLowerCase(Locale.ROOT));
            if (leg) {
                part.setPos(0, bone.getPivot().getY(), 0);
                part.setInitialPose(part.storePose());
            } else {
                ((BedrockModelPart)((Object)part)).bedrockskinutility$setPivot(new Vector3f(bone.getPivot().getX(), -bone.getPivot().getY() + 24.016F, bone.getPivot().getZ()));
            }

            // Java don't allow individual cubes to have their own rotation therefore, we have to separate each cube into ModelPart to be able to rotate.
            for (final Cube cube : bone.getCubes().values()) {
                final Position3V pos = cube.getPosition();

                final float sizeX = cube.getSize().getX(), sizeY = cube.getSize().getY(), sizeZ = cube.getSize().getZ();
                final float inflate = cube.getInflate();

                final UVMap uvMap = cube.getUvMap().clone();

                final Set<Direction> set = new HashSet<>();
                for (final Direction direction : Direction.values()) {
                    if (uvMap.getMap().containsKey(org.cube.converter.util.element.Direction.values()[direction.ordinal()])) {
                        set.add(direction);
                    }
                }

                final ModelPart.Cube cuboid = new ModelPart.Cube(0, 0, pos.getX(), leg ? pos.getY() : -(pos.getY() - 24.016F + sizeY), pos.getZ(), sizeX, sizeY, sizeZ, inflate, inflate, inflate, cube.isMirror(), uvWidth, uvHeight, set);
                applyUVMap(cuboid, set, uvMap, uvWidth, uvHeight, cube.getInflate(), cube.isMirror());

                final ModelPart cubePart = new ModelPart(List.of(cuboid), Map.of());
                ((BedrockModelPart)((Object)cubePart)).bedrockskinutility$setPivot(new Vector3f(cube.getPivot().getX(), -cube.getPivot().getY() + 24.016F, cube.getPivot().getZ()));
                ((BedrockModelPart)((Object)cubePart)).bedrockskinutility$setAngles(new Vector3f(cube.getRotation().getX(), cube.getRotation().getY(), cube.getRotation().getZ()));
                ((BedrockModelPart)((Object)cubePart)).bedrockskinutility$setBedrockModel();
                ((BedrockModelPart)((Object)cubePart)).bedrockskinutility$setNeededOffset(neededOffset);
                children.put(cube.getParent() + cube.hashCode(), cubePart);
            }

            String parent = bone.getParent();
            String name = bone.getName();
            switch (name.toLowerCase(Locale.ROOT)) { // Also do this with the overlays? Those are final, though.
                case "head", "rightarm", "body", "leftarm", "leftleg", "rightleg" -> parent = "root";
            }

            stringToPart.put(adjustFormatting(name), new PartInfo(adjustFormatting(parent), part, children));
        }

        PartInfo root = stringToPart.get("root");
        if (root == null) {
            final Map<String, ModelPart> rootParts = Maps.newHashMap();
            stringToPart.put("root", root = new PartInfo("", new ModelPart(List.of(), rootParts), rootParts));
        }

        for (Map.Entry<String, PartInfo> entry : stringToPart.entrySet()) {
            if (entry.getValue().parent.isBlank() && entry.getValue().part() != root.part) {
                root.children.put(entry.getKey(), entry.getValue().part());
                continue;
            }

            PartInfo parentPart = stringToPart.get(entry.getValue().parent);
            if (parentPart != null) {
                parentPart.children.put(entry.getKey(), entry.getValue().part);
            }
        }

        return new BedrockPlayerEntityModel<>(root.part());
    }

    private static String adjustFormatting(String name) {
        if (name == null) {
            return null;
        }

        return switch (name.toLowerCase(Locale.ROOT)) {
            case "leftarm" -> "left_arm";
            case "rightarm" -> "right_arm";
            case "leftleg" -> "left_leg";
            case "rightleg" -> "right_leg";
            case "leftpants" -> "left_pants";
            case "rightpants" -> "right_pants";
            default -> name.toLowerCase(Locale.ROOT);
        };
    }

    private record PartInfo(String parent, ModelPart part, Map<String, ModelPart> children) {
    }

    private static void applyUVMap(final ModelPart.Cube cuboid, final Set<Direction> set, final UVMap map, final float uvWidth, final float uvHeight, final float inflate, final boolean mirror) {
        float x = cuboid.minX, y = cuboid.minY, z = cuboid.minZ;
        float f = cuboid.maxX, g = cuboid.maxY, h = cuboid.maxZ;

        x -= inflate;
        y -= inflate;
        z -= inflate;
        f += inflate;
        g += inflate;
        h += inflate;

        if (mirror) {
            float i = f;
            f = x;
            x = i;
        }

        ModelPart.Vertex vertex = new ModelPart.Vertex(x, y, z, 0.0F, 0.0F);
        ModelPart.Vertex vertex2 = new ModelPart.Vertex(f, y, z, 0.0F, 8.0F);
        ModelPart.Vertex vertex3 = new ModelPart.Vertex(f, g, z, 8.0F, 8.0F);
        ModelPart.Vertex vertex4 = new ModelPart.Vertex(x, g, z, 8.0F, 0.0F);
        ModelPart.Vertex vertex5 = new ModelPart.Vertex(x, y, h, 0.0F, 0.0F);
        ModelPart.Vertex vertex6 = new ModelPart.Vertex(f, y, h, 0.0F, 8.0F);
        ModelPart.Vertex vertex7 = new ModelPart.Vertex(f, g, h, 8.0F, 8.0F);
        ModelPart.Vertex vertex8 = new ModelPart.Vertex(x, g, h, 8.0F, 0.0F);

        final ModelPart.Polygon[] sides = cuboid.polygons;
        int s = 0;

        if (set.contains(Direction.DOWN)) {
            final Float[] uv = map.getMap().get(org.cube.converter.util.element.Direction.DOWN);
            sides[s++] = new ModelPart.Polygon(new ModelPart.Vertex[]{vertex6, vertex5, vertex, vertex2}, uv[0], uv[1], uv[2], uv[3], uvWidth, uvHeight, mirror, Direction.DOWN);
        }

        if (set.contains(Direction.UP)) {
            final Float[] uv = map.getMap().get(org.cube.converter.util.element.Direction.UP);
            sides[s++] = new ModelPart.Polygon(new ModelPart.Vertex[]{vertex3, vertex4, vertex8, vertex7}, uv[0], uv[1], uv[2], uv[3], uvWidth, uvHeight, mirror, Direction.UP);
        }

        if (set.contains(Direction.WEST)) {
            final Float[] uv = map.getMap().get(org.cube.converter.util.element.Direction.WEST);
            sides[s++] = new ModelPart.Polygon(new ModelPart.Vertex[]{vertex, vertex5, vertex8, vertex4}, uv[0], uv[1], uv[2], uv[3], uvWidth, uvHeight, mirror, Direction.WEST);
        }

        if (set.contains(Direction.NORTH)) {
            final Float[] uv = map.getMap().get(org.cube.converter.util.element.Direction.NORTH);
            sides[s++] = new ModelPart.Polygon(new ModelPart.Vertex[]{vertex2, vertex, vertex4, vertex3}, uv[0], uv[1], uv[2], uv[3], uvWidth, uvHeight, mirror, Direction.NORTH);
        }

        if (set.contains(Direction.EAST)) {
            final Float[] uv = map.getMap().get(org.cube.converter.util.element.Direction.EAST);
            sides[s++] = new ModelPart.Polygon(new ModelPart.Vertex[]{vertex6, vertex2, vertex3, vertex7}, uv[0], uv[1], uv[2], uv[3], uvWidth, uvHeight, mirror, Direction.EAST);
        }

        if (set.contains(Direction.SOUTH)) {
            final Float[] uv = map.getMap().get(org.cube.converter.util.element.Direction.SOUTH);
            sides[s] = new ModelPart.Polygon(new ModelPart.Vertex[]{vertex5, vertex6, vertex7, vertex8}, uv[0], uv[1], uv[2], uv[3], uvWidth, uvHeight, mirror, Direction.SOUTH);
        }
    }
}
