package dev.necron.stone.events;

import dev.necron.stone.NecronStone;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class NecronStoneRespawnEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }


    private final NecronStone stone;

    public NecronStoneRespawnEvent(NecronStone stone) {
        this.stone = stone;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public NecronStone getStone() {
        return this.stone;
    }
}