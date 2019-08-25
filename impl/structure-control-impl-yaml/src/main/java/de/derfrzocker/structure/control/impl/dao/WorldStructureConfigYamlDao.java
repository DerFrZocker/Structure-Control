package de.derfrzocker.structure.control.impl.dao;

import de.derfrzocker.spigot.utils.dao.yaml.BasicYamlDao;
import de.derfrzocker.structure.control.api.WorldStructureConfig;
import de.derfrzocker.structure.control.api.dao.WorldStructureConfigDao;
import de.derfrzocker.structure.control.impl.WorldStructureConfigYamlImpl;
import lombok.NonNull;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.io.File;
import java.util.Optional;

public class WorldStructureConfigYamlDao extends BasicYamlDao<String, WorldStructureConfig> implements WorldStructureConfigDao {

    public WorldStructureConfigYamlDao(File file) {
        super(file);
    }

    @Override
    public Optional<WorldStructureConfig> get(final @NonNull String key) {
        return getFromStringKey(key);
    }

    @Override
    public void remove(final @NonNull WorldStructureConfig value) {
        saveFromStringKey(value.getName(), null);
    }

    @Override
    public void save(@NonNull WorldStructureConfig value) {
        if (!(value instanceof ConfigurationSerializable)) {
            value = new WorldStructureConfigYamlImpl(value.getName(), value.getStructureSettings());
        }

        saveFromStringKey(value.getName(), value);
    }
}
