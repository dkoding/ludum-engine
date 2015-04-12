package no.dkit.android.ludum.core.game.model.world.map.dungeon;

public class CircularDungeon extends City {

	public CircularDungeon(int width, int height, int corridor_style) {
		super(width, height, corridor_style);
	}

	//Block a circular pattern around the grid
	@Override
	protected void block_perimeter(){
		for (int w = 0; w < width; w++){
			for (int h = 0; h < height; h++){
				cells[w][h] = new Cell(w, h, Cell.BLOCKED);
			}
		}
		
		//Variables for working out the size of the inner circle
		int centre_x = (int) Math.floor(width/2);
		int centre_y = (int) Math.floor(height/2);
		int radius = Math.min(centre_x, centre_y);
		
		for (int y = radius*-1; y <= radius; y++){
			int half_row_width = (int) Math.sqrt(radius * radius - y * y);
			for (int x = half_row_width*-1; x <= half_row_width; x++){
				cells[centre_x + x][centre_y + y].setType(Cell.UNALLOCATED);
			}
		}
		
		for (int w = 0; w < width; w++){
			cells[w][0].setType(Cell.BLOCKED);
			cells[w][height-1].setType(Cell.BLOCKED);
		}
		for (int h = 0; h < height; h++){
			cells[0][h].setType(Cell.BLOCKED);
			cells[width-1][h].setType(Cell.BLOCKED);
		}
	}
}
