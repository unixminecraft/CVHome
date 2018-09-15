package org.cubeville.cvhome.commands;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Location;
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
        addBaseParameter(new CommandParameterString());
    }

    @Override
    public CommandResponse execute(Player player, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters)
            throws CommandExecutionException {
        
        if(!player.hasPermission("cvhome.admin.importhome")) {
            throw new CommandExecutionException("&cNo permission.");
        }
        
        player.sendMessage(ChatColor.AQUA + "Import starting...");
        
        BufferedReader pbr = null;
        BufferedReader hbr = null;
        String playerFileName = (String) baseParameters.get(0);
        String homeFileName = (String) baseParameters.get(1);
        String line = "";
        List<String> playerLinesReadIn = new ArrayList<String>();
        List<String> homeLinesReadIn = new ArrayList<String>();
        
        try {
            pbr = new BufferedReader(new FileReader(playerFileName));
            while((line = pbr.readLine()) != null) {
                playerLinesReadIn.add(line);
            }
        }
        catch(FileNotFoundException e) {
            throw new CommandExecutionException("&cCould not open player file!");
        }
        catch(IOException e) {
            throw new CommandExecutionException("&cError while trying to read in player file contents!");
        }
        
        if(pbr != null) {
            try {
                pbr.close();
            }
            catch (IOException e) {
                throw new CommandExecutionException("&cError while attempting to close the player reader.");
            }
        }
        
        line = "";
        
        try {
            hbr = new BufferedReader(new FileReader(homeFileName));
            while((line = hbr.readLine()) != null) {
                homeLinesReadIn.add(line);
            }
        }
        catch(FileNotFoundException e) {
            throw new CommandExecutionException("&cCould not open homes file!");
        }
        catch(IOException e) {
            throw new CommandExecutionException("&cError while trying to read in homes file contents!");
        }
        
        if(hbr != null) {
            try {
                hbr.close();
            }
            catch (IOException e) {
                throw new CommandExecutionException("&cError while attempting to close the home reader.");
            }
        }
        
        player.sendMessage(ChatColor.AQUA + "Files opened and read in.");
        
        String[][] playerLinesArray = new String[playerLinesReadIn.size()][2];
        line = "";
        String playerDelimiter = ";";
        String[] playerSplitLine = null;
        
        for(int i = 0; i < playerLinesReadIn.size(); i++) {
            line = playerLinesReadIn.get(i);
            playerSplitLine = line.split(playerDelimiter);
            for(int j = 0; j < 2; j++) {
                playerLinesArray[i][j] = playerSplitLine[j];
            }
        }
        
        Map<String, UUID> playerUUIDs = new HashMap<String, UUID>();
        for(int i = 0; i < playerLinesArray.length; i++) {
            try {
                playerUUIDs.put(playerLinesArray[i][0], UUID.fromString(playerLinesArray[i][1]));
            }
            catch(IllegalArgumentException e) {
                player.sendMessage(ChatColor.YELLOW + "Unable to process UUID for player: " + playerLinesArray[i][0]);
                continue;
            }
        }
        
        String[][] homeLinesArray = new String[homeLinesReadIn.size()][8];
        line = "";
        String homeDelimiter = ",";
        String[] homeSplitLine = null;
        
        for(int i = 0; i < homeLinesReadIn.size(); i++) {
            line = homeLinesReadIn.get(i);
            homeSplitLine = line.split(homeDelimiter);
            for(int j = 0; j < 8; j++) {
                homeLinesArray[i][j] = homeSplitLine[j].substring(1, homeSplitLine[j].length() - 1);
            }
        }
        
        String possiblePlayerName = null;
        Location possiblePlayerHome = null;
        Map<String, Location> initialPlayerHomes = new HashMap<String, Location>();
        for(int i = 0; i < homeLinesArray.length; i++) {
            possiblePlayerName = homeLinesArray[i][0];
            try {
                possiblePlayerHome = new Location(Bukkit.getWorld(homeLinesArray[i][1]), 
                        Double.parseDouble(homeLinesArray[i][3]), Double.parseDouble(homeLinesArray[i][4]),
                        Double.parseDouble(homeLinesArray[i][5]), Float.parseFloat(homeLinesArray[i][7]), 
                        Float.parseFloat(homeLinesArray[i][6]));
            }
            catch(NullPointerException | NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Problem reading in Location for player: " + homeLinesArray[i][0]);
                continue;
            }
            initialPlayerHomes.put(possiblePlayerName, possiblePlayerHome);
            possiblePlayerHome = null;
        }
        
        Iterator<String> playerHomeNames = initialPlayerHomes.keySet().iterator();
        String playerName = null;
        UUID playerUUID = null;
        Home playerHome = null;
        List<Home> finalPlayerHomes = new ArrayList<Home>();
        while(playerHomeNames.hasNext()) {
            playerName = playerHomeNames.next();
            if(playerUUIDs.containsKey(playerName)) {
                playerUUID = playerUUIDs.get(playerName);
                if(playerUUID == null) {
                    player.sendMessage(ChatColor.RED + "Problem getting UUID for player: " + playerName);
                    continue;
                }
                playerHome = new Home(playerUUID);
                playerHome.setHome1(initialPlayerHomes.get(playerName));
                finalPlayerHomes.add(playerHome);
                playerUUID = null;
            }
        }
        
        player.sendMessage(ChatColor.AQUA + "Homes created, importing into the HomeManager.");
        
        HomeManager homeManager = HomeManager.getInstance();
        homeManager.setHomesFromImport(finalPlayerHomes);
        
        return new CommandResponse("&aImport complete.");
    }
}
