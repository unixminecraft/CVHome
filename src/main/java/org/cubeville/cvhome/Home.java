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
    private String playerName;
    private UUID playerId;
    
    public Home(UUID playerId, String playerName) {
        if(playerId == null || playerName == null || playerName.equals("")) {
            throw new IllegalArgumentException();
        }
        home1 = null;
        home2 = null;
        home3 = null;
        home4 = null;
        maxHomes = 1;
        this.playerName = playerName;
        this.playerId = playerId;
    }
    
    public Home(Map<String, Object> config) {
        home1 = (Location) config.get("home1");
        home2 = (Location) config.get("home2");
        home3 = (Location) config.get("home3");
        home4 = (Location) config.get("home4");
        maxHomes = ((Integer) config.get("maxhomes")).intValue();
        if(config.containsKey("playername")) { playerName = (String) config.get("playername"); }
        else { playerName = (String) config.get("playerid"); }
        playerId = UUID.fromString((String) config.get("playerid"));
    }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> ret = new HashMap<String, Object>();
        ret.put("playerid", playerId.toString());
        ret.put("playername", playerName);
        ret.put("maxhomes", Integer.valueOf(maxHomes));
        ret.put("home1", home1);
        ret.put("home2", home2);
        ret.put("home3", home3);
        ret.put("home4", home4);
        return ret;
    }
    
    public Location getHome(int homeNumber) {
        if(homeNumber < 1 || homeNumber > 4) { throw new IndexOutOfBoundsException(); }
        if(homeNumber == 4) { return home4; }
        else if(homeNumber == 3) { return home3; }
        else if(homeNumber == 2) { return home2; }
        else { return home1; }
    }
    
    public void setHome(Location location, int homeNumber) {
        if(location == null) { throw new IllegalArgumentException(); }
        if(homeNumber < 1 || homeNumber > 4) { throw new IndexOutOfBoundsException(); }
        if(homeNumber == 4) { home4 = location; }
        else if(homeNumber == 3) { home3 = location; }
        else if(homeNumber == 2) { home2 = location; }
        else { home1 = location; }
    }
    
    public int getMaxHomes() {
        return maxHomes;
    }
    
    public void setMaxHomes(int maxHomes) {
        if(maxHomes < 1 || maxHomes > 4) { throw new IndexOutOfBoundsException(); }
        this.maxHomes = maxHomes;
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public void setPlayerName(String playerName) {
        if(playerName == null || playerName.equals("")) { throw new IllegalArgumentException(); }
        this.playerName = playerName;
    }
    
    public UUID getPlayerId() {
        return playerId;
    }
    
    public void setPlayerId(UUID playerId) {
        if(playerId == null) { throw new IllegalArgumentException(); }
        this.playerId = playerId;
    }
    
}