package com.smallmod.entity.goal;

import java.util.EnumSet;

import com.smallmod.ModAttachments;
import com.smallmod.VexTaming;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Vex;

public class VexOwnerHurtTargetGoal extends TargetGoal {
	private final Vex vex;
	private LivingEntity ownerLastHurt;
	private int timestamp;

	public VexOwnerHurtTargetGoal(Vex vex) {
		super(vex, false);
		this.vex = vex;
		this.setFlags(EnumSet.of(Goal.Flag.TARGET));
	}

	@Override
	public boolean canUse() {
		LivingEntity owner = this.vex.getOwner();
		if (owner == null || this.vex.getAttachedOrElse(ModAttachments.SITTING_VEX, false)) return false;

		this.ownerLastHurt = owner.getLastHurtMob();
		return owner.getLastHurtMobTimestamp() != this.timestamp
			&& this.canAttack(this.ownerLastHurt, TargetingConditions.DEFAULT)
			&& VexTaming.wantsToAttack(this.ownerLastHurt, owner);
	}

	@Override
	public void start() {
		this.vex.setTarget(this.ownerLastHurt);
		LivingEntity owner = this.vex.getOwner();
		if (owner != null) {
			this.timestamp = owner.getLastHurtMobTimestamp();
		}
		super.start();
	}
}
