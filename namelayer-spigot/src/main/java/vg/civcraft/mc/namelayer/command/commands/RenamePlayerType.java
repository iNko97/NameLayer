package vg.civcraft.mc.namelayer.command.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import vg.civcraft.mc.civmodcore.command.PlayerCommand;
import vg.civcraft.mc.namelayer.GroupManager;
import vg.civcraft.mc.namelayer.NameAPI;
import vg.civcraft.mc.namelayer.command.NameLayerTabCompleter;
import vg.civcraft.mc.namelayer.group.Group;
import vg.civcraft.mc.namelayer.permission.PermissionType;
import vg.civcraft.mc.namelayer.permission.PlayerType;
import vg.civcraft.mc.namelayer.permission.PlayerTypeHandler;

public class RenamePlayerType extends PlayerCommand {

	public RenamePlayerType(String name) {
		super(name);
		setIdentifier("nlrpt");
		setDescription("Renames a player type for a specific group");
		setUsage("/nlrpt <group> <playerType> <newName>");
		setArguments(3, 3);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.MAGIC + "BACK OFF");
			return true;
		}
		Player p = (Player) sender;
		Group group = GroupManager.getGroup(args[0]);
		if (group == null) {
			sender.sendMessage(ChatColor.RED + "That group doesn't exist");
			return true;
		}
		PlayerTypeHandler handler = group.getPlayerTypeHandler();
		PlayerType type = handler.getType(args[1]);
		if (type == null) {
			p.sendMessage(ChatColor.RED + "That player type doesn't exist");
			return true;
		}
		if (!NameAPI.getGroupManager().hasAccess(group, p.getUniqueId(),
				PermissionType.getPermission("RENAME_PLAYERTYPE"))) {
			p.sendMessage(ChatColor.RED
					+ "You don't have the required permissions to do this");
			return true;
		}
		String name = args[2];
		// enforce regulations on the name
		if (name.length() > 32) {
			p.sendMessage(ChatColor.RED
					+ "The player type name is not allowed to contain more than 32 characters");
			return true;
		}
		if (!CreateGroup.isConformName(name)) {
			p.sendMessage(ChatColor.RED
					+ "You used characters, which are not allowed");
			return true;
		}
		if (handler.getType(name) != null) {
			p.sendMessage(ChatColor.RED + "A type with this name already exists!");
			return true;
		}
		String oldName = type.getName();
		handler.renameType(type, name, true);
		p.sendMessage(ChatColor.GREEN +"Changed name of player type " + oldName + " to " + name);
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)){
			return null;
		}
		if (args.length == 0)
			return NameLayerTabCompleter.completeGroupWithPermission(null, PermissionType.getPermission("RENAME_PLAYERTYPE"), (Player) sender);
		else if (args.length == 1)
			return NameLayerTabCompleter.completeGroupWithPermission(args[0], PermissionType.getPermission("RENAME_PLAYERTYPE"), (Player) sender);
		else if (args.length == 2)
			return NameLayerTabCompleter.completePlayerType(args[1], GroupManager.getGroup(args [0]));
		else 
			return null;
	}
}