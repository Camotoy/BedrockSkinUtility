package net.camotoy.bedrockskinutility.client;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MixinConfigPlugin implements IMixinConfigPlugin {

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.equals("net.camotoy.bedrockskinutility.client.mixin.CapeFeatureRendererMixin")) {
            boolean capes = FabricLoader.getInstance().getModContainer("capes").isPresent()
                    || FabricLoader.getInstance().getModContainer("cosmetica").isPresent();
            if (capes) {
                // these mods have Mixins that just set all capes to transparent, so we don't need this Mixin
                LoggerFactory.getLogger(MixinConfigPlugin.class).info("Disabling transparent cape mixin in BedrockSkinUtility as another cape-related mod is also installed.");
                return false;
            }
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
