package dev.necron.stone.hologram.state;

import com.hakan.core.HCore;
import com.hakan.core.hologram.HHologram;
import com.hakan.core.utils.ColorUtil;
import dev.necron.stone.NecronStone;
import dev.necron.stone.configuration.config.NecronStoneConfigContainer;
import dev.necron.stone.hologram.NecronStoneHologramMode;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class NecronStoneHologramActive implements NecronHologramState {

    private final NecronStone stone;
    private HHologram hologram;

    public NecronStoneHologramActive(NecronStone stone) {
        this.stone = stone;
    }

    @Override
    public void create() {
        String id = "necron_stone_hologram_" + this.stone.getUID().toString();
        Location location = this.calculateLocation();

        this.hologram = HCore.createHologram(id, location);
        this.hologram.addLines(this.calculateLines());
    }

    @Override
    public void update() {
        List<String> lines = this.calculateLines();
        IntStream.range(0, lines.size()).forEach(i -> this.hologram.setLine(i, lines.get(i)));
    }

    @Override
    public void move(Location location) {
        this.hologram.setLocation(location);
    }

    @Override
    public void delete() {
        this.hologram.delete();
    }

    @Override
    public NecronStoneHologramMode getMode() {
        return NecronStoneHologramMode.ACTIVE;
    }


    @Override
    public Location calculateLocation() {
        Location location = this.stone.getLocation().add(0.5, 0, 0.5);
        location.setY(location.getY() + NecronStoneConfigContainer.HOLOGRAM_ACTIVE_HEIGHT.asDouble());
        return location;
    }

    @Override
    public List<String> calculateLines() {
        List<String> lines = new ArrayList<>();

        List<String> configLines = NecronStoneConfigContainer.HOLOGRAM_ACTIVE_LINES.getValue();
        for (String line : configLines) {
            lines.add(ColorUtil.colored(line)
                    .replace("%max_health%", this.stone.getMaxHealth() + "")
                    .replace("%health%", this.stone.getHealth() + "")
                    .replace("%last_damager%", this.stone.getLastDamager()));
        }

        return lines;
    }
}