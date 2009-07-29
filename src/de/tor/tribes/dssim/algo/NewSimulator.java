/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.dssim.algo;

import de.tor.tribes.dssim.types.AbstractUnitElement;
import de.tor.tribes.dssim.types.KnightItem;
import de.tor.tribes.dssim.types.SimulatorResult;
import de.tor.tribes.dssim.types.UnitHolder;
import de.tor.tribes.dssim.util.ConfigManager;
import de.tor.tribes.dssim.util.UnitManager;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Charon
 */
public class NewSimulator extends AbstractSimulator {

    private static final int ID_OFF = 0;
    private static final int ID_DEF = 1;
    private KnightItem offItem = null;
    private List<KnightItem> defItems = null;

    public SimulatorResult calculate(Hashtable<UnitHolder, AbstractUnitElement> pOff, Hashtable<UnitHolder, AbstractUnitElement> pDef, KnightItem pOffItem, List<KnightItem> pDefItems, boolean pNightBonus, double pLuck, double pMoral, int pWallLevel, int pBuildingLevel, int pFarmLevel, boolean pAttackerBelieve, boolean pDefenderBelieve, boolean pCataChurch, boolean pCataFarm) {
        offItem = pOffItem;
        defItems = pDefItems;
        return calculate(pOff, pDef, pNightBonus, pLuck, pMoral, pWallLevel, pBuildingLevel, pFarmLevel, pAttackerBelieve, pDefenderBelieve, pCataChurch, pCataFarm);
    }

