package dev.necron.stone.hologram;

import com.hakan.core.HCore;
import com.hakan.core.hologram.HHologram;
import com.hakan.core.utils.ColorUtil;
import com.hakan.core.utils.TimeUtil;
import dev.necron.stone.NecronStone;
import dev.necron.stone.configuration.config.NecronStoneConfigContainer;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class NecronStoneHologram {

    private final NecronStone stone;
    private HHologram hologram;
    private NecronStoneHologramType type;

    public NecronStoneHologram(NecronStone stone) {
        this.stone = stone;
        this.type = NecronStoneHologramType.ACTIVE;
    }

    public NecronStone getStone() {
        return this.stone;
    }

    public NecronStoneHologramType getType() {
        return this.type;
    }

    public void changeType(NecronStoneHologramType type) {
        this.type = type;
        this.delete();
        this.create();
    }


    public void create() {
        String id = "necron_stone_hologram_" + this.stone.getUID().toString();
        Location location = this.stone.getLocation().add(0.5, this.calculateHeight(), 0.5);

        this.hologram = HCore.createHologram(id, location);
        this.hologram.addLines(this.calculateLines());
    }

    public void update() {
        List<String> lines = this.calculateLines();
        IntStream.range(0, lines.size()).forEach(i -> this.hologram.setLine(i, lines.get(i)));
    }

    public void move(Location location) {
        this.hologram.setLocation(location);
    }

    public void delete() {
        this.hologram.delete();
    }


    private double calculateHeight() {
        if (this.type == NecronStoneHologramType.COOLDOWN)
            return NecronStoneConfigContainer.HOLOGRAM_COOLDOWN_HEIGHT.asDouble();
        return NecronStoneConfigContainer.HOLOGRAM_ACTIVE_HEIGHT.asDouble();
    }

    private List<String> calculateLines() {
        List<String> lines = new ArrayList<>();

        if (this.type == NecronStoneHologramType.ACTIVE) {
            List<String> configLines = NecronStoneConfigContainer.HOLOGRAM_ACTIVE_LINES.getValue();
            for (String line : configLines) {
                lines.add(ColorUtil.colored(line)
                        .replace("%max_health%", this.stone.getMaxHealth() + "")
                        .replace("%health%", this.stone.getHealth() + "")
                        .replace("%last_damager%", this.stone.getLastDamager()));
            }
        } else {
            List<String> configLines = NecronStoneConfigContainer.HOLOGRAM_COOLDOWN_LINES.getValue();
            for (String line : configLines) {
                String colored = ColorUtil.colored(line);
                Matcher matcher = Pattern.compile("(?<=%<timePattern=)(?<timePattern>.+?)(?=>%)").matcher(colored);
                while (matcher.find()) {
                    String pattern = matcher.group("timePattern");
                    String format = TimeUtil.formatTime(this.stone.getRemainTime(), pattern);
                    colored = colored.replace("%<timePattern=" + pattern + ">%", format);
                }
                lines.add(colored);
            }
        }

        return lines;
    }
}