/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.dssim.algo;

import de.tor.tribes.dssim.types.AbstractUnitElement;
import de.tor.tribes.dssim.types.UnitHolder;
import de.tor.tribes.dssim.util.UnitManager;
import java.util.Hashtable;

/**
 *
 * @author Charon
 */
public class OldSimulator extends AbstractSimulator {

    boolean DEBUG = false;
    private Hashtable<UnitHolder, AbstractUnitElement> off = null;
    private Hashtable<UnitHolder, AbstractUnitElement> def = null;

    public void calculate(Hashtable<UnitHolder, AbstractUnitElement> pOff, Hashtable<UnitHolder, AbstractUnitElement> pDef, boolean pNightBonus, double pLuck, double pMoral, int pWallLevel, int pBuildingLevel) {
        off = pOff;
        def = pDef;
        setMoral(pMoral);
        setLuck(pLuck);
        setNightBonus(pNightBonus);
        setWallLevel(pWallLevel);
        setBuildingLevel(pBuildingLevel);
        double offInfantryValue = calculateInfantryValue(off);
        double offCavaleryValue = calculateCavaleryValue(off);
        double infantryDefValue = calculateInfantryDefValue();
        double cavaleryDefValue = calculateCavaleryDefValue();
        double infantryRation = offInfantryValue / (offInfantryValue + offCavaleryValue);
        double cavaleryRation = 1 - infantryRation;
        double defStrength = infantryRation * infantryDefValue + cavaleryRation * cavaleryDefValue;
        AbstractUnitElement rams = off.get(UnitManager.getSingleton().getUnitByPlainName("ram"));
        double ramCount = 0;
        double ramAttPoint = 0;
        double wallAtFight = getWallLevel();
        if (rams != null) {
            ramCount = rams.getCount();
            double wallReduction = ramCount / (4 * Math.pow(1.09, getWallLevel()));
            if (wallReduction > (double) getWallLevel() / 2) {
                wallReduction = (double) getWallLevel() / 2;
            }
            switch (rams.getTech()) {
                case 2: {
                    ramAttPoint = 2.5;
                    break;
                }
                case 3: {
                    ramAttPoint = 2.8;
                    break;
                }
                default: {
                    ramAttPoint = 2;
                }
            }
            println("WallReduction: " + wallReduction);
            wallAtFight = Math.round(getWallLevel() - wallReduction);
            println("WallAtFight: " + wallAtFight);
            println("RamCount: " + ramCount);
            println("RamAttPoints: " + ramAttPoint);
        }

        AbstractUnitElement cata = off.get(UnitManager.getSingleton().getUnitByPlainName("catapult"));
        double cataAttPoint = 0;
        if (cata != null) {
            switch (cata.getTech()) {
                case 2: {
                    cataAttPoint = 125;
                    break;
                }
                case 3: {
                    cataAttPoint = 140;
                    break;
                }
                default: {
                    cataAttPoint = 100;
                }
            }
        }
        //double wallReduction = off.get(getUnitByPlainName("ram")) / (4 * 1.09 ^ wall level)
        //include wall
        defStrength = (20 + 50 * wallAtFight) + (defStrength * Math.pow(1.037, wallAtFight));

        println("OffInf " + offInfantryValue);
        println("OffCav " + offCavaleryValue);

        println("DefInf " + (infantryDefValue * infantryRation));
        println("DefCav " + (cavaleryDefValue * cavaleryRation));
        println("InfRatio " + infantryRation);
        println("CavRatio " + cavaleryRation);
        println("---------------");
        double offStrength = offInfantryValue + offCavaleryValue;
        println("OffStrength " + offStrength);
        println("DefStrength " + defStrength);

        double lossRatioOff = Math.pow((defStrength / offStrength), 1.5);
        double lossRatioDef = Math.pow((offStrength / defStrength), 1.5);
        println("LossOff: " + lossRatioOff);
        println("LossDef: " + lossRatioDef);
        double wallAfter = getWallLevel();
        if (lossRatioOff > 1) {
            //attack losses
            double wallDemolish = Math.pow((offStrength / defStrength), 1.5) * (ramAttPoint * ramCount) / (8 * Math.pow(1.09, getWallLevel()));
            println("Demo " + wallDemolish);
            wallAfter = Math.round(getWallLevel() - wallDemolish);
        } else {
            //attacker wins
            double wallDemolish = (2 - Math.pow((defStrength / offStrength), 1.5)) * (ramAttPoint * ramCount) / (8 * Math.pow(1.09, getWallLevel()));
            println("Demo " + wallDemolish);
            wallAfter = Math.round(getWallLevel() - wallDemolish);
        }

        double buildingAfter = getBuildingLevel();
        if (cata != null && cata.getCount() != 0) {
            if (lossRatioOff > 1) {
                //attack losses
                double buildingDemolish = Math.pow((offStrength / defStrength), 1.5) * (cataAttPoint * cata.getCount()) / (600 * Math.pow(1.09, getBuildingLevel()));
                println("DemoBuild " + buildingDemolish);
                buildingAfter = Math.round(getBuildingLevel() - buildingDemolish);
            } else {
                //attacker wins
                double buildingDemolish = (2 - Math.pow((defStrength / offStrength), 1.5)) * (cataAttPoint * cata.getCount()) / (600 * Math.pow(1.09, getBuildingLevel()));
                println("DemoBuild " + buildingDemolish);
                buildingAfter = Math.round(getBuildingLevel() - buildingDemolish);
            }
        }
        println("WallAfter: " + wallAfter);
        println("BuildingAfter: " + buildingAfter);
        setWin(lossRatioOff < 1);
        setOffDecrement(lossRatioOff);
        setDefDecrement(lossRatioDef);
        setWallResult((wallAfter <= 0) ? 0 : (int) wallAfter);
        setCataResult((buildingAfter <= 0) ? 0 : (int) buildingAfter);
    }

