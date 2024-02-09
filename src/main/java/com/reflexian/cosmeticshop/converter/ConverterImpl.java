package com.reflexian.cosmeticshop.converter;

import com.reflexian.cosmeticshop.CosmeticShop;
import com.reflexian.cosmeticshop.config.DefaultConfig;
import com.reflexian.cosmeticshop.utilities.BundledCosmetic;
import com.reflexian.cosmeticshop.utilities.BundledShop;
import com.reflexian.levitycosmetics.data.objects.cosmetics.CosmeticType;
import com.reflexian.levitycosmetics.data.objects.cosmetics.helpers.Cosmetic;
import com.reflexian.levitycosmetics.data.objects.user.UserData;
import com.reflexian.levitycosmetics.data.objects.user.UserDataService;
import lombok.Getter;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.reflexian.levitycosmetics.data.objects.cosmetics.CosmeticType.*;

@Getter
public class ConverterImpl implements Converter {

    final List<CosmeticType> validTypes = Arrays.asList(CosmeticType.HAT,CosmeticType.CROWN,CosmeticType.GLOW,CosmeticType.CHAT_COLOR,CosmeticType.NICKNAME_PAINT,CosmeticType.TITLE,CosmeticType.TITLE_PAINT,CosmeticType.TAB_COLOR,JOIN_MESSAGE);
    private final PlayerPointsAPI playerPointsAPI;

    public ConverterImpl(){
        playerPointsAPI = PlayerPoints.getInstance().getAPI();
    }

    @Override
    public boolean canAfford(Player player, double price) {
        return playerPointsAPI.look(player.getUniqueId()) >= price;
    }

    @Override
    public void withdraw(Player player, double price) {
        if (!canAfford(player, price)) return;
        playerPointsAPI.take(player.getUniqueId(), (int) price);
    }

    @Override
    public boolean hasCosmetic(Player player, String cosmetic) {
        UserData userData = UserDataService.shared.retrieveUserFromCache(player.getUniqueId());
        if (userData == null) return true;
        return userData.getUserCosmetics().stream().anyMatch(e->e.getCosmetic() != null && e.getCosmetic().getName().equalsIgnoreCase(cosmetic));
    }

    @Override
    public void giveCosmetic(Player player, String cosmetic) {
        Cosmetic cos = Cosmetic.getCosmetic(cosmetic);
        if (cos == null) return;
        UserData userData = UserDataService.shared.retrieveUserFromCache(player.getUniqueId());
        if (userData == null) return;
        cos.giveToUser(userData);
    }

    @Override
    public List<String> getValidCosmetics() {
        List<String> cosmetics = Cosmetic.getAllCosmetics().stream().filter(e->validTypes.contains(e.getType())).map(Cosmetic::getName).collect(Collectors.toList());
        List<String> blacklist = CosmeticShop.getDEFAULT_CONFIG().getBlacklist();
        if (CosmeticShop.getDEFAULT_CONFIG().getReverseBlacklist()) {
            cosmetics.removeIf(e->!blacklist.contains(e));
        } else {
            for (String s : CosmeticShop.getDEFAULT_CONFIG().getBlacklist()) {
//                if (blacklist.contains(s)) {
//                    System.out.println("Blacklist contains " + s);
//                } else {
//                    System.out.println("Blacklist does not contain " + s);
//                }
                cosmetics.removeIf(e->e.equalsIgnoreCase(s));
            }
//            System.out.println("Blacklist size: " + blacklist.size());
            cosmetics.removeIf(blacklist::contains);
//            System.out.println("After size: " + cosmetics.size());
        }

        return cosmetics;
    }

//    private final ConcurrentHashMap<CosmeticType, LinkedList<BundledCosmetic>> todayCosmetics = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<CosmeticType, BundledShop> todayCosmetics = new ConcurrentHashMap<>();
    private final List<String> hotCosmetics = new ArrayList<>();
    private long lastUpdate = 0;
    private long lastUpdateHot = 0; // hot update is twice per day

    @Override
    public LinkedList<BundledCosmetic> getTodayCosmetics(CosmeticType type) {

        // set last update to today at 5PM EST if it is not already set
        if (lastUpdate == 0) {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("EST"));
            calendar.set(Calendar.HOUR_OF_DAY, 17);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            lastUpdate = calendar.getTimeInMillis();
            lastUpdateHot = lastUpdate;
        }

        if (lastUpdateHot == 0) {
            lastUpdateHot = lastUpdate-1000*60*60*12; // remove 12 hours
        }

        if (System.currentTimeMillis() > lastUpdate + (1000 * 60 * 60 * 24)){
            todayCosmetics.clear();
            lastUpdate = 0;
        }

