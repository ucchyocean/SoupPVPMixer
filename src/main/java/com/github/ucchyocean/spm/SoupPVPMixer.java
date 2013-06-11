/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.spm;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.ucchyocean.bp.BPData;
import com.github.ucchyocean.bp.BPUserData;
import com.github.ucchyocean.bp.BattlePoints;

/**
 * Soup PVP Mixer
 * @author ucchy
 */
public class SoupPVPMixer extends JavaPlugin {

    private static final String[] COMMANDS = {
        "kit", "clear", "teleport", "return", "match",
    };

    protected static SoupPVPMixer instance;
    protected static SoupPVPMixerConfig config;
    private BattlePoints bp;

    private Player spectator;
    private ArrayList<MatchingData> matching;

    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
     */
    @Override
    public void onEnable() {

        instance = this;

        // コンフィグをロードする
        config = new SoupPVPMixerConfig();

        // BattlePoints を取得する
        bp = (BattlePoints)getServer().getPluginManager().getPlugin("BattlePoints");
    }

    /**
     * プラグインのデータフォルダを返す
     * @return プラグインのデータフォルダ
     */
    protected static File getConfigFolder() {
        return instance.getDataFolder();
    }

    /**
     * プラグインのJarファイル自体を示すFileオブジェクトを返す
     * @return プラグインのJarファイル
     */
    protected static File getPluginJarFile() {
        return instance.getFile();
    }

    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    @Override
    public boolean onCommand(
            CommandSender sender, Command command, String label, String[] args) {


        if ( args.length <= 0 ) {
            return false;
        }

        if ( !isValidCommand(args[0]) ) {
            return false;
        }

        if ( !sender.hasPermission("souppvpmixer." + args[0]) ) {
            sender.sendMessage(ChatColor.RED + "You don't have permission \"souppvpmixer." + args[0] + "\".");
            return true;
        }

        if ( args[0].equalsIgnoreCase("kit") ) {
            return doKit(sender, command, label, args);
        } else if ( args[0].equalsIgnoreCase("clear") ) {
            return doClear(sender, command, label, args);
        } else if ( args[0].equalsIgnoreCase("teleport") ) {
            return doTeleport(sender, command, label, args);
        } else if ( args[0].equalsIgnoreCase("return") ) {
            return doReturn(sender, command, label, args);
        } else if ( args[0].equalsIgnoreCase("match") ) {
            return doMatch(sender, command, label, args);
        }

        return false;
    }

    private boolean doKit(CommandSender sender, Command command, String label, String[] args) {

        // プレイヤーを取得する
        ArrayList<Player> players = getPlayersWithoutCreative();

        if ( players.size() <= 0 ) {
            sender.sendMessage("対象のプレイヤーが誰も居ません。");
            return true;
        }

        for ( Player p : players ) {
            // 全回復、全アイテムクリア
            p.setHealth(20);
            p.setFoodLevel(20);
            p.getInventory().clear();
            p.getInventory().setHelmet(null);
            p.getInventory().setChestplate(null);
            p.getInventory().setLeggings(null);
            p.getInventory().setBoots(null);

            // アイテム追加
            for ( ItemStack i : config.kitItems ) {
                p.getInventory().addItem(i);
            }
        }

        return true;
    }

    private boolean doClear(CommandSender sender, Command command, String label, String[] args) {

        // プレイヤーを取得する
        ArrayList<Player> players = getPlayersWithoutCreative();

        if ( players.size() <= 0 ) {
            sender.sendMessage("対象のプレイヤーが誰も居ません。");
            return true;
        }

        for ( Player p : players ) {
            // 全回復、全アイテムクリア
            p.setHealth(20);
            p.setFoodLevel(20);
            p.getInventory().clear();
            p.getInventory().setHelmet(null);
            p.getInventory().setChestplate(null);
            p.getInventory().setLeggings(null);
            p.getInventory().setBoots(null);
        }

        return true;
    }

