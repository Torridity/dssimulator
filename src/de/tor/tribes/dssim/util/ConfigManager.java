/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.dssim.util;

import de.tor.tribes.dssim.types.UnitHolder;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;

/**
 *
 * @author Jejkal
 */
public class ConfigManager {

    public final static int ID_TECH_10 = 0;
    public final static int ID_TECH_3 = 1;
    public final static int ID_SIMPLE_TECH = 2;
    public final static int ID_NO_KNIGHT = 0;
    public final static int ID_KNIGHT_WITHOUT_ITEMS = 1;
    public final static int ID_KNIGHT_WITH_ITEMS = 2;
    private static ConfigManager SINGLETON = null;
    private int tech = 2;
    private int farmLimit = 0;
    private int knightType = 0;
    private int knightNewItems = 0;
    private int church = 0;
    private int spyType = 10;

    public static synchronized ConfigManager getSingleton() {
        if (SINGLETON == null) {
            SINGLETON = new ConfigManager();
        }
        return SINGLETON;
    }

    public void parseConfig(String pServerID) throws Exception {
        try {
            Document d = JaxenUtils.getDocument(UnitManager.class.getResourceAsStream("/res/servers/config_" + pServerID + ".xml"));
            setTech(Integer.parseInt(JaxenUtils.getNodeValue(d, "/config/game/tech")));
            setFarmLimit(Integer.parseInt(JaxenUtils.getNodeValue(d, "/config/game/farm_limit")));
            setKnightType(Integer.parseInt(JaxenUtils.getNodeValue(d, "/config/game/knight")));
            setKnightNewItems(Integer.parseInt(JaxenUtils.getNodeValue(d, "/config/game/knight_new_items")));
            setChurch(Integer.parseInt(JaxenUtils.getNodeValue(d, "/config/game/church")));
            setSpyType(Integer.parseInt(JaxenUtils.getNodeValue(d, "/config/game/spy")));
        } catch (Exception outer) {
            throw new Exception("Failed to load config for server '" + pServerID + "'", outer);
        }
    }

    /**
     * @return the tech
     */
    public int getTech() {
        return tech;
    }

    /**
     * @param tech the tech to set
     */
    public void setTech(int tech) {
        this.tech = tech;
    }

    /**
     * @return the knightNewItems
     */
    public int getKnightNewItems() {
        return knightNewItems;
    }

    /**
     * @param knightNewItems the knightNewItems to set
     */
    public void setKnightNewItems(int knightNewItems) {
        this.knightNewItems = knightNewItems;
    }

    /**
     * @return the knightType
     */
    public int getKnightType() {
        return knightType;
    }

    /**
     * @param knightType the knightType to set
     */
    public void setKnightType(int knightType) {
        this.knightType = knightType;
    }

    /**
     * @return the farmLimit
     */
    public int getFarmLimit() {
        return farmLimit;
    }

    /**
     * @param farmLimit the farmLimit to set
     */
    public void setFarmLimit(int farmLimit) {
        this.farmLimit = farmLimit;
    }

    /**
     * @return the church
     */
    public boolean isChurch() {
        return (church == 1);
    }

    /**
     * @param church the church to set
     */
    public void setChurch(int church) {
        this.church = church;
    }

    /**
     * @return the spyType
     */
    public int getSpyType() {
        return spyType;
    }

    /**
     * @param spyType the spyType to set
     */
    public void setSpyType(int spyType) {
        this.spyType = spyType;
    }
}
