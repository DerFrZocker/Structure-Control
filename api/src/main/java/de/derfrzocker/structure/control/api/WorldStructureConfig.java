package de.derfrzocker.structure.control.api;

import java.util.Map;
import java.util.Optional;

public interface WorldStructureConfig {

    String getName();

    Optional<StructureSettings> getStructureSettings(Structure structure);

    void setStructureSettings(StructureSettings structureSettings);

    Map<Structure, StructureSettings> getStructureSettings();

}
