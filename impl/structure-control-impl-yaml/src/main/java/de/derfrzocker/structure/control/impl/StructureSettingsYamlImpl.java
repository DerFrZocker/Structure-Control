package de.derfrzocker.structure.control.impl;

import de.derfrzocker.structure.control.api.*;
import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@SerializableAs("Structure-Control#StructureSettings")
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class StructureSettingsYamlImpl implements StructureSettings, ConfigurationSerializable {

    @Getter
    private final Map<Setting, Object> settings = new ConcurrentHashMap<>();
    @NonNull
    private final String structureName;
    private Structure structure;

    @Getter
    @Setter
    private boolean activated = true;

    public StructureSettingsYamlImpl(final @NonNull Structure structure) {
        this.structureName = structure.getName();
        this.structure = structure;
    }

    StructureSettingsYamlImpl(final String structureName, final @NonNull Map<Setting, Object> settings) {
        this(structureName);
        this.settings.putAll(settings);
    }

    @Override
    public Structure getStructure() {
        if (structure != null)
            return structure;

        final StructureControlService structureService = Bukkit.getServicesManager().load(StructureControlService.class);

        if (structureService == null)
            throw new IllegalStateException("No StructureControlService is registered in Bukkit ServiceManager");

        structure = structureService.getStructure(structureName).orElseThrow(() -> new NullPointerException("The Structure: " + structureName + " is not registered in the StructureControlService"));

        return structure;
    }

    @Override
    public Optional<Object> getValue(final @NonNull Setting setting) {
        return Optional.ofNullable(settings.get(setting));
    }

    @Override
    public void setValue(final @NonNull Setting setting, final @NonNull Object value) {
        if (!setting.getType().getType().isInstance(value))
            throw new IllegalArgumentException("The value: " + value + " is not an instance of " + setting.getType().getType());

        settings.put(setting, value);
    }

    @Override
    public StructureSettings clone() {
        return new StructureSettingsYamlImpl(structureName, getSettings());
    }

    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> map = new LinkedHashMap<>();

        map.put("structure-name", structureName);

        if (!activated)
            map.put("status", false);

        getSettings().forEach((setting, object) -> map.put(setting.getName() + "#" + setting.getType(), object));

        return map;
    }

    public static StructureSettingsYamlImpl deserialize(final @NonNull Map<String, Object> map) {
        final Map<Setting, Object> settings = new LinkedHashMap<>();
        final String structureName = (String) map.get("structure-name");

        map.forEach((key, value) -> {
            final Setting setting = createSetting(key);

            if (setting == null)
                return;

            settings.put(setting, value);
        });

        final StructureSettingsYamlImpl structureSettings = new StructureSettingsYamlImpl(structureName, settings);

        if (map.containsKey("status"))
            structureSettings.setActivated((Boolean) map.get("status"));

        return structureSettings;
    }

    private static Setting createSetting(final @NonNull String settingString) {
        final String[] split = settingString.split("#");

        if (split.length != 2)
            return null;

        try {
            final SettingType settingType = SettingType.valueOf(split[1]);

            return Setting.getSetting(split[0], settingType);
        } catch (final IllegalArgumentException e) {
            return null;
        }
    }

}
