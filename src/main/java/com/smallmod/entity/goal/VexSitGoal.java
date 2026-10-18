package com.smallmod.entity.goal;

import java.util.EnumSet;

import com.smallmod.ModAttachments;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.phys.Vec3;

public class VexSitGoal extends Goal {
	private final Vex vex;

	public VexSitGoal(Vex vex) {
		this.vex = vex;
		this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
	}

	@Override
	public boolean canUse() {
		return this.vex.getAttachedOrElse(ModAttachments.SITTING_VEX, false);
	}

	@Override
	public boolean canContinueToUse() {
		return this.canUse();
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}

	@Override
	public void start() {
		this.vex.getMoveControl().setWait();
		this.vex.setTarget(null);
		this.vex.setDeltaMovement(Vec3.ZERO);
	}

	@Override
	public void tick() {
		this.vex.setDeltaMovement(Vec3.ZERO);
	}
}