    @Override
    public SimulatorResult calculate(Hashtable<UnitHolder, AbstractUnitElement> pOff, Hashtable<UnitHolder, AbstractUnitElement> pDef,
            boolean pNightBonus,
            double pLuck,
            double pMoral,
            int pWallLevel,
            int pBuildingLevel,
            int pFarmLevel,
            boolean pAttackerBelieve,
            boolean pDefenderBelieve,
            boolean pCataChurch,
            boolean pCataFarm) {
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
        setCataChurch(pCataChurch);
        setCataFarm(pCataFarm);
        if (offItem == null) {
            offItem = KnightItem.factoryKnightItem(KnightItem.ID_NO_ITEM);
        }
        if (defItems == null) {
            defItems = new LinkedList<KnightItem>();
        }
        SimulatorResult result = new SimulatorResult(getOff(), getDef());
        AbstractUnitElement ramElement = pOff.get(UnitManager.getSingleton().getUnitByPlainName("ram"));
        int ramCount = 0;
        if (ramElement != null) {
            ramCount = ramElement.getCount();
        }
        double ramFactor = (offItem.affectsUnit(ramElement.getUnit())) ? 2.0 : 1.0;

        if (!isAttackerBelieve()) {
            //if attacker does not believe, ram fight at half power
            ramCount /= 2;
        }

        int wallAtFight = getWallLevel() - (int) Math.round((ramCount * ramFactor) / (4 * Math.pow(1.09, getWallLevel())));
        double additionalDamageFactor = 1.0;
        if (ConfigManager.getSingleton().getKnightNewItems() == 0 || ramFactor == 1.0) {
            additionalDamageFactor = 1.0;
        } else {
            additionalDamageFactor = 2.0;
        }
        if (wallAtFight < (int) Math.round((double) getWallLevel() / (2.0 * additionalDamageFactor))) {
            wallAtFight = (int) Math.round((double) getWallLevel() / (2.0 * additionalDamageFactor));
        }

        //enter three calculation rounds
        for (int i = 0; i < 3; i++) {
            //calculate strengths based on survivors of each round
            double[] offStrengths = calculateOffStrengths(result.getSurvivingOff());
            double[] defStrengths = calculateDefStrengths(result.getSurvivingDef(), offStrengths, wallAtFight, (i == 0));
            //calculate losses
            double[] offLosses = calulateLosses(offStrengths, defStrengths, ID_OFF);
            double[] defLosses = calulateLosses(offStrengths, defStrengths, ID_DEF);

            // <editor-fold defaultstate="collapsed" desc="Debug output">
            /*if (i == 0) {
            System.out.println("OffStrength[");
            for (int j = 0; j < 3; j++) {
            System.out.println("  " + offStrengths[j]);
            }
            System.out.println("]");
            System.out.println("DefStrength[");
            for (int j = 0; j < 3; j++) {
            System.out.println("  " + defStrengths[j]);
            }
            System.out.println("]");
            System.out.println("OffLosses[");
            for (int j = 0; j < 3; j++) {
            System.out.println("  " + offLosses[j]);
            }
            System.out.println("]");
            System.out.println("DefLosses[");
            for (int j = 0; j < 3; j++) {
            System.out.println("  " + defLosses[j]);
            }
            System.out.println("]");
            }*/
            // </editor-fold>

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

        // <editor-fold defaultstate="collapsed" desc="Wall calculation">
        if (result.isWin() && ramCount > 0) {
            //calculate wall after fight
            //1.09
            double maxDecrement = ramCount * 2 * ramFactor / (4 * Math.pow(1.09, getWallLevel()));
            //System.out.println("MaxDec " + maxDecrement);
            //double maxDecrement = ramCount * 2 / (4 * Math.pow(1.090012, getWallLevel()));
            double lostUnits = 0;
            double totalUnits = 0;

            for (UnitHolder unit : UnitManager.getSingleton().getUnits()) {
                //if (!isSpy(unit)) {
                totalUnits += getOff().get(unit).getCount() * unit.getAttack();
                lostUnits += (getOff().get(unit).getCount() - result.getSurvivingOff().get(unit).getCount()) * unit.getAttack();
            //} 
            }

            double ratio = lostUnits / totalUnits;
            // System.out.println("Ratio: " + ratio);
            int wallDecrement = (int) Math.round(-1 * maxDecrement / 2 * ratio + maxDecrement);
            //System.out.println("WallDec " + wallDecrement);
            result.setWallLevel((getWallLevel() - wallDecrement < 0) ? 0 : getWallLevel() - wallDecrement);
        } else if (ramCount <= 0) {
            //no change
            result.setWallLevel(getWallLevel());
        } else {
            //lost scenario
            double lostUnits = 0;
            double totalUnits = 0;
            for (UnitHolder unit : UnitManager.getSingleton().getUnits()) {
                totalUnits += getDef().get(unit).getCount();
                lostUnits += getDef().get(unit).getCount() - result.getSurvivingDef().get(unit).getCount();
            }
            double ratio = lostUnits / totalUnits;
            int wallDecrement = (int) Math.round((ramCount * ratio) * 2 * ramFactor / (8 * Math.pow(1.09, getWallLevel())));
            result.setWallLevel((getWallLevel() - wallDecrement < 0) ? 0 : getWallLevel() - wallDecrement);
        }
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="Building calculation">
        //demolish building
        double buildingAfter = getBuildingLevel();
        AbstractUnitElement cata = getOff().get(UnitManager.getSingleton().getUnitByPlainName("catapult"));
        if (cata != null && cata.getCount() != 0) {
            //get additional cata factor for special item
            double cataFactor = (offItem.affectsUnit(cata.getUnit())) ? 2.0 : 1.0;
            double cataCount = cata.getCount();
            if (!isAttackerBelieve()) {
                //if attacker does not believe, ram fight at half power
                cataCount /= 2;
            }

            if (!result.isWin()) {
                //attack lost
                double lostUnits = 0;
                double totalUnits = 0;
                for (UnitHolder unit : UnitManager.getSingleton().getUnits()) {
                    totalUnits += getDef().get(unit).getCount();
                    lostUnits += getDef().get(unit).getCount() - result.getSurvivingDef().get(unit).getCount();
                }
                double ratio = lostUnits / totalUnits;
                int buildingDecrement = 0;
                /* if (isCataChurch() && ConfigManager.getSingleton().isChurch()) {
                buildingDecrement = getMaxChurchDestruction((int) Math.round(cataCount * ratio / 2));
                if (buildingDecrement == -1) {
                //church level larger than 3, so use normal calculation
                buildingDecrement = (int) Math.round((((cataCount * ratio) * cata.getUnit().getAttack() * cataFactor) / (600 * Math.pow(1.09, getBuildingLevel()))));
                }
                } else {*/
                buildingDecrement = (int) Math.round(((cataCount * cata.getUnit().getAttack() * cataFactor) / (600 * Math.pow(1.09, getBuildingLevel()))) * ratio);
                // }
                buildingAfter = getBuildingLevel() - buildingDecrement;

            //@TODO: implement cata -> church (cataCount/ratio/2 must be equal the min number of needed catas per level

            } else {
                //attacker wins
                double lostUnits = 0;
                double totalUnits = 0;

                for (UnitHolder unit : UnitManager.getSingleton().getUnits()) {
                    totalUnits += getOff().get(unit).getCount();
                    lostUnits += getOff().get(unit).getCount() - result.getSurvivingOff().get(unit).getCount();
                }

                double ratio = lostUnits / totalUnits;
                double maxDecrement = 0.0;
                /* if (isCataChurch() && ConfigManager.getSingleton().isChurch()) {
                maxDecrement = getMaxChurchDestruction((int) Math.round(cataCount));
                if (maxDecrement == -1) {
                //church level larger than 3, so use normal calculation
                maxDecrement = cataCount * cata.getUnit().getAttack() * cataFactor / (300 * Math.pow(1.09, getBuildingLevel()));
                }
                } else {*/
                maxDecrement = cataCount * cata.getUnit().getAttack() * cataFactor / (300 * Math.pow(1.09, getBuildingLevel()));
                /*double churchDec = (-61826.086 - 19913.043 * getBuildingLevel()) / (1.0 - 2.0217392 * getBuildingLevel());
                System.out.println("Build " + getBuildingLevel());
                
                System.out.println("ChurchDec " + churchDec);
                churchDec = ((cataCount * 100) / churchDec);
                System.out.println("CataDec" + churchDec);
                System.out.println("CorrDec " + (int) Math.round(-1.0 * churchDec / 2.0 * ratio + churchDec));
                 */
                //  }
                int buildingDecrement = (int) Math.round(-1 * maxDecrement / 2 * ratio + maxDecrement);
                //  System.out.println("Ratio " + ratio);
                buildingAfter = getBuildingLevel() - buildingDecrement;
            //@TODO: implement cata -> church (max. dec depending on cata count -> do normal actual destr. calculation afterwards
            }
            result.setBuildingLevel((buildingAfter <= 0) ? 0 : (int) buildingAfter);
        } else {
            //no demolishion
            result.setBuildingLevel(getBuildingLevel());
        }
        // </editor-fold>
        return result;
    }

    private int getMaxChurchDestruction(int pCataCount) {
        if (pCataCount < 120) {
            //nothing to do
            return 0;
        }
        switch (getBuildingLevel()) {
            case 1: {
                if (pCataCount >= 400) {
                    //need at lease 400 cata to destroy church level 1
                    return 1;
                } else {
                    //less than 400 catas
                    return 0;
                }
            }
            case 2: {
                if (pCataCount < 167) {
                    //to less catas
                    return 0;
                }
                if ((pCataCount >= 167) && (pCataCount < 500)) {
                    //max. 1 level
                    return 1;
                } else if (pCataCount >= 500) {
                    //complete church can be destroyed
                    return 2;
                }
            }
            case 3: {
                if ((pCataCount >= 120) && (pCataCount < 360)) {
                    //max. 1 level can be destroyed
                    return 1;
                } else if ((pCataCount >= 360) && (pCataCount < 600)) {
                    //max. 2 levels can be destroyed
                    return 2;
                } else if (pCataCount >= 600) {
                    //church can be destroyed
                    return 3;
                }
            }
        }
        return -1;
    }

    private double[] calculateOffStrengths(Hashtable<UnitHolder, AbstractUnitElement> pTable) {
        double[] result = new double[3];
        Enumeration<UnitHolder> units = pTable.keys();
        double item = 1.0;
        while (units.hasMoreElements()) {
            UnitHolder unit = units.nextElement();
            AbstractUnitElement element = pTable.get(unit);
            //calculate knight item factor
            double itemFactor = (offItem.affectsUnit(unit)) ? offItem.getOffFactor() : 1.0;
            if (itemFactor != 1.0) {
                item = itemFactor;
            }
            //add strength to all appropriate array elements (e.g. marcher is cavalry and archer)
            if (isInfantry(unit) && !isArcher(unit)) {
                result[ID_INFANTRY] += unit.getAttack() * (double) element.getCount() * element.getTech() * itemFactor;
            }
            if (isCavalery(unit) && !isArcher(unit)) {
                if (isKnight(unit) && offItem.getItemId() == KnightItem.ID_SPY) {
                    //if knight is spy is has no off power
                } else {
                    result[ID_CAVALRY] += unit.getAttack() * (double) element.getCount() * element.getTech() * itemFactor;
                }
            }
            if (isArcher(unit)) {
                result[ID_ARCHER] += unit.getAttack() * (double) element.getCount() * element.getTech() * itemFactor;
            }
        }
        double moral = getMoral() / 100;
        double luck = ((100 + getLuck()) / 100);
        double believeFactor = (isAttackerBelieve()) ? 1.0 : 0.5;

        // <editor-fold defaultstate="collapsed" desc="Debug Output">
        /*
        System.out.println("Item(Off): " + item);
        System.out.println("Moral(Off): " + moral);
        System.out.println("Luck(Off): " + luck);
        System.out.println("Believe(Off): " + believeFactor);
         */
        // </editor-fold>

        result[ID_INFANTRY] = result[ID_INFANTRY] * moral * luck * believeFactor;
        result[ID_CAVALRY] = result[ID_CAVALRY] * moral * luck * believeFactor;
        result[ID_ARCHER] = result[ID_ARCHER] * moral * luck * believeFactor;
        return result;
    }

    private double[] calculateDefStrengths(Hashtable<UnitHolder, AbstractUnitElement> pTable, double[] pOffStrengths, int pWallAtFight, boolean pUseBasicDefense) {
        double[] result = new double[3];
        double totalOff = 0;
        for (double d : pOffStrengths) {
            totalOff += d;
        }
        double infantryMulti = (totalOff == 0) ? 0 : pOffStrengths[ID_INFANTRY] / totalOff;
        double cavalryMulti = (totalOff == 0) ? 0 : pOffStrengths[ID_CAVALRY] / totalOff;
        double archerMulti = (totalOff == 0) ? 0 : pOffStrengths[ID_ARCHER] / totalOff;
        Enumeration<UnitHolder> units = pTable.keys();
        while (units.hasMoreElements()) {
            UnitHolder unit = units.nextElement();

            AbstractUnitElement element = pTable.get(unit);
            double itemFactor = 1.0;
            for (KnightItem item : defItems) {
                if (item.affectsUnit(unit)) {
                    itemFactor = item.getDefFactor();
                    break;
                }
            }

            double believeFactor = (isDefenderBelieve()) ? 1.0 : 0.5;

            // <editor-fold defaultstate="collapsed" desc="Debug output">
            /*
            System.out.println("Item(Def): " + itemFactor);
            System.out.println("Believe(Def): " + believeFactor);
             */
            // </editor-fold>

            result[ID_INFANTRY] += infantryMulti * unit.getDefense() * (double) element.getCount() * element.getTech() * itemFactor * believeFactor;
            result[ID_CAVALRY] += cavalryMulti * unit.getDefenseCavalry() * (double) element.getCount() * element.getTech() * itemFactor * believeFactor;
            result[ID_ARCHER] += archerMulti * unit.getDefenseArcher() * (double) element.getCount() * element.getTech() * itemFactor * believeFactor;
        }

        // <editor-fold defaultstate="collapsed" desc="Debug output">
        /* System.out.println("BasicResult[");
        for (int j = 0; j < 3; j++) {
        System.out.println("  " + result[j]);
        }
        System.out.println("]");*/
        //</editor-fold>

        double nightBonus = (isNightBonus()) ? 2 : 1;
        double[] basicDefense = new double[]{0.0, 0.0, 0.0};
        if (pUseBasicDefense) {
            basicDefense[0] = (20.0 + (double) pWallAtFight * 50.0) * ((totalOff == 0) ? 0 : pOffStrengths[ID_INFANTRY] / totalOff);
            basicDefense[1] = (20.0 + (double) pWallAtFight * 50.0) * ((totalOff == 0) ? 0 : pOffStrengths[ID_CAVALRY] / totalOff);
            basicDefense[2] = (20.0 + (double) pWallAtFight * 50.0) * ((totalOff == 0) ? 0 : pOffStrengths[ID_ARCHER] / totalOff);
        }

        // <editor-fold defaultstate="collapsed" desc="Debug output">
       /* System.out.println("BasicDefense[");
        for (int j = 0; j < 3; j++) {
        System.out.println("  " + basicDefense[j]);
        }
        System.out.println("]");

        System.out.println("WallAtFight: " + pWallAtFight);
        System.out.println("WallMulti: " + Math.pow(1.037, pWallAtFight));*/
        //</editor-fold>

        result[0] = result[0] * nightBonus * Math.pow(1.037, pWallAtFight) + basicDefense[0];
        result[1] = result[1] * nightBonus * Math.pow(1.037, pWallAtFight) + basicDefense[1];
        result[2] = result[2] * nightBonus * Math.pow(1.037, pWallAtFight) + basicDefense[2];

        // <editor-fold defaultstate="collapsed" desc="Debug output">
        /* System.out.println("FinalResult[");
        for (int j = 0; j < 3; j++) {
        System.out.println("  " + result[j]);
        }
        System.out.println("]");*/
        //</editor-fold>

        return result;
    }

    private double[] calulateLosses(double[] pOffStrengths, double[] pDeffStrengths, int pType) {
        double[] losses = new double[3];
        double lossFactor = 1.5;
        if (ConfigManager.getSingleton().getFarmLimit() != 0) {
            lossFactor = 1.6;
        }
        if (pType == ID_OFF) {
            //calculate losses
            for (int i = 0; i <= ID_ARCHER; i++) {
                if (pOffStrengths[i] == 0 || pDeffStrengths[i] == 0) {
                    //one party was completely killed
                    losses[i] = 0;
                } else {
                    if (pOffStrengths[i] > pDeffStrengths[i]) {
                        losses[i] = Math.pow(pDeffStrengths[i] / pOffStrengths[i], lossFactor);
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
                        losses[i] = Math.pow(pOffStrengths[i] / pDeffStrengths[i], lossFactor);
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
                        //int spyLosses = 0;
                        double spyLosses = 0;
                        double spyRateTillDeath = 2.0;
                        if (ConfigManager.getSingleton().getSpyType() == 3) {
                            spyRateTillDeath = 1.0;
                        }
                        double lossFactor = 1.5;
                        if (ConfigManager.getSingleton().getFarmLimit() != 0) {
                            lossFactor = 1.6;
                        }

                        //special spy calculation
                        if (unitOffElement.getCount() == 0) {
                            //no spy
                            spyLosses = 0;
                        } else if ((double) unitDefElement.getCount() / (double) unitOffElement.getCount() >= spyRateTillDeath) {
                            //no change
                            spyLosses = unitOffElement.getCount();
                        } else {
                            //increment Def by 1 and use lossRatio and spyRate depending on server
                            spyLosses = (double) unitOffElement.getCount() * Math.pow((double) (unitDefElement.getCount() + 1) / ((double) unitOffElement.getCount() * spyRateTillDeath), lossFactor);
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
