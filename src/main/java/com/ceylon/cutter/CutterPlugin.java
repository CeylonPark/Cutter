package com.ceylon.cutter;

import com.ceylon.cutter.command.CutterCommand;
import com.ceylon.cutter.command.CutterCreateCommand;
import com.ceylon.cutter.listener.CutterInventory;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public final class CutterPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        if(this.getDataFolder().mkdirs()) {
            this.getLogger().info("\""+this.getDataFolder().getPath()+"\" 풀더가 생성되었습니다.");
        }
        // file load
        this.loadConfig();
        CutterAddition.getInstance().load(this);
        // init CutterInventory
        CutterInventory cutterInventory = new CutterInventory(this, "세공열기");
        // init Listeners
        this.getServer().getPluginManager().registerEvents(cutterInventory, this);
        // init Commands
        Objects.requireNonNull(this.getCommand("세공열기")).setExecutor(cutterInventory);
        Objects.requireNonNull(this.getCommand("세공열기")).setTabCompleter(cutterInventory);
        CutterCommand cutterCommand = new CutterCommand(this, "세공설정");
        Objects.requireNonNull(this.getCommand("세공설정")).setExecutor(cutterCommand);
        Objects.requireNonNull(this.getCommand("세공설정")).setTabCompleter(cutterCommand);
        CutterCreateCommand cutterCreateCommand = new CutterCreateCommand(this, "돌제작");
        Objects.requireNonNull(this.getCommand("돌제작")).setExecutor(cutterCreateCommand);
        Objects.requireNonNull(this.getCommand("돌제작")).setTabCompleter(cutterCreateCommand);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        // file save
        CutterAddition.getInstance().save(this);
    }

    private void loadConfig() {
        File file = new File(this.getDataFolder(), "config.yml");
        try {
            if(file.createNewFile()) {
                FileConfiguration yml = YamlConfiguration.loadConfiguration(file);
                yml.set("initial", 75);
                yml.set("minimum", 25);
                yml.set("maximum", 75);
                yml.set("step", 10);
                yml.save(file);
                this.getLogger().info("config.yml 파일이 생성되었습니다.");
            }
        } catch(IOException ignore) {}
        CutterConfig cutterConfig = CutterConfig.getInstance();
        FileConfiguration yml = YamlConfiguration.loadConfiguration(file);
        cutterConfig.setInitial(yml.getInt("initial"));
        cutterConfig.setMinimum(yml.getInt("minimum"));
        cutterConfig.setMaximum(yml.getInt("maximum"));
        cutterConfig.setStep(yml.getInt("step"));
    }
}
