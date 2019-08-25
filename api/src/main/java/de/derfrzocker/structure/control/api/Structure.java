package de.derfrzocker.structure.control.api;

import java.util.Set;

public interface Structure {

    String getName();

    Set<Setting> getSettings();

    Set<Biome> getBiomes();

}
