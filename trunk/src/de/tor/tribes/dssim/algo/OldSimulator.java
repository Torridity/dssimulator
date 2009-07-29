/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.dssim.algo;

import de.tor.tribes.dssim.types.AbstractUnitElement;
import de.tor.tribes.dssim.types.SimulatorResult;
import de.tor.tribes.dssim.types.UnitHolder;
import de.tor.tribes.dssim.util.ConfigManager;
import de.tor.tribes.dssim.util.UnitManager;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 *@TODO handle NAN values for spy only attacks
 * @author Charon
 */
public class OldSimulator extends AbstractSimulator {

    boolean DEBUG = false;

    public SimulatorResult calculate(Hashtable<UnitHolder, AbstractUnitElement> pOff, Hashtable<UnitHolder, AbstractUnitElement> pDef, boolean pNightBonus, double pLuck, double pMoral, int pWallLevel, int pBuildingLevel, int pFarmLevel, boolean pAttackerBelieve, boolean pDefenderBelieve, boolean pCataChurch, boolean pCataFarm) {
        setOff(pOff);
        setDef(pDef);
        setMoral(pMoral);
        setLuck(pLuck);
        setNightBonus(pNightBonus);
        setWallLevel(pWallLevel);
        setBuildingLevel(pBuildingLevel);
        setFarmLevel(pFarmLevel);
        setAttackerBelieve(pAttackerBelieve);
        setDefenderBelieve(pDefenderBelieve);
        setCataFarm(pCataFarm);
        setCataChurch(pCataChurch);
        SimulatorResult result = new SimulatorResult(getOff(), getDef());
        double[] offStrengths = calculateOffStrengthts();
        double[] defStrengths = calculateDefStrengths();

        double infantryRatio = offStrengths[ID_INFANTRY] / (offStrengths[ID_INFANTRY] + offStrengths[ID_CAVALRY]);
        double cavaleryRatio = 1 - infantryRatio;
        double defStrength = infantryRatio * defStrengths[ID_INFANTRY] + cavaleryRatio * defStrengths[ID_CAVALRY];
        if (ConfigManager.getSingleton().getFarmLimit() != 0) {
            double limit = getFarmLevel() * ConfigManager.getSingleton().getFarmLimit();
            double defFarmUsage = calculateDefFarmUsage();
            double factor = limit / defFarmUsage;
            if (factor > 1.0) { 
                factor = 1.0;
            }
            defStrength = factor * defStrength;
        }

        AbstractUnitElement rams = getOff().get(UnitManager.getSingleton().getUnitByPlainName("ram"));
        double ramCount = 0;
        double ramAttPoint = 0;
        double wallAtFight = getWallLevel();
        if (rams != null && rams.getCount() != 0) {
            ramCount = rams.getCount();
            double wallReduction = ramCount / (4 * Math.pow(1.09, getWallLevel()));
            if (wallReduction > (double) getWallLevel() / 2.0) {
                wallReduction = (double) getWallLevel() / 2.0;
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

        AbstractUnitElement cata = getOff().get(UnitManager.getSingleton().getUnitByPlainName("catapult"));
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
        //include wall
        defStrength = (20 + 50 * wallAtFight) + (defStrength * Math.pow(1.037, wallAtFight));

        println("OffInf " + offStrengths[ID_INFANTRY]);
        println("OffCav " + offStrengths[ID_CAVALRY]);

        println("DefInf " + (defStrengths[ID_INFANTRY] * infantryRatio));
        println("DefCav " + (defStrengths[ID_CAVALRY] * cavaleryRatio));
        println("InfRatio " + infantryRatio);
        println("CavRatio " + cavaleryRatio);
        println("---------------");
        double offStrength = offStrengths[ID_INFANTRY] + offStrengths[ID_CAVALRY];
        println("OffStrength " + offStrength);
        println("DefStrength " + defStrength);

        double lossPowerValue = 1.5;
        if ((ConfigManager.getSingleton().getTech() == ConfigManager.ID_TECH_10) ||
                (ConfigManager.getSingleton().getFarmLimit() != 0)) {
            lossPowerValue = 1.6;
        }

        double lossRatioOff = Math.pow((defStrength / offStrength), lossPowerValue);
        double lossRatioDef = Math.pow((offStrength / defStrength), lossPowerValue);
        println("LossOff: " + lossRatioOff);
        println("LossDef: " + lossRatioDef);
        double wallAfter = getWallLevel();
        if (lossRatioOff > 1) {
            //attack losses
            double wallDemolish = Math.pow((offStrength / defStrength), lossPowerValue) * (ramAttPoint * ramCount) / (8 * Math.pow(1.09, getWallLevel()));
            println("Demo " + wallDemolish);
            wallAfter = Math.round(getWallLevel() - wallDemolish);
        } else {
            //attacker wins
            double wallDemolish = (2 - Math.pow((defStrength / offStrength), lossPowerValue)) * (ramAttPoint * ramCount) / (8 * Math.pow(1.09, getWallLevel()));
            println("Demo " + wallDemolish);
            wallAfter = Math.round(getWallLevel() - wallDemolish);
        }
        double buildingAfter = getBuildingLevel();
        if (cata != null && cata.getCount() != 0) {
            if (lossRatioOff > 1) {
                //attack losses
                double buildingDemolish = Math.pow((offStrength / defStrength), lossPowerValue) * (cataAttPoint * cata.getCount()) / (600 * Math.pow(1.09, getBuildingLevel()));
                println("DemoBuild " + buildingDemolish);
                buildingAfter = Math.round(getBuildingLevel() - buildingDemolish);
            } else {
                //attacker wins
                double buildingDemolish = (2 - Math.pow((defStrength / offStrength), lossPowerValue)) * (cataAttPoint * cata.getCount()) / (600 * Math.pow(1.09, (getBuildingLevel())));
                println("DemoBuild " + buildingDemolish);
                buildingAfter = Math.round(getBuildingLevel() - buildingDemolish);
            }
        } else {
            //no demolishion
            result.setBuildingLevel(getBuildingLevel());
        }
        println("WallAfter: " + wallAfter);
        println("BuildingAfter: " + buildingAfter);
        result.setWin(lossRatioOff < 1);
        result.setWallLevel((wallAfter <= 0) ? 0 : (int) wallAfter);
        result.setBuildingLevel((buildingAfter <= 0) ? 0 : (int) buildingAfter);

        //build result units
        for (UnitHolder unit : UnitManager.getSingleton().getUnits()) {
            int survivors = 0;
            if (!isSpy(unit)) {
                survivors = (int) Math.round(getOff().get(unit).getCount() - lossRatioOff * getOff().get(unit).getCount());
                result.getSurvivingOff().get(unit).setCount((survivors < 0) ? 0 : survivors);
            } else {
                int spyLosses = 0;
                //special spy calculation
                if (getOff().get(unit).getCount() == 0) {
                    //no spy
                    spyLosses = 0;
                } else if (getDef().get(unit).getCount() / getOff().get(unit).getCount() >= 2) {
                    //no change
                    spyLosses = getOff().get(unit).getCount();
                } else {
                    spyLosses = (int) Math.round((double) getOff().get(unit).getCount() * Math.pow((double) getDef().get(unit).getCount() / (double) getOff().get(unit).getCount() / 2.0, 1.5));
                }
                result.getSurvivingOff().get(unit).setCount(result.getSurvivingOff().get(unit).getCount() - spyLosses);
            }

            survivors = (int) Math.round(getDef().get(unit).getCount() - lossRatioDef * getDef().get(unit).getCount());
            result.getSurvivingDef().get(unit).setCount((survivors < 0) ? 0 : survivors);
        }

        return result;
    }

    private void println(String value) {
        if (DEBUG) {
            System.out.println(value);
        }
    }

    private double[] calculateOffStrengthts() {
        double[] result = new double[]{0.0, 0.0};
        Enumeration<UnitHolder> units = getOff().keys();
        while (units.hasMoreElements()) {
            UnitHolder unit = units.nextElement();
            AbstractUnitElement unitElement = getOff().get(unit);
            if (isInfantry(unit)) {
                result[ID_INFANTRY] += unitElement.getCount() * unit.getAttack() * getTechFactor(unitElement.getTech());
            }
            if (isCavalery(unit)) {
                result[ID_CAVALRY] += unitElement.getCount() * unit.getAttack() * getTechFactor(unitElement.getTech());
            }
        }
        //integrate moral and luck
        double moral = getMoral() / 100;
        double luck = ((100 + getLuck()) / 100);
        System.out.println("AS  " + result[ID_INFANTRY]);
        System.out.println("AS2  " + result[ID_CAVALRY]);
        result[ID_INFANTRY] = result[ID_INFANTRY] * moral * luck;
        result[ID_CAVALRY] = result[ID_CAVALRY] * moral * luck;
        return result;
    }

    private double[] calculateDefStrengths() {
        double[] result = new double[]{0.0, 0.0};
        Enumeration<UnitHolder> units = getDef().keys();
        while (units.hasMoreElements()) {
            UnitHolder unit = units.nextElement();
            AbstractUnitElement unitElement = getDef().get(unit);
            result[ID_INFANTRY] += unitElement.getCount() * unit.getDefense() * getTechFactor(unitElement.getTech());
            result[ID_CAVALRY] += unitElement.getCount() * unit.getDefenseCavalry() * getTechFactor(unitElement.getTech());
        }

        result[ID_INFANTRY] = result[ID_INFANTRY] * ((isNightBonus()) ? 2.0 : 1.0);
        result[ID_CAVALRY] = result[ID_CAVALRY] * ((isNightBonus()) ? 2.0 : 1.0);
        return result;
    }

    private double calculateDefFarmUsage() {
        Enumeration<UnitHolder> units = getDef().keys();
        int result = 0;
        while (units.hasMoreElements()) {
            UnitHolder unit = units.nextElement();
            AbstractUnitElement unitElement = getDef().get(unit);
            result += unit.getPop() * unitElement.getCount();
        }
        return result;
    }

    private double getTechFactor(int pLevel) {
        if (ConfigManager.getSingleton().getTech() == ConfigManager.ID_TECH_3) {
            switch (pLevel) {
                case 2:
                    return 1.25;
                case 3:
                    return 1.4;
                default:
                    return 1;
            }
        } else {
            //use 10 level tech factor
            return Math.pow(1.04608, (pLevel - 1));
        }
    }
}
