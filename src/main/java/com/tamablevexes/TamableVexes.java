package com.tamablevexes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;

public class TamableVexes implements ModInitializer {
	public static final String MOD_ID = "tamable_vexes";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Tamable Vexes loaded!");
		ModAttachments.register();
		VexTaming.register();
		VexSwordSwap.register();
		VexSitting.register();
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
