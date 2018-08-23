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
import org.cubeville.cvhome.exceptions.NoAdminPermissionException;
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
        
        if(baseParameters.size() == 0) {
            return teleportToPlayerHome(homeManager, sender.getUniqueId(), sender.getUniqueId(), sender, 1, false);
        }
        else if (baseParameters.size() == 1) {
            
            String param = (String) baseParameters.get(0);
            if(isArgumentInteger(param)) {
                int homeNumber = getArgumentInteger(param);
                return teleportToPlayerHome(homeManager, sender.getUniqueId(), sender.getUniqueId(), sender, homeNumber, false);
            }
            else if(isArgumentPlayer(param, homeManager)) {
                OfflinePlayer serverPlayer = getArgumentPlayer(param, homeManager);
                return teleportToPlayerHome(homeManager, sender.getUniqueId(), serverPlayer.getUniqueId(), sender, 1, sender.hasPermission("cvhome.admin.teleporthome"));
            }
            else {
                throw new CommandExecutionException("&cSyntax: /home [number]");
            }
        }
        else {
            
            String param1 = (String) baseParameters.get(0);
            String param2 = (String) baseParameters.get(1);
            if(isArgumentInteger(param1) && isArgumentPlayer(param2, homeManager)) {
                int homeNumber = getArgumentInteger(param1);
                OfflinePlayer serverPlayer = getArgumentPlayer(param2, homeManager);
                return teleportToPlayerHome(homeManager, sender.getUniqueId(), serverPlayer.getUniqueId(), sender, homeNumber, sender.hasPermission("cvhome.admin.teleporthome"));
            }
            else if (isArgumentInteger(param2) && isArgumentPlayer(param1, homeManager)) {
                int homeNumber = getArgumentInteger(param2);
                OfflinePlayer serverPlayer = getArgumentPlayer(param1, homeManager);
                return teleportToPlayerHome(homeManager, sender.getUniqueId(), serverPlayer.getUniqueId(), sender, homeNumber, sender.hasPermission("cvhome.admin.teleporthome"));
            }
            else {
                throw new CommandExecutionException("&cSyntax: /home [number]");
            }
        }
    }
    
    private CommandResponse teleportToPlayerHome(HomeManager homeManager, UUID senderId, UUID playerId, Player sender,
            int homeNumber, boolean adminOverride) throws CommandExecutionException {
        
        Location location = null;
        try {
            location = homeManager.teleportToPlayerHome(senderId, playerId, homeNumber, adminOverride);
        }
        catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            throw new CommandExecutionException("&cSyntax: /home [number]");
        }
        catch (AdditionalHomeNotPermittedException | NoAdminPermissionException e) {
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
