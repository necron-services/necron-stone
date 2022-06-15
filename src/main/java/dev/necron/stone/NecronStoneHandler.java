package dev.necron.stone;

import com.hakan.core.HCore;
import com.hakan.core.particle.HParticle;
import dev.necron.stone.commands.NecronStoneCommand;
import dev.necron.stone.configuration.NecronStoneConfiguration;
import dev.necron.stone.configuration.config.NecronStoneConfigContainer;
import dev.necron.stone.database.NecronStoneDatabase;
import dev.necron.stone.listeners.NecronStoneListener;
import dev.necron.stone.utils.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class NecronStoneHandler {

    private static final Map<Location, NecronStone> stoneMap = new HashMap<>();

    public static void initialize(NecronStonePlugin plugin) {

        //CONFIGURATION
        NecronStoneConfiguration.initialize(plugin);


        //COMMAND
        HCore.registerCommands(new NecronStoneCommand());


        //DATABASE
        NecronStoneDatabase.initialize();


        //CACHE
        NecronStoneDatabase.getProvider().getValues().forEach(stone -> {
            stoneMap.put(stone.getLocation(), stone);
            stone.getHologram().create();
        });


        //TIMER
        HCore.syncScheduler().every(20)
                .freezeIf(task -> stoneMap.values().size() <= 0)
                .run(() -> stoneMap.values().forEach(stone -> {
                    if (!stone.isAlive())
                        stone.getHologram().update();
                    if (!stone.isAlive() && stone.getRemainTime() <= 0) {
                        stone.respawn();

                        Bukkit.broadcastMessage(NecronStoneConfigContainer.MESSAGE_INFO_RESPAWN_STONE_BROADCAST.asString()
                                .replace("%location%", LocationUtil.serialize(stone.getLocation()).replace(":", ",")));

                        List<Player> show = new ArrayList<>(stone.getLocation().getWorld().getPlayers());
                        HParticle particle1 = new HParticle("PORTAL", 250, 0.1, new Vector(1.2, 1.2, 1.2));
                        HCore.playParticle(show, stone.getLocation().add(0.5, 0.5, 0.5), particle1);
                    }
                }));


        //EVENT
        HCore.registerListeners(new NecronStoneListener());

        HCore.registerEvent(BlockBreakEvent.class)
                .filter(event -> event.getPlayer().isSneaking())
                .filter(event -> event.getPlayer().isOp() || event.getPlayer().hasPermission("necronstone.break"))
                .consume(event -> NecronStoneHandler.findByLocation(event.getBlock().getLocation()).ifPresent(NecronStoneHandler::delete));
    }

    public static void uninitialize() {
        NecronStoneDatabase.getProvider()
                .getUpdater().updateAll();
    }


    public static Map<Location, NecronStone> getContentSafe() {
        return new HashMap<>(stoneMap);
    }

    public static Map<Location, NecronStone> getContent() {
        return stoneMap;
    }

    public static Collection<NecronStone> getValuesSafe() {
        return new ArrayList<>(stoneMap.values());
    }

    public static Collection<NecronStone> getValues() {
        return stoneMap.values();
    }

    public static Optional<NecronStone> findByLocation(Location location) {
        return Optional.ofNullable(stoneMap.get(location));
    }

    public static NecronStone getByLocation(Location location) {
        return findByLocation(location).orElseThrow(() -> new IllegalArgumentException("there is no stone at this location"));
    }

    public static NecronStone create(Location location) {
        NecronStone stone = new NecronStone(location, NecronStoneConfigContainer.REWARD_COMMANDS.getValue());
        stone.getDatabase().insertAsync();
        stone.getHologram().create();

        stoneMap.put(location, stone);
        return stone;
    }

    public static void delete(NecronStone stone) {
        stoneMap.remove(stone.getLocation());
        stone.getHologram().delete();
        stone.getDatabase().deleteAsync();
    }
}