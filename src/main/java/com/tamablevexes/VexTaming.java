package com.tamablevexes;

import com.tamablevexes.entity.goal.VexHurtByTargetGoal;
import com.tamablevexes.entity.goal.VexOwnerHurtByTargetGoal;
import com.tamablevexes.entity.goal.VexOwnerHurtTargetGoal;
import com.tamablevexes.entity.goal.VexFollowOwnerGoal;
import com.tamablevexes.entity.goal.VexPanicGoal;
import com.tamablevexes.entity.goal.VexSitGoal;
import com.tamablevexes.entity.goal.VexStrollGoal;
import com.tamablevexes.mixin.MobAccessor;
import com.tamablevexes.mixin.VexAccessor;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.EquipmentSlot;


public class VexTaming {
    private static final float TAMED_MAX_HEALTH = 70.0F;
    private static final float GLOWSTONE_HEAL = 10.0F;
    private static final Class<?> RANDOM_MOVE_GOAL = findRandomMoveGoal();

    private static final Class<?> COPY_OWNER_TARGET_GOAL = findVexGoal("VexCopyOwnerTargetGoal");

    private static Class<?> findRandomMoveGoal() {
        return findVexGoal("VexRandomMoveGoal");
    }

    private static Class<?> findVexGoal(String name) {
        try {
            return Class.forName("net.minecraft.world.entity.monster.Vex$" + name);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Vex$" + name + " no longer exists; update VexTaming", e);
        }
    }

    public static boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
        if (target instanceof Creeper || target instanceof Ghast || target instanceof ArmorStand) return false;
        if (target instanceof Vex other) return other.getOwner() == null;
        if (target instanceof Wolf wolf) return !wolf.isTame() || wolf.getOwner() != owner;
        if (target instanceof Player player && owner instanceof Player ownerPlayer && !ownerPlayer.canHarmPlayer(player)) return false;
        if (target instanceof AbstractHorse horse && horse.isTamed()) return false;
        return !(target instanceof TamableAnimal animal && animal.isTame());
    }

    public static boolean unableToMoveToOwner(Vex vex) {
        LivingEntity owner = vex.getOwner();
        return vex.getAttachedOrElse(ModAttachments.SITTING_VEX, false)
            || vex.isPassenger()
            || vex.mayBeLeashed()
            || owner != null && owner.isSpectator();
    }

    public static void register() {
        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            ItemStack stack = player.getItemInHand(hand);

            if (!(entity instanceof Vex vex) || !stack.is(Items.TOTEM_OF_UNDYING) || vex.getAttachedOrElse(ModAttachments.TAMED_VEX, false)) {
                return InteractionResult.PASS;
            }
            if (level.isClientSide()) {
                return InteractionResult.SUCCESS;
            }

            stack.consume(1, player);
            vex.setTarget(null);
            vex.setAttached(ModAttachments.TAMED_VEX, true);
            vex.getAttribute(Attributes.MAX_HEALTH).setBaseValue(TAMED_MAX_HEALTH);
            vex.setHealth(TAMED_MAX_HEALTH);
            ((VexAccessor) vex).tamablevexes$setHasLimitedLife(false);
            ((VexAccessor) vex).tamablevexes$setOwner(EntityReference.<LivingEntity>of(player));
            applyTamedAi(vex);

            ((ServerLevel) level).sendParticles(ParticleTypes.HEART, vex.getX(), vex.getY() + 0.5, vex.getZ(), 7, 0.4, 0.4, 0.4, 0.02);
            return InteractionResult.SUCCESS_SERVER;
            
        });

        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            ItemStack stack = player.getItemInHand(hand);

            if (hand != InteractionHand.MAIN_HAND
                    || !isTamedVex(entity)
                    || !stack.is(Items.GLOWSTONE_DUST)
                    || ((Vex) entity).getHealth() >= ((Vex) entity).getMaxHealth()) {
                return InteractionResult.PASS;
            }
            if (level.isClientSide()) {
                return InteractionResult.SUCCESS;
            }

            Vex vex = (Vex) entity;
            stack.consume(1, player);
            vex.heal(GLOWSTONE_HEAL);
            return InteractionResult.SUCCESS_SERVER;
        });

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (isTamedVex(entity)
                    && entity.level() instanceof ServerLevel serverLevel
                    && serverLevel.getGameRules().get(GameRules.SHOW_DEATH_MESSAGES)
                    && ((Vex) entity).getOwner() instanceof ServerPlayer owner) {
                owner.sendSystemMessage(entity.getCombatTracker().getDeathMessage());
            }
        });

        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
            if (entity instanceof Vex vex && vex.getAttachedOrElse(ModAttachments.TAMED_VEX, false)) {
                applyTamedAi(vex);
            }
        });
        
    }

    public static boolean isTamedVex(Object entity) {
        return entity instanceof Vex vex && vex.getAttachedOrElse(ModAttachments.TAMED_VEX, false);
    }

    public static void applyTamedAi(Vex vex) {
        MobAccessor mob = (MobAccessor) vex;
        mob.tamablevexes$getTargetSelector().removeAllGoals(goal -> goal instanceof NearestAttackableTargetGoal
                || goal instanceof HurtByTargetGoal
                || COPY_OWNER_TARGET_GOAL.isInstance(goal));
        mob.tamablevexes$getTargetSelector().addGoal(1, new VexOwnerHurtByTargetGoal(vex));
        mob.tamablevexes$getTargetSelector().addGoal(2, new VexOwnerHurtTargetGoal(vex));
        mob.tamablevexes$getTargetSelector().addGoal(3, new VexHurtByTargetGoal(vex));
        mob.tamablevexes$getGoalSelector().removeAllGoals(RANDOM_MOVE_GOAL::isInstance);
        vex.setPersistenceRequired();
        vex.setGuaranteedDrop(EquipmentSlot.MAINHAND);
        mob.tamablevexes$getGoalSelector().addGoal(1, new VexPanicGoal(vex, 0.75));
        mob.tamablevexes$getGoalSelector().addGoal(2, new VexSitGoal(vex));
        mob.tamablevexes$getGoalSelector().addGoal(7, new VexFollowOwnerGoal(vex));
        mob.tamablevexes$getGoalSelector().addGoal(8, new VexStrollGoal(vex));
        mob.tamablevexes$getGoalSelector().addGoal(10, new RandomLookAroundGoal(vex));
    }

}
