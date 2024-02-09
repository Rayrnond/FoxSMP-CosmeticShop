package com.reflexian.cosmeticshop.utilities;

import com.reflexian.levitycosmetics.data.objects.cosmetics.CosmeticType;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

@Getter@Setter
public class BundledShop {

    private final CosmeticType type;
    private final LinkedList<BundledCosmetic> T1 = new LinkedList<>();
    private final LinkedList<BundledCosmetic> T2 = new LinkedList<>();
    private final LinkedList<BundledCosmetic> T3 = new LinkedList<>();
    private final LinkedList<BundledCosmetic> T4 = new LinkedList<>();
    private final LinkedList<BundledCosmetic> T5 = new LinkedList<>();

    private boolean shuffled = false;

    private final long lastRefresh = System.currentTimeMillis();

    public BundledShop(CosmeticType type) {
        this.type = type;
    }

    public LinkedList<BundledCosmetic> getAll(){
        if (!shuffled) {
            Collections.shuffle(T4);
            Collections.shuffle(T5);
            shuffled = true;
        }
//        Bukkit.broadcastMessage("T1: " + T1.size());
//        Bukkit.broadcastMessage("T2: " + T2.size());
//        Bukkit.broadcastMessage("T3: " + T3.size());
//        Bukkit.broadcastMessage("T4: " + T4.size());
        return new LinkedList<BundledCosmetic>(){{
            addAll(T1);
            addAll(T2);
            addAll(T3);
            addAll(T4);
            addAll(T5);
        }};
    }

    public void purgeHot() {
        T1.removeIf(BundledCosmetic::isHot);
        T2.removeIf(BundledCosmetic::isHot);
        T3.removeIf(BundledCosmetic::isHot);
        T4.removeIf(BundledCosmetic::isHot);
        T5.removeIf(BundledCosmetic::isHot);
        shuffled = false;
    }
}
