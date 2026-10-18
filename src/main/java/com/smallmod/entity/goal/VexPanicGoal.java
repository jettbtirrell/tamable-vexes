package com.smallmod.entity.goal;

import com.smallmod.VexTaming;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.phys.Vec3;

public class VexPanicGoal extends PanicGoal {
	private static final double TELEPORT_DIST_SQ = 12.0 * 12.0;

	private final Vex vex;

	public VexPanicGoal(Vex vex, double speedModifier) {
		super(vex, speedModifier, DamageTypeTags.PANIC_ENVIRONMENTAL_CAUSES);
		this.vex = vex;
	}

	@Override
	protected boolean findRandomPosition() {
		for (int attempt = 0; attempt < 10; attempt++) {
			BlockPos pos = this.vex.blockPosition().offset(
				this.vex.getRandom().nextIntBetweenInclusive(-5, 5),
				this.vex.getRandom().nextIntBetweenInclusive(-4, 4),
				this.vex.getRandom().nextIntBetweenInclusive(-5, 5));
			if (this.vex.level().isEmptyBlock(pos)) {
				this.posX = pos.getX() + 0.5;
				this.posY = pos.getY() + 0.5;
				this.posZ = pos.getZ() + 0.5;
				return true;
			}
		}
		return false;
	}

	@Override
	public void start() {
		this.vex.getMoveControl().setWantedPosition(this.posX, this.posY, this.posZ, this.speedModifier);
		this.isRunning = true;
	}

	@Override
	public boolean canContinueToUse() {
		return this.vex.getMoveControl().hasWanted();
	}

	@Override
	public void stop() {
		super.stop();
		this.vex.getMoveControl().setWait();
		Vec3 motion = this.vex.getDeltaMovement();
		this.vex.setDeltaMovement(motion.scale(0.25));
	}

	@Override
	public void tick() {
		LivingEntity owner = this.vex.getOwner();
		if (owner != null && !VexTaming.unableToMoveToOwner(this.vex) && this.vex.distanceToSqr(owner) >= TELEPORT_DIST_SQ) {
			VexFollowOwnerGoal.teleportToOwner(this.vex, owner);
		}
	}
}
