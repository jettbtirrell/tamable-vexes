package com.tamablevexes.mixin;

import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityReference;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;


@Mixin(Vex.class)
public interface VexAccessor {
    @Accessor("hasLimitedLife")
    void tamablevexes$setHasLimitedLife(boolean value);

    @Accessor("owner")
    void tamablevexes$setOwner(EntityReference<LivingEntity> owner);
}