package com.ceylon.cutter;

import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CutterAddition {
    private static final CutterAddition instance = new CutterAddition();

    public static CutterAddition getInstance() {
        return CutterAddition.instance;
    }

    private final Map<String, Integer> cutterAdditions = new HashMap<>();

    private CutterAddition() { }

    public void setCutterAddition(String playerName, int addition) {
        this.cutterAdditions.put(playerName, addition);
    }

    public int getAddition(String playerName) {
        return this.cutterAdditions.getOrDefault(playerName, 0);
    }

    public void load(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), "players.yml");
        if(file.mkdirs()) {
            plugin.getLogger().info("\"" + file.getPath() + "\" 파일이 생성되었습니다.");
        }
        FileConfiguration yml = YamlConfiguration.loadConfiguration(file);
        MemorySection players = (MemorySection) yml.get("players");
        if(players == null) {
            return;
        }
        for(String playerName : players.getKeys(false)) {
            this.cutterAdditions.put(playerName, yml.getInt("players." + playerName));
        }
    }

    public void save(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), "players.yml");
        FileConfiguration yml = YamlConfiguration.loadConfiguration(file);
        for (String playerName : this.cutterAdditions.keySet()) {
            yml.set("players." + playerName, this.cutterAdditions.get(playerName));
        }
        try {
            yml.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
