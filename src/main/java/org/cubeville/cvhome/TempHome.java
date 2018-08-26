package org.cubeville.cvhome;

import java.util.UUID;

import org.bukkit.Location;

public class TempHome {

    private Location home;
    private String name;
    private UUID playerId;
    
    public TempHome(Location home, String name, UUID playerId) {
        this.home = home;
        this.name = name;
        this.playerId = playerId;
    }
    
    public Location getHome() {
        return this.home;
    }
    
    public void setHome(Location home) {
        this.home = home;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public UUID getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }
}
