package com.smallmod;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class VexSitting {
    private static boolean isHandledElsewhere(ItemStack stack) {
        return stack.is(ItemTags.SWORDS) || stack.is(Items.NAME_TAG) || stack.is(Items.LEAD);
    }

    public static void register() {
        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            if (hand != InteractionHand.MAIN_HAND
                    || !(entity instanceof Vex vex)
                    || isHandledElsewhere(player.getItemInHand(hand))
                    || !vex.getAttachedOrElse(ModAttachments.TAMED_VEX, false)
                    || !player.equals(vex.getOwner())) {
                return InteractionResult.PASS;
            }
            if (level.isClientSide()) {
                return InteractionResult.SUCCESS;
            }

            boolean sitting = vex.getAttachedOrElse(ModAttachments.SITTING_VEX, false);
            vex.setAttached(ModAttachments.SITTING_VEX, !sitting);
            return InteractionResult.SUCCESS_SERVER;
        });

        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (entity instanceof Vex vex
                    && vex.getAttachedOrElse(ModAttachments.SITTING_VEX, false)
                    && !vex.isInvulnerableTo((ServerLevel) vex.level(), source)) {
                vex.setAttached(ModAttachments.SITTING_VEX, false);
            }
            return true;
        });
    }
}
