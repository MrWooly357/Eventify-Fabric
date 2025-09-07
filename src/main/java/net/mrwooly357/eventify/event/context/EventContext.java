package net.mrwooly357.eventify.event.context;

import net.minecraft.server.world.ServerWorld;

public class EventContext {

    protected final ServerWorld world;

    public EventContext(ServerWorld world) {
        this.world = world;
    }


    public ServerWorld getWorld() {
        return world;
    }


    @FunctionalInterface
    public interface Creator<C extends EventContext> {


        C create(ServerWorld world);
    }
}
