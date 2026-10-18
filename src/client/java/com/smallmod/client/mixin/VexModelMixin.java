package com.smallmod.client.mixin;

import com.smallmod.client.render.TamedVexRenderState;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.monster.vex.VexModel;
import net.minecraft.client.renderer.entity.state.VexRenderState;
import net.minecraft.world.entity.HumanoidArm;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VexModel.class)
public class VexModelMixin {
	@Shadow @Final private ModelPart rightArm;
	@Shadow @Final private ModelPart leftArm;
	@Shadow @Final private ModelPart body;

	@Inject(method = "setupAnim", at = @At("RETURN"))
	private void smallmod$crossArmsWhenSitting(VexRenderState state, CallbackInfo ci) {
		if (!(state instanceof TamedVexRenderState tamed) || !tamed.isSitting) return;

		// Arms tilt forward off the chest, then sweep inward; the left sits slightly in front so they overlap cleanly.
		this.rightArm.xRot = -0.45F;
		this.rightArm.yRot = 0.0F;
		this.rightArm.zRot = -1.3F;
		this.leftArm.xRot = -0.6F;
		this.leftArm.yRot = 0.0F;
		this.leftArm.zRot = 1.3F;
	}

	// Held-item scale and hand offset from VexModel.translateToHand, and the grip point ItemInHandLayer applies afterwards.
	private static final float HAND_SCALE = 0.55F;
	private static final float HAND_OFFSET_Y = -0.15625F;
	private static final float HAND_OFFSET_Z = 0.078125F;
	private static final float HAND_OFFSET_X = 0.046875F;

	// Where the sword grip should sit, in body-model pixels: centered on the chest, just in front of the crossed arms.
	private static final float SWORD_X = 0.0F;
	private static final float SWORD_Y = 1.2F;
	private static final float SWORD_Z = -1.9F;

	// Holds the item blade-down at the centre of the crossed arms, as if gripped in both hands.
	@Inject(method = "translateToHand", at = @At("RETURN"))
	private void smallmod$holdItemDownWhenSitting(VexRenderState state, HumanoidArm arm, PoseStack poseStack, CallbackInfo ci) {
		if (!(state instanceof TamedVexRenderState tamed) || !tamed.isSitting) return;

		boolean right = arm == HumanoidArm.RIGHT;
		ModelPart activeArm = right ? this.rightArm : this.leftArm;
		Quaternionf armRotation = new Quaternionf().rotationZYX(activeArm.zRot, activeArm.yRot, activeArm.xRot);
		Quaternionf bodyAndArm = new Quaternionf().rotationZYX(this.body.zRot, this.body.yRot, this.body.xRot).mul(armRotation, new Quaternionf());

		// Grip point in the held-item frame (after the 0.55 scale), matching ItemInHandLayer's rotate/translate.
		float side = right ? -1.0F : 1.0F;
		Vector3f grip = new Vector3f(side / 16.0F, 10.0F / 16.0F, -2.0F / 16.0F);
		Vector3f handOffset = new Vector3f(right ? HAND_OFFSET_X : -HAND_OFFSET_X, HAND_OFFSET_Y, HAND_OFFSET_Z);

		// Current grip position in body space (blocks), then the shift needed to reach the chest target.
		Vector3f armOrigin = new Vector3f((right ? -1.75F : 1.75F) / 16.0F, 0.25F / 16.0F, 0.0F);
		Vector3f current = armRotation.transform(handOffset.add(grip, new Vector3f()).mul(HAND_SCALE)).add(armOrigin);
		Vector3f target = new Vector3f(SWORD_X, SWORD_Y, SWORD_Z).div(16.0F);
		Vector3f shift = armRotation.conjugate(new Quaternionf()).transform(target.sub(current, new Vector3f())).div(HAND_SCALE);
		poseStack.translate(shift.x, shift.y, shift.z);

		// Turn the blade (arm-forward by default) to point straight down, pivoting on the grip.
		Vector3f worldDown = bodyAndArm.conjugate(new Quaternionf()).transform(new Vector3f(0.0F, 1.0F, 0.0F));
		Quaternionf aim = new Quaternionf().rotationTo(new Vector3f(0.0F, 0.0F, -1.0F), worldDown);
		poseStack.translate(grip.x, grip.y, grip.z);
		poseStack.mulPose(new Matrix4f().rotation(aim));
		poseStack.translate(-grip.x, -grip.y, -grip.z);
	}
}
