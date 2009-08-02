/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.dssim.io;

import de.tor.tribes.dssim.types.AbstractUnitElement;
import de.tor.tribes.dssim.types.UnitHolder;
import de.tor.tribes.dssim.util.JaxenUtils;
import de.tor.tribes.dssim.util.UnitManager;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;

/**
 *
 * @author Charon
 */
public class SimIOHelper {

    static {
        String userDir = System.getProperty("user.home");
        if (!new File(userDir + "/.astar").exists()) {
            new File(userDir + "/.astar").mkdir();
        }
    }

    public static String getDataDir() {
        return System.getProperty("user.home") + "/.astar";
    }

    public static List<String> getOffSetups() {
        List<String> result = new LinkedList<String>();
        File[] files = new File(getDataDir()).listFiles(new FileFilter() {

            public boolean accept(File pathname) {
                if (pathname.getName().endsWith("_off.xml")) {
                    return true;
                }
                return false;
            }
        });

        for (File f : files) {
            result.add(f.getName().replaceAll("_off.xml", ""));
        }

        return result;
    }

    public static List<String> getDefSetups() {
        List<String> result = new LinkedList<String>();
        File[] files = new File(getDataDir()).listFiles(new FileFilter() {

            public boolean accept(File pathname) {
                if (pathname.getName().endsWith("_def.xml")) {
                    return true;
                }
                return false;
            }
        });

        for (File f : files) {
            result.add(f.getName().replaceAll("_def.xml", ""));
        }

        return result;
    }

    public static void writeTroopSetup(List<AbstractUnitElement> pTroops, String pFile) throws Exception {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<troopSetup>\n");
        for (AbstractUnitElement elem : pTroops) {
            buffer.append("<unit name=\"" + elem.getUnit().getPlainName() + "\" tech=\"" + elem.getTech() + "\" count=\"" + elem.getCount() + "\"/>\n");
        }
        buffer.append("</troopSetup>\n");
        FileWriter w = new FileWriter(new File(pFile));
        w.write(buffer.toString());
        w.flush();
        w.close();
    }

    public static List<AbstractUnitElement> readTroopSetup(String pFile) throws Exception {
        List<AbstractUnitElement> result = new LinkedList<AbstractUnitElement>();

        Document doc = JaxenUtils.getDocument(new File(pFile));
        for (Element unit : (List<Element>) JaxenUtils.getNodes(doc, "//troopSetup/unit")) {
            try {
                String name = unit.getAttribute("name").getValue();
                UnitHolder unitHolder = UnitManager.getSingleton().getUnitByPlainName(name);
                if (unitHolder != null) {
                    int tech = unit.getAttribute("tech").getIntValue();
                    int count = unit.getAttribute("count").getIntValue();
                    result.add(new AbstractUnitElement(unitHolder, count, tech));
                } else {
                    //unit not available on selected server
                }
            } catch (Exception e) {
                //failed to read unit
            }
        }
        return result;
    }
}
