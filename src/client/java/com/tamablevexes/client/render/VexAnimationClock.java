package com.tamablevexes.client.render;

import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.world.entity.monster.Vex;

public class VexAnimationClock {
	private static final float SLOW_BELOW_HEALTH = 0.5F;
	private static final float MIN_FLAP_SPEED = 0.35F;
	private static final Map<Vex, float[]> CLOCKS = new WeakHashMap<>();

	public static float slowedTime(Vex vex, float ageInTicks) {
		float healthRatio = vex.getHealth() / vex.getMaxHealth();
		float speed = healthRatio >= SLOW_BELOW_HEALTH ? 1.0F : MIN_FLAP_SPEED + (1.0F - MIN_FLAP_SPEED) * (healthRatio / SLOW_BELOW_HEALTH);
		float[] clock = CLOCKS.computeIfAbsent(vex, v -> new float[] { ageInTicks, ageInTicks });
		float delta = ageInTicks - clock[0];
		clock[0] = ageInTicks;
		if (delta < 0.0F || delta > 20.0F) {
			clock[1] = ageInTicks;
		} else {
			clock[1] += delta * speed;
		}
		return clock[1];
	}
}
