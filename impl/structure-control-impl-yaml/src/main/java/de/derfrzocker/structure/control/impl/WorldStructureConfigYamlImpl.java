package de.derfrzocker.structure.control.impl;

import de.derfrzocker.structure.control.api.Structure;
import de.derfrzocker.structure.control.api.StructureControlService;
import de.derfrzocker.structure.control.api.StructureSettings;
import de.derfrzocker.structure.control.api.WorldStructureConfig;
import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@SerializableAs("Structure-Control#WorldStructureConfig")
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class WorldStructureConfigYamlImpl implements WorldStructureConfig, ConfigurationSerializable {

    @Getter
    private final String name;

    @Getter
    private final Map<Structure, StructureSettings> structureSettings = new ConcurrentHashMap<>();

    public WorldStructureConfigYamlImpl(final String name, final @NonNull Map<Structure, StructureSettings> structureSettings) {
        this(name);
        this.structureSettings.putAll(structureSettings);
    }

    @Override
    public Optional<StructureSettings> getStructureSettings(final @NonNull Structure structure) {
        return Optional.ofNullable(structureSettings.get(structure));
    }

    @Override
    public void setStructureSettings(final @NonNull StructureSettings structureSettings) {
        this.structureSettings.put(structureSettings.getStructure(), structureSettings);
    }

    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> map = new LinkedHashMap<>();

        map.put("name", name);

        getStructureSettings().entrySet().stream().filter(entry -> !entry.getValue().getSettings().isEmpty() || !entry.getValue().isActivated()).
                map(entry -> {
                    if (entry.getValue() instanceof ConfigurationSerializable)
                        return entry.getValue();
                    final StructureSettingsYamlImpl oreSettingsYaml = new StructureSettingsYamlImpl(entry.getKey().getName(), entry.getValue().getSettings());
                    oreSettingsYaml.setActivated(entry.getValue().isActivated());
                    return oreSettingsYaml;
                }).forEach(value -> map.put(value.getStructure().getName(), value));

        return map;
    }

    public static WorldStructureConfigYamlImpl deserialize(final @NonNull Map<String, Object> map) {
        final Map<Structure, StructureSettings> structureSettings = new LinkedHashMap<>();
        final String name = (String) map.get("name");
        final StructureControlService structureService = Bukkit.getServicesManager().load(StructureControlService.class);

        if (structureService == null)
            throw new IllegalStateException("No StructureControlService is registered in Bukkit ServiceManager");

        map.entrySet().stream().filter(entry -> structureService.getStructure(entry.getKey()).isPresent()).
                forEach(entry -> structureSettings.put(structureService.getStructure(entry.getKey()).get(), (StructureSettings) entry.getValue()));

        return new WorldStructureConfigYamlImpl(name, structureSettings);
    }

}
