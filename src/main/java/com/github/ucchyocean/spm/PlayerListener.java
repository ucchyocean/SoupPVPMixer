/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.spm;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

/**
 * プレイヤーの行動を検知するリスナークラス
 * @author ucchy
 */
public class PlayerListener implements Listener {

    private ArrayList<String> respawnCache;

    protected PlayerListener() {
        respawnCache = new ArrayList<String>();
    }

    /**
     * プレイヤーの死亡を検知するメソッド
     * @param event
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

        Player player = event.getEntity();
        MatchingData data = SoupPVPMixer.instance.getMatchingDataFromPlayer(player);

        if ( data == null ) {
            return;
        }

        String winner;
        if ( data.getPlayer1().name.equals(player.getName()) ) {
            winner = data.getPlayer2().name;
        } else {
            winner = data.getPlayer1().name;
        }

        // 勝者のインベントリをクリアして、客席にテレポートする
        if ( SoupPVPMixer.config.winnerTeleportToSpectator ) {
            Player winnerPlayer = Bukkit.getPlayerExact(winner);
            SoupPVPMixer.clearInvAndHeal(winnerPlayer);
            if ( SoupPVPMixer.config.teleport.containsKey("spectator") ) {
                Location loc = SoupPVPMixer.config.teleport.get("spectator");
                winnerPlayer.teleport(loc, TeleportCause.PLUGIN);
            }
        }

        // 敗者はキャッシュして、リスポーン時にテレポートする
        if ( SoupPVPMixer.config.loserRespawnToSpectator ) {
            respawnCache.add(player.getName());
        }

        // マッチングデータを削除する
        SoupPVPMixer.instance.removeMatching(data);
    }

    /**
     * プレイヤーのリスポーンを検知するメソッド
     * @param event
     */
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {

        if ( SoupPVPMixer.config.loserRespawnToSpectator &&
                respawnCache.contains(event.getPlayer().getName()) &&
                SoupPVPMixer.config.teleport.containsKey("spectator") ) {
            Location loc = SoupPVPMixer.config.teleport.get("spectator");
            event.setRespawnLocation(loc);
            respawnCache.remove(event.getPlayer().getName());
        }
    }
}
