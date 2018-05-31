package com.wk.server.external;

/**
 * The definition wrapper for trees
 */
public final class ObjectWoodcuttingDef {

    /**
     * How much experience identifying gives
     */
    private double exp;

    /**
     * Percent chance the tree will fall
     */
    private int fell;

    /**
     * The id of the ore this turns into
     */
    private int logId;

    /**
     * Herblaw level required to identify
     */
    private int requiredLvl;

    /**
     * How long the tree takes to respawn afterwards
     */
    private int respawnTime;

    public double getExp() {
        return exp;
    }

    public int getFell() {
        return fell;
    }

    public int getLogId() {
        return logId;
    }

    public int getReqLevel() {
        return requiredLvl;
    }

    public int getRespawnTime() {
        return respawnTime;
    }

}
