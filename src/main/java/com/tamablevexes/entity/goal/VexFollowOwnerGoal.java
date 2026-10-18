package com.tamablevexes.entity.goal;

import java.util.EnumSet;

import com.tamablevexes.VexTaming;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Vex;

public class VexFollowOwnerGoal extends Goal {
	private static final double SPEED_MODIFIER = 0.5;
	private static final double START_DIST_SQ = 10.0 * 10.0;
	private static final double STOP_DIST_SQ = 2.0 * 2.0;
	private static final double TELEPORT_DIST_SQ = 12.0 * 12.0;

	private final Vex vex;
	private LivingEntity owner;
	private int timeToRecalcPath;

	public VexFollowOwnerGoal(Vex vex) {
		this.vex = vex;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		LivingEntity owner = this.vex.getOwner();
		if (owner == null || !owner.isAlive() || VexTaming.unableToMoveToOwner(this.vex)) {
			return false;
		}
		if (this.vex.distanceToSqr(owner) < START_DIST_SQ) {
			return false;
		}
		this.owner = owner;
		return true;
	}

	@Override
	public boolean canContinueToUse() {
		return this.owner != null
			&& this.owner.isAlive()
			&& !VexTaming.unableToMoveToOwner(this.vex)
			&& this.vex.getMoveControl().hasWanted()
			&& this.vex.distanceToSqr(this.owner) > STOP_DIST_SQ;
	}

	@Override
	public void start() {
		this.timeToRecalcPath = 0;
	}

	@Override
	public void stop() {
		this.owner = null;
		this.vex.getMoveControl().setWait();
		this.vex.setDeltaMovement(this.vex.getDeltaMovement().scale(0.25));
	}

	@Override
	public void tick() {
		boolean ownerFarAway = this.vex.distanceToSqr(this.owner) >= TELEPORT_DIST_SQ;
		if (!ownerFarAway) {
			this.vex.getLookControl().setLookAt(this.owner, 10.0F, this.vex.getMaxHeadXRot());
		}

		if (--this.timeToRecalcPath <= 0) {
			this.timeToRecalcPath = this.adjustedTickDelay(10);
			if (ownerFarAway) {
				teleportToOwner(this.vex, this.owner);
			} else {
				this.vex.getMoveControl().setWantedPosition(
					this.owner.getX(), this.owner.getY() + 1.5, this.owner.getZ(), SPEED_MODIFIER);
			}
		}
	}

	public static void teleportToOwner(Vex vex, LivingEntity owner) {
		BlockPos ownerPos = owner.blockPosition();
		for (int attempt = 0; attempt < 10; attempt++) {
			int dx = vex.getRandom().nextIntBetweenInclusive(-3, 3);
			int dz = vex.getRandom().nextIntBetweenInclusive(-3, 3);
			if (Math.abs(dx) < 2 && Math.abs(dz) < 2) continue;
			int dy = vex.getRandom().nextIntBetweenInclusive(0, 2);

			double x = ownerPos.getX() + dx + 0.5;
			double y = ownerPos.getY() + dy;
			double z = ownerPos.getZ() + dz + 0.5;
			if (vex.level().noCollision(vex, vex.getBoundingBox().move(
				x - vex.getX(), y - vex.getY(), z - vex.getZ()))) {
				vex.snapTo(x, y, z, vex.getYRot(), vex.getXRot());
				vex.setDeltaMovement(0, 0, 0);
				vex.getMoveControl().setWait();
				return;
			}
		}
	}
}
