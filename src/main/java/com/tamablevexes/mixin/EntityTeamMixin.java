package com.tamablevexes.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.scores.PlayerTeam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.tamablevexes.VexTaming;

@Mixin(Entity.class)
public class EntityTeamMixin {
	@Inject(method = "getTeam", at = @At("RETURN"), cancellable = true)
	private void tamablevexes$tamedVexJoinsOwnerTeam(CallbackInfoReturnable<PlayerTeam> cir) {
		if (cir.getReturnValue() == null && VexTaming.isTamedVex(this)) {
			LivingEntity owner = ((Vex) (Object) this).getOwner();
			if (owner != null) {
				cir.setReturnValue(owner.getTeam());
			}
		}
	}
}
