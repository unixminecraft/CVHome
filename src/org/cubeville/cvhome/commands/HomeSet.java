package org.cubeville.cvhome.commands;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;

import org.cubeville.commons.commands.Command;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandResponse;

import org.cubeville.cvhome.Home;
import org.cubeville.cvhome.HomeManager;

public class HomeSet extends Command {

    public HomeSet() {
        super("");
        addOptionalBaseParameter(new CommandParameterString());
        addOptionalBaseParameter(new CommandParameterString());
    }

    @Override
    public CommandResponse execute(Player player, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters)
        throws CommandExecutionException {
        
        HomeManager homeManager = HomeManager.getInstance();
        Player sender = player;
        UUID senderId = sender.getUniqueId();
        
        if(baseParameters.size() == 0) {
            if(homeManager.doesPlayerHomeExist(senderId)) {
                boolean ret = homeManager.updatePlayerHome(senderId, senderId, 1, sender.getLocation(), false);
                if (!ret) { throw new CommandExecutionException("&cNo permission!"); }
                return new CommandResponse("&aHome set.");
            }
            List<Home> playerHomes = homeManager.getHomes();
        }
        
        return null;
    }
    
}