        if (System.currentTimeMillis() > lastUpdateHot + (1000 * 60 * 60 * 12)) {
            for (CosmeticType cosmeticType : todayCosmetics.keySet()) {
                todayCosmetics.get(cosmeticType).purgeHot();
            }

            hotCosmetics.clear();
            lastUpdateHot = 0;
        }


        if (todayCosmetics.containsKey(type)) {
            return todayCosmetics.getOrDefault(type, new BundledShop(type)).getAll();
        }
        updateCosmetics();
        return todayCosmetics.getOrDefault(type, new BundledShop(type)).getAll();
    }

    @Override
    public String formattedUntilExpires(boolean hot) {
        long expires = lastUpdate+(1000*60*60*(hot?12:24));

        long diff = expires-System.currentTimeMillis();
        long hours = diff/(1000*60*60);
        long minutes = (diff%(1000*60*60))/(1000*60);

        if (hours > 0) {
            return hours+"h "+minutes+"m";
        } else if (minutes > 0) {
            return minutes+"m";
        } else {
            return "Soon!";
        }
    }

    private void updateCosmetics() {
        LinkedList<String> cosmeticStrings = new LinkedList<>(getValidCosmetics());
        Collections.shuffle(cosmeticStrings);

        DefaultConfig config = CosmeticShop.getDEFAULT_CONFIG();

        for (CosmeticType validType : getValidTypes()) {
            if (!todayCosmetics.containsKey(validType)) todayCosmetics.put(validType, new BundledShop(validType));
        }


        for (CosmeticType validType : validTypes) {
            BundledShop bundledShop = todayCosmetics.get(validType);

            List<Cosmetic> cosmetics = getFrom(validType);
            if (validType.equals(CosmeticType.TITLE)) cosmetics.addAll(getFrom(CosmeticType.TITLE_PAINT));

            for (Cosmetic cosmetic : cosmetics) {
                if (cosmetic == null) continue;
                boolean hot = false;


                if (config.getHot().contains(cosmetic.getName())) {
                    hotCosmetics.add(cosmetic.getName());
                    hot = true;
                }

                double cost = config.getOverride().getOrDefault(cosmetic.getName(), config.getCosts().getOrDefault(cosmetic.getRarity()+"", 1000));

                if (validType == CosmeticType.CROWN) {
                    if (cosmetic.getType() == CosmeticType.CROWN) {
                        if (cosmetic.getRarity() == 1 && bundledShop.getT1().isEmpty()) {
                            bundledShop.getT1().add(new BundledCosmetic(cosmetic, cost, hot,false));
                        } else if (cosmetic.getRarity() == 2 && bundledShop.getT2().isEmpty()) {
                            bundledShop.getT2().add(new BundledCosmetic(cosmetic, cost, hot,false));
                        }
                        continue;
                    }
                }

                if (validType == CosmeticType.TITLE) {

                    if (cosmetic.getType() == CosmeticType.TITLE) {
                        if (bundledShop.getT5().size()>=7) continue;
                        bundledShop.getT5().add(new BundledCosmetic(cosmetic, cost, hot,false));
                        continue;
                    }

                }

                if (cosmetic.getRarity() == 1 && bundledShop.getT1().isEmpty()) {
                    bundledShop.getT1().add(new BundledCosmetic(cosmetic, cost, hot,false));
                } else if (cosmetic.getRarity() == 2 && bundledShop.getT2().isEmpty()) {
                    bundledShop.getT2().add(new BundledCosmetic(cosmetic, cost, hot,false));
                } else if (cosmetic.getRarity() == 3 && bundledShop.getT3().size() < 2) {
                    bundledShop.getT3().add(new BundledCosmetic(cosmetic, cost, hot,false));
                } else if (cosmetic.getType() == TITLE_PAINT && cosmetic.getRarity() == 4 && bundledShop.getT4().size() < 2) {
                    bundledShop.getT4().add(new BundledCosmetic(cosmetic, cost, hot,false));
                } else if (cosmetic.getType() != TITLE_PAINT && cosmetic.getType() != TITLE && cosmetic.getRarity() == 4) {
                    if (bundledShop.getT4().size()>=8 || (validType==CosmeticType.TITLE && cosmetic.getType()!=CosmeticType.TITLE)) continue;
                    bundledShop.getT4().add(new BundledCosmetic(cosmetic, cost, hot,false));
                }

            }
            todayCosmetics.replace(validType, bundledShop);
        }

    }

    private List<Cosmetic> getFrom(CosmeticType type) {
        return Cosmetic.getAllCosmetics().stream().filter(e->e.getType() == type).collect(Collectors.toList());
    }
}
