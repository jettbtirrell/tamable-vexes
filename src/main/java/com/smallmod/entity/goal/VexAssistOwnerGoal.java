package com.smallmod.entity.goal;

import java.util.EnumSet;

import com.smallmod.ModAttachments;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Vex;

public class VexAssistOwnerGoal extends TargetGoal {
	private final Vex vex;
	private LivingEntity candidate;
	private int lastDefendStamp;
	private int lastAttackStamp;

	public VexAssistOwnerGoal(Vex vex) {
		super(vex, false);
		this.vex = vex;
		this.setFlags(EnumSet.of(Goal.Flag.TARGET));
	}

	@Override
	public boolean canUse() {
		LivingEntity owner = this.vex.getOwner();
		if (owner == null || this.vex.getAttachedOrElse(ModAttachments.SITTING_VEX, false)) return false;

		LivingEntity found = null;
		if (owner.getLastHurtByMobTimestamp() != this.lastDefendStamp) {
			found = owner.getLastHurtByMob();
		} else if (owner.getLastHurtMobTimestamp() != this.lastAttackStamp) {
			found = owner.getLastHurtMob();
		}

		if (found == null || found == owner || isAlly(found, owner)) return false;
		if (!this.canAttack(found, TargetingConditions.DEFAULT)) return false;
		this.candidate = found;
		return true;
	}

	@Override
	public void start() {
		LivingEntity owner = this.vex.getOwner();
		this.vex.setTarget(this.candidate);
		if (owner != null) {
			this.lastDefendStamp = owner.getLastHurtByMobTimestamp();
			this.lastAttackStamp = owner.getLastHurtMobTimestamp();
		}
		super.start();
	}

	private static boolean isAlly(LivingEntity target, LivingEntity owner) {
		if (target instanceof Vex other) return other.getOwner() != null;
		if (target instanceof Wolf wolf) return wolf.isTame() && owner.equals(wolf.getOwner());
		if (target instanceof TamableAnimal pet) return pet.isTame();
		if (target instanceof AbstractHorse horse) return horse.isTamed();
		if (target instanceof Player player && owner instanceof Player ownerPlayer) return !ownerPlayer.canHarmPlayer(player);
		return target instanceof Creeper || target instanceof Ghast || target instanceof ArmorStand;
	}
}
