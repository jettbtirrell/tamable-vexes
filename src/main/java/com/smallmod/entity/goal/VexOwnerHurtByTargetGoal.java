package com.smallmod.entity.goal;

import java.util.EnumSet;

import com.smallmod.ModAttachments;
import com.smallmod.VexTaming;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Vex;

public class VexOwnerHurtByTargetGoal extends TargetGoal {
	private final Vex vex;
	private LivingEntity ownerLastHurtBy;
	private int timestamp;

	public VexOwnerHurtByTargetGoal(Vex vex) {
		super(vex, false);
		this.vex = vex;
		this.setFlags(EnumSet.of(Goal.Flag.TARGET));
	}

	@Override
	public boolean canUse() {
		LivingEntity owner = this.vex.getOwner();
		if (owner == null || this.vex.getAttachedOrElse(ModAttachments.SITTING_VEX, false)) return false;

		DamageSource lastDamageSource = owner.getLastDamageSource(100);
		if (lastDamageSource == null || lastDamageSource.is(DamageTypeTags.NO_WOLF_RETALIATION)) return false;

		this.ownerLastHurtBy = owner.getLastHurtByMob();
		return owner.getLastHurtByMobTimestamp() != this.timestamp
			&& this.canAttack(this.ownerLastHurtBy, TargetingConditions.DEFAULT)
			&& VexTaming.wantsToAttack(this.ownerLastHurtBy, owner);
	}

	@Override
	public void start() {
		this.vex.setTarget(this.ownerLastHurtBy);
		LivingEntity owner = this.vex.getOwner();
		if (owner != null) {
			this.timestamp = owner.getLastHurtByMobTimestamp();
		}
		super.start();
	}
}
