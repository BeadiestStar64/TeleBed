package com.github.BeadiestStar64.Minecraft.Plugins.teleBed;

import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class TeleportData implements Serializable {
    @Serial
    private static final long serialVersionUID = 7475152136215709136L;
    private final int locationX;
    private final int locationY;
    private final int locationZ;
    private final World.Environment environment;
    private final UUID uuid;

    public TeleportData(Player player) {
        this.locationX = player.getLocation().getBlockX();
        this.locationY = player.getLocation().getBlockY();
        this.locationZ = player.getLocation().getBlockZ();
        this.environment = player.getWorld().getEnvironment();
        this.uuid = player.getUniqueId();
    }

    public World.Environment getEnvironment() {
        return environment;
    }

    public String getDimension() {

        String dimension;

        switch (getEnvironment()) {
            case NORMAL -> dimension = "オーバーワールド";
            case NETHER -> dimension = "ネザー";
            case THE_END -> dimension = "エンド";
            default -> dimension = "カスタム又は未対応";
        }

        return dimension;
    }

    public int getLocationX() {
        return locationX;
    }

    public int getLocationY() {
        return locationY;
    }

    public int getLocationZ() {
        return locationZ;
    }

    public UUID getUUID() {
        return uuid;
    }
}
