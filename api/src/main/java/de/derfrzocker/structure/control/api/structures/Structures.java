package de.derfrzocker.structure.control.api.structures;

import de.derfrzocker.structure.control.api.Structure;
import de.derfrzocker.structure.control.api.StructureControlService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Structures {

    public final static MonsterRoom MONSTER_ROOM = registerStructure(new MonsterRoom());

    private static <S extends Structure> S registerStructure(final S structure) {
        final StructureControlService structureService = Bukkit.getServicesManager().load(StructureControlService.class);

        if (structureService == null)
            throw new IllegalStateException("Structures get initialized before a StructureControlService is ready");

        structureService.registerStructure(structure);

        return structure;
    }

    public static void init() {
    }

}
