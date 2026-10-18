package com.smallmod.entity.goal;

import com.smallmod.VexTaming;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Vex;

public class VexHurtByTargetGoal extends HurtByTargetGoal {
	private final Vex vex;

	public VexHurtByTargetGoal(Vex vex) {
		super(vex);
		this.vex = vex;
		this.setAlertOthers();
	}

	@Override
	protected void alertOther(Mob other, LivingEntity hurtByMob) {
		if (VexTaming.isTamedVex(other) && other.canAttack(hurtByMob) && this.vex.getOwner() == ((Vex) other).getOwner()) {
			super.alertOther(other, hurtByMob);
		}
	}
}
