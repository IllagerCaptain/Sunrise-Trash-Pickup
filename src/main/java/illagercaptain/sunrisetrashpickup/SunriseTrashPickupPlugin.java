package illagercaptain.sunrisetrashpickup;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.state.TickableBlockState;
import com.hypixel.hytale.server.core.universe.world.meta.BlockState;
import com.hypixel.hytale.server.core.universe.world.meta.state.ItemContainerState;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapManager;

import javax.annotation.Nonnull;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings({"unused", "removal"})
public class SunriseTrashPickupPlugin extends JavaPlugin {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public SunriseTrashPickupPlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    protected void setup() {
        getBlockStateRegistry().registerBlockState(TrashCanState.class, "illagercaptain.sunrisetrashcanpickup.trashcan", TrashCanState.CODEC, ItemContainerState.ItemContainerStateData.class, ItemContainerState.ItemContainerStateData.CODEC);
    }

    public static class TrashCanState extends ItemContainerState implements TickableBlockState {
        public static final Codec<TrashCanState> CODEC = BuilderCodec.builder(TrashCanState.class, TrashCanState::new, BlockState.BASE_CODEC)
                .append(new KeyedCodec<>("Custom", Codec.BOOLEAN), (state, o) -> state.custom = o, state -> state.custom)
                .add()
                .append(new KeyedCodec<>("AllowViewing", Codec.BOOLEAN), (state, o) -> state.allowViewing = o, state -> state.allowViewing)
                .add()
                .append(new KeyedCodec<>("Droplist", Codec.STRING), (state, o) -> state.droplist = o, state -> state.droplist)
                .add()
                .append(new KeyedCodec<>("Marker", WorldMapManager.MarkerReference.CODEC), (state, o) -> state.marker = o, state -> state.marker)
                .add()
                .append(new KeyedCodec<>("ItemContainer", SimpleItemContainer.CODEC), (state, o) -> state.itemContainer = o, state -> state.itemContainer)
                .add()
                .build();

        // FIXME: This causes a memory leak when a world is removed. JDK pls give us ConcurrentWeakHashMap I beg of you
        private static final ConcurrentHashMap<World, WorldTimeResource> worldTimeResources = new ConcurrentHashMap<>();

        @Override
        public void tick(float v, int i, ArchetypeChunk<ChunkStore> archetypeChunk, Store<ChunkStore> store, CommandBuffer<ChunkStore> commandBuffer) {
            WorldChunk chunk = this.getChunk();
            if (chunk != null) {
                WorldTimeResource worldTimeResource;
                World world = chunk.getWorld();
                worldTimeResource = worldTimeResources.computeIfAbsent(world, w ->
                        w.getEntityStore().getStore().getResource(WorldTimeResource.getResourceType())
                );
                float dayProgress = worldTimeResource.getDayProgress();
                if (MathUtil.within(dayProgress, 0.24899999797344207763671875, 0.250999987125396728515625)) {
                    if (this.getWindows().isEmpty()) {
                        this.getItemContainer().clear();
                    }
                }
            } else {
                LOGGER.atSevere().log("Chunk corresponding to %s was null! Skipping tick attempt...", archetypeChunk.toString());
            }
        }
    }
}