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
import org.cubeville.cvhome.HomeManager;
import org.cubeville.cvhome.exceptions.AdditionalHomeNotPermittedException;
import org.cubeville.cvhome.exceptions.PlayerHomeNotFoundException;

public class HomeTeleport extends Command {

    public HomeTeleport() {
        super("");
        addOptionalBaseParameter(new CommandParameterString());
        addOptionalBaseParameter(new CommandParameterString());
    }

    @Override
    public CommandResponse execute(Player player, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters)
            throws CommandExecutionException {
        
        HomeManager homeManager = HomeManager.getInstance();
        Player sender = player;
        boolean adminOverride = sender.hasPermission("cvhome.admin.teleporthome");
        
        if(baseParameters.size() == 0) {
            return teleportToPlayerHome(homeManager, sender.getUniqueId(), sender, 1);
        }
        else if (baseParameters.size() == 1) {
            
            String param = (String) baseParameters.get(0);
            
            if(isArgumentInteger(param)) {
                
                int homeNumber = getArgumentInteger(param);
                return teleportToPlayerHome(homeManager, sender.getUniqueId(), sender, homeNumber);
                
            }
            else if(isArgumentPlayer(param, homeManager)) {
                
                if(!adminOverride) {
                    throw new CommandExecutionException("&cNo permission.");
                }
                
                OfflinePlayer offlinePlayer = getArgumentPlayer(param, homeManager);
                return teleportToPlayerHome(homeManager, offlinePlayer.getUniqueId(), sender, 1);
                
            }
            else {
                throw new CommandExecutionException("&cSyntax: /home");
                //throw new CommandExecutionException("&cSyntax: /home [number]");
                // TODO: Add this back later.
            }
        }
        else {
            
            if(!adminOverride) {
                throw new CommandExecutionException("&cNo permission.");
            }
            
            String param1 = (String) baseParameters.get(0);
            String param2 = (String) baseParameters.get(1);
            
            if(isArgumentInteger(param1) && isArgumentPlayer(param2, homeManager)) {
                
                int homeNumber = getArgumentInteger(param1);
                OfflinePlayer serverPlayer = getArgumentPlayer(param2, homeManager);
                return teleportToPlayerHome(homeManager, serverPlayer.getUniqueId(), sender, homeNumber);
                
            }
            else if (isArgumentInteger(param2) && isArgumentPlayer(param1, homeManager)) {
                
                int homeNumber = getArgumentInteger(param2);
                OfflinePlayer serverPlayer = getArgumentPlayer(param1, homeManager);
                return teleportToPlayerHome(homeManager, serverPlayer.getUniqueId(), sender, homeNumber);
                
            }
            else {
                throw new CommandExecutionException("&cSyntax: /home");
                //throw new CommandExecutionException("&cSyntax: /home [number]");
                // TODO: Add this back later.
            }
        }
    }
    
    private CommandResponse teleportToPlayerHome(HomeManager homeManager, UUID playerId, Player sender,
            int homeNumber) throws CommandExecutionException {
        
        Location location = null;
        try {
            location = homeManager.teleportToPlayerHome(playerId, homeNumber);
        }
        catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            throw new CommandExecutionException("&cSyntax: /home");
            //throw new CommandExecutionException("&cSyntax: /home [number]");
            // TODO: Add this back later.
        }
        catch (AdditionalHomeNotPermittedException e) {
            throw new CommandExecutionException("&cNo permission.");
        }
        catch (PlayerHomeNotFoundException e) {
            throw new CommandExecutionException("&cPlayer home not found!");
        }
        
        if(location == null) { throw new CommandExecutionException("&cPlayer home not found!"); }
        
        sender.teleport(location);
        return new CommandResponse("&aTeleported.");
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
