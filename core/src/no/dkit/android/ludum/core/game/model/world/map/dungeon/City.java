package no.dkit.android.ludum.core.game.model.world.map.dungeon;

import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class City {
	public static final float ROOM_SIZE_WEIGHT = 0.5f;
	public static final float ROOM_ALLOCATION_WEIGHT = 1f;
	public static final int MAX_PLACEMENT_ITERATION = 10;
	public static final int ROOM_DOORS = 4;
	
	public static final int CORRIDOR_STRAIGHT = 0;
	public static final int CORRIDOR_BENT = 1;
	public static final int CORRIDOR_LABYRINTH = 2;
	
	protected int width;
	protected int height;
	private List<Cell> corridor_seeds;
	private Map<Cell, RoomBranch> room_map;
	private Map<Cell, CorridorBranch> corridor_map;
	
	private int rooms_placed = 0;
	private int corridor_style = CORRIDOR_STRAIGHT;

	protected Cell[][] cells;
	
	public City(int width, int height, int corridor_style){
		//Initialise Direction class
		Direction.initDirections();
		this.corridor_style = corridor_style;
		
		//Ensure heights and widths are odd,
		//if not add 1
		if (width % 2 == 0) width++;
		if (height % 2 == 0) height++;
		
		this.width = width;
		this.height = height;
		
		//Initialise cell array
		initialise_cell_array();
		
		//Block the perimeter
		block_perimeter();
		
		//Generate rooms
		generate_rooms();
		
		//Generate corridors
		generate_corridors();
		
		//Trim tree
		trim_tree();
	}
	
	//Initialises the cell array with unallocated cells
	private void initialise_cell_array(){
		cells = new Cell[width][height];
		room_map = new HashMap<Cell, RoomBranch>();
		corridor_map = new HashMap<Cell, CorridorBranch>();
		corridor_seeds = new ArrayList<Cell>();
		
		for (int w = 0; w < width; w++){
			for (int h = 0; h < height; h++){
				cells[w][h] = new Cell(w, h, Cell.UNALLOCATED);
			}
		}
	}
	
	//Sets the perimeter cells to be blocked
	protected void block_perimeter(){
		for (int w = 0; w < width; w++){
			cells[w][0].setType(Cell.BLOCKED);
			cells[w][height-1].setType(Cell.BLOCKED);
		}
		for (int h = 0; h < height; h++){
			cells[0][h].setType(Cell.BLOCKED);
			cells[width-1][h].setType(Cell.BLOCKED);
		}
	}
	
	//Gets the number of available cells for rooms
	private int get_allocated_cells(){
		int allocated_cells = 0;
		
		for (int w = 0; w < width; w++){
			for (int h = 0; h < height; h++){
				if (cells[w][h].getType() != Cell.BLOCKED){ 
					allocated_cells++;
				}
			}
		}
		
		return (int) Math.floor(allocated_cells * ROOM_ALLOCATION_WEIGHT);
	}
	
	//Generates the rooms for the dungeon
	//There can only be a given number of rooms in the dungeon
	//Where the allocated space in a dungeon for rooms is area / room_weight
	private void generate_rooms(){
		int allocated_cells = get_allocated_cells();
		int taken_cells = 0;
		int room_id = 1;
		
		float max_room_w = width * ROOM_SIZE_WEIGHT - 1;
		float max_room_h = height * ROOM_SIZE_WEIGHT - 1;
		
		//Make rooms until all the allocation has dried out
		while (taken_cells < allocated_cells){
			int r_w = (int) (Math.ceil(MathUtils.random() * max_room_w) + 1);
			int r_h = (int) (Math.ceil(MathUtils.random() * max_room_h) + 1);
			
			//If r_w and r_h are both odd
			if (r_w % 2 != 0 && r_h % 2 != 0){
				place_room(r_w, r_h, room_id);
				taken_cells += r_w * r_h;
				room_id++;
			}
		}
		
		System.out.println("Rooms Placed: " + rooms_placed);
	}
	
	//Randomly places the rooms where there is space, and builds the perimeter wall
	private void place_room(int w, int h, int room_id){
		int max_tries = MAX_PLACEMENT_ITERATION;
		int c_try = 0;

		LinkedList<Cell> room_cells = new LinkedList<Cell>();
		
		while (c_try < max_tries){
			//Get the top-left corner, must be even
			int tli = (int) Math.floor(MathUtils.random() * width);
			if (tli % 2 != 0) tli++;
			int tlj = (int) Math.floor(MathUtils.random() * height);
			if (tlj % 2 != 0) tlj++;
			
			//Check all the cells from this point are unallocated,
			//and an extra 1 width around for the perimeter
			boolean failed = false;
			for (int i = tli; i < tli+w+2; i++){
				for (int j = tlj; j < tlj+h+2; j++){
					if (cells[i][j].getType() != Cell.UNALLOCATED){
						failed = true;
						break;
					}
				}
				if (failed) break;
			}
			
			//If checks ok, build the room and the perimeter
			if (!failed){
				//Probability falls so there are an average of 2 doors per room
				int per = 2*(w+h);
				//System.out.println((float)ROOM_DOORS/(float)per);
				float door_chance = (float)ROOM_DOORS / (float)per;
				
				for (int i = tli; i < tli+w+2; i++){
					for (int j = tlj; j < tlj+h+2; j++){
						cells[i][j].setType(Cell.ROOM);
						cells[i][j].setRoomId(room_id);
						
						//If the perimeter wall, assign door if random number is under the threshold
						if (i == tli || i == tli+w+1 || j == tlj || j == tlj+h+1){
							cells[i][j].setType(Cell.PERIMETER);
							//Only place doors on odd cells
							if (i %2 != 0 || j%2 != 0){
								if (MathUtils.random() < door_chance){
									cells[i][j].setType(Cell.ENTRANCE);
								}
							}
							
							//Store the room cells read for assignment to logical structure
							room_cells.add(cells[i][j]);
						}
					}
					if (failed) break;
				}
				
				//Meet loop criteria
				rooms_placed++;
				c_try = MAX_PLACEMENT_ITERATION;
				
				//Build logical object and map entrance cells to the room
				RoomBranch room_obj = new RoomBranch(room_cells);
				for (int i = 0; i < room_cells.size(); i++){
					Cell c = room_cells.get(i);
					
					if (c.getType() == Cell.ENTRANCE){
						room_map.put(c, room_obj);
					}
				}
			} else {
				c_try++;
			}
		}
	}
	
	//Generates corridors on all viable corridor seeds (odd (x,y)'s)
	private void generate_corridors(){
		for (int i = 0; i < width; i++){
			for (int j = 0; j < height; j++){
				if (i%2 != 0 && j%2 != 0){
					if (cells[i][j].getType() == Cell.UNALLOCATED){
						carve_corridors(i, j, null);
						corridor_seeds.add(cells[i][j]);
					}
				}
			}
		} 
	}
	
	//Carve out corridor sections in every direction from a give (x,y)
	private void carve_corridors(int i, int j, CorridorBranch parent){
		String[] dirs;
		
		if (corridor_style == CORRIDOR_LABYRINTH) {
			dirs = Direction.shuffleKeys();
		} else if (corridor_style == CORRIDOR_STRAIGHT){
			dirs = Direction.getKeys();
		} else {
			if (MathUtils.random() < 0.5f){
				dirs = Direction.shuffleKeys();
			} else {
				dirs = Direction.getKeys();
			}
		}
		
		//Pick a random direction
		for (int d = 0; d < dirs.length; d++){
			String d_key = dirs[d];
			
			//Check the next two squares in that direction
            LinkedList<Cell> t_cells = new LinkedList<Cell>();
			t_cells.add(cells[i][j]);
			boolean failed = false;
			boolean corientrance = false;
			
			for (int step = 1; step <= 2; step++){
				int[] jump = Direction.DIRECTIONS.get(d_key);
				if (t_cells.get(step-1) != null && !failed){
					t_cells.add(cells[t_cells.get(step-1).getX() + jump[0]][t_cells.get(step-1).getY() + jump[1]]);
					if (t_cells.get(step) == null || t_cells.get(step).getType() != Cell.UNALLOCATED){
						failed = true;
					}
					
					if (t_cells.get(step) != null && t_cells.get(step).getType() == Cell.ENTRANCE){
						corientrance = true;
						failed = true;
						break;
					}
				} else {
					failed = true;
				}
			}
			
			//If unallocated, carve and start next section
			if (!failed){
				//Create corridor logic structure
				CorridorBranch corridor_obj = new CorridorBranch(t_cells);
				
				for (int b = 0; b < t_cells.size(); b++){
					Cell c = t_cells.get(b);
					
					c.setType(Cell.CORRIDOR);
					corridor_map.put(c, corridor_obj);
				}
				
				//Add to corridor tree
				if (parent != null){
					parent.getChildren().add(corridor_obj);
				}
				
				Cell l_cell = t_cells.getLast();
				carve_corridors(l_cell.getX(), l_cell.getY(), corridor_obj);
			}
			
			//Special case for entrance corridor links
			if (corientrance){
				//Create corridor logical structure
				CorridorBranch corridor_obj = new CorridorBranch(t_cells);
				
				for (int b = 0; b < t_cells.size(); b++){
					Cell c = t_cells.get(b);
					
					corridor_map.put(c, corridor_obj);;
				}
				
				Cell l_cell = t_cells.getLast();
				//Add to corridor tree
				if (parent != null){
					parent.getChildren().add(room_map.get(l_cell));
				}
			}
		}
	}
	
	//Go through the tree and trim off all branches without rooms
	private void trim_tree(){
		for (int i = 0; i < corridor_seeds.size(); i++){
			Cell seed = corridor_seeds.get(i);
			
			check_branch(corridor_map.get(seed));
		}
	}
	
	//Does this branch have a room on the end?
	//If it doesn't, remove the reference
	private boolean check_branch(Branch branch){
		if (branch == null) return false;
		boolean room = false;
		
		//Recurse through the tree to see if rooms exist
		for (int i = 0; i < branch.getChildren().size(); i++){
			Branch c = branch.getChildren().get(i);
			
			boolean t_room = check_branch(c);
			if (!t_room || branch.getChildren().size() > width/2){
				//If there is no room, remove the corridor
				for (int j = 0; j < c.getCells().size(); j++){
					Cell cell = c.getCells().get(j);
					
					cell.setType(Cell.UNALLOCATED);
				}
			} else {
				if (!room) room = true;
			}
		}
		
		//Due to corridor overlap, this makes sure the corridor 
		//gets rebuilt when a lower branch is wiped
		if (room) branch.getCells().getLast().setType(Cell.CORRIDOR);
		
		//True if a room
		if (!room) room = branch instanceof RoomBranch;
		
		return room;
	}
	
	public int getWidth(){
		return this.width;
	}
	public int getHeight(){
		return this.height;
	}
	public List<Cell> getCorridorSeeds(){
		return this.corridor_seeds;
	}
	public Map<Cell, RoomBranch> getRoomMap(){
		return this.room_map;
	}
	public Map<Cell, CorridorBranch> getCorridorMap(){
		return this.corridor_map;
	}
	
	public Cell[][] getCells(){
		return this.cells;
	}
	public void setCells(Cell[][] cells){
		this.cells = cells;
	}
	
	@Override
	public String toString(){
		String str = "";
		
		for (int i = 0; i < width; i++){
			for (int j = 0; j < height; j++){
				str += cells[i][j].toString();
			}
			str += '\n';
		}
				
		
		return str;
	}
}
