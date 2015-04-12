package no.dkit.android.ludum.core.game.model.world.map.dungeon;

import no.dkit.android.ludum.core.game.model.world.map.UniverseMap;

public class Cell {
	public static final int UNALLOCATED = UniverseMap.CLEAR;
	public static final int BLOCKED = UniverseMap.BORDER;
	public static final int PERIMETER = UniverseMap.OCCUPIED;
	public static final int ROOM = UniverseMap.ROOM;
	public static final int ENTRANCE = UniverseMap.DOOR;
	public static final int CORRIDOR = UniverseMap.CORRIDOR;
	
	private int x, y, type, id, room_id;
	
	public Cell(int x, int y, int type){
		this.x = x;
		this.y = y;
		this.type = type;
	}
	
	@Override
	public String toString(){
		String ch = "";
		
		switch(type){
			case BLOCKED:
				ch = "#";
				break;
			case PERIMETER:
				ch = "P";
				break;
			case ROOM:
				ch = "R";
				break;
			case CORRIDOR:
				ch = "C";
				break;
			case ENTRANCE:
				ch = "E";
				break;
			default:
				ch = " ";
		}
		
		return ch;
	}
	
	public int getX(){
		return this.x;
	}
	public void setX(int x){
		this.x = x;
	}
	
	public int getY(){
		return this.y;
	}
	public void setY(int y){
		this.y = y;
	}
	
	public int getType(){
		return this.type;
	}
	public void setType(int type){
		this.type = type;
	}
	
	public int getId(){
		return this.id;
	}
	public void setId(int id){
		this.id = id;
	}
	
	public int getRoomId(){
		return this.room_id;
	}
	public void setRoomId(int room_id){
		this.room_id = room_id;
	}
}
