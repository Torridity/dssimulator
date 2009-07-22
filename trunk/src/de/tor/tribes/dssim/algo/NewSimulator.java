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

        int wallAtFight = (int) Math.round(getWallLevel() - (ramCount / (4 * Math.pow(1.090012, getWallLevel()))));
        if (wallAtFight < (int) Math.round(getWallLevel() / 2)) {
            wallAtFight = (int) Math.round(getWallLevel() / 2);
        }

        //enter three calculation rounds
        for (int i = 0; i < 3; i++) {
            //calculate strengths based on survivors of each round
            double[] offStrengths = calculateOffStrengths(result.getSurvivingOff());
            double[] defStrengths = calculateDefStrengths(result.getSurvivingDef(), offStrengths, wallAtFight, (i == 0));
            //calculate losses
            double[] offLosses = calulateLosses(offStrengths, defStrengths, ID_OFF);
            double[] defLosses = calulateLosses(offStrengths, defStrengths, ID_DEF);
            //correct troops and repeat calculation
            correctTroops(offStrengths, offLosses, defLosses, result, (i == 0));
        }

        //initialize to "win"
        result.setWin(true);
        for (UnitHolder u : UnitManager.getSingleton().getUnits()) {
            if (result.getSurvivingDef().get(u).getCount() > 0) {
                //at least one defender has survived
                result.setWin(false);
                break;
            }
        }

        if (result.isWin() && ramCount > 0) {
            //calculate wall after fight
            double maxDecrement = ramCount * 2 / (4 * Math.pow(1.090012, getWallLevel()));
            double lostUnits = 0;
            double totalUnits = 0;

            for (UnitHolder unit : UnitManager.getSingleton().getUnits()) {
                totalUnits += getOff().get(unit).getCount();
                lostUnits += getOff().get(unit).getCount() - result.getSurvivingOff().get(unit).getCount();
            }

            double ratio = lostUnits / totalUnits;
            int wallDecrement = (int) Math.round(-1 * maxDecrement / 2 * ratio + maxDecrement);
            result.setWallLevel((getWallLevel() - wallDecrement < 0) ? 0 : getWallLevel() - wallDecrement);
        } else if (ramCount <= 0) {
            //no change
        } else {
            //lost scenario
            double lostUnits = 0;
            double totalUnits = 0;
            for (UnitHolder unit : UnitManager.getSingleton().getUnits()) {
                totalUnits += getDef().get(unit).getCount();
                lostUnits += getDef().get(unit).getCount() - result.getSurvivingDef().get(unit).getCount();
            }
            double ratio = lostUnits / totalUnits;
            int wallDecrement = (int) Math.round((ramCount * ratio) * 2 / (8 * Math.pow(1.090012, getWallLevel())));
            result.setWallLevel((getWallLevel() - wallDecrement < 0) ? 0 : getWallLevel() - wallDecrement);
        }

        //demolish building
        AbstractUnitElement cata = getOff().get(UnitManager.getSingleton().getUnitByPlainName("catapult"));
        double buildingAfter = getBuildingLevel();

        if (cata != null && cata.getCount() != 0) {
            double[] offStrengths = calculateOffStrengths(getOff());
            double[] defStrengths = calculateDefStrengths(getDef(), offStrengths, 0, true);
            double offStrength = offStrengths[ID_INFANTRY] + offStrengths[ID_CAVALRY] + offStrengths[ID_ARCHER];
            double defStrength = defStrengths[ID_INFANTRY] + defStrengths[ID_CAVALRY] + defStrengths[ID_ARCHER];
            if (!result.isWin()) {
                //attack losses
                double buildingDemolish = Math.pow((offStrength / defStrength), 1.5) * (cata.getUnit().getAttack() * cata.getCount()) / (600 * Math.pow(1.090012, getBuildingLevel()));
                buildingAfter = Math.round(getBuildingLevel() - buildingDemolish);
            } else {
                //attacker wins
                double buildingDemolish = (2 - Math.pow((defStrength / offStrength), 1.5)) * (cata.getUnit().getAttack() * cata.getCount()) / (600 * Math.pow(1.090012, getBuildingLevel()));
                buildingAfter = Math.round(getBuildingLevel() - buildingDemolish);
            }
            result.setBuildingLevel((buildingAfter <= 0) ? 0 : (int) buildingAfter);
        } else {
            //no demolishion
            result.setBuildingLevel(getBuildingLevel());
        }
        return result;
    }

    private double[] calculateOffStrengths(Hashtable<UnitHolder, AbstractUnitElement> pTable) {
        double[] result = new double[3];
        Enumeration<UnitHolder> units = pTable.keys();

        while (units.hasMoreElements()) {
            UnitHolder unit = units.nextElement();
            AbstractUnitElement element = pTable.get(unit);
            //add strength to all appropriate array elements (e.g. marcher is cavalry and archer)
            if (isInfantry(unit) && !isArcher(unit)) {
                result[ID_INFANTRY] += unit.getAttack() * (double) element.getCount() * element.getTech();
            }
            if (isCavalery(unit) && !isArcher(unit)) {
                result[ID_CAVALRY] += unit.getAttack() * (double) element.getCount() * element.getTech();
            }
            if (isArcher(unit)) {
                result[ID_ARCHER] += unit.getAttack() * (double) element.getCount() * element.getTech();
            }
        }
        double moral = getMoral() / 100;
        double luck = ((100 + getLuck()) / 100);
        result[ID_INFANTRY] = result[ID_INFANTRY] * moral * luck;
        result[ID_CAVALRY] = result[ID_CAVALRY] * moral * luck;
        result[ID_ARCHER] = result[ID_ARCHER] * moral * luck;
        return result;
    }

    private double[] calculateDefStrengths(Hashtable<UnitHolder, AbstractUnitElement> pTable, double[] pOffStrengths, int pWallLevel, boolean pUseBasicDefense) {
        double[] result = new double[3];
        double totalOff = 0;
        for (double d : pOffStrengths) {
            totalOff += d;
        }
        double infantryMulti = (totalOff != 0) ? pOffStrengths[ID_INFANTRY] / totalOff : 0;
        double cavalryMulti = (totalOff != 0) ? pOffStrengths[ID_CAVALRY] / totalOff : 0;
        double archerMulti = (totalOff != 0) ? pOffStrengths[ID_ARCHER] / totalOff : 0;
        Enumeration<UnitHolder> units = pTable.keys();
        double moral = getMoral() / 100;
        while (units.hasMoreElements()) {
            UnitHolder unit = units.nextElement();
            AbstractUnitElement element = pTable.get(unit);
            result[ID_INFANTRY] += infantryMulti * unit.getDefense() * (double) element.getCount() * element.getTech();
            result[ID_CAVALRY] += cavalryMulti * unit.getDefenseCavalry() * (double) element.getCount() * element.getTech();
            result[ID_ARCHER] += archerMulti * unit.getDefenseArcher() * (double) element.getCount() * element.getTech();
        }

        double luck = ((100 + getLuck()) / 100);
        double nightBonus = (isNightBonus()) ? 2 : 1;
        double[] basicDefense = new double[]{0.0, 0.0, 0.0};
        if (pUseBasicDefense) {
            basicDefense[0] = (20.0 + (double) pWallLevel * 50.0) * ((totalOff == 0) ? 0 : pOffStrengths[ID_INFANTRY] / totalOff);
            basicDefense[1] = (20.0 + (double) pWallLevel * 50.0) * ((totalOff == 0) ? 0 : pOffStrengths[ID_CAVALRY] / totalOff);
            basicDefense[2] = (20.0 + (double) pWallLevel * 50.0) * ((totalOff == 0) ? 0 : pOffStrengths[ID_ARCHER] / totalOff);
        }
        result[0] = result[0] * luck * moral * nightBonus * Math.pow(1.037, pWallLevel) + basicDefense[0];
        result[1] = result[1] * luck * moral * nightBonus * Math.pow(1.037, pWallLevel) + basicDefense[1];
        result[2] = result[2] * luck * moral * nightBonus * Math.pow(1.037, pWallLevel) + basicDefense[2];
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
               /* if(i == ID_ARCHER){
            System.out.println("ARCHER_LOSS");
            System.out.println("Off " + pOffStrengths[i]);
            System.out.println("Def " + pDeffStrengths[i]);
            System.out.println(losses[i]);
            System.out.println("----");
            }*/
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

    private void correctTroops(double[] pOffStrengths, double[] pOffLosses, double[] pDefLosses, SimulatorResult pResult, boolean pSpyRound) {
        //correct off
        for (UnitHolder unit : UnitManager.getSingleton().getUnits()) {
            AbstractUnitElement unitOffElement = pResult.getSurvivingOff().get(unit);
            AbstractUnitElement unitDefElement = pResult.getSurvivingDef().get(unit);
            if (isInfantry(unit) && !isArcher(unit)) {
                unitOffElement.setCount((int) Math.round((double) unitOffElement.getCount() - ((double) unitOffElement.getCount() * pOffLosses[ID_INFANTRY])));
            } else if (isCavalery(unit) && !isArcher(unit)) {
                if (!isSpy(unit)) {
                    unitOffElement.setCount((int) Math.round((double) unitOffElement.getCount() - (double) unitOffElement.getCount() * pOffLosses[ID_CAVALRY]));
                } else {
                    //only correct spys in first round
                    if (pSpyRound) {
                        int spyLosses = 0;
                        //special spy calculation
                        if (unitOffElement.getCount() == 0) {
                            //no spy
                            spyLosses = 0;
                        } else if ((double) unitDefElement.getCount() / (double) unitOffElement.getCount() >= 2) {
                            //no change
                            spyLosses = unitOffElement.getCount();
                        } else {
                            spyLosses = (int) Math.round((double) unitOffElement.getCount() * Math.pow((double) unitDefElement.getCount() / ((double) unitOffElement.getCount() * 2.0), 1.5));
                        }
                        unitOffElement.setCount((int) Math.round((double) unitOffElement.getCount() - spyLosses));
                    }
                }
            } else {
                unitOffElement.setCount((int) Math.round((double) unitOffElement.getCount() - ((double) unitOffElement.getCount() * pOffLosses[ID_ARCHER])));
            }
        }
        double totalOff = 0;
        for (double d : pOffStrengths) {
            totalOff += d;
        }
        //correct def
        for (UnitHolder unit : UnitManager.getSingleton().getUnits()) {
            AbstractUnitElement unitDefElement = pResult.getSurvivingDef().get(unit);
            double decreaseFactor = pOffStrengths[ID_INFANTRY] * pDefLosses[ID_INFANTRY] + pOffStrengths[ID_CAVALRY] * pDefLosses[ID_CAVALRY] + pOffStrengths[ID_ARCHER] * pDefLosses[ID_ARCHER];
            int survive = (int) Math.round((double) unitDefElement.getCount() - ((double) unitDefElement.getCount() / ((totalOff == 0) ? 1 : totalOff) * decreaseFactor));
            unitDefElement.setCount(survive);
        }
    }
}
