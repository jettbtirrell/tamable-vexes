package com.smallmod;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.codec.ByteBufCodecs;

public class ModAttachments {
    public static final AttachmentType<Boolean> TAMED_VEX = AttachmentRegistry.create(
        SmallMod.id("tamed_vex"),
        builder -> builder
                    .initializer(() -> false)
                    .persistent(Codec.BOOL)
                    .syncWith(ByteBufCodecs.BOOL, AttachmentSyncPredicate.all())
    );

    public static final AttachmentType<Boolean> SITTING_VEX = AttachmentRegistry.create(
        SmallMod.id("sitting_vex"),
        builder -> builder
                    .initializer(() -> false)
                    .persistent(Codec.BOOL)
                    .syncWith(ByteBufCodecs.BOOL, AttachmentSyncPredicate.all())
    );

    public static void register() {}
}