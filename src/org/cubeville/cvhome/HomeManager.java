package org.cubeville.cvhome;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class HomeManager implements Listener {

    private Plugin plugin;
    private Integer taskId;
    private List<Home> playerHomes;
    private static HomeManager instance;
    
}