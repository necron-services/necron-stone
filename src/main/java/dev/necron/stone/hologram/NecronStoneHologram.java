package dev.necron.stone.hologram;

import dev.necron.stone.NecronStone;
import dev.necron.stone.hologram.state.NecronHologramState;
import dev.necron.stone.hologram.state.NecronStoneHologramActive;
import org.bukkit.Location;

import java.util.List;

public class NecronStoneHologram {

    private final NecronStone stone;
    private NecronHologramState state;

    public NecronStoneHologram(NecronStone stone) {
        this.stone = stone;
        this.state = new NecronStoneHologramActive(stone);
    }

    public NecronStone getStone() {
        return this.stone;
    }

    public void create() {
        this.state.create();
    }

    public void update() {
        this.state.update();
    }

    public void move(Location location) {
        this.state.move(location);
    }

    public void delete() {
        this.state.delete();
    }

    public Location calculateLocation() {
        return this.state.calculateLocation();
    }

    public List<String> calculateLines() {
        return this.state.calculateLines();
    }

    public NecronStoneHologramMode getMode() {
        return this.state.getMode();
    }

    public void setMode(NecronStoneHologramMode mode) {
        this.state.delete();
        this.state = mode.create(this.stone);
        this.state.create();
    }
}