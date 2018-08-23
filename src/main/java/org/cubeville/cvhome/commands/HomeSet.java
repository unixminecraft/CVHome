package org.cubeville.cvhome.commands;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import org.cubeville.commons.commands.Command;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.cvhome.Home;
import org.cubeville.cvhome.HomeManager;
import org.cubeville.cvhome.exceptions.AdditionalHomeNotPermittedException;
import org.cubeville.cvhome.exceptions.NoAdminPermissionException;
import org.cubeville.cvhome.exceptions.PlayerHomeNotFoundException;

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

        if(baseParameters.size() == 0) {
            
            return getOrCreatePlayerHome(homeManager, sender.getUniqueId(), sender.getUniqueId(), sender, 1, sender.getLocation(), false);
        }
        else if(baseParameters.size() == 1) {
            
            String param = (String) baseParameters.get(0);
            if(isArgumentInteger(param)) {
                int homeNumber = getArgumentInteger(param);
                return getOrCreatePlayerHome(homeManager, sender.getUniqueId(), sender.getUniqueId(), sender, homeNumber, sender.getLocation(), false);
            }
            else if(isArgumentPlayer(param, homeManager)) {
                OfflinePlayer offlinePlayer = getArgumentPlayer(param, homeManager);
                if(offlinePlayer.isOnline()) {
                    Player onlinePlayer = offlinePlayer.getPlayer();
                    return getOrCreatePlayerHome(homeManager, sender.getUniqueId(), onlinePlayer.getUniqueId(), onlinePlayer, 1, sender.getLocation(), sender.hasPermission("cvhome.admin.sethome"));
                }
                else {
                    return getOrCreatePlayerHome(homeManager, sender.getUniqueId(), offlinePlayer.getUniqueId(), 1, sender.getLocation(), sender.hasPermission("cvhome.admin.sethome"));
                }
            }
            else {
                throw new CommandExecutionException("&cSyntax: /sethome [number]");
            }
        }
        else {
            
            String param1 = (String) baseParameters.get(0);
            String param2 = (String) baseParameters.get(1);
            if(isArgumentInteger(param1) && isArgumentPlayer(param2, homeManager)) {
                int homeNumber = getArgumentInteger(param1);
                OfflinePlayer offlinePlayer = getArgumentPlayer(param2, homeManager);
                if(offlinePlayer.isOnline()) {
                    Player onlinePlayer = offlinePlayer.getPlayer();
                    return getOrCreatePlayerHome(homeManager, sender.getUniqueId(), onlinePlayer.getUniqueId(), onlinePlayer, homeNumber, sender.getLocation(), sender.hasPermission("cvhome.admin.sethome"));
                }
                else {
                    return getOrCreatePlayerHome(homeManager, sender.getUniqueId(), offlinePlayer.getUniqueId(), homeNumber, sender.getLocation(), sender.hasPermission("cvhome.admin.sethome"));
                }
            }
            else if(isArgumentInteger(param2) && isArgumentPlayer(param1, homeManager)) {
                int homeNumber = getArgumentInteger(param2);
                OfflinePlayer offlinePlayer = getArgumentPlayer(param1, homeManager);
                if(offlinePlayer.isOnline()) {
                    Player onlinePlayer = offlinePlayer.getPlayer();
                    return getOrCreatePlayerHome(homeManager, sender.getUniqueId(), onlinePlayer.getUniqueId(), onlinePlayer, homeNumber, sender.getLocation(), sender.hasPermission("cvhome.admin.sethome"));
                }
                else {
                    return getOrCreatePlayerHome(homeManager, sender.getUniqueId(), offlinePlayer.getUniqueId(), homeNumber, sender.getLocation(), sender.hasPermission("cvhome.admin.sethome"));
                }
            }
            else {
                throw new CommandExecutionException("&cSyntax: /sethome [number]");
            }
        }
    }
    
    private CommandResponse getOrCreatePlayerHome(HomeManager homeManager, UUID senderId, UUID playerId,
            Player player, int homeNumber, Location location, boolean adminOverride)
            throws CommandExecutionException {
        if(homeManager.doesPlayerHomeExist(playerId)) {
            return updatePlayerHome(homeManager, senderId, playerId, player, homeNumber, location, adminOverride);
        }
        else {
            Home playerHome = null;
            try {
                playerHome = new Home(playerId);
                homeManager.add(playerHome);
            }
            catch (IllegalArgumentException e) {
                throw new CommandExecutionException("&cInternal error, please try again later.");
            }
            return updatePlayerHome(homeManager, senderId, playerId, player, homeNumber, location, adminOverride);
        }
    }
    
    private CommandResponse getOrCreatePlayerHome(HomeManager homeManager, UUID senderId, UUID offlinePlayerId,
            int homeNumber, Location location, boolean adminOverride)
            throws CommandExecutionException {
        if(homeManager.doesPlayerHomeExist(offlinePlayerId)) {
            return updatePlayerHome(homeManager, senderId, offlinePlayerId, homeNumber, location, adminOverride);
        }
        else {
            Home offlinePlayerHome = null;
            try {
                offlinePlayerHome = new Home(offlinePlayerId);
                homeManager.add(offlinePlayerHome);
            }
            catch (IllegalArgumentException e) {
                throw new CommandExecutionException("&cInternal error, please try again later.");
            }
            return updatePlayerHome(homeManager, senderId, offlinePlayerId, homeNumber, location, adminOverride);
        }
    }
    
    private CommandResponse updatePlayerHome(HomeManager homeManager, UUID senderId, UUID playerId,
            Player player, int homeNumber, Location location, boolean adminOverride)
            throws CommandExecutionException {
        
        try {
            homeManager.updateMaxHomes(player);
            homeManager.updatePlayerHome(senderId, playerId, homeNumber, location, adminOverride);
        }
        catch(IllegalArgumentException | IndexOutOfBoundsException e) {
            throw new CommandExecutionException("&cSyntax: /sethome [number]");
        }
        catch(AdditionalHomeNotPermittedException | NoAdminPermissionException e) {
            throw new CommandExecutionException("&cNo permission.");
        }
        catch(PlayerHomeNotFoundException e) {
            throw new CommandExecutionException("&cPlayer home not found!");
        }
        
        return new CommandResponse("&aHome set.");
    }
    
    private CommandResponse updatePlayerHome(HomeManager homeManager, UUID senderId, UUID offlinePlayerId,
            int homeNumber, Location location, boolean adminOverride)
            throws CommandExecutionException {
        try {
            homeManager.updatePlayerHome(senderId, offlinePlayerId, homeNumber, location, adminOverride);
        }
        catch(IllegalArgumentException | IndexOutOfBoundsException e) {
            throw new CommandExecutionException("&cSyntax: /sethome [number]");
        }
        catch(AdditionalHomeNotPermittedException | NoAdminPermissionException e) {
            throw new CommandExecutionException("&cNo permission.");
        }
        catch(PlayerHomeNotFoundException e) {
            throw new CommandExecutionException("&cPlayer home not found!");
        }
        
        return new CommandResponse("&aHome set.");
    }
    
    private boolean isArgumentInteger(String arg) {
        try {
            Integer.parseInt(arg);
        }
        catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    
    private int getArgumentInteger(String arg) {
        return Integer.parseInt(arg);
    }
    
    private boolean isArgumentPlayer(String arg, HomeManager homeManager) {
        OfflinePlayer[] playerList = homeManager.getPlugin().getServer().getOfflinePlayers();
        for(int i = 0; i < playerList.length; i++) {
            if(playerList[i].getName().equalsIgnoreCase(arg)) {
                return true;
            }
        }
        return false;
    }
    
    private OfflinePlayer getArgumentPlayer(String arg, HomeManager homeManager) {
        OfflinePlayer offlinePlayer = null;
        OfflinePlayer[] offlinePlayerList = homeManager.getPlugin().getServer().getOfflinePlayers();
        for(int i = 0; i < offlinePlayerList.length; i++) {
            if(offlinePlayerList[i].getName().equalsIgnoreCase(arg)) {
                offlinePlayer = offlinePlayerList[i];
                break;
            }
        }
        return offlinePlayer;
    }
}
