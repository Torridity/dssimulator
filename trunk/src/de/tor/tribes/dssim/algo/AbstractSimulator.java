/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.dssim.algo;

import de.tor.tribes.dssim.types.AbstractUnitElement;
import de.tor.tribes.dssim.types.SimulatorResult;
import de.tor.tribes.dssim.types.UnitHolder;
import de.tor.tribes.dssim.util.ConfigManager;
import java.util.Hashtable;

/**
 *
 * @author Charon
 */
public abstract class AbstractSimulator {

    public final int ID_INFANTRY = 0;
    public final int ID_CAVALRY = 1;
    public final int ID_ARCHER = 2;
    private Hashtable<UnitHolder, AbstractUnitElement> off = null;
    private Hashtable<UnitHolder, AbstractUnitElement> def = null;
    private boolean nightBonus = false;
    private double luck = 0.0;
    private double moral = 100;
    private int wallLevel = 0;
    private int buildingLevel = 0;
    private int farmLevel = 0;
    private boolean attackerBelieve = false;
    private boolean defenderBelieve = false;
    private boolean cataChurch = false;
    private boolean cataFarm = false;

    public abstract SimulatorResult calculate(Hashtable<UnitHolder, AbstractUnitElement> pOff, Hashtable<UnitHolder, AbstractUnitElement> pDef, boolean pNightBonus, double pLuck, double pMoral, int pWallLevel, int pBuildingLevel, int pFarmLevel, boolean pAttackerBelieve, boolean pDefenderBelieve, boolean pCataChurch, boolean pCataFarm);

    public SimulatorResult bunkerBuster(Hashtable<UnitHolder, AbstractUnitElement> pOff, Hashtable<UnitHolder, AbstractUnitElement> pDef, boolean pNightBonus, double pLuck, double pMoral, int pWallLevel, int pBuildingLevel, int pFarmLevel, boolean pAttackerBelieve, boolean pDefenderBelieve, boolean pCataChurch, boolean pCataFarm) {
        SimulatorResult result = calculate(pOff, pDef, pNightBonus, pLuck, pMoral, pWallLevel, pBuildingLevel, pFarmLevel, pAttackerBelieve, pDefenderBelieve, pCataChurch, pCataFarm);
        setFarmLevel(pFarmLevel);
        setCataChurch(pCataChurch);
        setCataFarm(pCataFarm);
        int cnt = 1;
        while (!result.isWin() && cnt <= 1000) {
            cnt++;
            result = calculate(pOff, result.getSurvivingDef(), pNightBonus, pLuck, pMoral, result.getWallLevel(), result.getBuildingLevel(), pFarmLevel, pAttackerBelieve, pDefenderBelieve, pCataChurch, pCataFarm);
        }
        if (cnt > 1000) {
            result.setNukes(Integer.MAX_VALUE);
        } else {
            result.setNukes(cnt);
        }
        return result;
    }

    public boolean isInfantry(UnitHolder pUnit) {
        return !isCavalery(pUnit);
    }

    public boolean isCavalery(UnitHolder pUnit) {
        return (pUnit.getPlainName().equals("spy") ||
                pUnit.getPlainName().equals("light") ||
                pUnit.getPlainName().equals("marcher") ||
                pUnit.getPlainName().equals("heavy") ||
                pUnit.getPlainName().equals("knight"));
    }

    public boolean isArcher(UnitHolder pUnit) {
        return (pUnit.getPlainName().equals("archer") ||
                pUnit.getPlainName().equals("marcher"));
    }

    public boolean isSpy(UnitHolder pUnit) {
        return (pUnit.getPlainName().equals("spy"));
    }

    public void setOff(Hashtable<UnitHolder, AbstractUnitElement> pOff) {
        off = pOff;
    }

    public Hashtable<UnitHolder, AbstractUnitElement> getOff() {
        return off;
    }

    public void setDef(Hashtable<UnitHolder, AbstractUnitElement> pDef) {
        def = pDef;
    }

    public Hashtable<UnitHolder, AbstractUnitElement> getDef() {
        return def;
    }

    /**
     * @return the nightBonus
     */
    public boolean isNightBonus() {
        return nightBonus;
    }

    /**
     * @param nightBonus the nightBonus to set
     */
    public void setNightBonus(boolean nightBonus) {
        this.nightBonus = nightBonus;
    }

    /**
     * @return the luck
     */
    public double getLuck() {
        return luck;
    }

    /**
     * @param luck the luck to set
     */
    public void setLuck(double luck) {
        this.luck = luck;
    }

    /**
     * @return the moral
     */
    public double getMoral() {
        return moral;
    }

    /**
     * @param moral the moral to set
     */
    public void setMoral(double moral) {
        this.moral = moral;
    }

    /**
     * @return the wallLevel
     */
    public int getWallLevel() {
        return wallLevel;
    }

    /**
     * @param wallLevel the wallLevel to set
     */
    public void setWallLevel(int wallLevel) {
        this.wallLevel = wallLevel;
    }

    /**
     * @return the buildingLevel
     */
    public int getBuildingLevel() {
        return buildingLevel;
    }

    /**
     * @param buildingLevel the buildingLevel to set
     */
    public void setBuildingLevel(int buildingLevel) {
        this.buildingLevel = buildingLevel;
    }

    /**
     * @return the farmLevel
     */
    public int getFarmLevel() {
        return farmLevel;
    }

    /**
     * @param farmLevel the farmLevel to set
     */
    public void setFarmLevel(int farmLevel) {
        this.farmLevel = farmLevel;
    }

    /**
     * @return the attackerBelieve
     */
    public boolean isAttackerBelieve() {
        if (ConfigManager.getSingleton().isChurch()) {
            return attackerBelieve;
        } else {
            //if no church is used take care that the believe factor would be 1 later
            return true;
        }
    }

    /**
     * @param attackerBelieve the attackerBelieve to set
     */
    public void setAttackerBelieve(boolean attackerBelieve) {
        this.attackerBelieve = attackerBelieve;
    }

    /**
     * @return the defenderBelieve
     */
    public boolean isDefenderBelieve() {
        if (ConfigManager.getSingleton().isChurch()) {
            return defenderBelieve;
        } else {
            //if no church is used take care that the believe factor would be 1 later
            return true;
        }
    }

    /**
     * @param defenderBelieve the defenderBelieve to set
     */
    public void setDefenderBelieve(boolean defenderBelieve) {
        this.defenderBelieve = defenderBelieve;
    }

    /**
     * @return the cataChurch
     */
    public boolean isCataChurch() {
        return cataChurch;
    }

    /**
     * @param cataChurch the cataChurch to set
     */
    public void setCataChurch(boolean cataChurch) {
        this.cataChurch = cataChurch;
    }

    /**
     * @return the cataFarm
     */
    public boolean isCataFarm() {
        return cataFarm;
    }

    /**
     * @param cataFarm the cataFarm to set
     */
    public void setCataFarm(boolean cataFarm) {
        this.cataFarm = cataFarm;
    }
}
