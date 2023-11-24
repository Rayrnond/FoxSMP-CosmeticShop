package com.reflexian.cosmeticshop.utilities.inventory;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

public class ClickAction {


    @Getter private final String id;
    @Getter private final BiConsumer<Player,ClickAction> consumer;
    @Getter private boolean disabled = false;

    public ClickAction(String id, BiConsumer<Player, ClickAction> consumer) {
        this.id = id;
        this.consumer = consumer;
    }

    public ClickAction disableIf(boolean key) {
        disabled=key;
        return this;
    }

}
