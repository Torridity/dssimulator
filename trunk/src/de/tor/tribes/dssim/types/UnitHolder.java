/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tor.tribes.dssim.types;

import java.io.Serializable;
import org.jdom.Element;

/**
 *
 * @author Charon
 */
public class UnitHolder implements Serializable {

    private String plainName = null;
    private String name = null;
    private double wood = 0;
    private double stone = 0;
    private double iron = 0;
    private double pop = 0;
    private double speed = 0;
    private double attack = 0;
    private double defense = 0;
    private double defenseCavalry = 0;
    private double defenseArcher = 0;
    private double carry = 0;
    private double buildTime = 0;

    public UnitHolder(Element pElement) throws Exception {
        try {
            setPlainName(pElement.getName());
            if (pElement.getName().equals("spear")) {
                setName("Speerträger");
            } else if (pElement.getName().equals("sword")) {
                setName("Schwertkämpfer");
            } else if (pElement.getName().equals("axe")) {
                setName("Axtkämpfer");
            } else if (pElement.getName().equals("archer")) {
                setName("Bogenschütze");
            } else if (pElement.getName().equals("spy")) {
                setName("Späher");
            } else if (pElement.getName().equals("light")) {
                setName("Leichte Kavallerie");
            } else if (pElement.getName().equals("marcher")) {
                setName("Berittener Bogenschütze");
            } else if (pElement.getName().equals("heavy")) {
                setName("Schwere Kavallerie");
            } else if (pElement.getName().equals("ram")) {
                setName("Ramme");
            } else if (pElement.getName().equals("catapult")) {
                setName("Katapult");
            } else if (pElement.getName().equals("knight")) {
                setName("Paladin");
            } else if (pElement.getName().equals("snob")) {
                setName("Adelsgeschlecht");
            } else {
                setName("Unbekannt (" + pElement.getName() + ")");
            }

            setWood(Double.parseDouble(pElement.getChild("wood").getText()));
            setStone(Double.parseDouble(pElement.getChild("stone").getText()));
            setIron(Double.parseDouble(pElement.getChild("iron").getText()));
            setPop(Double.parseDouble(pElement.getChild("pop").getText()));
            setSpeed(Double.parseDouble(pElement.getChild("speed").getText()));
            setAttack(Double.parseDouble(pElement.getChild("attack").getText()));
            setDefense(Double.parseDouble(pElement.getChild("defense").getText()));
            setDefenseCavalry(Double.parseDouble(pElement.getChild("defense_cavalry").getText()));
            setDefenseArcher(Double.parseDouble(pElement.getChild("defense_archer").getText()));
            setCarry(Double.parseDouble(pElement.getChild("carry").getText()));
            setBuildTime(Double.parseDouble(pElement.getChild("build_time").getText()));
        } catch (Exception e) {
            throw new Exception("Fehler beim laden von Einheit '" + pElement.getName() + "'", e);
        }
    }

    public String getPlainName() {
        return plainName;
    }

    public void setPlainName(String name) {
        this.plainName = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getNames() {
        if (getPlainName().equals("archer")) {
            return new String[]{"Bogenschützen", getName()};
        } else if (getPlainName().equals("marcher")) {
            return new String[]{"Berittene Bogenschützen", getName()};
        } else if (getPlainName().equals("ram")) {
            return new String[]{"Rammen", getName()};
        } else if (getPlainName().equals("catapult")) {
            return new String[]{"Katapulte", getName()};
        } else if (getPlainName().equals("knight")) {
            return new String[]{"Paladine", getName()};
        } else if (getPlainName().equals("snob")) {
            return new String[]{"Adelsgeschlechter", getName()};
        } else {
            return new String[]{getName()};
        }
    }

    public double getWood() {
        return wood;
    }

    public void setWood(double wood) {
        this.wood = wood;
    }

    public double getStone() {
        return stone;
    }

    public void setStone(double stone) {
        this.stone = stone;
    }

    public double getIron() {
        return iron;
    }

    public void setIron(double iron) {
        this.iron = iron;
    }

    public double getPop() {
        return pop;
    }

    public void setPop(double pop) {
        this.pop = pop;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getAttack() {
        return attack;
    }

    public void setAttack(double attack) {
        this.attack = attack;
    }

    public double getDefense() {
        return defense;
    }

    public void setDefense(double defense) {
        this.defense = defense;
    }

    public double getDefenseCavalry() {
        return defenseCavalry;
    }

    public void setDefenseCavalry(double defenseCavalry) {
        this.defenseCavalry = defenseCavalry;
    }

    public double getDefenseArcher() {
        return defenseArcher;
    }

    public void setDefenseArcher(double defenseArcher) {
        this.defenseArcher = defenseArcher;
    }

    public double getCarry() {
        return carry;
    }

    public void setCarry(double carry) {
        this.carry = carry;
    }

    public double getBuildTime() {
        return buildTime;
    }

    public void setBuildTime(double buildTime) {
        this.buildTime = buildTime;
    }

    @Override
    public String toString() {
        return getName();// + "(" + getSpeed() + " Minuten/Feld)";
    }
}
