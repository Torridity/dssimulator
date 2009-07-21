/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.dssim.algo;

import de.tor.tribes.dssim.types.AbstractUnitElement;
import de.tor.tribes.dssim.types.UnitHolder;
import java.util.Hashtable;

/**
 *
 * @author Charon
 */
public abstract class AbstractSimulator {

    private boolean nightBonus = false;
    private double luck = 0.0;
    private double moral = 100;
    private int wallLevel = 0;
    private int buildingLevel = 0;
    private boolean win = false;
    private double offDecrement = 0;
    private double defDecrement = 0;
    private int wallResult = 0;
    private int cataResult = 0;

    public abstract void calculate(Hashtable<UnitHolder, AbstractUnitElement> pOff, Hashtable<UnitHolder, AbstractUnitElement> pDef, boolean pNightBonus, double pLuck, double pMoral, int pWallLevel, int pBuildingLevel);

    public boolean isInfantry(UnitHolder pUnit) {
        return !isCavalery(pUnit);
    }

    public boolean isCavalery(UnitHolder pUnit) {
        return (pUnit.getPlainName().equals("spy") ||
                pUnit.getPlainName().equals("light") ||
                pUnit.getPlainName().equals("marcher") ||
                pUnit.getPlainName().equals("heavy"));
    }

    public boolean isArcher(UnitHolder pUnit) {
        return (pUnit.getPlainName().equals("archer") ||
                pUnit.getPlainName().equals("marcher"));
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
     * @return the win
     */
    public boolean isWin() {
        return win;
    }

    /**
     * @param win the win to set
     */
    public void setWin(boolean win) {
        this.win = win;
    }

    /**
     * @return the offDecrement
     */
    public double getOffDecrement() {
        return offDecrement;
    }

    /**
     * @param offDecrement the offDecrement to set
     */
    public void setOffDecrement(double offDecrement) {
        this.offDecrement = offDecrement;
    }

    /**
     * @return the defDecrement
     */
    public double getDefDecrement() {
        return defDecrement;
    }

    /**
     * @param defDecrement the defDecrement to set
     */
    public void setDefDecrement(double defDecrement) {
        this.defDecrement = defDecrement;
    }

    /**
     * @return the wallResult
     */
    public int getWallResult() {
        return wallResult;
    }

    /**
     * @param wallResult the wallResult to set
     */
    public void setWallResult(int wallResult) {
        this.wallResult = wallResult;
    }

    /**
     * @return the cataResult
     */
    public int getCataResult() {
        return cataResult;
    }

    /**
     * @param cataResult the cataResult to set
     */
    public void setCataResult(int cataResult) {
        this.cataResult = cataResult;
    }
}
