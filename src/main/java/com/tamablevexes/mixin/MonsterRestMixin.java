package com.tamablevexes.mixin;

import com.tamablevexes.VexTaming;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Monster.class)
public class MonsterRestMixin {
	@Inject(method = "isPreventingPlayerRest", at = @At("HEAD"), cancellable = true)
	private void tamablevexes$tamedVexAllowsSleeping(ServerLevel level, Player player, CallbackInfoReturnable<Boolean> cir) {
		if (VexTaming.isTamedVex(this)) {
			cir.setReturnValue(false);
		}
	}
}
