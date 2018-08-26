package org.cubeville.cvhome;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import org.cubeville.cvhome.exceptions.AdditionalHomeNotPermittedException;
import org.cubeville.cvhome.exceptions.NoAdminPermissionException;
import org.cubeville.cvhome.exceptions.PlayerHomeNotFoundException;

public class HomeManager implements Listener {

    private Plugin plugin;
    private List<Home> playerHomes;
    private static HomeManager instance;
    
    @SuppressWarnings({ "static-access", "unchecked" })
    public HomeManager(Plugin plugin) {
        this.plugin = plugin;
        this.instance = this;
        this.playerHomes = (List<Home>) plugin.getConfig().get("Homes");
        if(this.playerHomes == null) { this.playerHomes = new ArrayList<Home>(); }
    }
    
    public Plugin getPlugin() {
        return this.plugin;
    }
    
    public static HomeManager getInstance() {
        return instance;
    }
    
    public boolean doesPlayerHomeExist(UUID playerId) {
        for(int i = 0; i < this.playerHomes.size(); i++) {
            if(this.playerHomes.get(i).getPlayerId().equals(playerId)) {
                return true;
            }
        }
        return false;
    }
    
    public void updatePlayerHome(UUID senderId, UUID playerId, int homeNumber, Location location, boolean adminOverride)
            throws AdditionalHomeNotPermittedException, IllegalArgumentException,
            IndexOutOfBoundsException, NoAdminPermissionException, PlayerHomeNotFoundException {
        
        if(senderId == null || playerId == null || location == null) { throw new IllegalArgumentException(); }
        if((!senderId.equals(playerId)) && (!adminOverride)) { throw new NoAdminPermissionException(); }
        if(homeNumber < 1 || homeNumber > 4) { throw new IndexOutOfBoundsException(); }
        
        Home playerHome = getPlayerHome(playerId);
        
        if(playerHome == null) { throw new PlayerHomeNotFoundException(); }
        if(checkMaxHomes(playerId) < homeNumber) { throw new AdditionalHomeNotPermittedException(); }
        
        if(homeNumber == 4) { playerHome.setHome4(location); }
        else if(homeNumber == 3) { playerHome.setHome3(location); }
        else if(homeNumber == 2) { playerHome.setHome2(location); }
        else { playerHome.setHome1(location); }
        
        setPlayerHome(playerId, playerHome);
        save();
    }
    
    public Location teleportToPlayerHome(UUID senderId, UUID playerId, int homeNumber, boolean adminOverride)
            throws AdditionalHomeNotPermittedException, IllegalArgumentException,
            IndexOutOfBoundsException, NoAdminPermissionException, PlayerHomeNotFoundException {
        
        if(senderId == null || playerId == null) { throw new IllegalArgumentException(); }
        if((!senderId.equals(playerId)) && (!adminOverride)) { throw new NoAdminPermissionException(); }
        if(homeNumber < 1 || homeNumber > 4) { throw new IndexOutOfBoundsException(); }
        
        Home playerHome = getPlayerHome(playerId);
        
        if(playerHome == null) { throw new PlayerHomeNotFoundException(); }
        if(checkMaxHomes(playerId) < homeNumber) { throw new AdditionalHomeNotPermittedException(); }
        
        if(homeNumber == 4) { return playerHome.getHome4(); }
        else if(homeNumber == 3) { return playerHome.getHome3(); }
        else if(homeNumber == 2) { return playerHome.getHome2(); }
        else { return playerHome.getHome1(); }
    }
    
    public Location getPlayerHomeInfo(UUID playerId, int homeNumber, boolean adminOverride)
            throws AdditionalHomeNotPermittedException, IllegalArgumentException,
            IndexOutOfBoundsException, NoAdminPermissionException, PlayerHomeNotFoundException {
        
        if(playerId == null) { throw new IllegalArgumentException(); }
        if(!adminOverride) { throw new NoAdminPermissionException(); }
        if(homeNumber < 1 || homeNumber > 4) { throw new IndexOutOfBoundsException(); }
        
        Home playerHome = getPlayerHome(playerId);
        
        if(playerHome == null) { throw new PlayerHomeNotFoundException(); }
        if(checkMaxHomes(playerId) < homeNumber) { throw new AdditionalHomeNotPermittedException(); }
        
        if(homeNumber == 4) { return playerHome.getHome4(); }
        else if (homeNumber == 3) { return playerHome.getHome3(); }
        else if (homeNumber == 2) { return playerHome.getHome2(); }
        else { return playerHome.getHome1(); }
    }
    
    public void add(Home home) {
        if (home == null) { throw new IllegalArgumentException(); }
        
        Home playerHome = null;
        for(Home pHome: this.playerHomes) {
            if(pHome.getPlayerId().equals(home.getPlayerId())) {
                playerHome = pHome;
                break;
            }
        }
        if(playerHome == null) { this.playerHomes.add(home); }
        else { this.playerHomes.set(this.playerHomes.indexOf(playerHome), home); }
    }
    
    public void updateMaxHomes(Player player) throws IllegalArgumentException, PlayerHomeNotFoundException {
        if(player == null) { throw new IllegalArgumentException(); }
        
        Home playerHome = null;
        if(doesPlayerHomeExist(player.getUniqueId())) {
            playerHome = getPlayerHome(player.getUniqueId());
        }
        
        if(playerHome == null) { throw new PlayerHomeNotFoundException(); }
        
        if(player.hasPermission("cvhome.max.4")) { playerHome.setMaxHomes(4); }
        else if(player.hasPermission("cvhome.max.3")) { playerHome.setMaxHomes(3); }
        else if(player.hasPermission("cvhome.max.2")) { playerHome.setMaxHomes(2); }
        else { playerHome.setMaxHomes(1); }
        setPlayerHome(player.getUniqueId(), playerHome);
    }
    
    public void setHomesFromImport(List<Home> playerHomes) {
        this.playerHomes = playerHomes;
        save();
    }
    
    private int checkMaxHomes(UUID playerId) {
        Home playerHome = getPlayerHome(playerId);
        return playerHome.getMaxHomes();
    }
    
    private Home getPlayerHome(UUID playerId) {
        Home playerHome = null;
        for(Home home: this.playerHomes) {
            if(home.getPlayerId().equals(playerId)) {
                playerHome = home;
                break;
            }
        }
        return playerHome;
    }
    
    private void setPlayerHome(UUID playerId, Home home) {
        for(int i = 0; i < this.playerHomes.size(); i++) {
            if(this.playerHomes.get(i).getPlayerId().equals(playerId)) {
                this.playerHomes.set(i, home);
                break;
            }
        }
    }
    
    private void save() {
        this.plugin.getConfig().set("Homes", this.playerHomes);
        this.plugin.saveConfig();
    }
}