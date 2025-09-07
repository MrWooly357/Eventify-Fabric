package net.mrwooly357.eventify.event;

import net.minecraft.server.world.ServerWorld;
import net.mrwooly357.eventify.event.context.EventContext;

public final class EventType<C extends EventContext, E extends Event<C>> {

    private final EventContext.Creator<C> contextCreator;
    private final Event.Creator<C, E> eventCreator;

    private EventType(EventContext.Creator<C> contextCreator, Event.Creator<C, E> eventCreator) {
        this.contextCreator = contextCreator;
        this.eventCreator = eventCreator;
    }


    public static <C extends EventContext, E extends Event<C>> EventType<C, E> of(EventContext.Creator<C> contextCreator, Event.Creator<C, E> eventCreator) {
        return new EventType<>(contextCreator, eventCreator);
    }

    public EventContext.Creator<C> getContextCreator() {
        return contextCreator;
    }

    public C createContext(ServerWorld world) {
        return contextCreator.create(world);
    }

    public Event.Creator<C, E> getEventCreator() {
        return eventCreator;
    }

    public E createEvent(ServerWorld world) {
        return eventCreator.create(createContext(world));
    }
}
