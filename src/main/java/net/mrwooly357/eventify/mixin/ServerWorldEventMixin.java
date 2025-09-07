package net.mrwooly357.eventify.mixin;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.RandomSequencesState;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.spawner.SpecialSpawner;
import net.mrwooly357.eventify.Eventify;
import net.mrwooly357.eventify.event.manager.EventManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldEventMixin implements EventManager.Holder {

    @Unique
    private EventManager eventManager;


    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectConstructor(
            MinecraftServer server,
            Executor workerExecutor,
            LevelStorage.Session session,
            ServerWorldProperties properties,
            RegistryKey<World> worldKey,
            DimensionOptions dimensionOptions,
            WorldGenerationProgressListener worldGenerationProgressListener,
            boolean debugWorld,
            long seed,
            List<SpecialSpawner> spawners,
            boolean shouldTickTime,
            RandomSequencesState randomSequencesState,
            CallbackInfo ci
    ) {
        ServerWorld world = (ServerWorld) (Object) this;
        eventManager = getPersistentStateManager().getOrCreate(EventManager.getPersistentStateType(world), EventManager.getId(world.getDimensionEntry()));
    }

    @Shadow
    public abstract PersistentStateManager getPersistentStateManager();

    @Override
    public EventManager getEventManager() {
        return eventManager;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void injectTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        Profiler eventifyProfiler = ((ServerWorld) (Object) this).getProfiler();
        eventifyProfiler.push(Eventify.MOD_ID + ".eventManagement");
        eventManager.tick();
        eventifyProfiler.pop();
    }
}
