package com.smallmod.mixin;

import com.smallmod.VexTaming;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Vex;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityAllyMixin {
	@Inject(method = "considersEntityAsAlly", at = @At("HEAD"), cancellable = true)
	private void smallmod$tamedVexAlliesWithOwner(Entity other, CallbackInfoReturnable<Boolean> cir) {
		if (!VexTaming.isTamedVex(this)) return;
		LivingEntity owner = ((Vex) (Object) this).getRootOwner();
		if (owner == null) return;
		cir.setReturnValue(other == owner || owner.isAlliedTo(other));
	}
}
