package org.cubeville.cvhome;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("Home")
public class Home implements ConfigurationSerializable {

    private Location home1;
    private Location home2;
    private Location home3;
    private Location home4;
    private int maxHomes;
    private UUID playerId;
    
    public Home(UUID playerId) {
        
        if(playerId == null) { throw new IllegalArgumentException(); }
        
        this.home1 = null;
        this.home2 = null;
        this.home3 = null;
        this.home4 = null;
        this.maxHomes = 1;
        this.playerId = playerId;
    }
    
    public Home(Map<String, Object> config) {
        this.home1 = (Location) config.get("home1");
        this.home2 = (Location) config.get("home2");
        this.home3 = (Location) config.get("home3");
        this.home4 = (Location) config.get("home4");
        this.maxHomes = ((Integer) config.get("maxhomes")).intValue();
        this.playerId = UUID.fromString((String) config.get("playerid"));
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> ret = new HashMap<String, Object>();
        ret.put("playerid", this.playerId.toString());
        ret.put("maxhomes", Integer.valueOf(this.maxHomes));
        ret.put("home1", this.home1);
        ret.put("home2", this.home2);
        ret.put("home3", this.home3);
        ret.put("home4", this.home4);
        return ret;
    }
    
    public Location getHome1() {
        return this.home1;
    }
    
    public void setHome1(Location home) {
        this.home1 = home;
    }
    
    public Location getHome2() {
        return this.home2;
    }
    
    public void setHome2(Location home) {
        this.home2 = home;
    }
    
    public Location getHome3() {
        return this.home3;
    }
    
    public void setHome3(Location home) {
        this.home3 = home;
    }
    
    public Location getHome4() {
        return this.home4;
    }
    
    public void setHome4(Location home) {
        this.home4 = home;
    }
    
    public int getMaxHomes() {
        return this.maxHomes;
    }
    
    public void setMaxHomes(int maxHomes) {
        if(maxHomes < 1 || maxHomes > 4) { throw new IndexOutOfBoundsException(); }
        this.maxHomes = maxHomes;
    }
    
    public UUID getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(UUID playerId) {
        if(playerId == null) { throw new IllegalArgumentException(); }
        this.playerId = playerId;
    }
    
}