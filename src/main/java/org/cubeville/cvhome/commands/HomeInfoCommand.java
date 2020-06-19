package org.cubeville.cvhome.commands;

import java.text.DecimalFormat;
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

public class HomeInfoCommand extends Command {
	
	private CVHome plugin;
	
    public HomeInfoCommand(CVHome plugin) {
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
        if(!sender.hasPermission("cvhome.command.homeinfo.other") && !samePlayer) { throw new CommandExecutionException("&cNo permission."); }
        
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
    		throw new CommandExecutionException("&cPlease pick only 1 home per player to view at a time.");
    	}
    	
    	Player player = plugin.getServer().getPlayer(uniqueId);
    	if(player.isOnline()) { plugin.updateMaxHomes(player); }
    	
    	Home home = plugin.getHome(uniqueId);
    	if(home == null) { throw new CommandExecutionException("&6" + playerName + " &chas not set a home yet."); }
    	
    	int max = home.getMaxHomes();
    	if(number > max) { throw new CommandExecutionException("&6" + playerName + " &ccan have a maximum of " + max + (max == 1 ? " home." : " homes.")); }
    	
    	Location loc = home.getHome(number);
    	CommandResponse response = new CommandResponse();
    	response.addMessage("&8--------------------------------");
    	response.addMessage("&6" + playerName + "'s &bHomes");
    	response.addMessage("&bMaximum allowed: &6" + max);
    	response.addMessage("&8--------------------------------");
    	response.addMessage("&bHome number: &6" + number);
    	
    	if(loc == null) {
    		response.addMessage("&cNot set yet.");
    	}
    	else {
    		DecimalFormat format = new DecimalFormat("#.###");
    		response.addMessage("&bWorld   : &6" + loc.getWorld().getName());
    		response.addMessage("&bX Coord : &6" + format.format(loc.getX()));
    		response.addMessage("&bY Coord : &6" + format.format(loc.getY()));
    		response.addMessage("&bZ Coord : &6" + format.format(loc.getZ()));
    		response.addMessage("&bPitch   : &6" + format.format(loc.getPitch()));
    		response.addMessage("&bYaw     : &6" + format.format(loc.getYaw()));
    	}
    	response.addMessage("&8--------------------------------");
    	return response;
    }
}