package no.dkit.android.ludum.core.game.model.world.map.dungeon;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Branch {
    protected LinkedList<Cell> cells;
    protected List<Branch> children;

    public Branch(LinkedList<Cell> cells) {
        this.cells = cells;
        this.children = new ArrayList<Branch>();
    }

    public LinkedList<Cell> getCells() {
        return this.cells;
    }

    public void setCells(LinkedList<Cell> cells) {
        this.cells = cells;
    }

    public List<Branch> getChildren() {
        return this.children;
    }

    public void setChildren(List<Branch> children) {
        this.children = children;
    }
}
