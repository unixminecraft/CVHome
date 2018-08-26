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
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import org.cubeville.commons.commands.Command;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandResponse;

import org.cubeville.cvhome.Home;
import org.cubeville.cvhome.HomeManager;
import org.cubeville.cvhome.TempHome;

public class HomeImport extends Command {

    public HomeImport() {
        super("");
        addBaseParameter(new CommandParameterString());
    }

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
        
        UUID possiblePlayerId = null;
        Map<UUID, Integer> numberOfHomesPerPlayer = new HashMap<UUID, Integer>();
        int temp;
        TempHome possibleHome = null;
        Location possibleLocation = null;
        List<TempHome> possibleHomes = new ArrayList<TempHome>();
        
        for(int i = 0; i < linesArray.length; i++) {
            try {
                possiblePlayerId = UUID.fromString(linesArray[i][2]);
            }
            catch (IllegalArgumentException e) {
                continue;
            }
            try {
                possibleLocation = new Location(Bukkit.getWorld(linesArray[i][1]), Double.parseDouble(linesArray[i][3]),
                        Double.parseDouble(linesArray[i][4]), Double.parseDouble(linesArray[i][5]),
                        Float.parseFloat(linesArray[i][7]), Float.parseFloat(linesArray[i][6]));
            }
            catch (NullPointerException | NumberFormatException e) {
                continue;
            }
            
            possibleHome = new TempHome(possibleLocation, linesArray[i][0], possiblePlayerId);
            possibleHomes.add(possibleHome);
            
            if(numberOfHomesPerPlayer.containsKey(possiblePlayerId)) {
                temp = numberOfHomesPerPlayer.get(possiblePlayerId).intValue() + 1;
                numberOfHomesPerPlayer.replace(possiblePlayerId, Integer.valueOf(temp));
            } else {
                numberOfHomesPerPlayer.put(possiblePlayerId, Integer.valueOf(1));
            }
        }
        
        Iterator<UUID> uuidCountIterator = numberOfHomesPerPlayer.keySet().iterator();
        UUID tempHomeUUID = null;
        TempHome singleTempHome = null;
        Home singleHome = null;
        List<Home> singleHomes = new ArrayList<Home>();
        List<TempHome> retTempHomes = null;
        List<TempHome> multipleTempHomes = new ArrayList<TempHome>();
        List<UUID> multipleHomeUUID = new ArrayList<UUID>();
        
        while(uuidCountIterator.hasNext()) {
            tempHomeUUID = uuidCountIterator.next();
            if(numberOfHomesPerPlayer.get(tempHomeUUID).intValue() == 1) {
                singleTempHome = findTempHome(tempHomeUUID, possibleHomes);
                singleHome = new Home(singleTempHome.getPlayerId());
                singleHome.setHome1(singleTempHome.getHome());
                singleHomes.add(singleHome);
            }
            else {
                multipleHomeUUID.add(tempHomeUUID);
                retTempHomes = findTempHomes(tempHomeUUID, possibleHomes);
                for(TempHome retTempHome: retTempHomes) {
                    multipleTempHomes.add(retTempHome);
                }
            }
        }
        
        List<TempHome> possibleMultiHomes = null;
        HomeManager homeManager = HomeManager.getInstance();
        OfflinePlayer[] offlinePlayers = homeManager.getPlugin().getServer().getOfflinePlayers();
        Home multipleToSingleHome = null;
        List<Home> multipleToSingleHomes = new ArrayList<Home>();
        
        for(UUID multiUUID: multipleHomeUUID) {
            possibleMultiHomes = findTempHomes(multiUUID, multipleTempHomes);
            for(TempHome tempHome: possibleMultiHomes) {
                if(doesPlayerExist(tempHome, offlinePlayers)) {
                    multipleToSingleHome = new Home(tempHome.getPlayerId());
                    multipleToSingleHome.setHome1(tempHome.getHome());
                    multipleToSingleHomes.add(multipleToSingleHome);
                    break;
                }
            }
        }
        
        List<Home> finalHomesToAdd = new ArrayList<Home>();
        for(Home finalSingleHome: singleHomes) {
            finalHomesToAdd.add(finalSingleHome);
        }
        for(Home finalMultipleToSingleHome: multipleToSingleHomes) {
            finalHomesToAdd.add(finalMultipleToSingleHome);
        }
        
        homeManager.setHomesFromImport(finalHomesToAdd);
        
        return new CommandResponse("&aImport complete.");
    }
    
    private TempHome findTempHome(UUID playerId, List<TempHome> tempHomes) {
        for(TempHome tempHome: tempHomes) {
            if(tempHome.getPlayerId().equals(playerId)) {
                return tempHome;
            }
        }
        return null;
    }
    
    private List<TempHome> findTempHomes(UUID playerId, List<TempHome> tempHomes) {
        List<TempHome> ret = new ArrayList<TempHome>();
        for(TempHome tempHome: tempHomes) {
            if(tempHome.getPlayerId().equals(playerId)) {
                ret.add(tempHome);
            }
        }
        return ret;
    }
    
    private boolean doesPlayerExist(TempHome tempHome, OfflinePlayer[] offlinePlayers) {
        for(int i = 0; i < offlinePlayers.length; i++) {
            if(tempHome.getName().equals(offlinePlayers[i].getName())) {
                return true;
            }
        }
        return false;
    }
}
