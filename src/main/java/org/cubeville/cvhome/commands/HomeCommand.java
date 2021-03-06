package org.cubeville.cvhome.commands;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import org.cubeville.commons.commands.Command;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.cvhome.CVHome;
import org.cubeville.cvhome.Home;

public class HomeCommand extends Command {
	
	private CVHome plugin;
	
    public HomeCommand(CVHome plugin) {
        super("");
        addFlag("1");
        addFlag("2");
        addFlag("3");
        addFlag("4");
        addOptionalBaseParameter(new CommandParameterString());
        this.plugin = plugin;
    }

    @Override
    public CommandResponse execute(Player sender, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters) throws CommandExecutionException {
        String playerName;
        if(baseParameters.isEmpty()) { playerName = sender.getName(); }
        else { playerName = (String) baseParameters.get(0); }
        boolean samePlayer = sender.getName().equalsIgnoreCase(playerName);
        if(!sender.hasPermission("cvhome.command.home.other") && !samePlayer) { throw new CommandExecutionException("&cNo permission."); }
        
        UUID uniqueId;
        if(samePlayer) { uniqueId = sender.getUniqueId(); }
        else { uniqueId = plugin.getUniqueId(playerName); }
        if(uniqueId == null) { throw new CommandExecutionException("&cPlayer &6" + playerName + " &cnot found."); }
        playerName = plugin.getName(uniqueId);
    	
        int number;
        if(flags.size() == 0) {
        	number = 1;
        }
        else if(flags.size() == 1) {
        	if(flags.contains("4")) { number = 4; }
        	else if(flags.contains("3")) { number = 3; }
        	else if(flags.contains("2")) { number = 2; }
        	else { number = 1; }
        }
        else {
        	throw new CommandExecutionException("&cSyntax: /sethome");
        }
        
        Player player;
        if(samePlayer) { player = sender; }
        else { player = plugin.getServer().getPlayer(uniqueId); }
        if(player != null && player.isOnline()) { plugin.updateMaxHomes(player); }
        
        Home home = plugin.getHome(uniqueId);
        if(player != null && player.isOnline() && plugin.getMaxHomes(player) < number) { throw new CommandExecutionException("&cNo permission."); }
        else if(home == null) { throw new CommandExecutionException("&cNo home set."); }
        else if(home.getMaxHomes() < number) { throw new CommandExecutionException("&cNo permission."); }
        
        Location loc = home.getHome(number);
        if(loc == null) { throw new CommandExecutionException("&cHome not set."); }
        sender.teleport(loc);
        
        return new CommandResponse("&aTeleported.");
    }
}