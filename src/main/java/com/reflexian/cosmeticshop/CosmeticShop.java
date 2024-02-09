package com.reflexian.cosmeticshop;

import com.reflexian.cosmeticshop.commands.ShopCommand;
import com.reflexian.cosmeticshop.config.DefaultConfig;
import com.reflexian.cosmeticshop.converter.ConverterImpl;
import com.reflexian.cosmeticshop.utilities.inventory.InvUtils;
import fr.minuskube.inv.InventoryManager;
import lombok.Getter;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import pl.mikigal.config.ConfigAPI;
import pl.mikigal.config.style.CommentStyle;
import pl.mikigal.config.style.NameStyle;

import java.io.File;
import java.io.InputStreamReader;

@Getter
public final class CosmeticShop extends JavaPlugin {


    @Getter private static CosmeticShop instance;
    @Getter private static DefaultConfig DEFAULT_CONFIG;
    private InventoryManager inventoryManager;
    private ConverterImpl converter;


    @Override
    public void onEnable() {
        instance=this;

        checkInvFile("shop.yml");
        checkInvFile("page.yml");
        checkInvFile("confirmation.yml");
        checkInvFile("nicknames.yml");
        checkInvFile("titles.yml");
        checkInvFile("crowns.yml");

        inventoryManager = new InventoryManager(this);
        inventoryManager.init();

        InvUtils.init();

        converter = new ConverterImpl();

        this.loadConfigs();
        getCommand("cosmetics").setExecutor(new ShopCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void loadConfigs() {
        try {
            DEFAULT_CONFIG = ConfigAPI.init(DefaultConfig.class, NameStyle.CAMEL_CASE, CommentStyle.ABOVE_CONTENT, true, instance);
        }catch (Exception e) {
            instance.getLogger().severe("--START ERROR -- Failed to load config files! Disabling plugin.");
            e.printStackTrace();
            instance.getLogger().severe("--END ERROR -- Failed to load config files! Disabling plugin.");
            instance.getServer().getPluginManager().disablePlugin(instance);
        }
    }

    private void checkInvFile(String file) {
        File configFile = new File(getDataFolder()+ "/inventories", file);
        YamlConfiguration c;
        if (!configFile.exists()) {
            try {
                c = YamlConfiguration.loadConfiguration(new InputStreamReader(this.getResource("inventories/"+file), "UTF8"));
                c.save(getDataFolder() + "/inventories" + File.separator + file);
            } catch (Exception e) {
                getLogger().warning("Unable to save " +configFile.getName()+"!");
            }
            getLogger().info("Generated " + configFile.getName()+"!");
        }
    }
}
