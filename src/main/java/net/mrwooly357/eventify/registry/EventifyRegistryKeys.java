package net.mrwooly357.eventify.registry;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.mrwooly357.eventify.Eventify;
import net.mrwooly357.eventify.event.MultiTickEvent;
import net.mrwooly357.eventify.event.EventType;

public final class EventifyRegistryKeys {

    public static final RegistryKey<Registry<EventType<?, ?>>> EVENT_TYPE = create("event_type");
    public static final RegistryKey<Registry<MultiTickEvent.Deserializer<?, ?>>> MULTI_TICK_EVENT_DESERIALIZER = create("multi_tick_event_deserializer");
    public static final RegistryKey<Registry<MultiTickEvent.Listener.Deserializer<?, ?, ?>>> MULTI_TICK_EVENT_LISTENER_DESERIALIZER = create("multi_tick_event_listener_deserializer");


    private static <T> RegistryKey<Registry<T>> create(String name) {
        return RegistryKey.ofRegistry(Identifier.of(Eventify.MOD_ID, name));
    }
}
