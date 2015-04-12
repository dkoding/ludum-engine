package no.dkit.android.ludum.core.game.model.world.map.dungeon;

import java.util.LinkedList;

public class CorridorBranch extends Branch{

	public CorridorBranch(LinkedList<Cell> cells) {
		super(cells);
	}
	
	public boolean isFull(){
		//For each cell in the branch
		for (int i = 0; i < cells.size(); i++){
			Cell c = cells.get(i);
			
			if (c.getType() != Cell.CORRIDOR){
				return false;
			}
		}
		
		return true;
	}

}
