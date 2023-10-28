package net.camotoy.bedrockskinutility.client;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;

public class BedrockPlayerEntityModel<T extends LivingEntity> extends PlayerEntityModel<T> {
    public BedrockPlayerEntityModel(ModelPart root) {
        super(root, false);
    }
}
