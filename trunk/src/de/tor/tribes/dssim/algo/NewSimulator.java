/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.dssim.algo;

import de.tor.tribes.dssim.types.AbstractUnitElement;
import de.tor.tribes.dssim.types.SimulatorResult;
import de.tor.tribes.dssim.types.UnitHolder;
import de.tor.tribes.dssim.util.UnitManager;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 *
 * @author Charon
 */
public class NewSimulator extends AbstractSimulator {

    private static final int ID_OFF = 0;
    private static final int ID_DEF = 1;

    @Override
    public SimulatorResult calculate(Hashtable<UnitHolder, AbstractUnitElement> pOff, Hashtable<UnitHolder, AbstractUnitElement> pDef, boolean pNightBonus, double pLuck, double pMoral, int pWallLevel, int pBuildingLevel) {
        setOff(pOff);
        setDef(pDef);
        setMoral(pMoral);
        setLuck(pLuck);
        setNightBonus(pNightBonus);
        setWallLevel(pWallLevel);
        setBuildingLevel(pBuildingLevel);
        SimulatorResult result = new SimulatorResult(getOff(), getDef());
        AbstractUnitElement ramElement = pOff.get(UnitManager.getSingleton().getUnitByPlainName("ram"));
        int ramCount = 0;
        if (ramElement != null) {
            ramCount = ramElement.getCount();
        }

        int wallAtFight = (int) Math.round(getWallLevel() - (ramCount / (4 * Math.pow(1.09, getWallLevel()))));
        if (wallAtFight < (int) Math.round(getWallLevel() / 2)) {
            wallAtFight = (int) Math.round(getWallLevel() / 2);
        }

        //enter three calculation rounds
        for (int i = 0; i < 3; i++) {
            //  System.out.println("Round " + i);
            //calculate strengths based on survivors of each round
            double[] offStrengths = calculateOffStrengths(result.getSurvivingOff());
            double[] defStrengths = calculateDefStrengths(result.getSurvivingDef(), offStrengths, wallAtFight);
            /* for (int s = 0; s < 3; s++) {
            System.out.println("Strength" + s + " (Off): " + offStrengths[s]);
            System.out.println("Strength" + s + " (Def): " + defStrengths[s]);
            }*/
            //calculate losses
            double[] offLosses = calulateLosses(offStrengths, defStrengths, ID_OFF);
            double[] defLosses = calulateLosses(offStrengths, defStrengths, ID_DEF);
            /* for (int s = 0; s < 3; s++) {
            System.out.println("Losses" + s + " (Off): " + offLosses[s]);
            System.out.println("Losses" + s + " (Def): " + defLosses[s]);
            }
            System.out.println("----------");*/
            //correct troops and repeat calculation
            correctTroops(offLosses, defLosses, result);
        }

        if (result.isWin() && ramCount > 0) {
            //calculate wall after fight

            double maxDecrement = ramCount * 2 / (4 * Math.pow(1.09, getWallLevel()));
            double lostUnits = 0;
            double totalUnits = 0;

            for (UnitHolder unit : UnitManager.getSingleton().getUnits()) {
                totalUnits += getOff().get(unit).getCount();
                lostUnits += getOff().get(unit).getCount() - result.getSurvivingOff().get(unit).getCount();
            }

            double ratio = lostUnits / totalUnits;
            int wallDecrement = (int) Math.round(-1 * maxDecrement / 2 * ratio + maxDecrement);
            result.setWallLevel((getWallLevel() - wallDecrement < 0) ? 0 : getWallLevel() - wallDecrement);
        }

        /* System.out.println("RESULTS");
        for (UnitHolder unit : UnitManager.getSingleton().getUnits()) {
        System.out.println(unit + " (Off): " + getOff().get(unit).getCount());
        System.out.println(unit + " (Def): " + getDef().get(unit).getCount());
        }
        System.out.println("-------");*/
        return result;
    }

    private double[] calculateOffStrengths(Hashtable<UnitHolder, AbstractUnitElement> pTable) {
        double[] result = new double[3];
        Enumeration<UnitHolder> units = pTable.keys();

        while (units.hasMoreElements()) {
            UnitHolder unit = units.nextElement();
            AbstractUnitElement element = pTable.get(unit);
            //add strength to all appropriate array elements (e.g. marcher is cavalry and archer)
            if (isInfantry(unit)) {
                result[ID_INFANTRY] += unit.getAttack() * element.getCount() * element.getTech();
            }
            if (isCavalery(unit) && !isArcher(unit)) {
                result[ID_CAVALRY] += unit.getAttack() * element.getCount() * element.getTech();
            }
            if (isArcher(unit)) {
                result[ID_ARCHER] += unit.getAttack() * element.getCount() * element.getTech();
            }
        }
        double moral = getMoral() / 100;
        double luck = ((100 + getLuck()) / 100);
        result[ID_INFANTRY] = result[ID_INFANTRY] * moral * luck;
        result[ID_CAVALRY] = result[ID_CAVALRY] * moral * luck;
        result[ID_ARCHER] = result[ID_ARCHER] * moral * luck;
        return result;
    }

