package com.smallmod;

import com.smallmod.entity.goal.VexAssistOwnerGoal;
import com.smallmod.entity.goal.VexFollowOwnerGoal;
import com.smallmod.entity.goal.VexPanicGoal;
import com.smallmod.entity.goal.VexSitGoal;
import com.smallmod.entity.goal.VexStrollGoal;
import com.smallmod.mixin.MobAccessor;
import com.smallmod.mixin.VexAccessor;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.EquipmentSlot;


public class VexTaming {
    private static final float TAMED_MAX_HEALTH = 70.0F;
    private static final Class<?> RANDOM_MOVE_GOAL = findRandomMoveGoal();

    private static Class<?> findRandomMoveGoal() {
        try {
            return Class.forName("net.minecraft.world.entity.monster.Vex$VexRandomMoveGoal");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Vex$VexRandomMoveGoal no longer exists; update VexTaming", e);
        }
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
            vex.setDropChance(EquipmentSlot.MAINHAND, 1.0F);
            vex.getAttribute(Attributes.MAX_HEALTH).setBaseValue(TAMED_MAX_HEALTH);
            vex.setHealth(TAMED_MAX_HEALTH);
            ((VexAccessor) vex).smallmod$setHasLimitedLife(false);
            ((VexAccessor) vex).smallmod$setOwner(EntityReference.<LivingEntity>of(player));
            applyTamedAi(vex);

            ((ServerLevel) level).sendParticles(ParticleTypes.HEART, vex.getX(), vex.getY() + 0.5, vex.getZ(), 7, 0.4, 0.4, 0.4, 0.02);
            return InteractionResult.SUCCESS_SERVER;
            
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
        mob.smallmod$getTargetSelector().removeAllGoals(goal -> goal instanceof NearestAttackableTargetGoal);
        mob.smallmod$getTargetSelector().addGoal(1, new VexAssistOwnerGoal(vex));
        mob.smallmod$getGoalSelector().removeAllGoals(RANDOM_MOVE_GOAL::isInstance);
        vex.setPersistenceRequired();
        mob.smallmod$getGoalSelector().addGoal(1, new VexPanicGoal(vex, 0.75));
        mob.smallmod$getGoalSelector().addGoal(2, new VexSitGoal(vex));
        mob.smallmod$getGoalSelector().addGoal(7, new VexFollowOwnerGoal(vex));
        mob.smallmod$getGoalSelector().addGoal(8, new VexStrollGoal(vex));
        mob.smallmod$getGoalSelector().addGoal(10, new RandomLookAroundGoal(vex));
    }

}
