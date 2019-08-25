package de.derfrzocker.structure.control.impl;

import de.derfrzocker.structure.control.StructureControl;
import de.derfrzocker.structure.control.api.*;
import de.derfrzocker.structure.control.api.dao.WorldStructureConfigDao;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.World;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class StructureControlServiceImpl implements StructureControlService {

    private final Map<String, Structure> structures = new ConcurrentHashMap<>();
    @NonNull
    private final WorldStructureConfigDao dao;

    @Override
    public void registerStructure(final @NonNull Structure structure) {
        if (getStructure(structure.getName()).isPresent())
            throw new IllegalArgumentException("Structure " + structure.getName() + " is already registered!");

        structures.put(structure.getName().toUpperCase(), structure);
    }

    @Override
    public Optional<Structure> getStructure(final @NonNull String name) {
        return Optional.ofNullable(structures.get(name.toUpperCase()));
    }

    @Override
    public Optional<WorldStructureConfig> getWorldStructureConfig(final @NonNull World world) {
        return dao.get(world.getName());
    }

    @Override
    public WorldStructureConfig createWorldStructureConfig(final @NonNull World world) {
        return new WorldStructureConfigYamlImpl(world.getName());
    }

    @Override
    public void saveWorldStructureConfig(final @NonNull WorldStructureConfig worldStructureConfig) {
        dao.save(worldStructureConfig);
    }

    @Override
    public void removeWorldStructureConfig(final @NonNull WorldStructureConfig worldStructureConfig) {
        dao.remove(worldStructureConfig);
    }

    @Override
    public Set<WorldStructureConfig> getAllWorldStructureConfigs() {
        return dao.getAll();
    }

    @Override
    public Object getValue(final @NonNull Structure structure, final @NonNull Setting setting, final @NonNull WorldStructureConfig worldStructureConfig, final @NonNull Biome biome) {
        return worldStructureConfig.getStructureSettings(structure).
                orElseGet(() -> StructureControl.getInstance().getDefaultSettings().getStructureSettings(structure)).getValue(setting).
                orElseGet(() -> StructureControl.getInstance().getDefaultSettings().getStructureSettings(structure).getValue(setting).get());
    }
}
