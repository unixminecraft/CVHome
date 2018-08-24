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

public class HomeInfo extends Command {

    public HomeInfo() {
        super("");
        addBaseParameter(new CommandParameterString());
        addOptionalBaseParameter(new CommandParameterString());
    }

    @Override
    public CommandResponse execute(Player player, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters)
            throws CommandExecutionException {
        
        HomeManager homeManager = HomeManager.getInstance();
        Player sender = player;
        
        if(baseParameters.size() == 1) {
            
            String param = (String) baseParameters.get(0);
            if(isArgumentPlayer(param, homeManager)) {
                OfflinePlayer offlinePlayer = getArgumentPlayer(param, homeManager);
                return getPlayerHomeInfo(homeManager, offlinePlayer.getUniqueId(), offlinePlayer, 1, sender.hasPermission("cvhome.admin.infohome"));
            }
            else {
                throw new CommandExecutionException("&cSyntax: /homeinfo <player> [home_number]");
            }
        }
        else {
            
            String param1 = (String) baseParameters.get(0);
            String param2 = (String) baseParameters.get(1);
            if(isArgumentPlayer(param1, homeManager) && isArgumentInteger(param2)) {
                OfflinePlayer offlinePlayer = getArgumentPlayer(param1, homeManager);
                int homeNumber = getArgumentInteger(param2);
                return getPlayerHomeInfo(homeManager, offlinePlayer.getUniqueId(), offlinePlayer, homeNumber, sender.hasPermission("cvhome.admin.infohome"));
            }
            else {
                throw new CommandExecutionException("&cSyntax: /homeinfo <player> [home_number]");
            }
        }
    }
    
    private CommandResponse getPlayerHomeInfo(HomeManager homeManager, UUID playerId, OfflinePlayer offlinePlayer,
            int homeNumber, boolean adminOverride) throws CommandExecutionException {
        
        Location location = null;
        try {
            location = homeManager.getPlayerHomeInfo(playerId, homeNumber, adminOverride);
        }
        catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            throw new CommandExecutionException("&cSyntax: /homeinfo <player> [home_number]");
        }
        catch (NoAdminPermissionException e) {
            throw new CommandExecutionException("&cNo permission.");
        }
        catch (AdditionalHomeNotPermittedException e) {
            throw new CommandExecutionException("&cPlayer does not have permission for " + homeNumber + " homes!");
        }
        catch (PlayerHomeNotFoundException e) {
            throw new CommandExecutionException("&cPlayer home not found!");
        }
        
        if(location == null) { throw new CommandExecutionException("&cPlayer home not found!"); }
        
        CommandResponse ret = new CommandResponse();
        ret.addMessage("&8-------------------");
        ret.addMessage("&6Home info for: &a" + offlinePlayer.getName());
        ret.addMessage("&8-------------------");
        ret.addMessage("&bWorld: " + location.getWorld().toString());
        ret.addMessage("&bX loc: " + location.getX());
        ret.addMessage("&bY loc: " + location.getY());
        ret.addMessage("&bZ loc: " + location.getZ());
        ret.addMessage("&bPitch: " + location.getPitch());
        ret.addMessage("&bYaw  : " + location.getYaw());
        ret.addMessage("&8-------------------");
        return ret;
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
