package de.derfrzocker.structure.control;

import de.derfrzocker.spigot.utils.Version;
import de.derfrzocker.structure.control.api.NMSUtil;
import de.derfrzocker.structure.control.api.StructureControlService;
import de.derfrzocker.structure.control.api.structures.Structures;
import de.derfrzocker.structure.control.command.SetCommand;
import de.derfrzocker.structure.control.command.StructureControlCommand;
import de.derfrzocker.structure.control.impl.StructureControlServiceImpl;
import de.derfrzocker.structure.control.impl.StructureSettingsYamlImpl;
import de.derfrzocker.structure.control.impl.WorldStructureConfigYamlImpl;
import de.derfrzocker.structure.control.impl.dao.WorldStructureConfigYamlDao;
import de.derfrzocker.structure.control.impl.v1_14_R1.NMSUtil_v1_14_R1;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.function.Supplier;

public class StructureControl extends JavaPlugin {

    static {
        ConfigurationSerialization.registerClass(WorldStructureConfigYamlImpl.class);
        ConfigurationSerialization.registerClass(StructureSettingsYamlImpl.class);
    }

    @Getter
    @Setter
    @NonNull
    private static StructureControl instance;

    private final StructureControlCommand structureControlCommand = new StructureControlCommand();

    private NMSUtil nmsUtils;

    @Getter
    private final DefaultSettings defaultSettings = new DefaultSettings(new File(getDataFolder(), "data/default-settings.yml"));

    @Override
    public void onLoad() {

        instance = this;

        if (Version.getCurrent() == Version.v1_14_R1) {
            nmsUtils = new NMSUtil_v1_14_R1(StructureControlServiceSupplier.INSTANCE);
        }

        // if no suitable version was found, throw an Exception and stop onLoad part
        if (nmsUtils == null)
            throw new IllegalStateException("no matching server version found, stop plugin start", new NullPointerException("overrider can't be null"));

    }

    @Override
    public void onEnable() {
        if (nmsUtils == null) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        final WorldStructureConfigYamlDao worldStructureConfigDao = new WorldStructureConfigYamlDao(new File(getDataFolder(), "data/world-structure-configs.yml"));
        final StructureControlService structureControlService = new StructureControlServiceImpl(worldStructureConfigDao);

        Bukkit.getServicesManager().register(StructureControlService.class, structureControlService, this, ServicePriority.Normal);

        if (!new File(getDataFolder(), "data/default-settings.yml").exists())
            saveResource("data/default-settings.yml", false);

        Structures.init();
        worldStructureConfigDao.init();
        defaultSettings.init();

        getCommand("structurecontrol").setExecutor(structureControlCommand);
        structureControlCommand.registerExecutor(new SetCommand(), "set");
        nmsUtils.replaceNMS();

        new Metrics(this);

    }

    private static final class StructureControlServiceSupplier implements Supplier<StructureControlService> {

        private static final StructureControlServiceSupplier INSTANCE = new StructureControlServiceSupplier();

        private StructureControlService service;

        @Override
        public StructureControlService get() {
            final StructureControlService tempService = Bukkit.getServicesManager().load(StructureControlService.class);

            if (service == null && tempService == null)
                throw new NullPointerException("The Bukkit Service has no StructureControlService and no StructureControlService is cached!");

            if (tempService != null && service != tempService)
                service = tempService;

            return service;
        }

    }

}
