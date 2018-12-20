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
import org.cubeville.cvhome.Home;
import org.cubeville.cvhome.HomeManager;

public class HomeSet extends Command {

    public HomeSet() {
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
        
        HomeManager homeManager = HomeManager.instance();
        Player sender = player;
        boolean adminOverride = sender.hasPermission("cvhome.admin.sethome");

        if(baseParameters.size() == 0) {
            
            if(flags.size() == 0) {
                return setPlayerHome(homeManager, sender, 1, sender.getLocation());
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
                return setPlayerHome(homeManager, sender, homeNumber, sender.getLocation());
            }
            else {
                throw new CommandExecutionException("&cSyntax: /sethome");
                //TODO: REMOVE ABOVE, ADD BELOW, ONCE MULTIPLE HOMES HAS BEEN APPROVED.
                //throw new CommandExecutionException("&cSyntax: /sethome [number]");
            }
        }
        else {
            if(adminOverride) {

                String possiblePlayerName = (String) baseParameters.get(0);
                Player possiblePlayer = homeManager.getPlugin().getServer().getPlayerExact(possiblePlayerName);
                
                if(flags.size() == 0) {
                    if(possiblePlayer != null) {
                        return setPlayerHome(homeManager, possiblePlayer, 1, sender.getLocation());
                    }
                    else {
                        return setPlayerHome(homeManager, possiblePlayerName, 1, sender.getLocation());
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
                        return setPlayerHome(homeManager, possiblePlayer, homeNumber, sender.getLocation());
                    }
                    else {
                        return setPlayerHome(homeManager, possiblePlayerName, homeNumber, sender.getLocation());
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
    
    private CommandResponse setPlayerHome(HomeManager homeManager, Player player, int homeNumber,
            Location location) throws CommandExecutionException {
        
        if(homeManager.homeExists(player)) {
            System.out.println("homeManager.updatePlayerMaxHomes:" + System.currentTimeMillis());
            homeManager.updatePlayerMaxHomes(player);
            System.out.println("homeManager.getMaxPlayerHomes:" + System.currentTimeMillis());
            int maxHomes = homeManager.getMaxHomes(player);
            if(homeNumber > maxHomes) {
                throw new CommandExecutionException("&cNo permission.");
            }
            else {
                System.out.println("homeManager.setPlayerHome:" + System.currentTimeMillis());
                homeManager.setHome(player, homeNumber, location);
            }
        }
        else {
            Home playerHome = null;
            try {
                playerHome = new Home(player.getUniqueId(), player.getName());
                homeManager.addHome(playerHome);
                homeManager.setHome(player, homeNumber, location);
            }
            catch(IllegalArgumentException e) {
                throw new CommandExecutionException("&cInternal error, please try again later.");
            }
        }
        System.out.println("setPlayerHome done: " + System.currentTimeMillis());
        return new CommandResponse("&aHome set.");
    }
    
    private CommandResponse setPlayerHome(HomeManager homeManager, String playerName, int homeNumber,
            Location location) throws CommandExecutionException {
        
        if(homeManager.homeExists(playerName)) {
            int maxHomes = homeManager.getMaxHomes(playerName);
            if(homeNumber > maxHomes) {
                throw new CommandExecutionException("&cPlayer does not have permission for home " + homeNumber + "!");
            }
            else {
                homeManager.setHome(playerName, homeNumber, location);
                return new CommandResponse("&aHome set.");
            }
        }
        else {
            throw new CommandExecutionException("&cPlayer not found!&r &6Please visit&r " + 
                    "&6namemc.com and check other names for that player!&r");
        }
    }
}