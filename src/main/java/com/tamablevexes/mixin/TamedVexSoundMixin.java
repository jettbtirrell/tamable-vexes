package com.tamablevexes.mixin;

import com.tamablevexes.VexTaming;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class TamedVexSoundMixin {
	private static final float TAMED_VOLUME = 0.7F;
	private static final float TAMED_PITCH_SCALE = 0.85F;

	@Inject(method = "getSoundVolume", at = @At("HEAD"), cancellable = true)
	private void tamablevexes$softerVolume(CallbackInfoReturnable<Float> cir) {
		if (VexTaming.isTamedVex(this)) {
			cir.setReturnValue(TAMED_VOLUME);
		}
	}

	@Inject(method = "getVoicePitch", at = @At("RETURN"), cancellable = true)
	private void tamablevexes$lowerPitch(CallbackInfoReturnable<Float> cir) {
		if (VexTaming.isTamedVex(this)) {
			cir.setReturnValue(cir.getReturnValueF() * TAMED_PITCH_SCALE);
		}
	}
}
