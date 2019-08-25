package de.derfrzocker.structure.control.api;

import org.bukkit.World;

import java.util.Optional;
import java.util.Set;

public interface StructureControlService {

    void registerStructure(Structure structure);

    Optional<Structure> getStructure(String name);

    Optional<WorldStructureConfig> getWorldStructureConfig(World world);

    WorldStructureConfig createWorldStructureConfig(World world);

    void saveWorldStructureConfig(WorldStructureConfig worldStructureConfig);

    void removeWorldStructureConfig(WorldStructureConfig worldStructureConfig);

    Set<WorldStructureConfig> getAllWorldStructureConfigs();

    Object getValue(Structure structure, Setting setting, WorldStructureConfig worldStructureConfig, Biome biome);

}
