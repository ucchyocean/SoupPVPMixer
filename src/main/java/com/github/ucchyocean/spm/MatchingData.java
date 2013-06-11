/*
 * @author     ucchy
 * @license    GPLv3
 * @copyright  Copyright ucchy 2013
 */
package com.github.ucchyocean.spm;

import com.github.ucchyocean.bp.BPUserData;

/**
 * マッチングデータ
 * @author ucchy
 */
public class MatchingData {

    private BPUserData player1;
    private BPUserData player2;

    public MatchingData(BPUserData player1, BPUserData player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    /**
     * @return player1
     */
    public BPUserData getPlayer1() {
        return player1;
    }

    /**
     * @return player2
     */
    public BPUserData getPlayer2() {
        return player2;
    }
}
