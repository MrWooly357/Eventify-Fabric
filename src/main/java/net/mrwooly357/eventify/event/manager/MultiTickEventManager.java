package net.mrwooly357.eventify.event.manager;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.mrwooly357.eventify.event.EventType;
import net.mrwooly357.eventify.event.MultiTickEvent;
import net.mrwooly357.eventify.event.context.EventContext;
import net.mrwooly357.eventify.registry.EventifyRegistries;

import java.util.ArrayList;
import java.util.List;

public final class MultiTickEventManager {

    private final ServerWorld world;
    private final List<MultiTickEvent<? extends EventContext>> events = new ArrayList<>();

    private static final String COUNT_KEY = "Count";

    private MultiTickEventManager(ServerWorld world) {
        this.world = world;
    }


    public static MultiTickEventManager create(ServerWorld world) {
        return new MultiTickEventManager(world);
    }

    public void tick(ServerWorld world) {
        events.forEach(event -> processMultiTickEvent(world, event));
    }

    private <C extends EventContext, E extends MultiTickEvent<C>> void processMultiTickEvent(ServerWorld world, E event) {
        C context = event.getType().createContext(world);
        event.tick(context);
    }

    public <C extends EventContext, E extends MultiTickEvent<C>> void startEvent(E event) {
        events.add(event);
    }

    public <C extends EventContext, E extends MultiTickEvent<C>> void startEvent(EventType<C, E> type) {
        startEvent(type.createEvent(world));
    }

    public NbtCompound serialize(ServerWorld world) {
        NbtCompound nbt = new NbtCompound();
        int count = events.size();
        nbt.putInt(COUNT_KEY, count);

        for (int i = 0; i < count; i++) {
            MultiTickEvent<? extends EventContext> event = events.get(i);
            nbt.put(String.valueOf(i), event.serialize(world));
        }

        return nbt;
    }

    public void deserialize(NbtCompound nbt) {
        int count = nbt.getInt(COUNT_KEY);

        for (int i = 0; i < count; i++)
            events.add(MultiTickEvent.deserialize(world, nbt.getCompound(String.valueOf(i))));
    }
}
