package dev.necron.stone.hologram;

import dev.necron.stone.NecronStone;
import dev.necron.stone.hologram.types.NecronStoneHologramActive;
import dev.necron.stone.hologram.types.NecronStoneHologramCooldown;

public enum NecronStoneHologramMode {

    ACTIVE(NecronStoneHologramActive.class),
    COOLDOWN(NecronStoneHologramCooldown.class),
    ;


    private final Class<? extends NecronStoneHologram> hologramClass;

    NecronStoneHologramMode(Class<? extends NecronStoneHologram> hologramClass) {
        this.hologramClass = hologramClass;
    }

    public NecronStoneHologram create(NecronStone stone) {
        try {
            return this.hologramClass
                    .getConstructor(NecronStone.class)
                    .newInstance(stone);
        } catch (Exception e) {
            return new NecronStoneHologramActive(stone);
        }
    }
}