package com.tamablevexes.client.mixin;

import com.tamablevexes.client.TamedVexStateAccess;

import net.minecraft.client.renderer.entity.state.VexRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(VexRenderState.class)
public class VexRenderStateMixin implements TamedVexStateAccess {
	@Unique private boolean tamablevexes$tamed;
	@Unique private boolean tamablevexes$sitting;

	@Override
	public boolean tamablevexes$isTamed() {
		return this.tamablevexes$tamed;
	}

	@Override
	public boolean tamablevexes$isSitting() {
		return this.tamablevexes$sitting;
	}

	@Override
	public void tamablevexes$setTamedState(boolean tamed, boolean sitting) {
		this.tamablevexes$tamed = tamed;
		this.tamablevexes$sitting = sitting;
	}
}
