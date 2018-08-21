package org.cubeville.cvhome;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import org.cubeville.cvhome.exception.NoAdminPermissionException;
import org.cubeville.cvhome.exception.PlayerMismatchException;

public class HomeManager implements Listener {

    private Plugin plugin;
    private Integer taskId;
    private List<Home> playerHomes;
    private static HomeManager instance;
    
    @SuppressWarnings({ "static-access", "unchecked" })
    public HomeManager(Plugin plugin) {
        this.plugin = plugin;
        this.instance = this;
        this.taskId = null;
        this.playerHomes = (List<Home>) plugin.getConfig().get("PlayerHomes");
        if(this.playerHomes == null) { this.playerHomes = new ArrayList<Home>(); }
    }
    
    public static HomeManager getInstance() {
        return instance;
    }
    
    public void save() {
        this.plugin.getConfig().set("PlayerHomes", this.playerHomes);
        this.plugin.saveConfig();
    }
    
    public List<Home> getHomes() {
        return this.playerHomes;
    }
    
    public boolean doesPlayerHomeExist(UUID playerId) {
        for(int i = 0; i < this.playerHomes.size(); i++) {
            if(this.playerHomes.get(i).getPlayerId().equals(playerId)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean updatePlayerHome(UUID senderId, UUID playerId, int homeNumber, Location location, boolean adminOverride)
        throws IndexOutOfBoundsException, IllegalArgumentException, NoAdminPermissionException, PlayerMismatchException {
        if(senderId == null || playerId == null || location == null) { return false; }
        if((!senderId.equals(playerId)) && (!adminOverride)) { return false; }
        
        
        return false;
    }
    
}