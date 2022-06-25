package dev.necron.stone.hologram.state;

import dev.necron.stone.hologram.NecronStoneHologramMode;
import org.bukkit.Location;

import java.util.List;

public interface NecronHologramState {

    void create();

    void update();

    void move(Location location);

    void delete();

    NecronStoneHologramMode getMode();

    Location calculateLocation();

    List<String> calculateLines();
}