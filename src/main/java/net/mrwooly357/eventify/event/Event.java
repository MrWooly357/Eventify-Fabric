package net.mrwooly357.eventify.event;

import net.minecraft.server.world.ServerWorld;
import net.mrwooly357.eventify.event.context.EventContext;

public abstract class Event<C extends EventContext> {

    protected Event(C context) {}


    public abstract EventType<C, ? extends Event<C>> getType();


    @FunctionalInterface
    public interface Creator<C extends EventContext, E extends Event<C>> {


        E create(C context);
    }


    @FunctionalInterface
    public interface Emitter {


        void tick(ServerWorld world);
    }
}
