package com.smallmod.client.render;

import java.util.Map;
import java.util.WeakHashMap;

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

    private static final float SLOW_BELOW_HEALTH = 0.5F;
    private static final float MIN_FLAP_SPEED = 0.35F;
    private static final Map<Vex, float[]> ANIMATION_CLOCKS = new WeakHashMap<>();

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
            if (tamedState.isTamed) {
                state.ageInTicks = slowedAnimationTime(entity, state.ageInTicks);
            }
        }
    }

    // Accumulate animation time at a health-scaled rate so the wings slow down smoothly (no phase jumps).
    private static float slowedAnimationTime(Vex vex, float ageInTicks) {
        float healthRatio = vex.getHealth() / vex.getMaxHealth();
        float speed = healthRatio >= SLOW_BELOW_HEALTH ? 1.0F : MIN_FLAP_SPEED + (1.0F - MIN_FLAP_SPEED) * (healthRatio / SLOW_BELOW_HEALTH);
        float[] clock = ANIMATION_CLOCKS.computeIfAbsent(vex, v -> new float[] { ageInTicks, ageInTicks });
        float delta = ageInTicks - clock[0];
        clock[0] = ageInTicks;
        if (delta < 0.0F || delta > 20.0F) {
            clock[1] = ageInTicks;
        } else {
            clock[1] += delta * speed;
        }
        return clock[1];
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