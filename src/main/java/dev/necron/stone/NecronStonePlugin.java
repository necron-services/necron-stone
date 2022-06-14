package dev.necron.stone;

import com.hakan.core.HCore;
import org.bukkit.plugin.java.JavaPlugin;

public class NecronStonePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        HCore.initialize(this);
        NecronStoneHandler.initialize(this);
    }

    @Override
    public void onDisable() {
        NecronStoneHandler.uninitialize();
    }
}