    private double[] calculateDefStrengths(Hashtable<UnitHolder, AbstractUnitElement> pTable, double[] pOffStrengths, int pWallLevel) {
        double[] result = new double[3];
        double totalOff = 0;
        for (double d : pOffStrengths) {
            totalOff += d;
        }
        /*
        System.out.println("OffInf: " + pOffStrengths[ID_INFANTRY]);
        System.out.println("OffCav: " + pOffStrengths[ID_CAVALRY]);
        System.out.println("OffArch: " + pOffStrengths[ID_ARCHER]);
        System.out.println("TotalOff " + totalOff);
         */
        double infantryMulti = (totalOff != 0) ? pOffStrengths[ID_INFANTRY] / totalOff : 0;
        double cavalryMulti = (totalOff != 0) ? pOffStrengths[ID_CAVALRY] / totalOff : 0;
        double archerMulti = (totalOff != 0) ? pOffStrengths[ID_ARCHER] / totalOff : 0;

        Enumeration<UnitHolder> units = pTable.keys();
        double moral = getMoral() / 100;
        while (units.hasMoreElements()) {
            UnitHolder unit = units.nextElement();
            AbstractUnitElement element = pTable.get(unit);
            //add strength to all appropriate array elements (e.g. marcher is cavalry and archer)
            if (isInfantry(unit)) {
                result[ID_INFANTRY] += infantryMulti * unit.getDefense() * element.getCount() * element.getTech();
            }
            if (isCavalery(unit)) {
                result[ID_CAVALRY] += cavalryMulti * unit.getDefenseCavalry() * element.getCount() * element.getTech();
            }
            if (isArcher(unit)) {
                result[ID_ARCHER] += archerMulti * unit.getDefenseArcher() * element.getCount() * element.getTech();
            }
        }
        double luck = ((100 + getLuck()) / 100);
        result[0] = result[0] * luck * moral * Math.pow(1.037, pWallLevel) + (20 + pWallLevel * 50) * ((totalOff == 0) ? 0 : pOffStrengths[ID_INFANTRY] / totalOff);
        result[1] = result[1] * luck * moral * Math.pow(1.037, pWallLevel) + (20 + pWallLevel * 50) * ((totalOff == 0) ? 0 : pOffStrengths[ID_CAVALRY] / totalOff);
        result[2] = result[2] * luck * moral * Math.pow(1.037, pWallLevel) + (20 + pWallLevel * 50) * ((totalOff == 0) ? 0 : pOffStrengths[ID_ARCHER] / totalOff);
        return result;
    }

    private double[] calulateLosses(double[] pOffStrengths, double[] pDeffStrengths, int pType) {
        double[] losses = new double[3];
        if (pType == ID_OFF) {
            //calculate losses
            for (int i = 0; i <= ID_ARCHER; i++) {
                if (pOffStrengths[i] == 0 || pDeffStrengths[i] == 0) {
                    //one party was completely killed
                    losses[i] = 0;
                } else {
                    if (pOffStrengths[i] > pDeffStrengths[i]) {
                        losses[i] = Math.pow(pDeffStrengths[i] / pOffStrengths[i], 1.5);
                    } else {
                        //off completely list
                        losses[i] = 1;
                    }//end of complete loss
                }//end of nothing lost
            }//end of for loop
        } else {
            //calculate losses
            for (int i = 0; i <= ID_ARCHER; i++) {
                if (pOffStrengths[i] == 0 || pDeffStrengths[i] == 0) {
                    //one party was completely killed
                    losses[i] = 0;
                } else {
                    if (pDeffStrengths[i] > pOffStrengths[i]) {
                        losses[i] = Math.pow(pOffStrengths[i] / pDeffStrengths[i], 1.5);
                    } else {
                        //off completely list
                        losses[i] = 1;
                    }//end of complete loss
                }//end of nothing lost
            }//end of for loop
        }//end of def calculation

        return losses;
    }

    private void correctTroops(double[] pOffLosses, double[] pDefLosses, SimulatorResult pResult) {

        for (UnitHolder unit : UnitManager.getSingleton().getUnits()) {
            AbstractUnitElement unitOffElement = pResult.getSurvivingOff().get(unit);
            AbstractUnitElement unitDefElement = pResult.getSurvivingDef().get(unit);
            if (isInfantry(unit)) {
                // System.out.println("Reduce inf " + unit + " by factor " + pOffLosses[ID_INFANTRY]);
                unitOffElement.setCount((int) Math.round(unitOffElement.getCount() - unitOffElement.getCount() * pOffLosses[ID_INFANTRY]));
                unitDefElement.setCount((int) Math.round(unitDefElement.getCount() - unitDefElement.getCount() * pDefLosses[ID_INFANTRY]));
            } else if (isCavalery(unit)) {
                unitOffElement.setCount((int) Math.round(unitOffElement.getCount() - unitOffElement.getCount() * pOffLosses[ID_CAVALRY]));
                unitDefElement.setCount((int) Math.round(unitDefElement.getCount() - unitDefElement.getCount() * pDefLosses[ID_CAVALRY]));
            } else {
                unitOffElement.setCount((int) Math.round(unitOffElement.getCount() - unitOffElement.getCount() * pOffLosses[ID_ARCHER]));
                unitDefElement.setCount((int) Math.round(unitDefElement.getCount() - unitDefElement.getCount() * pDefLosses[ID_ARCHER]));
            }
        }
    }
}
