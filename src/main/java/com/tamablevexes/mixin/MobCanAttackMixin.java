package com.tamablevexes.mixin;

import com.tamablevexes.VexTaming;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.animal.golem.SnowGolem;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.Vex;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public class MobCanAttackMixin {
	@Inject(method = "canAttack", at = @At("HEAD"), cancellable = true)
	private void tamablevexes$neverAttackVexOwner(LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
		if ((Object) this instanceof Vex vex && target.equals(vex.getOwner())) {
			cir.setReturnValue(false);
		} else if (tamablevexes$hatesEnemies() && VexTaming.isTamedVex(target)) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
	private void tamablevexes$tamedVexIsNotAnEnemy(LivingEntity target, CallbackInfo ci) {
		if (tamablevexes$hatesEnemies() && VexTaming.isTamedVex(target)) {
			ci.cancel();
		}
	}

	private boolean tamablevexes$hatesEnemies() {
		Object self = this;
		return self instanceof IronGolem || self instanceof SnowGolem || self instanceof Shulker;
	}
}
