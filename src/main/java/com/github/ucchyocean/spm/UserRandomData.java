/*
 * @author     ucchy
 * @license    LGPLv3
 * @copyright  Copyright ucchy 2014
 */
package com.github.ucchyocean.spm;


/**
 *
 * @author ucchy
 */
public class UserRandomData {

    private int point;
    private String name;

    public UserRandomData(int point, String name) {
        this.point = point;
        this.name = name;
    }

    /**
     * @return point
     */
    public int getPoint() {
        return point;
    }

    /**
     * @return data
     */
    public String getName() {
        return name;
    }
}
