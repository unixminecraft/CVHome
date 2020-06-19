/*
 * CVHome Bukkit plugin for Minecraft
 * Copyright (C) 2017-2018,2020  Matt Ciolkosz (https://github.com/mciolkosz)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.cubeville.cvhome;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("Home")
public class Home implements ConfigurationSerializable {
	
	private UUID uniqueId;
    private int maxHomes;
    private Location home1;
    private Location home2;
    private Location home3;
    private Location home4;
    
    public Home(UUID uniqueId) {
    	this.uniqueId = uniqueId;
    	this.maxHomes = 1;
    	this.home1 = null;
    	this.home2 = null;
    	this.home3 = null;
    	this.home4 = null;
    }
    
    public Home(Map<String, Object> config) {
    	this.uniqueId = UUID.fromString((String) config.get("unique_id"));
    	this.maxHomes = ((Integer) config.get("max_homes")).intValue();
    	this.home1 = (Location) config.get("home_1");
    	this.home2 = (Location) config.get("home_2");
    	this.home3 = (Location) config.get("home_3");
    	this.home4 = (Location) config.get("home_4");
    }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("unique_id", uniqueId.toString());
        config.put("max_homes", Integer.valueOf(maxHomes));
        config.put("home_1", home1);
        config.put("home_2", home2);
        config.put("home_3", home3);
        config.put("home_4", home4);
        return config;
    }
    
    public UUID getUniqueId() {
    	return uniqueId;
    }
    
    public int getMaxHomes() {
    	return maxHomes;
    }
    
    public Location getHome(int number) {
    	if(number < 1 || number > 4) { throw new IndexOutOfBoundsException(); }
    	if(number == 4) { return home4; }
    	else if (number == 3) { return home3; }
    	else if (number == 2) { return home2; }
    	else { return home1; }
    }
    
    public void setMaxHomes(int maxHomes) {
    	if(maxHomes < 1 || maxHomes > 4) { throw new IndexOutOfBoundsException(); }
    	this.maxHomes = maxHomes;
    }
    
    public void setHome(Location home, int number) {
    	if(home == null) { throw new IllegalArgumentException(); }
    	if(number < 1 || number > 4) { throw new IndexOutOfBoundsException(); }
    	if(number == 4) { this.home4 = home; }
    	else if(number == 3) { this.home3 = home; }
    	else if(number == 2) { this.home2 = home; }
    	else { this.home1 = home; }
    }
}