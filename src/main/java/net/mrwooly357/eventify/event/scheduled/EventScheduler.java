package net.mrwooly357.eventify.event.scheduled;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.mrwooly357.eventify.event.Event;
import net.mrwooly357.eventify.event.MultiTickEvent;
import net.mrwooly357.eventify.event.SingleTickEvent;
import net.mrwooly357.eventify.event.context.EventContext;
import net.mrwooly357.eventify.event.manager.EventManager;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class EventScheduler {

    private final List<ScheduledEvent<?, ?>> scheduled = new ArrayList<>();

    private static final String COUNT_KEY = "Count";

    private EventScheduler() {}


    public static EventScheduler create() {
        return new EventScheduler();
    }

    public void tick(ServerWorld world) {
        EventManager manager = ((EventManager.Holder) world).getEventManager();
        scheduled.removeIf(event -> process(manager, event, world));
    }

    @SuppressWarnings("unchecked")
    private static <C extends EventContext, E extends Event<C>> boolean process(EventManager manager, ScheduledEvent<?, ?> scheduledEvent, ServerWorld world) {
        ScheduledEvent<C, E> castedScheduledEvent = (ScheduledEvent<C, E>) scheduledEvent;
        Result<Boolean> result = Result.of(false);
        castedScheduledEvent.getType(world).ifPresent(type -> {
            E event = type.createEvent(world);
            if (event instanceof SingleTickEvent<?> singleTickEvent)
                manager.emitEvent(singleTickEvent);
            else if (event instanceof MultiTickEvent<?> multiTickEvent)
                manager.startEvent(multiTickEvent);

            result.set(true);
        });

        return result.get();
    }

    public <C extends EventContext, E extends Event<C>> void scheduleEvent(ScheduledEvent<C, E> event) {
        scheduled.add(event);
    }

    public NbtCompound serialize() {
        NbtCompound nbt = new NbtCompound();
        int count = scheduled.size();
        nbt.putInt(COUNT_KEY, count);

        for (int i = 0; i < count; i++)
            nbt.put(String.valueOf(i), scheduled.get(i).serialize());

        return nbt;
    }

    public void deserialize(NbtCompound nbt) {
        int size = nbt.getInt(COUNT_KEY);

        for (int i = 0; i < size; i++)
            scheduleEvent(ScheduledEvent.deserialize(nbt.getCompound(String.valueOf(i))));
    }


    private static final class Result<T> {

        @Nullable
        private T value;

        private Result(@Nullable T value) {
            this.value = value;
        }


        private static <T> Result<T> of() {
            return of(null);
        }

        private static <T> Result<T> of(@Nullable T value) {
            return new Result<>(value);
        }

        private T get() {
            return value;
        }

        private void set(@Nullable T value) {
            this.value = value;
        }
    }
}
