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
    
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        
        ConfigurationSerialization.registerClass(Home.class);
        
        homeManager = new HomeManager(this);
        homeManager.start();
        
        infoHomeCommandParser = new CommandParser();
        infoHomeCommandParser.addCommand(new HomeInfo());
        
        setHomeCommandParser = new CommandParser();
        setHomeCommandParser.addCommand(new HomeSet());
        
        tpHomeCommandParser = new CommandParser();
        tpHomeCommandParser.addCommand(new HomeTeleport());
        
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        homeManager.updatePlayerName(event.getPlayer());
        homeManager.updatePlayerMaxHomes(event.getPlayer());
    }
    
    @Override
    public void onDisable() {
        homeManager.stop();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equals("homeinfo")) {
            return infoHomeCommandParser.execute(sender, args);
        }
        else if(command.getName().equals("sethome")) {
            return setHomeCommandParser.execute(sender, args);
        }
        else if(command.getName().equals("home")) {
            return tpHomeCommandParser.execute(sender, args);
        }
        else {
            return false;
        }
    }
}