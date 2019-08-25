package de.derfrzocker.structure.control.command;

import de.derfrzocker.spigot.utils.CommandUtil;
import de.derfrzocker.structure.control.StructureControl;
import de.derfrzocker.structure.control.api.*;
import de.derfrzocker.structure.control.impl.StructureSettingsYamlImpl;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

public class SetCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        //TODO temp

        CommandUtil.runAsynchronously(commandSender, StructureControl.getInstance(), () -> {
            String worldName = args[0];
            String structureName = args[1];
            String settingName = args[2];
            String value = args[3];

            StructureControlService structureService = Bukkit.getServicesManager().load(StructureControlService.class);
            World world = Bukkit.getWorld(worldName);
            Structure structure = structureService.getStructure(structureName.toUpperCase()).get();
            Setting setting = structure.getSettings().stream().filter(value2 -> value2.getName().equalsIgnoreCase(settingName)).findAny().get();
            WorldStructureConfig worldStructureConfig = structureService.getWorldStructureConfig(world).orElseGet(() -> structureService.createWorldStructureConfig(world));

            StructureSettings structureSettings = worldStructureConfig.getStructureSettings(structure).orElseGet(() -> {
                StructureSettings structureSettings2 = new StructureSettingsYamlImpl(structure);
                worldStructureConfig.setStructureSettings(structureSettings2);
                return structureSettings2;
            });

            if (setting.getType() == SettingType.INTEGER) {
                structureSettings.setValue(setting, Integer.parseInt(value));
            } else if (setting.getType() == SettingType.BOOLEAN) {
                structureSettings.setValue(setting, Boolean.parseBoolean(value));
            } else throw new IllegalArgumentException();

            structureService.saveWorldStructureConfig(worldStructureConfig);

        });


        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        //TODO add tapComplete
        return null;
    }
}
