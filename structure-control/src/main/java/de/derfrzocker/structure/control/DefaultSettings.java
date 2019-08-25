package de.derfrzocker.structure.control;

import de.derfrzocker.spigot.utils.dao.yaml.YamlDao;
import de.derfrzocker.structure.control.api.Structure;
import de.derfrzocker.structure.control.api.StructureSettings;
import lombok.NonNull;

import java.io.File;

public class DefaultSettings extends YamlDao<StructureSettings> {
    public DefaultSettings(File file) {
        super(file);
    }

    @Override
    public void init() {
        RELOAD_ABLES.add(this);
        super.init();
    }

    public StructureSettings getStructureSettings(final @NonNull Structure structure) {
        return getFromStringKey(structure.getName()).get();
    }

}
