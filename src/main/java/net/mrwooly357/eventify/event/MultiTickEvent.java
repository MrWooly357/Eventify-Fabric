package net.mrwooly357.eventify.event;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.mrwooly357.eventify.event.context.EventContext;
import net.mrwooly357.eventify.registry.EventifyRegistries;
import net.mrwooly357.wool.util.data.UnifiedData;

import java.util.ArrayList;
import java.util.List;

public abstract class MultiTickEvent<C extends EventContext> extends Event<C> {

    protected State state;
    protected final long startTime;
    protected final long endTime;
    protected final Duration duration;
    private final List<Listener<C, MultiTickEvent<C>>> listeners = new ArrayList<>();

    private static final String STATE_KEY = "State";
    private static final String START_TIME_KEY = "StartTime";
    private static final String END_TIME_KEY = "EndTime";
    private static final String DURATION_KEY = "Duration";
    private static final String DESERIALIZER_KEY = "Deserializer";
    public static final String LISTENER_COUNT_KEY = "ListenerCount";
    public static final String LISTENERS_KEY = "Listeners";

    protected MultiTickEvent(C context, Duration duration) {
        super(context);

        this.startTime = context.getWorld().getTime();
        this.endTime = startTime + duration.duration;
        this.duration = duration;
    }


    public void tick(C context) {
        long time = context.getWorld().getTime();

        if (time == startTime)
            start(context);
        else if (time == startTime + 1)
            changeState(context, State.IN_PROCESS);
        else if (time == endTime)
            end(context);
    }

    protected void start(C context) {
        changeState(context, State.START);
    }

    protected void end(C context) {
        changeState(context, State.END);
    }

    protected void changeState(C context, State state) {
        listeners.forEach(listener -> listener.onStateChanged(context, this, this.state, state));
        this.state = state;
    }

    protected void addListener(Listener<C, MultiTickEvent<C>> listener) {
        listeners.add(listener);
    }

    protected <E extends MultiTickEvent<C>> void triggerListeners(Listener.Trigger trigger, C context, E event, UnifiedData data) {
        listeners.forEach(listener -> listener.onTriggered(trigger, context, event, data));
    }

    public NbtCompound serialize(C context) {
        NbtCompound nbt = new NbtCompound();
        nbt.putInt(STATE_KEY, state.getIndex());
        nbt.putLong(START_TIME_KEY, startTime);
        nbt.putLong(END_TIME_KEY, endTime);
        nbt.put(DURATION_KEY, duration.serialize());
        nbt.putString(DESERIALIZER_KEY, EventifyRegistries.MULTI_TICK_EVENT_DESERIALIZER.getId(getDeserializer()).toString());

        NbtCompound listenersNbt = new NbtCompound();
        int listenerCount = listeners.size();
        listenersNbt.putInt(LISTENER_COUNT_KEY, listenerCount);

        for (int i = 0; i < listenerCount; i++)
            listenersNbt.put(String.valueOf(i), listeners.get(i).serialize(context, this));

        nbt.put(LISTENERS_KEY, listenersNbt);

        return nbt;
    }

    public final NbtCompound serialize(ServerWorld world) {
        return serialize(getType().createContext(world));
    }

    @SuppressWarnings("unchecked")
    public static <C extends EventContext, E extends MultiTickEvent<C>> E deserialize(ServerWorld world, NbtCompound nbt) {
        E event = (E) EventifyRegistries.MULTI_TICK_EVENT_DESERIALIZER.get(Identifier.of(nbt.getString(DESERIALIZER_KEY)))
                .deserialize(
                        world,
                        nbt,
                        nbt.getInt(STATE_KEY),
                        nbt.getLong(START_TIME_KEY),
                        nbt.getLong(END_TIME_KEY),
                        Duration.deserialize(nbt.getCompound(DURATION_KEY))
                );
        NbtCompound listenersNbt = nbt.getCompound(LISTENERS_KEY);
        for (int i = 0; i < listenersNbt.getInt(LISTENER_COUNT_KEY); i++)
            event.addListener(deserializeListener(world, event, listenersNbt.getCompound(String.valueOf(i))));

        return event;
    }

    @SuppressWarnings("unchecked")
    private static <C extends EventContext, E extends MultiTickEvent<C>, L extends Listener<C, E>> L deserializeListener(ServerWorld world, E event, NbtCompound nbt) {
        Listener.Deserializer<C, E, L> deserializer = (Listener.Deserializer<C, E, L>) EventifyRegistries.MULTI_TICK_EVENT_LISTENER_DESERIALIZER.get(Identifier.of(nbt.getString(Listener.DESERIALIZER_KEY)));
        return deserializer.deserialize(world, event, nbt);
    }

    protected abstract Deserializer<C, ? extends MultiTickEvent<C>> getDeserializer();


    @FunctionalInterface
    public interface State {

        State START = () -> 0;
        State IN_PROCESS = () -> 1;
        State END = () -> 2;


        int getIndex();
    }


    public static final class Duration {

        private final int duration;

        public static final Duration UNLIMITED = new Duration(-1);

        private Duration(int duration) {
            this.duration = duration;
        }


        public static Duration of(int duration) {
            if (duration < 1)
                throw new IllegalArgumentException("MultiTickEvent.Duration can't have a value of less than 1!");
            else
                return new Duration(duration);
        }

        public int get() {
            return duration;
        }

        public NbtCompound serialize() {
            NbtCompound nbt = new NbtCompound();
            nbt.putInt(DURATION_KEY, duration);

            return nbt;
        }

        public static Duration deserialize(NbtCompound nbt) {
            return new Duration(nbt.getInt(DURATION_KEY));
        }
    }


    @FunctionalInterface
    public interface Deserializer<C extends EventContext, E extends MultiTickEvent<C>> {


        E deserialize(ServerWorld world, NbtCompound nbt, int stateIndex, long startTime, long endTime, Duration duration);
    }


    public static abstract class Listener<C extends EventContext, E extends MultiTickEvent<C>> {

        private static final String DESERIALIZER_KEY = "Deserializer";

        protected Listener() {}


        protected void onTriggered(Trigger trigger, C context, E event, UnifiedData data) {}

        protected void onStateChanged(C context, E event, State oldState, State newState) {}

        protected NbtCompound serialize(C context, E event) {
            NbtCompound nbt = new NbtCompound();
            nbt.putString(DESERIALIZER_KEY, EventifyRegistries.MULTI_TICK_EVENT_LISTENER_DESERIALIZER.getId(getDeserializer()).toString());

            return nbt;
        }

        protected abstract MultiTickEvent.Listener.Deserializer<C, E, ? extends Listener<C, E>> getDeserializer();


        public static final class Trigger {

            private Trigger() {}


            public static Trigger create() {
                return new Trigger();
            }
        }


        @FunctionalInterface
        public interface Deserializer<C extends EventContext, E extends MultiTickEvent<C>, L extends Listener<C, E>> {


            L deserialize(ServerWorld world, E event, NbtCompound nbt);
        }
    }
}
