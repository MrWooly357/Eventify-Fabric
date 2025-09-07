package net.mrwooly357.eventify.util;

import net.minecraft.util.Identifier;
import net.mrwooly357.eventify.Eventify;

public final class Util {


    public static Identifier createDummyId() {
        return Identifier.of(Eventify.MOD_ID, "dummy");
    }
}
