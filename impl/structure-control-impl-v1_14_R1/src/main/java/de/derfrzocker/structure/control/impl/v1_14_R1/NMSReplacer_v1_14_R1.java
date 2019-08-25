package de.derfrzocker.structure.control.impl.v1_14_R1;

import com.mojang.datafixers.Dynamic;
import de.derfrzocker.structure.control.api.Biome;
import de.derfrzocker.structure.control.api.StructureControlService;
import de.derfrzocker.structure.control.api.structures.Structures;
import de.derfrzocker.structure.control.impl.v1_14_R1.decorator.WorldGenDecoratorDungeonOverrider_v1_14_R1;
import de.derfrzocker.structure.control.impl.v1_14_R1.generator.WorldGenDungeonsOverrider_v1_14_R1;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_14_R1.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class NMSReplacer_v1_14_R1 {

    @NonNull
    private final Supplier<StructureControlService> serviceSupplier;

    void replaceNMS() {
        for (Field field : Biomes.class.getFields()) {
            try {
                replaceBase((BiomeBase) field.get(null));
            } catch (Exception e) {
                throw new RuntimeException("Unexpected error while hook in NMS for Biome field: " + field.getName(), e);
            }
        }
    }

    private void replaceBase(final BiomeBase base) throws NoSuchFieldException, IllegalAccessException {
        final Biome biome;

        try {
            biome = Biome.valueOf(IRegistry.BIOME.getKey(base).getKey().toUpperCase());
        } catch (IllegalArgumentException e) {
            return;
        }

        final Map<WorldGenStage.Decoration, List<WorldGenFeatureConfigured<?>>> map = get(base);

        final List<WorldGenFeatureConfigured<?>> list = map.get(WorldGenStage.Decoration.UNDERGROUND_STRUCTURES);

        for (WorldGenFeatureConfigured<?> composite : list)
            replace(composite, biome);
    }

    @SuppressWarnings("unchecked")
    private Map<WorldGenStage.Decoration, List<WorldGenFeatureConfigured<?>>> get(final BiomeBase base)
            throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, ClassCastException {

        final Field field = getField(BiomeBase.class, "r");
        field.setAccessible(true);

        return (Map<WorldGenStage.Decoration, List<WorldGenFeatureConfigured<?>>>) field.get(base);
    }

    @SuppressWarnings("rawtypes")
    private Field getField(final Class clazz, final String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            final Class superClass = clazz.getSuperclass();
            if (superClass == null) {
                throw e;
            } else {
                return getField(superClass, fieldName);
            }
        }
    }

    private void replace(final WorldGenFeatureConfigured<?> composite, final Biome biome) throws NoSuchFieldException, IllegalAccessException {
        if (replaceMonsterRoom(composite, biome))
            return;

    }

    private boolean replaceMonsterRoom(WorldGenFeatureConfigured<?> composite, Biome biome) throws NoSuchFieldException, IllegalAccessException {
        final WorldGenFeatureCompositeConfiguration worldGenFeatureCompositeConfiguration = (WorldGenFeatureCompositeConfiguration) composite.b;

        if (!worldGenFeatureCompositeConfiguration.a.a.equals(WorldGenerator.MONSTER_ROOM))
            return false;

        {
            final Field field = getField(WorldGenFeatureConfigured.class, "a");
            field.setAccessible(true);
            field.set(worldGenFeatureCompositeConfiguration.a, new WorldGenDungeonsOverrider_v1_14_R1(getDynamicFunction(worldGenFeatureCompositeConfiguration.a.a), biome, Structures.MONSTER_ROOM, serviceSupplier));
        }

        {
            final Field field = getField(WorldGenDecoratorConfigured.class, "a");
            field.setAccessible(true);
            field.set(worldGenFeatureCompositeConfiguration.b, new WorldGenDecoratorDungeonOverrider_v1_14_R1(getDynamicFunction(worldGenFeatureCompositeConfiguration.b.a), biome, Structures.MONSTER_ROOM, serviceSupplier));
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    private Function<Dynamic<?>, ? extends WorldGenFeatureEmptyConfiguration> getDynamicFunction(WorldGenerator<?> worldGenerator) throws IllegalAccessException, NoSuchFieldException {
        final Field field = getField(WorldGenerator.class, "a");
        field.setAccessible(true);
        return (Function<Dynamic<?>, ? extends WorldGenFeatureEmptyConfiguration>) field.get(worldGenerator);
    }

    @SuppressWarnings("unchecked")
    private Function<Dynamic<?>, ? extends WorldGenDecoratorDungeonConfiguration> getDynamicFunction(WorldGenDecorator<?> worldGenerator) throws IllegalAccessException, NoSuchFieldException {
        final Field field = getField(WorldGenDecorator.class, "M");
        field.setAccessible(true);
        return (Function<Dynamic<?>, ? extends WorldGenDecoratorDungeonConfiguration>) field.get(worldGenerator);
    }

}
