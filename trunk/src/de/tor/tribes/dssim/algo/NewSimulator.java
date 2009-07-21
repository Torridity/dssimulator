/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.dssim.algo;

import de.tor.tribes.dssim.types.AbstractUnitElement;
import de.tor.tribes.dssim.types.UnitHolder;
import de.tor.tribes.dssim.util.UnitManager;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 *
 * @author Charon
 */
public class NewSimulator extends AbstractSimulator {

    private final int ID_INFANTRY = 0;
    private final int ID_CAVALRY = 1;
    private final int ID_ARCHER = 2;
    private Hashtable<UnitHolder, AbstractUnitElement> off = null;
    private Hashtable<UnitHolder, AbstractUnitElement> def = null;

    @Override
    public void calculate(Hashtable<UnitHolder, AbstractUnitElement> pOff, Hashtable<UnitHolder, AbstractUnitElement> pDef, boolean pNightBonus, double pLuck, double pMoral, int pWallLevel, int pBuildingLevel) {
        off = pOff;
        def = pDef;
        setMoral(pMoral);
        setLuck(pLuck);
        setNightBonus(pNightBonus);
        setWallLevel(pWallLevel);
        setBuildingLevel(pBuildingLevel);
        double[] offStrengths = calculateOffStrengths(off);
        // System.out.println("OffStr");
        for (double d : offStrengths) {
            System.out.println(d);
        }
        //System.out.println("----");
        AbstractUnitElement ramElement = pOff.get(UnitManager.getSingleton().getUnitByPlainName("ram"));
        int ramCount = 0;
        if (ramElement != null) {
            ramCount = ramElement.getCount();
        }

        int wallAtFight = (int) Math.round(getWallLevel() - (ramCount / (4 * Math.pow(1.09, getWallLevel()))));
        if (wallAtFight < (int) Math.round(getWallLevel() / 2)) {
            wallAtFight = (int) Math.round(getWallLevel() / 2);
        }
        //System.out.println("WallAtFight " + wallAtFight);
        double[] defStrengths = calculateDefStrengths(def, offStrengths, wallAtFight);

        /* System.out.println("DefStr");
        for (double d : defStrengths) {
        System.out.println(d);
        }
        System.out.println("----");*/

        double offInfantyLoss = 0;
        if (offStrengths[ID_INFANTRY] != 0 && defStrengths[ID_INFANTRY] != 0) {
            if (offStrengths[ID_INFANTRY] > defStrengths[ID_INFANTRY]) {
                offInfantyLoss = defStrengths[ID_INFANTRY] / offStrengths[ID_INFANTRY];
            } else {
                offInfantyLoss = 1;
            }
        }


    }

    private double[] calculateOffStrengths(Hashtable<UnitHolder, AbstractUnitElement> pTable) {
        double[] result = new double[3];
        Enumeration<UnitHolder> units = pTable.keys();
        double moral = getMoral() / 100;
        while (units.hasMoreElements()) {
            UnitHolder unit = units.nextElement();
            AbstractUnitElement element = pTable.get(unit);
            //add strength to all appropriate array elements (e.g. marcher is cavalry and archer)
            if (isInfantry(unit)) {
                result[ID_INFANTRY] += unit.getAttack() * element.getCount() * moral * element.getTech();
            }
            if (isCavalery(unit)) {
                result[ID_CAVALRY] += unit.getAttack() * element.getCount() * moral * element.getTech();
            }
            if (isArcher(unit)) {
                result[ID_ARCHER] += unit.getAttack() * element.getCount() * moral * element.getTech();
            }
        }
        double luck = ((100 + getLuck()) / 100);
        result[0] = result[0] * luck;
        result[1] = result[1] * luck;
        result[2] = result[2] * luck;
        return result;
    }

    private double[] calculateDefStrengths(Hashtable<UnitHolder, AbstractUnitElement> pTable, double[] pOffStrengths, int pWallLevel) {
        double[] result = new double[3];
        double totalOff = 0;
        for (double d : pOffStrengths) {
            totalOff += d;
        }
        System.out.println("TOtalOff " + totalOff);
        double infantryMulti = (totalOff != 0) ? pOffStrengths[ID_INFANTRY] / totalOff : 0;
        double cavalryMulti = (totalOff != 0) ? pOffStrengths[ID_CAVALRY] / totalOff : 0;
        double archerMulti = (totalOff != 0) ? pOffStrengths[ID_ARCHER] / totalOff : 0;

        System.out.println("MultiI " + infantryMulti);
        System.out.println("MultiC " + cavalryMulti);
        System.out.println("MultiA " + archerMulti);
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
}
