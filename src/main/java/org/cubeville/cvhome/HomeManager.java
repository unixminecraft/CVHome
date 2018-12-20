package org.cubeville.cvhome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class HomeManager {

    private static HomeManager instance;
    
    private Plugin plugin;
    private Map<UUID, Home> uuidMap;
    private Map<String, Home> nameMap;
    
    public HomeManager(Plugin plugin) {
        instance = this;
        this.plugin = plugin;
    }
    
    public Plugin getPlugin() {
        return plugin;
    }
    
    public static HomeManager instance() {
        return instance;
    }
    
    @SuppressWarnings("unchecked")
    public void start() {
        List<Home> homes = (List<Home>) plugin.getConfig().get("Homes");
        uuidMap = new HashMap<UUID, Home>();
        nameMap = new HashMap<String, Home>();
        if(homes == null) { return; }
        for(Home home: homes) {
            uuidMap.put(home.getPlayerId(), home);
            nameMap.put(home.getPlayerName(), home);
        }
    }
    
    public void updatePlayerName(Player player) {
        UUID playerId = player.getUniqueId();
        if(!uuidMap.containsKey(playerId)) { return; }
        String playerName = player.getName();
        if(nameMap.containsKey(playerName)) { return; }
        Home home = uuidMap.get(playerId);
        String oldName = home.getPlayerName();
        home.setPlayerName(playerName);
        uuidMap.put(playerId, home);
        nameMap.remove(oldName);
        nameMap.put(playerName, home);
        save();
    }
    
    public void stop() {
        save();
    }
    
    public boolean homeExists(Player player) {
        return uuidMap.containsKey(player.getUniqueId());
    }
    
    public boolean homeExists(String playerName) {
        return nameMap.containsKey(playerName);
    }
    
    public void updatePlayerMaxHomes(Player player) {
        if(!homeExists(player)) { return; }
        UUID playerId = player.getUniqueId();
        Home home = uuidMap.get(playerId);
        int playerMaxHomes = home.getMaxHomes();
        int maxHomes = 1;
        if(player.hasPermission("cvhome.max.2")) { maxHomes = 2; }
        if(player.hasPermission("cvhome.max.3")) { maxHomes = 3; }
        if(player.hasPermission("cvhome.max.4")) { maxHomes = 4; }
        if(playerMaxHomes == maxHomes) { return; }
        home.setMaxHomes(maxHomes);
        uuidMap.put(playerId, home);
        nameMap.put(player.getName(), home);
        save();
    }
    
    public void setHome(Player player, int homeNumber, Location location) {
        if(!homeExists(player)) { return; }
        UUID playerId = player.getUniqueId();
        Home home = uuidMap.get(playerId);
        home.setHome(location, homeNumber);
        uuidMap.put(playerId, home);
        nameMap.put(player.getName(), home);
        save();
    }
    
    public void setHome(String playerName, int homeNumber, Location location) {
        if(!homeExists(playerName)) { return; }
        Home home = nameMap.get(playerName);
        home.setHome(location, homeNumber);
        uuidMap.put(home.getPlayerId(), home);
        nameMap.put(playerName, home);
        save();
    }
    
    public void addHome(Home home) {
        uuidMap.put(home.getPlayerId(), home);
        nameMap.put(home.getPlayerName(), home);
        save();
    }
    
    public Location getHome(Player player, int homeNumber) {
        if(!homeExists(player)) { return null; }
        return uuidMap.get(player.getUniqueId()).getHome(homeNumber);
    }
    
    public Location getHome(String playerName, int homeNumber) {
        if(!homeExists(playerName)) { return null; }
        return nameMap.get(playerName).getHome(homeNumber);
    }
    
    public String getPlayerName(String playerName) {
        if(!homeExists(playerName)) { return "NO HOME EXISTS"; }
        return nameMap.get(playerName).getPlayerName();
    }
    
    public int getMaxHomes(Player player) {
        if(!homeExists(player)) { return 1; }
        return uuidMap.get(player.getUniqueId()).getMaxHomes();
    }
    
    public int getMaxHomes(String playerName) {
        if(!homeExists(playerName)) { return 1; }
        return nameMap.get(playerName).getMaxHomes();
    }
    
    private void save() {
        plugin.getConfig().set("Homes", new ArrayList<Home>(uuidMap.values()));
        plugin.saveConfig();
    }
}