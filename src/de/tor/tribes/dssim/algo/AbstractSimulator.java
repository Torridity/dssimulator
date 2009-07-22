/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.dssim.algo;

import de.tor.tribes.dssim.types.AbstractUnitElement;
import de.tor.tribes.dssim.types.SimulatorResult;
import de.tor.tribes.dssim.types.UnitHolder;
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
    private boolean win = false;

    public abstract SimulatorResult calculate(Hashtable<UnitHolder, AbstractUnitElement> pOff, Hashtable<UnitHolder, AbstractUnitElement> pDef, boolean pNightBonus, double pLuck, double pMoral, int pWallLevel, int pBuildingLevel);

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
}
