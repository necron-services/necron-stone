package dev.necron.stone.hologram.types;

import com.hakan.core.HCore;
import com.hakan.core.utils.ColorUtil;
import com.hakan.core.utils.TimeUtil;
import dev.necron.stone.NecronStone;
import dev.necron.stone.configuration.config.NecronStoneConfigContainer;
import dev.necron.stone.hologram.NecronStoneHologram;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class NecronStoneHologramCooldown extends NecronStoneHologram {

    public NecronStoneHologramCooldown(NecronStone stone) {
        super(stone);
    }

    @Override
    public void create() {
        String id = "necron_stone_hologram_" + super.stone.getUID().toString();
        Location location = super.stone.getLocation().add(0.5, this.calculateHeight(), 0.5);

        super.hologram = HCore.createHologram(id, location);
        super.hologram.addLines(this.calculateLines());
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
    protected double calculateHeight() {
        return NecronStoneConfigContainer.HOLOGRAM_COOLDOWN_HEIGHT.asDouble();
    }

    @Override
    protected List<String> calculateLines() {
        List<String> lines = new ArrayList<>();

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

        return lines;
    }
}