package net.mrwooly357.eventify.event.scheduled;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.mrwooly357.eventify.event.Event;
import net.mrwooly357.eventify.event.EventType;
import net.mrwooly357.eventify.event.context.EventContext;
import net.mrwooly357.eventify.registry.EventifyRegistries;

import java.util.Optional;

public final class ScheduledEvent<C extends EventContext, E extends Event<C>> {

    private final long time;
    private final EventType<C, E> type;

    private static final String TIME_KEY = "Time";
    private static final String TYPE_ID_KEY = "TypeId";

    private ScheduledEvent(long time, EventType<C, E> type) {
        this.time = time;
        this.type = type;
    }


    public static <C extends EventContext, E extends Event<C>> ScheduledEvent<C, E> create(ServerWorld world, int timeFromNow, EventType<C, E> type) {
        return new ScheduledEvent<>(world.getTime() + 1 + timeFromNow, type);
    }

    public Optional<EventType<C, E>> getType(ServerWorld world) {
        return world.getTime() == time ? Optional.of(type) : Optional.empty();
    }

    public NbtCompound serialize() {
        NbtCompound nbt = new NbtCompound();
        nbt.putLong(TIME_KEY, time);
        nbt.putString(TYPE_ID_KEY, String.valueOf(EventifyRegistries.EVENT_TYPE.getId(type)));

        return nbt;
    }

    public static ScheduledEvent<?, ?> deserialize(NbtCompound nbt) {
        long time = nbt.getLong(TIME_KEY);
        EventType<?, ?> type = EventifyRegistries.EVENT_TYPE.get(Identifier.of(nbt.getString(TYPE_ID_KEY)));

        return new ScheduledEvent<>(time, type);
    }
}
