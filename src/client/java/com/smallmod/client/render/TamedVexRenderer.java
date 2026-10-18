package com.smallmod.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.smallmod.ModAttachments;
import com.smallmod.SmallMod;

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.VexRenderer;
import net.minecraft.client.renderer.entity.state.VexRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.monster.Vex;

public class TamedVexRenderer extends VexRenderer {
    private static final Identifier TAMED = SmallMod.id("textures/entity/vex_tamed.png");
    private static final Identifier TAMED_CHARGING = SmallMod.id("textures/entity/vex_charging_tamed.png");

    public TamedVexRenderer(Context context) {
        super(context);
    }

    @Override
    public VexRenderState createRenderState() {
        return new TamedVexRenderState();
    }

    @Override
    public void extractRenderState(Vex entity, VexRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        if (state instanceof TamedVexRenderState tamedState) {
            tamedState.isTamed = entity.getAttachedOrElse(ModAttachments.TAMED_VEX, false);
            tamedState.isSitting = tamedState.isTamed && entity.getAttachedOrElse(ModAttachments.SITTING_VEX, false);
        }
    }

    @Override
    protected void setupRotations(VexRenderState state, PoseStack poseStack, float bodyRot, float entityScale) {
        super.setupRotations(state, poseStack, bodyRot, entityScale);
        if (state instanceof TamedVexRenderState tamedState && tamedState.isSitting) {
            float pivot = state.boundingBoxHeight / 2.0F / entityScale;
            poseStack.translate(0.0F, pivot, 0.0F);
            poseStack.rotateDegrees(Axis.ZP, 90.0F);
            poseStack.translate(0.0F, -pivot, 0.0F);
        }
    }

    @Override
    public Identifier getTextureLocation(VexRenderState state) {
        if (state instanceof TamedVexRenderState tamedState && tamedState.isTamed) {
            return state.isCharging ? TAMED_CHARGING : TAMED;
        }
        return super.getTextureLocation(state);
    }

}