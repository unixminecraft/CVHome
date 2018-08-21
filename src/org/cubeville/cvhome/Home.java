package org.cubeville.cvhome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import org.cubeville.commons.commands.CommandResponse;

@SerializableAs("Home")
public class Home implements ConfigurationSerializable {

    private UUID playerId;
    private List<Location> homes;
    private int maxHomes;
    
    public Home(UUID playerId, int maxHomes) {
        this.playerId = playerId;
        this.homes = new ArrayList<Location>(maxHomes);
        for(int i = 0; i < this.homes.size(); i++) { this.homes.set(i, null); }
        this.maxHomes = maxHomes;
    }
    
    @SuppressWarnings("unchecked")
    public Home(Map<String, Object> config) {
        this.playerId = (UUID) config.get("playerid");
        this.homes = (List<Location>) config.get("homes");
        this.maxHomes = ((Integer) config.get("maxhomes")).intValue();
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> ret = new HashMap<String, Object>();
        ret.put("playerid", this.playerId);
        ret.put("homes", this.homes);
        ret.put("maxhomes", Integer.valueOf(this.maxHomes));
        return ret;
    }
    
    public UUID getPlayerId() {
        return this.playerId;
    }
    
    public List<Location> getHomes() {
        return this.homes;
    }
    
    public int getMaxHomes() {
        return this.maxHomes;
    }
    
    public void setMaxHomes(int maxHomes) {
        if(this.maxHomes == maxHomes) { return; }
        
        this.maxHomes = maxHomes;
        List<Location> tmp = new ArrayList<Location>(maxHomes);
        if(maxHomes > this.homes.size()) {
            for(int i = 0; i < this.homes.size(); i++) { tmp.set(i, this.homes.get(i)); }
            for(int j = this.homes.size(); j < maxHomes; j++) { tmp.set(j, null); }
        }
        else {
            for(int i = 0; i < maxHomes; i++) { tmp.set(i, this.homes.get(i)); }
        }
        this.homes = tmp;
    }
    
    public Location getHome(UUID playerId, int homeNumber, boolean adminOverride) {
        if(playerId == null || homeNumber > this.maxHomes || homeNumber <= 0) { return null; }
        if((!this.playerId.equals(playerId)) && (!adminOverride)) { return null; }
        return this.homes.get(homeNumber - 1);
    }
    
    public CommandResponse updateHome(UUID playerId, Location location, int homeNumber, boolean adminOverride) {
        CommandResponse ret = new CommandResponse();
        if(playerId == null || location == null) {
            ret.addMessage("&cGeneral error with /sethome, please try again later.");
            return ret;
        }
        if((!this.playerId.equals(playerId)) && (!adminOverride)) {
            ret.addMessage("&cCannot update home, player mismatch.");
            return ret;
        }
        if(homeNumber > this.maxHomes) {
            ret.addMessage("&cYou may only set up to " + this.maxHomes + " homes!");
            return ret;
        }
        if(homeNumber <= 0) {
            ret.addMessage("&cPlease use a valid home number between 1 and " + this.maxHomes + "!");
            return ret;
        }
        
        this.homes.set(homeNumber - 1, location);
        ret.addMessage("&aHome set.");
        return ret;
    }
}