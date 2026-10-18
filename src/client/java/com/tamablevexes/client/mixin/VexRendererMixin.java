package com.tamablevexes.client.mixin;

import com.tamablevexes.ModAttachments;
import com.tamablevexes.client.TamedVexStateAccess;
import com.tamablevexes.client.render.TamedVexTextures;
import com.tamablevexes.client.render.VexAnimationClock;

import net.minecraft.client.renderer.entity.VexRenderer;
import net.minecraft.client.renderer.entity.state.VexRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.monster.Vex;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VexRenderer.class)
public class VexRendererMixin {
	@Inject(method = "extractRenderState(Lnet/minecraft/world/entity/monster/Vex;Lnet/minecraft/client/renderer/entity/state/VexRenderState;F)V", at = @At("RETURN"))
	private void tamablevexes$extractTamedState(Vex entity, VexRenderState state, float partialTicks, CallbackInfo ci) {
		boolean tamed = entity.getAttachedOrElse(ModAttachments.TAMED_VEX, false);
		boolean sitting = tamed && entity.getAttachedOrElse(ModAttachments.SITTING_VEX, false);
		((TamedVexStateAccess) state).tamablevexes$setTamedState(tamed, sitting);
		if (tamed) {
			state.ageInTicks = VexAnimationClock.slowedTime(entity, state.ageInTicks);
		}
	}

	@Inject(method = "getTextureLocation(Lnet/minecraft/client/renderer/entity/state/VexRenderState;)Lnet/minecraft/resources/Identifier;", at = @At("HEAD"), cancellable = true)
	private void tamablevexes$tamedTexture(VexRenderState state, CallbackInfoReturnable<Identifier> cir) {
		if (((TamedVexStateAccess) state).tamablevexes$isTamed()) {
			cir.setReturnValue(state.isCharging ? TamedVexTextures.TAMED_CHARGING : TamedVexTextures.TAMED);
		}
	}
}
