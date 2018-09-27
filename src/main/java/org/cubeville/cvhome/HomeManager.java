package org.cubeville.cvhome;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class HomeManager implements Listener {

    private Plugin plugin;
    private List<Home> playerHomes;
    private static HomeManager instance;
    
    @SuppressWarnings("static-access")
    public HomeManager(Plugin plugin) {
        this.plugin = plugin;
        this.instance = this;
    }
    
    public Plugin getPlugin() {
        return this.plugin;
    }
    
    public static HomeManager getInstance() {
        return instance;
    }
    
    @SuppressWarnings("unchecked")
    public void start() {
        this.playerHomes = (List<Home>) this.plugin.getConfig().get("Homes");
        if(this.playerHomes == null) { this.playerHomes = new ArrayList<Home>(); }
    }
    
    public void stop() {
        save();
    }
    
    public boolean doesPlayerHomeExist(Player player) {
        if(getPlayerHome(player) != null) { return true; }
        else { return false; }
    }
    
    public boolean doesPlayerHomeExist(String playerName) {
        if(getPlayerHome(playerName) != null) { return true; }
        else { return false; }
    }
    
    public void updatePlayerName(Player player) {
        UUID playerId = player.getUniqueId();
        String playerName = player.getName();
        Home playerHome = getPlayerHome(player);
        if(!playerHome.getPlayerName().equals(playerName)) {
            playerHome.setPlayerName(playerName);
            updatePlayerHome(playerId, playerHome);
        }
        save();
    }
    
    public void updatePlayerMaxHomes(Player player) {
        int maxHomes = 1;
        if(player.hasPermission("cvhome.max.4")) { maxHomes = 4; }
        else if(player.hasPermission("cvhome.max.3")) { maxHomes = 3; }
        else if(player.hasPermission("cvhome.max.2")) { maxHomes = 2; }
        UUID playerId = player.getUniqueId();
        Home playerHome = getPlayerHome(player);
        playerHome.setMaxHomes(maxHomes);
        updatePlayerHome(playerId, playerHome);
        save();
    }
    
    public void setPlayerHome(Player player, int homeNumber, Location location) {
        Home playerHome = getPlayerHome(player);
        if(homeNumber == 4) { playerHome.setHome4(location); }
        else if(homeNumber == 3) { playerHome.setHome3(location); }
        else if(homeNumber == 2) { playerHome.setHome2(location); }
        else { playerHome.setHome1(location); }
        updatePlayerHome(player.getUniqueId(), playerHome);
        save();
    }
    
    public void setPlayerHome(String playerName, int homeNumber, Location location) {
        Home playerHome = getPlayerHome(playerName);
        if(homeNumber == 4) { playerHome.setHome4(location); }
        else if(homeNumber == 3) { playerHome.setHome3(location); }
        else if(homeNumber == 2) { playerHome.setHome2(location); }
        else { playerHome.setHome1(location); }
        updatePlayerHome(playerName, playerHome);
        save();
    }
    
    public void addPlayerHome(Home playerHome) {
        this.playerHomes.add(playerHome);
        save();
    }
    
    public Location getPlayerHomeForTeleport(Player player, int homeNumber) {
        return getPlayerHomeLocationGeneric(player, homeNumber);
    }
    
    public Location getPlayerHomeForTeleport(String playerName, int homeNumber) {
        return getPlayerHomeLocationGeneric(playerName, homeNumber);
    }
    
    public Location getPlayerHomeForInfo(Player player, int homeNumber) {
        return getPlayerHomeLocationGeneric(player, homeNumber);
    }
    public Location getPlayerHomeForInfo(String playerName, int homeNumber) {
        return getPlayerHomeLocationGeneric(playerName, homeNumber);
    }
    
    public int getMaxPlayerHomesForInfo(Player player) {
        return getPlayerHome(player).getMaxHomes();
    }
    
    public int getMaxPlayerHomesForInfo(String playerName) {
        return getPlayerHome(playerName).getMaxHomes();
    }
    
    public void setHomesFromImport(List<Home> playerHomes) {
        this.playerHomes = playerHomes;
        save();
    }
    
    public List<Home> getHomesForUpdate() {
        return this.playerHomes;
    }
    
    public void setHomesFromUpdate(List<Home> playerHomes) {
        this.playerHomes = playerHomes;
        save();
    }
    
    private Location getPlayerHomeLocationGeneric(Player player, int homeNumber) {
        Home playerHome = getPlayerHome(player);
        if(homeNumber == 4) { return playerHome.getHome4(); }
        else if(homeNumber == 3) { return playerHome.getHome3(); }
        else if(homeNumber == 2) { return playerHome.getHome2(); }
        else { return playerHome.getHome1(); }
    }
    
    private Location getPlayerHomeLocationGeneric(String playerName, int homeNumber) {
        Home playerHome = getPlayerHome(playerName);
        if(homeNumber == 4) { return playerHome.getHome4(); }
        else if(homeNumber == 3) { return playerHome.getHome3(); }
        else if(homeNumber == 2) { return playerHome.getHome2(); }
        else { return playerHome.getHome1(); }
    }
    
    private void updatePlayerHome(UUID playerId, Home playerHome) {
        for(int i = 0; i < this.playerHomes.size(); i++) {
            if(this.playerHomes.get(i).getPlayerId().equals(playerId)) {
                this.playerHomes.set(i, playerHome);
                break;
            }
        }
    }
    
    private void updatePlayerHome(String playerName, Home playerHome) {
        for(int i = 0; i < this.playerHomes.size(); i++) {
            if(this.playerHomes.get(i).getPlayerName().equals(playerName)) {
                this.playerHomes.set(i, playerHome);
                break;
            }
        }
    }
    
    private Home getPlayerHome(Player player) {
        UUID playerId = player.getUniqueId();
        for(int i = 0; i < this.playerHomes.size(); i++) {
            if(this.playerHomes.get(i).getPlayerId().equals(playerId)) {
                return this.playerHomes.get(i);
            }
        }
        return null;
    }
    
    private Home getPlayerHome(String playerName) {
        for(int i = 0; i < this.playerHomes.size(); i++) {
            if(this.playerHomes.get(i).getPlayerName().equals(playerName)) {
                return this.playerHomes.get(i);
            }
        }
        return null;
    }
    
    private void save() {
        this.plugin.getConfig().set("Homes", this.playerHomes);
        this.plugin.saveConfig();
    }
}