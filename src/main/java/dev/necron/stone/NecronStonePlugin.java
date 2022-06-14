package dev.necron.stone;

import com.hakan.core.HCore;
import org.bukkit.plugin.java.JavaPlugin;

public class NecronStonePlugin extends JavaPlugin {

    private static NecronStonePlugin instance;

    public static NecronStonePlugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        HCore.initialize(this);
        NecronStoneHandler.initialize(this);
    }

    @Override
    public void onDisable() {
        NecronStoneHandler.uninitialize();
    }
}