    private boolean doTeleport(CommandSender sender, Command command, String label, String[] args) {

        if ( matching == null ) {
            sender.sendMessage("マッチングデータがまだありません。");
            return true;
        }

        for ( int i=0; i<matching.size(); i++ ) {
            MatchingData data = matching.get(i);
            String key = String.format("player%d-%d", (i+1), 1);
            Player player1 = Bukkit.getPlayerExact(data.getPlayer1().name);
            Location location1 = config.teleport.get(key);
            if ( player1 != null && location1 != null ) {
                player1.teleport(location1, TeleportCause.PLUGIN);
            }

            key = String.format("player%d-%d", (i+1), 2);
            Player player2 = Bukkit.getPlayerExact(data.getPlayer2().name);
            Location location2 = config.teleport.get(key);
            if ( player2 != null && location2 != null ) {
                player2.teleport(location2, TeleportCause.PLUGIN);
            }
        }

        Location spectatorLocation = config.teleport.get("spectator");
        if ( spectator != null && spectatorLocation != null ) {
            spectator.teleport(spectatorLocation, TeleportCause.PLUGIN);
        }

        return true;
    }

    private boolean doReturn(CommandSender sender, Command command, String label, String[] args) {

        ArrayList<Player> players = getPlayersWithoutCreative();

        if ( players.size() <= 0 ) {
            sender.sendMessage("対象のプレイヤーが誰も居ません。");
            return true;
        }

        Location spectatorLocation = config.teleport.get("spectator");
        if ( spectatorLocation != null ) {
            for ( Player p : players ) {
                p.teleport(spectatorLocation, TeleportCause.PLUGIN);
            }
        }
        return true;
    }

    private boolean doMatch(CommandSender sender, Command command, String label, String[] args) {

        // プレイヤーを取得する
        ArrayList<Player> players = getPlayersWithoutCreative();
        spectator = null;

        if ( players.size() <= 0 ) {
            sender.sendMessage("対象のプレイヤーが誰も居ません。");
            return true;
        }

        Random random = new Random();

        // 人数が奇数なら、1人ランダムで休ませる
        if ( players.size() % 2 == 1 ) {
            int index = random.nextInt(players.size());
            spectator = players.get(index);
            players.remove(index);
        }

        BPData data = bp.getBPData();
        HashMap<String, BPUserData> userdata = new HashMap<String, BPUserData>();
        ArrayList<BPUserData> userRandomData = new ArrayList<BPUserData>();

        for ( Player p : players ) {
            int point = data.getPoint(p.getName());
            int randomPoint = point + random.nextInt(config.matchingRandomRange);
            userdata.put(p.getName(), new BPUserData(p.getName(), point));
            userRandomData.add(new BPUserData(p.getName(), randomPoint));
        }

        // ソート
        Collections.sort(userRandomData, new Comparator<BPUserData>(){
            public int compare(BPUserData ent1, BPUserData ent2){
                return ent2.point - ent1.point;
            }
        });

        // 2人ずつペアにする
        matching = new ArrayList<MatchingData>();
        for ( int i=0; i<userRandomData.size()/2; i++ ) {
            String player1 = userRandomData.get(i*2).name;
            String player2 = userRandomData.get(i*2+1).name;
            MatchingData d = new MatchingData(
                    userdata.get(player1), userdata.get(player2));
            matching.add(d);
        }

        // マッチングを表示する
        Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "===== Matching =====");
        for ( int i=0; i<matching.size(); i++ ) {
            MatchingData d = matching.get(i);
            Bukkit.broadcastMessage(String.format(ChatColor.RED + "%d. %s(%dP) - %s(%dP)",
                    (i+1), d.getPlayer1().name, d.getPlayer1().point,
                    d.getPlayer2().name, d.getPlayer2().point));
        }

        return true;
    }

    private ArrayList<Player> getPlayersWithoutCreative() {

        ArrayList<Player> players = new ArrayList<Player>();
        Player[] all = Bukkit.getOnlinePlayers();
        for ( Player p : all ) {
            if ( p.getGameMode() != GameMode.CREATIVE ) {
                players.add(p);
            }
        }
        return players;
    }

    private boolean isValidCommand(String command) {

        for ( String c : COMMANDS ) {
            if ( c.equalsIgnoreCase(command) ) {
                return true;
            }
        }
        return false;
    }
}
