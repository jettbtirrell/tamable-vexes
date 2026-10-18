package com.tamablevexes.mixin;

import com.tamablevexes.VexTaming;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Mob.class)
public class MobDespawnMixin {
	@Redirect(method = "checkDespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityType;isAllowedInPeaceful()Z"))
	private boolean tamablevexes$tamedVexSurvivesPeaceful(EntityType<?> type) {
		return type.isAllowedInPeaceful() || VexTaming.isTamedVex(this);
	}
}
