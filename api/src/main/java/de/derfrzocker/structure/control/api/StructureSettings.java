package de.derfrzocker.structure.control.api;

import java.util.Map;
import java.util.Optional;

public interface StructureSettings extends Cloneable {

    Structure getStructure();

    Optional<Object> getValue(Setting setting);

    void setValue(Setting setting, Object value);

    Map<Setting, Object> getSettings();

    boolean isActivated();

    void setActivated(boolean status);

    StructureSettings clone();

}
