/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.dssim.types;

import de.tor.tribes.dssim.util.UnitManager;
import java.util.Hashtable;

/**
 *
 * @author Jejkal
 */
public class SimulatorResult {

    private boolean win = false;
    private int nukes = 1;
    private Hashtable<UnitHolder, AbstractUnitElement> survivingOff = null;
    private Hashtable<UnitHolder, AbstractUnitElement> survivingDef = null;
    private int wallLevel = 0;
    private int buildingLevel = 0;

    public SimulatorResult() {
        survivingOff = new Hashtable<UnitHolder, AbstractUnitElement>();
        survivingDef = new Hashtable<UnitHolder, AbstractUnitElement>();
        for (UnitHolder unit : UnitManager.getSingleton().getUnits()) {
            AbstractUnitElement copy = new AbstractUnitElement(unit, 0, 1);
            survivingOff.put(unit, copy);
            copy = new AbstractUnitElement(unit, 0, 1);
            survivingDef.put(unit, copy);
        }
    }

    public SimulatorResult(Hashtable<UnitHolder, AbstractUnitElement> pOff, Hashtable<UnitHolder, AbstractUnitElement> pDef) {
        survivingOff = new Hashtable<UnitHolder, AbstractUnitElement>();
        survivingDef = new Hashtable<UnitHolder, AbstractUnitElement>();
        for (UnitHolder unit : UnitManager.getSingleton().getUnits()) {
            AbstractUnitElement element = pOff.get(unit);
            AbstractUnitElement copy = new AbstractUnitElement(unit, element.getCount(), element.getTech());
            survivingOff.put(unit, copy);
            element = pDef.get(unit);
            copy = new AbstractUnitElement(unit, element.getCount(), element.getTech());
            survivingDef.put(unit, copy);
        }
    }

    public void setNukes(int pNukes) {
        nukes = pNukes;
    }

    public int getNukes() {
        return nukes;
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
     * @return the survivingOff
     */
    public Hashtable<UnitHolder, AbstractUnitElement> getSurvivingOff() {
        return survivingOff;
    }

    /**
     * @param survivingOff the survivingOff to set
     */
    public void setSurvivingOff(Hashtable<UnitHolder, AbstractUnitElement> survivingOff) {
        this.survivingOff = survivingOff;
    }

    /**
     * @return the survivingDef
     */
    public Hashtable<UnitHolder, AbstractUnitElement> getSurvivingDef() {
        return survivingDef;
    }

    /**
     * @param survivingDef the survivingDef to set
     */
    public void setSurvivingDef(Hashtable<UnitHolder, AbstractUnitElement> survivingDef) {
        this.survivingDef = survivingDef;
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
