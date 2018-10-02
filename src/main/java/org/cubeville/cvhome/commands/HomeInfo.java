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

public class HomeInfo extends Command {

    public HomeInfo() {
        super("");
        addFlag("1");
        addFlag("2");
        addFlag("3");
        addFlag("4");
        addBaseParameter(new CommandParameterString());
    }

    @Override
    public CommandResponse execute(Player player, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters)
            throws CommandExecutionException {
        
        HomeManager homeManager = HomeManager.getInstance();
        Player sender = player;
        
        if(!sender.hasPermission("cvhome.admin.infohome")) {
            throw new CommandExecutionException("&cNo permission.");
        }
        
        String possiblePlayerName = (String) baseParameters.get(0);
        Player possiblePlayer = homeManager.getPlugin().getServer().getPlayerExact(possiblePlayerName);
        
        if(flags.size() == 0) {
            if(possiblePlayer != null) {
                return getPlayerHomeInfo(homeManager, possiblePlayer, 0);
            }
            else {
                return getPlayerHomeInfo(homeManager, possiblePlayerName, 0);
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
            if(possiblePlayer != null) {
                return getPlayerHomeInfo(homeManager, possiblePlayer, homeNumber);
            }
            else {
                return getPlayerHomeInfo(homeManager, possiblePlayerName, homeNumber);
            }
        }
        else {
            throw new CommandExecutionException("&cSyntax: /homeinfo <player> [homenumber]");
        }
    }
    
    private CommandResponse getPlayerHomeInfo(HomeManager homeManager, Player player, int homeNumber)
            throws CommandExecutionException {
        
        if(homeManager.doesPlayerHomeExist(player)) {
            CommandResponse info = new CommandResponse();
            if(homeNumber == 0) {
                int maxHomes = homeManager.getMaxPlayerHomesForInfo(player);
                Location location = null;
                info.addMessage("&8-------------------");
                info.addMessage("&6Full home info for: &a" + player.getName());
                info.addMessage("&8-------------------");
                info.addMessage("&bMax Homes: " + maxHomes);
                info.addMessage("&8-------------------");
                for(int i = 1; i <= 4; i++) {
                    location = homeManager.getPlayerHomeForInfo(player, i);
                    info.addMessage("&bHome " + i + ":");
                    if(location == null) {
                        if(i > maxHomes) {
                            info.addMessage("&c - Home number not permitted.");
                            info.addMessage("&8-------------------");
                        }
                        else {
                            info.addMessage("&c - Home number not set.");
                            info.addMessage("&8-------------------");
                        }
                    }
                    else {
                        info.addMessage("&b - World: " + location.getWorld().getName());
                        info.addMessage("&b - x pos: " + location.getX());
                        info.addMessage("&b - y pos: " + location.getY());
                        info.addMessage("&b - z pos: " + location.getZ());
                        info.addMessage("&b - Pitch: " + location.getPitch());
                        info.addMessage("&b - Yaw  : " + location.getYaw());
                        info.addMessage("&8-------------------");
                    }
                }
                return info;
            }
            else {
                int maxHomes = homeManager.getMaxPlayerHomesForInfo(player);
                Location location = null;
                info.addMessage("&8-------------------");
                info.addMessage("&6Partial home info for: &a" + player.getName());
                info.addMessage("&8-------------------");
                info.addMessage("&bHome " + homeNumber + ":");
                location = homeManager.getPlayerHomeForInfo(player, homeNumber);
                if(location == null) {
                    if(homeNumber > maxHomes) {
                        info.addMessage("&c - Home number not permitted.");
                        info.addMessage("&8-------------------");
                    }
                    else {
                        info.addMessage("&c - Home number not set.");
                        info.addMessage("&8-------------------");
                    }
                }
                else {
                    info.addMessage("&b - World: " + location.getWorld().getName());
                    info.addMessage("&b - x pos: " + location.getX());
                    info.addMessage("&b - y pos: " + location.getY());
                    info.addMessage("&b - z pos: " + location.getZ());
                    info.addMessage("&b - Pitch: " + location.getPitch());
                    info.addMessage("&b - Yaw  : " + location.getYaw());
                    info.addMessage("&8-------------------");
                }
                return info;
            }
        }
        else {
            throw new CommandExecutionException("&cPlayer home not found.");
        }
    }
    
    private CommandResponse getPlayerHomeInfo(HomeManager homeManager, String playerName, int homeNumber)
            throws CommandExecutionException {
        
        if(homeManager.doesPlayerHomeExist(playerName)) {
            CommandResponse info = new CommandResponse();
            String properPlayerName = homeManager.getProperPlayerName(playerName);
            if(homeNumber == 0) {
                int maxHomes = homeManager.getMaxPlayerHomesForInfo(playerName);
                Location location = null;
                info.addMessage("&8-------------------");
                info.addMessage("&6Full home info for: &a" + properPlayerName);
                info.addMessage("&8-------------------");
                info.addMessage("&bMax Homes: " + maxHomes);
                info.addMessage("&8-------------------");
                for(int i = 1; i <= 4; i++) {
                    location = homeManager.getPlayerHomeForInfo(playerName, i);
                    info.addMessage("&bHome " + i + ":");
                    if(location == null) {
                        if(i > maxHomes) {
                            info.addMessage("&c - Home number not permitted.");
                            info.addMessage("&8-------------------");
                        }
                        else {
                            info.addMessage("&c - Home number not set.");
                            info.addMessage("&8-------------------");
                        }
                    }
                    else {
                        info.addMessage("&b - World: " + location.getWorld().getName());
                        info.addMessage("&b - x pos: " + location.getX());
                        info.addMessage("&b - y pos: " + location.getY());
                        info.addMessage("&b - z pos: " + location.getZ());
                        info.addMessage("&b - Pitch: " + location.getPitch());
                        info.addMessage("&b - Yaw  : " + location.getYaw());
                        info.addMessage("&8-------------------");
                    }
                }
                return info;
            }
            else {
                int maxHomes = homeManager.getMaxPlayerHomesForInfo(playerName);
                Location location = null;
                info.addMessage("&8-------------------");
                info.addMessage("&6Partial home info for: &a" + properPlayerName);
                info.addMessage("&8-------------------");
                info.addMessage("&bHome " + homeNumber + ":");
                location = homeManager.getPlayerHomeForInfo(playerName, homeNumber);
                if(location == null) {
                    if(homeNumber > maxHomes) {
                        info.addMessage("&c - Home number not permitted.");
                        info.addMessage("&8-------------------");
                    }
                    else {
                        info.addMessage("&c - Home number not set.");
                        info.addMessage("&8-------------------");
                    }
                }
                else {
                    info.addMessage("&b - World: " + location.getWorld().getName());
                    info.addMessage("&b - x pos: " + location.getX());
                    info.addMessage("&b - y pos: " + location.getY());
                    info.addMessage("&b - z pos: " + location.getZ());
                    info.addMessage("&b - Pitch: " + location.getPitch());
                    info.addMessage("&b - Yaw  : " + location.getYaw());
                    info.addMessage("&8-------------------");
                }
                return info;
            }
        }
        else {
            throw new CommandExecutionException("&cPlayer not found!&r &6Please visit&r " +
                    "&6namemc.com and check other names for that player!&r");
        }
    }
}
