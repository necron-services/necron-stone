package dev.necron.stone.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationUtil {

    public static String serialize(Location location) {
        if (location == null || location.getWorld() == null)
            return null;

        return location.getWorld().getName() + ":" + location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ();
    }

    public static Location deserialize(String stringLoc) {
        if (stringLoc == null)
            return null;

        String[] splitLoc = stringLoc.split(":");
        return new Location(Bukkit.getWorld(splitLoc[0]), Double.parseDouble(splitLoc[1]), Double.parseDouble(splitLoc[2]), Double.parseDouble(splitLoc[3]));
    }
}