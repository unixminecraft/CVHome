package org.cubeville.cvhome.commands;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.entity.Player;
import org.cubeville.commons.commands.Command;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.cvhome.Home;
import org.cubeville.cvhome.HomeManager;

public class HomeUpdate extends Command {

    public HomeUpdate() {
        super("");
        addBaseParameter(new CommandParameterString());
    }

    @Override
    public CommandResponse execute(Player player, Set<String> flags, Map<String, Object> parameters, List<Object> baseParameters)
            throws CommandExecutionException {
        
        if(!player.hasPermission("cvhome.admin.homeupdate")) {
            throw new CommandExecutionException("&cNo permission.");
        }
        
        player.sendMessage(ChatColor.AQUA + "Import starting.");
        
        BufferedReader br = null;
        String playerFileName = (String) baseParameters.get(0);
        String line = "";
        List<String> linesReadIn = new ArrayList<String>();
        
        try {
            br = new BufferedReader(new FileReader(playerFileName));
            while((line = br.readLine()) != null) {
                linesReadIn.add(line);
            }
        }
        catch(FileNotFoundException e) {
            throw new CommandExecutionException("&cCould not find the file.");
        }
        catch(IOException e) {
            throw new CommandExecutionException("&cCould not read the file.");
        }
        
        if(br != null) {
            try {
                br.close();
            }
            catch(IOException e) {
                throw new CommandExecutionException("&cError closing the reader.");
            }
        }
        
        player.sendMessage(ChatColor.AQUA + "File opened and read in.");
        
        String[][] linesArray = new String[linesReadIn.size()][2];
        line = "";
        String delimiter = ";";
        String[] splitLine = null;
        
        for(int i = 0; i < linesReadIn.size(); i++) {
            line = linesReadIn.get(i);
            splitLine = line.split(delimiter);
            for(int j = 0; j < 2; j++) {
                linesArray[i][j] = splitLine[j];
            }
        }
        
        Map<UUID,String> playerMap = new HashMap<UUID,String>();
        for(int i = 0; i < linesArray.length; i++) {
            try {
                playerMap.put(UUID.fromString(linesArray[i][1]), linesArray[i][0]);
            }
            catch(IllegalArgumentException e) {
                player.sendMessage(ChatColor.YELLOW + "Unable to process UUID for player: " + linesArray[i][0]);
                continue;
            }
        }
        
        HomeManager homeManager = HomeManager.getInstance();
        List<Home> playerHomes = homeManager.getHomesForUpdate();
        Home playerHome = null;
        for(int i = 0; i < playerHomes.size(); i++) {
            playerHome = playerHomes.get(i);
            playerHome.setPlayerName(playerMap.get(playerHome.getPlayerId()));
            playerHomes.set(i, playerHome);
        }
        homeManager.setHomesFromUpdate(playerHomes);
        
        return new CommandResponse("&aHome update complete.");
    }

}
