package dev.necron.stone.hologram;

import com.hakan.core.hologram.HHologram;
import dev.necron.stone.NecronStone;
import org.bukkit.Location;

import java.util.List;

public abstract class NecronStoneHologram {

    public static NecronStoneHologram create(NecronStone stone) {
        NecronStoneHologramMode mode = (stone.isAlive()) ? NecronStoneHologramMode.ACTIVE : NecronStoneHologramMode.COOLDOWN;
        return mode.create(stone);
    }


    protected final NecronStone stone;
    protected HHologram hologram;

    public NecronStoneHologram(NecronStone stone) {
        this.stone = stone;
    }

    public NecronStone getStone() {
        return this.stone;
    }

    public abstract void create();

    public abstract void update();

    public abstract void move(Location location);

    public abstract void delete();

    protected abstract double calculateHeight();

    protected abstract List<String> calculateLines();
}