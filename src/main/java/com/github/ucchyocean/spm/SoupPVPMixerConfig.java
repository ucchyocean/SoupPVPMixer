/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.spm;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

/**
 * Soup PVP Mixer Config
 * @author ucchy
 *
 */
public class SoupPVPMixerConfig {

    protected int matchingRandomRange;
    protected ArrayList<ItemStack> kitItems;
    protected ArrayList<ItemStack> kitArmor;
    protected HashMap<String, Location> teleport;

    public SoupPVPMixerConfig() {
        reloadConfig();
    }

    public void reloadConfig() {

        // フォルダやファイルがない場合は、作成したりする
        File dir = SoupPVPMixer.getConfigFolder();
        if ( !dir.exists() ) {
            dir.mkdirs();
        }

        File file = new File(dir, "config.yml");

        if ( !file.exists() ) {
            Utility.copyFileFromJar(SoupPVPMixer.getPluginJarFile(),
                    file, "config.yml", false);
        }

        // 再読み込み処理
        SoupPVPMixer.instance.reloadConfig();
        FileConfiguration config = SoupPVPMixer.instance.getConfig();

        // 各コンフィグの取得
        KitHandler handler = new KitHandler();

        matchingRandomRange = config.getInt("matchingRandomRange", 30);
        kitItems = handler.convertToItemStack(config.getString("kit.items", ""));
        kitArmor = handler.convertToItemStack(config.getString("kit.armor", ""));

        ConfigurationSection section = config.getConfigurationSection("teleport");
        teleport = new HashMap<String, Location>();
        if ( section != null ) {
            for ( String key : section.getKeys(false) ) {
                teleport.put(key, getLocation(section.getString(key)));
            }
        }
    }

    private Location getLocation(String str) {

        World world = Bukkit.getWorld("world");
        String[] temp = str.split(",");
        if ( temp.length >= 3 && temp[0].matches("-?[0-9]+") &&
                temp[1].matches("-?[0-9]+") && temp[2].matches("-?[0-9]+") ) {
            int x = Integer.parseInt(temp[0]);
            int y = Integer.parseInt(temp[1]);
            int z = Integer.parseInt(temp[2]);
            return new Location(world, x, y, z);
        }
        return null;
    }
}
