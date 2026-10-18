package com.tamablevexes.entity.goal;

import java.util.EnumSet;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Vex;

public class VexStrollGoal extends Goal {
	private static final int INTERVAL = 120;
	private static final double SPEED_MODIFIER = 0.5;
	private static final int RANGE_XZ = 10;
	private static final int RANGE_Y = 3;

	private final Vex vex;
	private BlockPos destination;

	public VexStrollGoal(Vex vex) {
		this.vex = vex;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE));
	}

	@Override
	public boolean canUse() {
		if (this.vex.getMoveControl().hasWanted() || this.vex.getRandom().nextInt(reducedTickDelay(INTERVAL)) != 0) {
			return false;
		}

		for (int attempt = 0; attempt < 10; attempt++) {
			BlockPos pos = this.vex.blockPosition().offset(
				this.vex.getRandom().nextIntBetweenInclusive(-RANGE_XZ, RANGE_XZ),
				this.vex.getRandom().nextIntBetweenInclusive(-RANGE_Y, RANGE_Y),
				this.vex.getRandom().nextIntBetweenInclusive(-RANGE_XZ, RANGE_XZ));
			if (this.vex.level().isEmptyBlock(pos)) {
				this.destination = pos;
				return true;
			}
		}
		return false;
	}

	@Override
	public void start() {
		this.vex.getMoveControl().setWantedPosition(
			this.destination.getX() + 0.5, this.destination.getY() + 0.5, this.destination.getZ() + 0.5, SPEED_MODIFIER);
	}

	@Override
	public boolean canContinueToUse() {
		return this.vex.getMoveControl().hasWanted();
	}

	@Override
	public void stop() {
		this.vex.getMoveControl().setWait();
		this.vex.setDeltaMovement(this.vex.getDeltaMovement().scale(0.25));
	}
}
