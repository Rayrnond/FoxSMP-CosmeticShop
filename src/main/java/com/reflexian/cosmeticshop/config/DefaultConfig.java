package com.reflexian.cosmeticshop.config;

import pl.mikigal.config.Config;
import pl.mikigal.config.annotation.Comment;
import pl.mikigal.config.annotation.ConfigName;
import pl.mikigal.config.annotation.ConfigPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigName("config.yml")
public interface DefaultConfig extends Config {


    @Comment("The price of the nickname ticket")
    @ConfigPath("nickname-ticket-price")
    default int getNicknameTicketPrice() {
        return 1000;
    }

    @Comment("Set the cost of each cosmetic by rarity.")
    @ConfigPath("costs")
    default Map<String, Integer> getCosts() {
        Map<String, Integer> map = new HashMap<>();
        map.put("1", 1000);
        map.put("2", 2000);
        map.put("3", 3000);
        map.put("4", 4000);
        return map;
    }


    @Comment("Cost override for specific cosmetics")
    @ConfigPath("override")
    default Map<String, Integer> getOverride() {
        Map<String, Integer> map = new HashMap<>();
        map.put("red", 1000);
        return map;
    }

    @Comment("Will never show these cosmetics")
    @ConfigPath("blacklist")
    default List<String> getBlacklist() {
        List<String> list = new ArrayList<>();
        list.add("blue");
        return list;
    }

    @Comment("Will only show the cosmetics in the blacklist")
    @ConfigPath("reverse-blacklist")
    default boolean getReverseBlacklist() {
        return false;
    }

//
//    @Comment("This will force certain cosmetics during a timeperiod.\nUses date format yyyy-MM-dd e.g. 2020-01-01 is 1st January 2020")
//    @ConfigPath("forced")
//    default Map<String, String> getForced() {
//        Map<String, String> map = new HashMap<>();
//        map.put("2020-01-01", "red");
//        return map;
//    }

    @Comment("This section specifies the cosmetics that will be rotated every 12 hours instead of 24.")
    @ConfigPath("hot")
    default List<String> getHot() {
        List<String> list = new ArrayList<>();
        list.add("purple");
        list.add("green");
        return list;
    }


}
