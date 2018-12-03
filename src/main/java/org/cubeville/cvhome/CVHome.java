package org.cubeville.cvhome;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import org.cubeville.commons.commands.CommandParser;
import org.cubeville.cvhome.commands.HomeInfo;
import org.cubeville.cvhome.commands.HomeSet;
import org.cubeville.cvhome.commands.HomeTeleport;

public class CVHome extends JavaPlugin implements Listener {

    private HomeManager homeManager;
    private CommandParser infoHomeCommandParser;
    private CommandParser setHomeCommandParser;
    private CommandParser tpHomeCommandParser;
    
    private static CVHome instance;
    
    public static CVHome getInstance() {
        return instance;
    }
    
    @Override
    public void onEnable() {
        instance = this;
        
        getServer().getPluginManager().registerEvents(this, this);
        
        ConfigurationSerialization.registerClass(Home.class);
        
        this.homeManager = new HomeManager(this);
        this.homeManager.start();
        
        this.infoHomeCommandParser = new CommandParser();
        this.infoHomeCommandParser.addCommand(new HomeInfo());
        
        this.setHomeCommandParser = new CommandParser();
        this.setHomeCommandParser.addCommand(new HomeSet());
        
        this.tpHomeCommandParser = new CommandParser();
        this.tpHomeCommandParser.addCommand(new HomeTeleport());
        
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.homeManager.updatePlayerName(event.getPlayer());
        this.homeManager.updatePlayerMaxHomes(event.getPlayer());
    }
    
    @Override
    public void onDisable() {
        this.homeManager.stop();
        instance = null;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equals("homeinfo")) {
            return this.infoHomeCommandParser.execute(sender, args);
        }
        else if(command.getName().equals("sethome")) {
            return this.setHomeCommandParser.execute(sender, args);
        }
        else if(command.getName().equals("home")) {
            return this.tpHomeCommandParser.execute(sender, args);
        }
        else {
            return false;
        }
    }
}