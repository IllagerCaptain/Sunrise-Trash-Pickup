package illagercaptain.sunrisetrashpickup;

import com.hypixel.hytale.assetstore.event.LoadedAssetsEvent;
import com.hypixel.hytale.assetstore.map.BlockTypeAssetMap;
import com.hypixel.hytale.common.plugin.PluginManifest;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.bench.ProcessingBench;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.Map;

@SuppressWarnings({"unused"})
public class SunriseTrashPickupPlugin extends JavaPlugin {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private final PluginManifest manifest = this.getManifest();
    private final String pluginIdentifier = manifest.getGroup() + "_" + manifest.getName();

    public SunriseTrashPickupPlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    protected void setup() {
        this.getEventRegistry().register(LoadedAssetsEvent.class, BlockType.class, (LoadedAssetsEvent<String, BlockType, BlockTypeAssetMap<String, BlockType>> loadedAssetsEvent) -> {
            Map<String, BlockType> assets = loadedAssetsEvent.getLoadedAssets();
            assets.entrySet().stream().filter(asset -> asset.getKey().startsWith(pluginIdentifier + "_")).forEach(asset -> {
                ProcessingBench bench = (ProcessingBench) asset.getValue().getBench();
                try {
                    Field outputSlotsCountField = ProcessingBench.class.getDeclaredField("outputSlotsCount");
                    outputSlotsCountField.setAccessible(true);
                    outputSlotsCountField.setInt(bench, 0);
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }
}