package de.derfrzocker.structure.control.api.structures;

import de.derfrzocker.structure.control.api.Biome;
import de.derfrzocker.structure.control.api.Setting;
import de.derfrzocker.structure.control.api.SettingType;
import de.derfrzocker.structure.control.api.Structure;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class MonsterRoom implements Structure {

    private static final String NAME = "MONSTER_ROOM";
    private static final Set<Setting> SETTINGS;
    private static final Set<Biome> BIOMES;

    static {
        final Set<Setting> settings = new HashSet<>();

        settings.add(Setting.getSetting("HEIGHT_RANGE", SettingType.INTEGER));
        settings.add(Setting.getSetting("MINIMUM_HEIGHT", SettingType.INTEGER));
        settings.add(Setting.getSetting("TRIES_PER_CHUNK", SettingType.INTEGER));
        settings.add(Setting.getSetting("CHESTS", SettingType.INTEGER));
        settings.add(Setting.getSetting("CHESTS_TRIES", SettingType.INTEGER));
        settings.add(Setting.getSetting("SKELETON_ENTRIES", SettingType.INTEGER));
        settings.add(Setting.getSetting("ZOMBIE_ENTRIES", SettingType.INTEGER));
        settings.add(Setting.getSetting("SPIDER_ENTRIES", SettingType.INTEGER));

        SETTINGS = Collections.unmodifiableSet(settings);

        final Set<Biome> biomes = new HashSet<>(Arrays.asList(Biome.values()));

        //TODO add remove biomes

        BIOMES = Collections.unmodifiableSet(biomes);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Set<Setting> getSettings() {
        return SETTINGS;
    }

    @Override
    public Set<Biome> getBiomes() {
        return BIOMES;
    }

}
