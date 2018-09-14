package org.cubeville.cvhome.commands;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import org.cubeville.commons.commands.Command;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandResponse;

import org.cubeville.cvhome.Home;
import org.cubeville.cvhome.HomeManager;

public class HomeImport extends Command {

    public HomeImport() {
        super("");
        addBaseParameter(new CommandParameterString());
    }

    @SuppressWarnings("deprecation")
    @Override
    public CommandResponse execute(Player player, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters)
            throws CommandExecutionException {
        
        if(!player.hasPermission("cvhome.admin.importhome")) {
            throw new CommandExecutionException("&cNo permission.");
        }
        
        player.sendMessage(ChatColor.AQUA + "Import starting...");
        
        BufferedReader br = null;
        String fileName = (String) baseParameters.get(0);
        String line = "";
        List<String> linesReadIn = new ArrayList<String>();
        
        try {
            br = new BufferedReader(new FileReader(fileName));
            while((line = br.readLine()) != null) {
                linesReadIn.add(line);
            }
        }
        catch(FileNotFoundException e) {
            throw new CommandExecutionException("&cCould not open file!");
        }
        catch(IOException e) {
            throw new CommandExecutionException("&cError while trying to read in file contents!");
        }
        
        if(br != null) {
            try {
                br.close();
            }
            catch (IOException e) {
                throw new CommandExecutionException("&cError while attempting to close the reader.");
            }
        }
        
        player.sendMessage(ChatColor.AQUA + "File opened and read in.");
        
        String[][] linesArray = new String[linesReadIn.size()][8];
        line = "";
        String delimiter = ",";
        String[] splitLine = null;
        
        for(int i = 0; i < linesReadIn.size(); i++) {
            line = linesReadIn.get(i);
            splitLine = line.split(delimiter);
            for(int j = 0; j < 8; j++) {
                linesArray[i][j] = splitLine[j];
            }
        }
        
        HomeManager homeManager = HomeManager.getInstance();
        OfflinePlayer offlinePlayer = null;
        Home playerHome = null;
        Location homeLocation = null;
        List<Home> playerHomes = new ArrayList<Home>();
        
        for(int i = 0; i < linesArray.length; i++) {
            
            offlinePlayer = homeManager.getPlugin().getServer().getOfflinePlayer(linesArray[i][0]);
            if(offlinePlayer == null) { continue; }
            
            playerHome = new Home(offlinePlayer.getUniqueId());
            try {
                homeLocation = new Location(Bukkit.getWorld(linesArray[i][1]), Double.parseDouble(linesArray[i][3]),
                        Double.parseDouble(linesArray[i][4]), Double.parseDouble(linesArray[i][5]),
                        Float.parseFloat(linesArray[i][7]), Float.parseFloat(linesArray[i][6]));
            }
            catch(NullPointerException | NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Problem reading in Location on line " + ChatColor.AQUA + i);
                continue;
            }
            
            playerHome.setHome1(homeLocation);
            playerHomes.add(playerHome);
            offlinePlayer = null;
        }
        
        homeManager.setHomesFromImport(playerHomes);
       
        return new CommandResponse("&aImport complete.");
        
    }
}
