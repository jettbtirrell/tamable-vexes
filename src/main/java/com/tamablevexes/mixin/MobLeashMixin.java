package com.tamablevexes.mixin;

import com.tamablevexes.VexTaming;

import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public class MobLeashMixin {
	@Inject(method = "canBeLeashed", at = @At("HEAD"), cancellable = true)
	private void tamablevexes$tamedVexCanBeLeashed(CallbackInfoReturnable<Boolean> cir) {
		if (VexTaming.isTamedVex(this)) {
			cir.setReturnValue(true);
		}
	}
}
