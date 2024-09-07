package com.github.BeadiestStar64.Minecraft.Plugins.teleBed;

import org.bukkit.Material;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UnsafeBlocks {

    // リスポーン地点に危険とみなす足場のマテリアルのSet
    private Set<Material> unsafeMaterials;

    // このクラスのインスタンス
    private static UnsafeBlocks instance;

    // インスタンスの取得
    public static UnsafeBlocks getInstance() {
        if (instance == null) {
            instance = new UnsafeBlocks();
        }
        return instance;
    }

    // Setの作成
    private void initUnsafeMaterials() {
        if (unsafeMaterials == null) {
            unsafeMaterials = Stream.of(Material.values())
                    .filter(this::isUnsafeMaterial)
                    .collect(Collectors.toSet());
        }
    }

    // 足場の判定を行う
    private boolean isUnsafeMaterial(Material material) {
        return isRail(material) || isButton(material) || isPressurePlate(material) || isSlab(material) || isStairs(material)
                || isDoor(material) || isGate(material) || isFence(material) || isTrapDoor(material) || isWall(material);
    }

    // 足場がレール系統のマテリアルか?
    private boolean isRail(Material material) {
        return material.name().endsWith("_RAIL") || (material == Material.RAIL) ;
    }

    // 足場がボタン系統のマテリアルか?
    private boolean isButton(Material material) {
        return material.name().endsWith("_BUTTON");
    }

    // 足場がドア系統のブロックか?
    private boolean isDoor(Material material) {return material.name().endsWith("_DOOR");}

    // 足場がフェンスゲートか?
    private boolean isGate(Material material) {return material.name().endsWith("_GATE");}

    // 足場がフェンスか?
    private boolean isFence(Material material) {return material.name().endsWith("_FENCE");}

    // 足場が感圧版系統のマテリアルか?
    private boolean isPressurePlate(Material material) {
        return material.name().endsWith("_PRESSURE_PLATE");
    }

    // 足場がハーフブロック系か?
    private boolean isSlab(Material material) {return material.name().endsWith("_SLAB");}

    // 足場が階段系統のブロックか?
    private boolean isStairs(Material material) {return material.name().endsWith("_STAIRS");}

    // 足場がトラップドアか?
    private boolean isTrapDoor(Material material) {return material.name().endsWith("_TRAPDOOR");}

    // 足場が塀か?
    private boolean isWall(Material material) {return material.name().endsWith("_WALL");}

    // 外部からの呼び出しに応答する関数
    public boolean isUnsafeBlock(Material material) {
        initUnsafeMaterials();
        return unsafeMaterials.contains(material);
    }
}
