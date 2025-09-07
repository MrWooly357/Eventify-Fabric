package net.mrwooly357.eventify.event;

import net.minecraft.registry.Registry;
import net.mrwooly357.eventify.event.context.EventContext;
import net.mrwooly357.eventify.registry.EventifyRegistries;
import net.mrwooly357.eventify.util.Util;

public final class DummyEvent extends MultiTickEvent<EventContext> {

    public static final EventType<EventContext, DummyEvent> TYPE = Registry.register(
            EventifyRegistries.EVENT_TYPE,
            Util.createDummyId(),
            EventType.of(EventContext::new, DummyEvent::new)
    );
    public static final Deserializer<EventContext, DummyEvent> DESERIALIZER = Registry.register(
            EventifyRegistries.MULTI_TICK_EVENT_DESERIALIZER,
            Util.createDummyId(),
            (world, nbt, stateIndex, startTime1, endTime1, duration1) -> new DummyEvent(new EventContext(world))
    );

    private DummyEvent(EventContext context) {
        super(context, Duration.UNLIMITED);
    }


    @Override
    public EventType<EventContext, DummyEvent> getType() {
        return TYPE;
    }

    @Override
    protected Deserializer<EventContext, DummyEvent> getDeserializer() {
        return DESERIALIZER;
    }


    public static final class DummyListener extends Listener<EventContext, DummyEvent> {

        public static final Deserializer<EventContext, DummyEvent, DummyListener> DESERIALIZER = Registry.register(
                EventifyRegistries.MULTI_TICK_EVENT_LISTENER_DESERIALIZER,
                Util.createDummyId(),
                (context, event, nbt) -> new DummyListener()
        );

        public DummyListener() {}


        @Override
        protected Deserializer<EventContext, DummyEvent, ? extends Listener<EventContext, DummyEvent>> getDeserializer() {
            return DESERIALIZER;
        }
    }
}
