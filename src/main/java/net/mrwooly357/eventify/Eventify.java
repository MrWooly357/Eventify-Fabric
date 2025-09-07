package net.mrwooly357.eventify;

import net.fabricmc.api.ModInitializer;

import net.minecraft.world.dimension.DimensionTypes;
import net.mrwooly357.eventify.event.manager.EventManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Eventify implements ModInitializer {

	public static final String MOD_ID = "eventify";
	public static final Logger LOGGER = LoggerFactory.getLogger("Eventify");


	@Override
	public void onInitialize() {
        EventManager.addDimension(DimensionTypes.OVERWORLD, DimensionTypes.OVERWORLD_ID.getPath());
        EventManager.addDimension(DimensionTypes.THE_NETHER, DimensionTypes.THE_NETHER_ID.getPath());
        EventManager.addDimension(DimensionTypes.THE_END, DimensionTypes.THE_END_ID.getPath());
    }
}
