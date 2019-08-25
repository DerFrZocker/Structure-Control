package de.derfrzocker.structure.control.command;

import de.derfrzocker.spigot.utils.CommandSeparator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class StructureControlCommand extends CommandSeparator {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        //TODO add tapComplete
        return null;
    }
}
