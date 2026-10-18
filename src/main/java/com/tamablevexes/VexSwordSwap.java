package com.tamablevexes;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.item.ItemStack;

public class VexSwordSwap {
    public static void register() {
        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            ItemStack held = player.getItemInHand(hand);

            if (!(entity instanceof Vex vex)
                    || !held.is(ItemTags.SWORDS)
                    || !vex.getAttachedOrElse(ModAttachments.TAMED_VEX, false)
                    || !player.equals(vex.getOwner())) {
                return InteractionResult.PASS;
            }
            if (level.isClientSide()) {
                return InteractionResult.SUCCESS;
            }

            ItemStack oldSword = vex.getMainHandItem().copy();
            ItemStack newSword = held.copyWithCount(1);
            held.consume(1, player);

            vex.setItemSlot(EquipmentSlot.MAINHAND, newSword);

            if (!oldSword.isEmpty()) {
                BehaviorUtils.throwItem(vex, oldSword, player.position());
            }
            return InteractionResult.SUCCESS_SERVER;
        });
    }
}