    private void println(String value) {
        if (DEBUG) {
            System.out.println(value);
        }
    }

    //calc infantry strength for spear, sword, axe, ram, cata, snob
    private double calculateInfantryValue(Hashtable<UnitHolder, AbstractUnitElement> pLocation) {
        UnitHolder unit = UnitManager.getSingleton().getUnitByPlainName("spear");
        AbstractUnitElement part = pLocation.get(unit);
        double techFactor = 0;
        double luckFactor = (100 + getLuck()) / 100;
        double result = 0;
        if (part != null) {
            techFactor = getTechFactor(part.getTech());
            result = part.getCount() * unit.getAttack() * getMoral() / 100 * techFactor;
        }
        unit = UnitManager.getSingleton().getUnitByPlainName("sword");
        part = pLocation.get(unit);
        if (part != null) {
            techFactor = getTechFactor(part.getTech());
            result += part.getCount() * unit.getAttack() * getMoral() / 100 * techFactor;
        }
        unit = UnitManager.getSingleton().getUnitByPlainName("axe");
        part = pLocation.get(unit);
        if (part != null) {
            techFactor = getTechFactor(part.getTech());
            result += part.getCount() * unit.getAttack() * getMoral() / 100 * techFactor;
        }
        unit = UnitManager.getSingleton().getUnitByPlainName("ram");
        part = pLocation.get(unit);
        if (part != null) {
            techFactor = getTechFactor(part.getTech());
            result += part.getCount() * unit.getAttack() * getMoral() / 100 * techFactor;
        }
        unit = UnitManager.getSingleton().getUnitByPlainName("catapult");
        part = pLocation.get(unit);
        if (part != null) {
            techFactor = getTechFactor(part.getTech());
            result += part.getCount() * unit.getAttack() * getMoral() / 100 * techFactor;
        }
        unit = UnitManager.getSingleton().getUnitByPlainName("snob");
        part = pLocation.get(unit);
        if (part != null) {
            techFactor = getTechFactor(part.getTech());
            result += part.getCount() * unit.getAttack() * getMoral() / 100 * techFactor;
        }
        return result * luckFactor;
    }

    //calc calvalery strength for light and heavy
    private double calculateCavaleryValue(Hashtable<UnitHolder, AbstractUnitElement> pLocation) {
        UnitHolder unit = UnitManager.getSingleton().getUnitByPlainName("light");
        double techFactor = 0;
        double luckFactor = (100 + getLuck()) / 100;
        double result = 0;
        AbstractUnitElement part = pLocation.get(unit);
        if (part != null) {
            techFactor = getTechFactor(part.getTech());
            result = part.getCount() * unit.getAttack() * getMoral() / 100 * techFactor;
        }
        unit = UnitManager.getSingleton().getUnitByPlainName("heavy");
        part = pLocation.get(unit);
        if (part != null) {
            techFactor = getTechFactor(part.getTech());
            result += part.getCount() * unit.getAttack() * getMoral() / 100 * techFactor;
        }
        return result * luckFactor;
    }

    private double calculateInfantryDefValue() {
        double result = 0;
        for (UnitHolder unit : UnitManager.getSingleton().getUnits()) {
            AbstractUnitElement part = def.get(unit);
            if (part != null) {
                double techFactor = getTechFactor(part.getTech());
                result += part.getCount() * unit.getDefense() * techFactor;
            }
        }
        return result * ((isNightBonus()) ? 2 : 1);
    }

    private double calculateCavaleryDefValue() {
        double result = 0;
        for (UnitHolder unit : UnitManager.getSingleton().getUnits()) {
            AbstractUnitElement part = def.get(unit);
            if (part != null) {
                double techFactor = getTechFactor(part.getTech());
                result += part.getCount() * unit.getDefenseCavalry() * techFactor;
            }
        }
        return result * ((isNightBonus()) ? 2 : 1);
    }

    private double getTechFactor(int pLevel) {
        switch (pLevel) {
            case 2:
                return 1.25;
            case 3:
                return 1.4;
            default:
                return 1;
        }
    }
    /*  public static void main(String[] args) {
    OldSimulator sim = new OldSimulator();
    sim.test();

    }*/
}
