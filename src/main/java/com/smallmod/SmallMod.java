package com.smallmod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;

public class SmallMod implements ModInitializer {
	public static final String MOD_ID = "smallmod";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Small Mod loaded!");
		ModEntities.register();
		ModAttachments.register();
		VexTaming.register();
		VexSwordSwap.register();
		VexSitting.register();
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
