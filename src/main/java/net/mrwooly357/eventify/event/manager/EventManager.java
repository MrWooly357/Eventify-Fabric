package net.mrwooly357.eventify.event.manager;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.dimension.DimensionType;
import net.mrwooly357.eventify.Eventify;
import net.mrwooly357.eventify.event.Event;
import net.mrwooly357.eventify.event.EventType;
import net.mrwooly357.eventify.event.MultiTickEvent;
import net.mrwooly357.eventify.event.SingleTickEvent;
import net.mrwooly357.eventify.event.context.EventContext;
import net.mrwooly357.eventify.event.scheduled.EventScheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EventManager extends PersistentState {

    private final ServerWorld world;
    private final List<Event.Emitter> emitters = new ArrayList<>();
    private final EventScheduler scheduler = EventScheduler.create();
    private final MultiTickEventManager multiTickEventManager;

    private static final Map<RegistryKey<DimensionType>, String> DIMENSIONS = new HashMap<>();
    private static final String SCHEDULER_KEY = "Scheduler";
    private static final String MULTI_TICK_EVENT_MANAGER_KEY = "MultiTickEventManager";

    private EventManager(ServerWorld world) {
        this.world = world;
        multiTickEventManager = MultiTickEventManager.create(world);
    }


    public void tick() {
        emitters.forEach(emitter -> emitter.tick(world));
        scheduler.tick(world);
        multiTickEventManager.tick(world);
        markDirty();
    }

    public <C extends EventContext, E extends SingleTickEvent<C>> void emitEvent(E event) {
        event.emit(event.getType().createContext(world));
    }

    public <C extends EventContext, E extends SingleTickEvent<C>> void emitEvent(EventType<C, E> type) {
        emitEvent(type.createEvent(world));
    }

    public <C extends EventContext, E extends MultiTickEvent<C>> void startEvent(E event) {
        multiTickEventManager.startEvent(event);
    }

    public <C extends EventContext, E extends MultiTickEvent<C>> void startEvent(EventType<C, E> type) {
        multiTickEventManager.startEvent(type);
    }

    public void addEventEmitter(Event.Emitter emitter) {
        emitters.add(emitter);
    }

    public EventScheduler getScheduler() {
        return scheduler;
    }

    public static Type<EventManager> getPersistentStateType(ServerWorld world) {
        return new Type<>(() -> new EventManager(world), (nbt, registryLookup) -> new EventManager(world).deserialize(nbt), null);
    }

    public static void addDimension(RegistryKey<DimensionType> dimensionTypeKey, String name) {
        DIMENSIONS.put(dimensionTypeKey, name);
    }

    public static String getId(RegistryEntry<DimensionType> dimensionTypeEntry) {
        return Eventify.MOD_ID + ".eventManager_" + DIMENSIONS.get(dimensionTypeEntry.getKey().orElseThrow());
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        serialize(nbt);

        return nbt;
    }

    public void serialize(NbtCompound nbt) {
        nbt.put(SCHEDULER_KEY, scheduler.serialize());
        nbt.put(MULTI_TICK_EVENT_MANAGER_KEY, multiTickEventManager.serialize(world));
    }

    public EventManager deserialize(NbtCompound nbt) {
        scheduler.deserialize(nbt.getCompound(SCHEDULER_KEY));
        multiTickEventManager.deserialize(nbt.getCompound(MULTI_TICK_EVENT_MANAGER_KEY));

        return this;
    }


    public interface Holder {


        EventManager getEventManager();
    }
}
