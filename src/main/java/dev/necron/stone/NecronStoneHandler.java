package dev.necron.stone;

import com.hakan.core.HCore;
import dev.necron.stone.commands.NecronStoneCommand;
import dev.necron.stone.configuration.NecronStoneConfiguration;
import dev.necron.stone.configuration.config.NecronStoneConfigContainer;
import dev.necron.stone.database.NecronStoneDatabase;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
                    if (!stone.isAlive() && stone.getRemainTime() <= 0)
                        stone.respawn();
                }));


        //EVENT
        HCore.registerEvent(BlockBreakEvent.class)
                .consume(event -> NecronStoneHandler.findByLocation(event.getBlock().getLocation()).ifPresent(stone -> {
                    event.setCancelled(true);

                    Player player = event.getPlayer();
                    if (!stone.isAlive())
                        return;

                    boolean isFinished = stone.damage(player, 1);
                    stone.getHologram().update();

                    if (isFinished) {
                        for (String rewardCommand : stone.getRewards()) {
                            String command = rewardCommand.replace("%player%", player.getName());
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                        }
                    }
                }));
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
        NecronStone stone = new NecronStone(location);
        stone.setRewards(NecronStoneConfigContainer.REWARD_COMMANDS.getValue());
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