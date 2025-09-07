package net.mrwooly357.eventify.registry;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.mrwooly357.eventify.event.DummyEvent;
import net.mrwooly357.eventify.event.MultiTickEvent;
import net.mrwooly357.eventify.event.EventType;

public final class EventifyRegistries {

    public static final Registry<EventType<?, ?>> EVENT_TYPE = create(EventifyRegistryKeys.EVENT_TYPE, registry -> DummyEvent.TYPE);
    public static final Registry<MultiTickEvent.Deserializer<?, ?>> MULTI_TICK_EVENT_DESERIALIZER = create(EventifyRegistryKeys.MULTI_TICK_EVENT_DESERIALIZER, registry -> DummyEvent.DESERIALIZER);
    public static final Registry<MultiTickEvent.Listener.Deserializer<?, ?, ?>> MULTI_TICK_EVENT_LISTENER_DESERIALIZER = create(EventifyRegistryKeys.MULTI_TICK_EVENT_LISTENER_DESERIALIZER, registry -> DummyEvent.DummyListener.DESERIALIZER);


    private static <T> Registry<T> create(RegistryKey<Registry<T>> key, Registries.Initializer<T> initializer) {
        return Registries.create(key, initializer);
    }
}
