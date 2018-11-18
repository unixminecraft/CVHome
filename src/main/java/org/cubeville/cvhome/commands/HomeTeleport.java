package org.cubeville.cvhome.commands;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import org.cubeville.commons.commands.Command;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.cvhome.HomeManager;

public class HomeTeleport extends Command {

    public HomeTeleport() {
        super("");
        addFlag("1");
        addFlag("2");
        addFlag("3");
        addFlag("4");
        addOptionalBaseParameter(new CommandParameterString());
    }

    @Override
    public CommandResponse execute(Player player, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters)
            throws CommandExecutionException {
        
        HomeManager homeManager = HomeManager.getInstance();
        Player sender = player;
        boolean adminOverride = sender.hasPermission("cvhome.admin.teleporthome");
        
        if(baseParameters.size() == 0) {
            
            if(flags.size() == 0) {
                return teleportToPlayerHome(homeManager, sender, sender, 1);
            }
            else if(flags.size() == 1) {
                int homeNumber = 0;
                if(flags.contains("4")) { homeNumber = 4; }
                else if(flags.contains("3")) { homeNumber = 3; }
                else if(flags.contains("2")) { homeNumber = 2; }
                else if(flags.contains("1")) { homeNumber = 1; }
                else {
                    throw new CommandExecutionException("&cInternal error, please try again later.");
                }
                if(homeNumber == 0) {
                    throw new CommandExecutionException("&cInternal error, please try again later.");
                }
                return teleportToPlayerHome(homeManager, sender, sender, homeNumber);
            }
            else {
                throw new CommandExecutionException("&cSyntax: /home");
                //TODO: REMOVE ABOVE, ADD BELOW, ONCE MULTIPLE HOMES HAS BEEN APPROVED.
                //throw new CommandExecutionException("&cSyntax: /home [number]");
            }
        }
        else {
            if(adminOverride) {
                
                String possiblePlayerName = (String) baseParameters.get(0);
                Player possiblePlayer = homeManager.getPlugin().getServer().getPlayerExact(possiblePlayerName);
                
                if(flags.size() == 0) {
                    if(possiblePlayer != null) {
                        return teleportToPlayerHome(homeManager, sender, possiblePlayer, 1);
                    }
                    else {
                        return teleportToPlayerHome(homeManager, sender, possiblePlayerName, 1);
                    }
                }
                else if(flags.size() == 1) {
                    int homeNumber = 0;
                    if(flags.contains("4")) { homeNumber = 4; }
                    else if(flags.contains("3")) { homeNumber = 3; }
                    else if(flags.contains("2")) { homeNumber = 2; }
                    else if(flags.contains("1")) { homeNumber = 1; }
                    else {
                        throw new CommandExecutionException("&cHow did you even do that? Nevermind, don't do it again.");
                    }
                    if(homeNumber == 0) {
                        throw new CommandExecutionException("&cHow did you even do that? Nevermind, don't do it again.");
                    }
                    if(possiblePlayer != null) {
                        return teleportToPlayerHome(homeManager, sender, possiblePlayer, homeNumber);
                    }
                    else {
                        return teleportToPlayerHome(homeManager, sender, possiblePlayerName, homeNumber);
                    }
                }
                else {
                    throw new CommandExecutionException("&cPlease only use 1 home at a time.");
                }
            }
            else {
                throw new CommandExecutionException("&cNo permission.");
            }
        }
    }
    
    private CommandResponse teleportToPlayerHome(HomeManager homeManager, Player sender, Player player,
            int homeNumber) throws CommandExecutionException {
        
        if(homeManager.doesPlayerHomeExist(player)) {
            Location location = homeManager.getPlayerHomeForTeleport(player, homeNumber);
            if(location != null) {
                sender.teleport(location);
                return new CommandResponse("&aTeleported.");
            }
            else {
                return new CommandResponse("&cPlayer home not set.");
            }
        }
        else {
            throw new CommandExecutionException("&cPlayer home not found.");
        }
    }
    
    private CommandResponse teleportToPlayerHome(HomeManager homeManager, Player sender, String playerName,
            int homeNumber) throws CommandExecutionException {
        
        if(homeManager.doesPlayerHomeExist(playerName)) {
            Location location = homeManager.getPlayerHomeForTeleport(playerName, homeNumber);
            if(location != null) {
                sender.teleport(location);
                return new CommandResponse("&aTeleported.");
            }
            else {
                return new CommandResponse("&cPlayer home not set.");
            }
        }
        else {
            throw new CommandExecutionException("&cPlayer not found!&r &6Please visit&r " +
                    "&6namemc.com and check other names for that player!&r");
        }
    }
}
