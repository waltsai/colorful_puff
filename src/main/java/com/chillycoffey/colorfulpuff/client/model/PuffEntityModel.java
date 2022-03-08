package com.chillycoffey.colorfulpuff.client.model;

import com.chillycoffey.colorfulpuff.entity.mob.PuffBaseEntity;
import com.google.common.collect.Iterables;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class PuffEntityModel<T extends PuffBaseEntity> extends PlayerEntityModel<T> {
    public final ModelPart eyes;
    public final ModelPart eyebrows;

    public PuffEntityModel(ModelPart root) {
        super(root, false);
        eyes = root.getChild("eyes");
        eyebrows = root.getChild("eyebrows");
    }

    public static ModelData getTexturedModelData(Dilation dilation) {
        ModelData modelData = PlayerEntityModel.getTexturedModelData(dilation, false);
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("eyes", ModelPartBuilder.create().uv(8, 12).cuboid(-3.0F, -3.0F, -4.01F, 1.0F, 2.0F, 1.0F, Dilation.NONE)
                .uv(12, 12).cuboid(2.0F, -3.0F, -4.01F, 1.0F, 2.0F, 1.0F, Dilation.NONE), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        modelPartData.addChild("eyebrows", ModelPartBuilder.create().uv(58, 31).cuboid(-3.5F, -2.0F, -4.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.4F))
                .uv(58, 31).cuboid(1.5F, -2.0F, -4.5F, 2.0F, 1.0F, 1.0F, new Dilation(-0.4F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        return modelData;
    }

    @Override
    public void setAngles(T livingEntity, float f, float g, float h, float i, float j) {
        if (livingEntity.isInSittingPose()) {
            boolean bl = livingEntity.getRoll() > 4;
            boolean bl2 = livingEntity.isInSwimmingPose();
            this.head.yaw = i * 0.017453292F;
            if (bl) {
                this.head.pitch = -0.7853982F;
            } else if (this.leaningPitch > 0.0F) {
                if (bl2) {
                    this.head.pitch = this.lerpAngle(this.leaningPitch, this.head.pitch, -0.7853982F);
                } else {
                    this.head.pitch = this.lerpAngle(this.leaningPitch, this.head.pitch, j * 0.017453292F);
                }
            } else {
                this.head.pitch = j * 0.017453292F;
            }

            this.rightArm.pitch = -0.62831855F;
            this.leftArm.pitch = -0.62831855F;
            this.rightLeg.pitch = -1.5707963F;
            this.rightLeg.yaw = 0.31415927F;
            this.rightLeg.roll = 0.07853982F;
            this.leftLeg.pitch = -1.5707963F;
            this.leftLeg.yaw = -0.31415927F;
            this.leftLeg.roll = -0.07853982F;

            this.rightArm.yaw = 0.0F;
            this.leftArm.yaw = 0.0F;

            //this.animateArms(livingEntity, h);

            float k = 10.5F;

            this.body.pitch = 0.0F;
            this.rightLeg.pivotZ = 0.1F;
            this.leftLeg.pivotZ = 0.1F;
            this.rightLeg.pivotY = 12.0F + k;
            this.leftLeg.pivotY = 12.0F + k;
            this.head.pivotY = 0.0F + k;
            this.body.pivotY = 0.0F + k;
            this.leftArm.pivotY = 2.0F + k;
            this.rightArm.pivotY = 2.0F + k;
        } else if(this.riding) {
            super.setAngles(livingEntity, f, g, h, i, j);
            float k = 10.5F;
            this.rightLeg.pivotY = 12.0F + k;
            this.leftLeg.pivotY = 12.0F + k;
            this.head.pivotY = 0.0F + k;
            this.eyes.pivotY = 0.0F + k;
            this.eyebrows.pivotY = 0.0F + k;
            this.body.pivotY = 0.0F + k;
            this.leftArm.pivotY = 2.0F + k;
            this.rightArm.pivotY = 2.0F + k;
        } else {
            super.setAngles(livingEntity, f, g, h, i, j);
        }

        this.eyes.pitch = this.head.pitch;
        this.eyes.yaw = this.head.yaw;
        this.eyes.roll = this.head.roll;

        this.eyes.visible = true;
        this.eyebrows.visible = true;
        this.eyebrows.copyTransform(this.head);
        this.hat.copyTransform(this.head);
        this.jacket.copyTransform(this.body);
        this.leftSleeve.copyTransform(this.leftArm);
        this.rightSleeve.copyTransform(this.rightArm);
        this.leftPants.copyTransform(this.leftLeg);
        this.rightPants.copyTransform(this.rightLeg);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.child = false;
        super.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
