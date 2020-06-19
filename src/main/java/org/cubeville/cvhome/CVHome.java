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

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import org.cubeville.commons.commands.CommandParser;
import org.cubeville.cvhome.commands.HomeInfoCommand;
import org.cubeville.cvhome.commands.SetHomeCommand;
import org.cubeville.cvhome.commands.HomeCommand;

public final class CVHome extends JavaPlugin implements Listener {
	
	private ConcurrentHashMap<String, UUID> names;
	private ConcurrentHashMap<UUID, String> uniqueIds;
	private ConcurrentHashMap<UUID, Home> homes;
	
	private File playerDataFolder;
	private File homeFolder;
	
    private CommandParser infoParser;
    private CommandParser setParser;
    private CommandParser teleportParser;
    
    @Override
    public void onEnable() {
        
    	names = new ConcurrentHashMap<String, UUID>();
    	uniqueIds = new ConcurrentHashMap<UUID, String>();
    	homes = new ConcurrentHashMap<UUID, Home>();
    	
        ConfigurationSerialization.registerClass(PlayerData.class);
        ConfigurationSerialization.registerClass(Home.class);
        
        playerDataFolder = new File(getDataFolder(), "PlayerData");
        if(!playerDataFolder.exists()) { playerDataFolder.mkdirs(); }
        for(File file : playerDataFolder.listFiles()) {
        	YamlConfiguration config = new YamlConfiguration();
        	try {
        		config.load(file);
        	}
        	catch(Exception e) {
        		getLogger().log(Level.WARNING, "Exception while loading player data: " + file.getName(), e);
        		continue;
        	}
        	PlayerData data = config.getSerializable("PlayerData", PlayerData.class);
        	names.put(data.getName().toLowerCase(), data.getUniqueId());
        }
        
        homeFolder = new File(getDataFolder(), "Homes");
        if(!homeFolder.exists()) { homeFolder.mkdirs(); }
        for(File file : homeFolder.listFiles()) {
        	YamlConfiguration config = new YamlConfiguration();
        	try {
        		config.load(file);
        	}
        	catch(Exception e) {
        		getLogger().log(Level.WARNING, "Exception while loading home: " + file.getName(), e);
        		continue;
        	}
        	Home home = config.getSerializable("Home", Home.class);
        	homes.put(home.getUniqueId(), home);
        }
        
        
        getServer().getPluginManager().registerEvents(this, this);
        
        infoParser = new CommandParser();
        infoParser.addCommand(new HomeInfoCommand(this));
        
        setParser = new CommandParser();
        setParser.addCommand(new SetHomeCommand(this));
        
        teleportParser = new CommandParser();
        teleportParser.addCommand(new HomeCommand(this));
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equals("homeinfo")) {
            return infoParser.execute(sender, args);
        }
        else if(command.getName().equals("sethome")) {
            return setParser.execute(sender, args);
        }
        else if(command.getName().equals("home")) {
            return teleportParser.execute(sender, args);
        }
        else {
            return false;
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String name = event.getPlayer().getName();
        UUID uniqueId = event.getPlayer().getUniqueId();
        if(!uniqueIds.containsKey(uniqueId)) {
        	names.put(name.toLowerCase(), uniqueId);
        	uniqueIds.put(uniqueId, name);
        	save(name, uniqueId);
        }
        else if(!names.containsKey(name.toLowerCase())) {
        	names.remove(uniqueIds.get(uniqueId).toLowerCase());
        	names.put(name.toLowerCase(), uniqueId);
        	uniqueIds.put(uniqueId, name);
        	save(name, uniqueId);
        }
    }
    
    public UUID getUniqueId(String name) {
    	return names.get(name.toLowerCase());
    }
    
    public String getName(UUID uniqueId) {
    	return uniqueIds.get(uniqueId);
    }
    
    public Home getHome(UUID uniqueId) {
    	return homes.get(uniqueId);
    }
    
    public int getMaxHomes(Player player) {
    	if(player.hasPermission("cvhome.max.4")) { return 4; }
    	else if(player.hasPermission("cvhome.max.3")) { return 3; }
    	else if(player.hasPermission("cvhome.max.2")) { return 2; }
    	else { return 1; }
    }
    
    public void setHome(UUID uniqueId, Home home) {
    	homes.put(uniqueId, home);
    	save(home);
    }
    
    public void updateMaxHomes(Player player) {
    	Home home = homes.get(player.getUniqueId());
    	if(home == null) { return; }
    	if(player.hasPermission("cvhome.max.4")) { home.setMaxHomes(4); }
    	else if(player.hasPermission("cvhome.max.3")) { home.setMaxHomes(3); }
    	else if(player.hasPermission("cvhome.max.2")) { home.setMaxHomes(2); }
    	else { home.setMaxHomes(1); };
    	homes.put(player.getUniqueId(), home);
    	save(home);
    }
    
    private void save(String name, UUID uniqueId) {
    	final YamlConfiguration config = new YamlConfiguration();
    	config.set("PlayerData", new PlayerData(name, uniqueId));
    	final File file = new File(playerDataFolder, uniqueId.toString() + ".yml");
    	getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
    		
    		@Override
    		public void run() {
    			try {
    				if(!file.exists()) { file.createNewFile(); }
    				config.save(file);
    			}
    			catch(IOException e) {  }
    		}
    	});
    }
    
    private void save(Home home) {
    	final YamlConfiguration config = new YamlConfiguration();
    	config.set("Home", home);
    	final File file = new File(homeFolder, home.getUniqueId().toString() + ".yml");
    	getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
    		
    		@Override
    		public void run() {
    			try {
    				if(!file.exists()) { file.createNewFile(); }
    				config.save(file);
    			}
    			catch(IOException e) {  }
    		}
    	});
    }
}