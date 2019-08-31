package de.derfrzocker.structure.control.impl.v1_14_R1.generator;

import com.mojang.datafixers.Dynamic;
import de.derfrzocker.structure.control.api.*;
import lombok.NonNull;
import net.minecraft.server.v1_14_R1.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

public class WorldGenDungeonsOverrider_v1_14_R1 extends WorldGenDungeons {

    @NonNull
    private final Biome biome;

    @NonNull
    private final Structure structure;

    @NonNull
    private final Supplier<StructureControlService> serviceSupplier;

    public WorldGenDungeonsOverrider_v1_14_R1(final Function<Dynamic<?>, ? extends WorldGenFeatureEmptyConfiguration> generatorAccess, final Biome biome, final Structure structure, final Supplier<StructureControlService> serviceSupplier) {
        super(generatorAccess);
        this.biome = biome;
        this.structure = structure;
        this.serviceSupplier = serviceSupplier;
    }

    @Override
    public boolean a(GeneratorAccess generatorAccess, ChunkGenerator<? extends GeneratorSettingsDefault> chunkGenerator, Random random, BlockPosition blockPosition, WorldGenFeatureEmptyConfiguration worldGenFeatureEmptyConfiguration) {
        final StructureControlService service = serviceSupplier.get();
        final Optional<WorldStructureConfig> worldStructureConfigOptional = service.getWorldStructureConfig(generatorAccess.getMinecraftWorld().getWorld());

        if (!worldStructureConfigOptional.isPresent())
            return super.a(generatorAccess, chunkGenerator, random, blockPosition, worldGenFeatureEmptyConfiguration);

        final WorldStructureConfig worldStructureConfig = worldStructureConfigOptional.get();

        final int xSize = random.nextInt(2) + 2;
        final int xSizeMinus = -xSize - 1;
        final int xSizePlus = xSize + 1;
        final int zSize = random.nextInt(2) + 2;
        final int zSizeMinus = -zSize - 1;
        final int zSizePlus = zSize + 1;
        int emptyBlocks = 0;

        for (int x = xSizeMinus; x <= xSizePlus; ++x) {
            for (int y = -1; y <= 4; ++y) {
                for (int z = zSizeMinus; z <= zSizePlus; ++z) {

                    final BlockPosition offsetBlockPosition = blockPosition.b(x, y, z);
                    final Material material = generatorAccess.getType(offsetBlockPosition).getMaterial();
                    final boolean buildable = material.isBuildable();

                    if (y == -1 && !buildable) {
                        return false;
                    }

                    if (y == 4 && !buildable) {
                        return false;
                    }

                    if ((x == xSizeMinus || x == xSizePlus || z == zSizeMinus || z == zSizePlus) && y == 0 && generatorAccess.isEmpty(offsetBlockPosition) && generatorAccess.isEmpty(offsetBlockPosition.up())) {
                        ++emptyBlocks;
                    }
                }
            }
        }


        if (emptyBlocks < 1 || emptyBlocks > 5) {
            return false;
        }
        for (int x = xSizeMinus; x <= xSizePlus; ++x) {
            for (int y = 3; y >= -1; --y) {
                for (int z = zSizeMinus; z <= zSizePlus; ++z) {

                    final BlockPosition structurePosition = blockPosition.b(x, y, z);

                    if (x == xSizeMinus || y == -1 || z == zSizeMinus || x == xSizePlus || y == 4 || z == zSizePlus) {
                        if (structurePosition.getY() >= 0 && !generatorAccess.getType(structurePosition.down()).getMaterial().isBuildable()) {
                            generatorAccess.setTypeAndData(structurePosition, Blocks.CAVE_AIR.getBlockData(), 2);
                        } else if (generatorAccess.getType(structurePosition).getMaterial().isBuildable() && generatorAccess.getType(structurePosition).getBlock() != Blocks.CHEST) {
                            if (y == -1 && random.nextInt(4) != 0) {
                                generatorAccess.setTypeAndData(structurePosition, Blocks.MOSSY_COBBLESTONE.getBlockData(), 2);
                            } else {
                                generatorAccess.setTypeAndData(structurePosition, Blocks.COBBLESTONE.getBlockData(), 2);
                            }
                        }
                    } else if (generatorAccess.getType(structurePosition).getBlock() != Blocks.CHEST) {
                        generatorAccess.setTypeAndData(structurePosition, Blocks.CAVE_AIR.getBlockData(), 2);
                    }
                }
            }
        }
        for (int maxChests = 0; maxChests < (int) service.getValue(structure, Setting.getSetting("CHESTS", SettingType.INTEGER), worldStructureConfig, biome); ++maxChests) {
            for (int triesPerChest = 0; triesPerChest < (int) service.getValue(structure, Setting.getSetting("CHESTS_TRIES", SettingType.INTEGER), worldStructureConfig, biome); ++triesPerChest) {
                final int x = blockPosition.getX() + random.nextInt(xSize * 2 + 1) - xSize;
                final int y = blockPosition.getY();
                final int z = blockPosition.getZ() + random.nextInt(zSize * 2 + 1) - zSize;
                final BlockPosition chestPosition = new BlockPosition(x, y, z);
                if (generatorAccess.isEmpty(chestPosition)) {
                    int solidBlocks = 0;
                    for (final EnumDirection enumDirection : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
                        if (generatorAccess.getType(chestPosition.shift(enumDirection)).getMaterial().isBuildable()) { // note: air = false, stone = true
                            ++solidBlocks;
                        }
                    }
                    if (solidBlocks == 1) {
                        generatorAccess.setTypeAndData(chestPosition, StructurePiece.a(generatorAccess, chestPosition, Blocks.CHEST.getBlockData()), 2);
                        TileEntityLootable.a(generatorAccess, random, chestPosition, LootTables.d);
                        break;
                    }
                }
            }
        }

        generatorAccess.setTypeAndData(blockPosition, Blocks.SPAWNER.getBlockData(), 2);

        final TileEntity tileEntity = generatorAccess.getTileEntity(blockPosition);

        final List<EntityTypes<?>> entities = new LinkedList<>();

        Setting[] entityEntries = structure.getSettings().stream().filter(setting -> setting.getName().contains("_ENTRIES")).filter(setting -> setting.getType() == SettingType.INTEGER).toArray(Setting[]::new);

        for (final Setting setting : entityEntries) {
            final EntityTypes<?> entity = IRegistry.ENTITY_TYPE.get(new MinecraftKey(setting.getName().replace("_ENTRIES", "").toLowerCase()));
            final int entries = (int) service.getValue(structure, setting, worldStructureConfig, biome);
            for (int i = 0; i < entries; i++)
                entities.add(entity);
        }


        if (tileEntity instanceof TileEntityMobSpawner) {
            ((TileEntityMobSpawner) tileEntity).getSpawner().setMobName(entities.toArray(new EntityTypes<?>[0])[random.nextInt(entities.size())]);
        } else {
            System.out.println("Failed to fetch mob spawner entity at (" + blockPosition.getX() + ", " + blockPosition.getY() + ", " + blockPosition.getZ() + ")");
        }

        return true;
    }

}
