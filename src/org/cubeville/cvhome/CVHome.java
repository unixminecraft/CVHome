package org.cubeville.cvhome;

import org.bukkit.plugin.java.JavaPlugin;

public class CVHome extends JavaPlugin {

    private HomeManager homeManager;
    
    private static CVHome instance;
    
    public static CVHome getInstance() {
        return instance;
    }
    
}