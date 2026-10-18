package com.tamablevexes.mixin;

import java.util.List;

import com.tamablevexes.VexTaming;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ConduitBlockEntity.class)
public class ConduitTargetMixin {
	@ModifyVariable(method = "selectNewTarget", at = @At("STORE"), ordinal = 0)
	private static List<LivingEntity> tamablevexes$skipTamedVexes(List<LivingEntity> candidates) {
		candidates.removeIf(VexTaming::isTamedVex);
		return candidates;
	}
}
