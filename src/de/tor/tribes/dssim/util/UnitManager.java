/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.dssim.util;

import de.tor.tribes.dssim.types.UnitHolder;
import java.util.LinkedList;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;

/**
 *
 * @author Charon
 */
public class UnitManager {

    private static List<UnitHolder> units = new LinkedList<UnitHolder>();
    private static UnitManager SINGLETON = null;

    public static synchronized UnitManager getSingleton() {
        if (SINGLETON == null) {
            SINGLETON = new UnitManager();
        }
        return SINGLETON;
    }

    /**Parse the list of units*/
    public void parseUnits(String pServerID) {
        units.clear();
        try {
            Document d = JaxenUtils.getDocument(UnitManager.class.getResourceAsStream("/res/servers/units_" + pServerID + ".xml"));
            List<Element> l = JaxenUtils.getNodes(d, "/config/*");
            for (Element e : l) {
                try {
                    units.add(new UnitHolder(e));
                } catch (Exception inner) {
                    inner.printStackTrace();
                }
            }
        } catch (Exception outer) {
            outer.printStackTrace();
        }
    }

    /**Get a unit by its name*/
    public UnitHolder getUnitByPlainName(String pName) {
        for (UnitHolder u : units) {
            if (u.getPlainName().equals(pName)) {
                return u;
            }
        }
        return null;
    }

    public UnitHolder[] getUnits() {
        return units.toArray(new UnitHolder[]{});
    }
}
