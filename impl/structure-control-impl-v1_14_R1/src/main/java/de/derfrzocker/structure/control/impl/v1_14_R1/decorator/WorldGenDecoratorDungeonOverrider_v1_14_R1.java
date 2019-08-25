package de.derfrzocker.structure.control.impl.v1_14_R1.decorator;

import com.mojang.datafixers.Dynamic;
import de.derfrzocker.structure.control.api.*;
import lombok.NonNull;
import net.minecraft.server.v1_14_R1.*;

import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class WorldGenDecoratorDungeonOverrider_v1_14_R1 extends WorldGenDecoratorDungeon {

    @NonNull
    private final Biome biome;

    @NonNull
    private final Structure structure;

    @NonNull
    private final Supplier<StructureControlService> serviceSupplier;

    public WorldGenDecoratorDungeonOverrider_v1_14_R1(final @NonNull Function<Dynamic<?>, ? extends WorldGenDecoratorDungeonConfiguration> dynamicFunction, final Biome biome, final Structure structure, final Supplier<StructureControlService> serviceSupplier) {
        super(dynamicFunction);
        this.biome = biome;
        this.structure = structure;
        this.serviceSupplier = serviceSupplier;
    }

    @Override
    public Stream<BlockPosition> a(final @NonNull GeneratorAccess generatorAccess, final @NonNull ChunkGenerator<? extends GeneratorSettingsDefault> chunkGenerator, final @NonNull Random random, final @NonNull WorldGenDecoratorDungeonConfiguration worldGenDecoratorDungeonConfiguration, final @NonNull BlockPosition blockPosition) {
        final StructureControlService service = serviceSupplier.get();

        final Optional<WorldStructureConfig> worldStructureConfigOptional = service.getWorldStructureConfig(generatorAccess.getMinecraftWorld().getWorld());

        if (!worldStructureConfigOptional.isPresent())
            return super.a(generatorAccess, chunkGenerator, random, worldGenDecoratorDungeonConfiguration, blockPosition);

        final WorldStructureConfig worldStructureConfig = worldStructureConfigOptional.get();

        final int tries = (int) service.getValue(structure, Setting.getSetting("TRIES_PER_CHUNK", SettingType.INTEGER), worldStructureConfig, biome);
        final int minimumHeight = (int) service.getValue(structure, Setting.getSetting("MINIMUM_HEIGHT", SettingType.INTEGER), worldStructureConfig, biome);
        final int heightRange = (int) service.getValue(structure, Setting.getSetting("HEIGHT_RANGE", SettingType.INTEGER), worldStructureConfig, biome);

        return IntStream.range(0, tries).mapToObj((ignore) -> {
            int x = random.nextInt(16);
            int y = random.nextInt(heightRange) + minimumHeight;
            int z = random.nextInt(16);
            return blockPosition.b(x, y, z);
        });
    }

}
