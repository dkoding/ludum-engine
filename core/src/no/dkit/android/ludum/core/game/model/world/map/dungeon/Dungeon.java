package no.dkit.android.ludum.core.game.model.world.map.dungeon;

import com.badlogic.gdx.math.MathUtils;
import no.dkit.android.ludum.core.game.model.world.map.AbstractMap;

public class Dungeon {

    //size of the map
    private int xsize = 0;
    private int ysize = 0;

    //number of "objects" to generate on the map
    private int objects = 0;

    //define the %chance to generate either a room or a corridor on the map
    //BTW, rooms are 1st priority so actually it's enough to just define the chance of generating a room
    private int chanceRoom = 75;
    private int chanceCorridor = 25;

    //our map
    private int[][] dungeon_map;
    private int[][] item_map;

    //a list over tile types we're using
    final private int tileUnused = AbstractMap.CLEAR;
    final private int tileDirtWall = AbstractMap.OCCUPIED; //not in use
    final private int tileDirtFloor = AbstractMap.ROOM;
    final private int tileCorridor = AbstractMap.CORRIDOR;

    public void createDungeon(int inx, int iny, int inobj) {
        // Here's the one generating the whole map
        if (inobj < 1) objects = 10;
        else objects = inobj;

        // Adjust the size of the map if it's too small
        if (inx < 3) xsize = 3;
        else xsize = inx;

        if (iny < 3) ysize = 3;
        else ysize = iny;

        //redefine the map var, so it's adjusted to our new map size
        dungeon_map = new int[xsize][ysize];
        item_map = new int[xsize][ysize];

        //start with making the "standard stuff" on the map
        for (int y = 0; y < ysize; y++) {
            for (int x = 0; x < xsize; x++) {
                //ie, making the borders of unwalkable walls
                if (y == 0) setCell(x, y, AbstractMap.SOLID);
                else if (y == ysize - 1) setCell(x, y, AbstractMap.SOLID);
                else if (x == 0) setCell(x, y, AbstractMap.SOLID);
                else if (x == xsize - 1) setCell(x, y, AbstractMap.SOLID);

                    //and fill the rest with dirt
                else setCell(x, y, tileUnused);
            }
        }

        //start with making a room in the middle, which we can start building upon
        makeRoom(xsize / 2, ysize / 2, 8, 6, MathUtils.random(0, 3));

        //keep count of the number of "objects" we've made
        int currentFeatures = 1; //+1 for the first room we just made

        //then we start the main loop
        for (int countingTries = 0; countingTries < 1000; countingTries++) {

            //check if we've reached our quota
            if (currentFeatures == objects) {
                break;
            }

            //start with a random wall
            int newx = 0;
            int xmod = 0;
            int newy = 0;
            int ymod = 0;
            int validTile = -1;

            //1000 chances to find a suitable object (room or corridor)..
            //(yea, i know it's kinda ugly with a for-loop... -_-')

            for (int testing = 0; testing < 1000; testing++) {
                newx = MathUtils.random(1, xsize - 1);
                newy = MathUtils.random(1, ysize - 1);
                validTile = -1;

                //System.out.println("tempx: " + newx + "\ttempy: " + newy);

                if (getCell(newx, newy) == tileDirtWall || getCell(newx, newy) == tileCorridor) {
                    //check if we can reach the place
                    if (getCell(newx, newy + 1) == tileDirtFloor || getCell(newx, newy + 1) == tileCorridor) {
                        validTile = 0; //
                        xmod = 0;
                        ymod = -1;
                    } else if (getCell(newx - 1, newy) == tileDirtFloor || getCell(newx - 1, newy) == tileCorridor) {
                        validTile = 1; //
                        xmod = +1;
                        ymod = 0;
                    } else if (getCell(newx, newy - 1) == tileDirtFloor || getCell(newx, newy - 1) == tileCorridor) {
                        validTile = 2; //
                        xmod = 0;
                        ymod = +1;
                    } else if (getCell(newx + 1, newy) == tileDirtFloor || getCell(newx + 1, newy) == tileCorridor) {
                        validTile = 3; //
                        xmod = -1;
                        ymod = 0;
                    }

                    //check that we haven't got another door nearby, so we won't get alot of openings besides each other

                    if (validTile > -1) {
                        if (getCell(newx, newy + 1) == AbstractMap.DOOR) //north
                            validTile = -1;
                        else if (getCell(newx - 1, newy) == AbstractMap.DOOR)//east
                            validTile = -1;
                        else if (getCell(newx, newy - 1) == AbstractMap.DOOR)//south
                            validTile = -1;
                        else if (getCell(newx + 1, newy) == AbstractMap.DOOR)//west
                            validTile = -1;
                    }

                    //if we can, jump out of the loop and continue with the rest
                    if (validTile > -1) break;
                }
            }

            if (validTile > -1) {

                //choose what to build now at our newly found place, and at what direction
                int feature = MathUtils.random(0, 100);
                if (feature <= chanceRoom) { //a new room
                    if (makeRoom((newx + xmod), (newy + ymod), 8, 6, validTile)) {
                        currentFeatures++; //add to our quota

                        //then we mark the wall opening with a door
                        setCell(newx, newy, AbstractMap.DOOR);

                        //clean up infront of the door so we can reach it
                        setCell((newx + xmod), (newy + ymod), tileDirtFloor);
                    }
                } else if (feature >= chanceRoom) { //new corridor
                    if (makeCorridor((newx + xmod), (newy + ymod), 6, validTile)) {
                        //same thing here, add to the quota and a door
                        currentFeatures++;
                        setCell(newx, newy, AbstractMap.DOOR);
                    }
                }
            }
        }

        //sprinkle out the bonusstuff (stairs, chests etc.) over the map
        int newx = 0;
        int newy = 0;
        int ways = 0; //from how many directions we can reach the random spot from
        int state = 0; //the state the loop is in, start with the stairs

        while (state != 10) {
            for (int testing = 0; testing < 1000; testing++) {
                newx = MathUtils.random(1, xsize - 2);
                newy = MathUtils.random(1, ysize - 2); //cheap bugfix, pulls down newy to 0<y<24, from 0<y<25
                //System.out.println("x: " + newx + "\ty: " + newy);
                ways = 4; //the lower the better

                //check if we can reach the spot
                if (getCell(newx, newy + 1) == tileDirtFloor || getCell(newx, newy + 1) == tileCorridor) {
                    //north
                    if (getCell(newx, newy + 1) != AbstractMap.DOOR)
                        ways--;
                }

                if (getCell(newx - 1, newy) == tileDirtFloor || getCell(newx - 1, newy) == tileCorridor) {
                    //east
                    if (getCell(newx - 1, newy) != AbstractMap.DOOR)
                        ways--;
                }

                if (getCell(newx, newy - 1) == tileDirtFloor || getCell(newx, newy - 1) == tileCorridor) {
                    //south
                    if (getCell(newx, newy - 1) != AbstractMap.DOOR)
                        ways--;
                }

                if (getCell(newx + 1, newy) == tileDirtFloor || getCell(newx + 1, newy) == tileCorridor) {
                    //west
                    if (getCell(newx + 1, newy) != AbstractMap.DOOR)
                        ways--;
                }

                if (state == 0) {
                    if (ways == 0) {
                        //we're in state 0, let's place a "upstairs" thing
                        setItem(newx, newy, AbstractMap.START_HINT);
                        state = 1;
                        break;
                    }
                } else if (state == 1) {
                    if (ways == 0) {
                        //state 1, place a "downstairs"
                        setItem(newx, newy, AbstractMap.END_HINT);
                        state = 10;
                        break;
                    }
                }
            }
        }
    }

    //setting a tile's type
    private void setCell(int x, int y, int celltype) {
        dungeon_map[x][y] = celltype;
    }

    private void setItem(int x, int y, int celltype) {
        item_map[x][y] = celltype;
    }

    //returns the type of a tile
    public int getCell(int x, int y) {
        return dungeon_map[x][y];
    }

    //returns the type of a tile
    public int getItem(int x, int y) {
        return item_map[x][y];
    }

    private boolean makeCorridor(int x, int y, int lenght, int direction) {
        /*******************************************************************************/
        //define the dimensions of the corridor (er.. only the width and height..)
        int len = MathUtils.random(2, lenght);
        int floor = tileCorridor;
        int dir = 0;
        if (direction > 0 && direction < 4) dir = direction;

        int xtemp = 0;
        int ytemp = 0;

        // reject corridors that are out of bounds
        if (x < 0 || x > xsize) return false;
        if (y < 0 || y > ysize) return false;

        switch (dir) {

            case 0: //north
                xtemp = x;

                // make sure it's not out of the boundaries
                for (ytemp = y; ytemp > (y - len); ytemp--) {
                    if (ytemp < 0 || ytemp > ysize) return false; //oh boho, it was!
                    if (getCell(xtemp, ytemp) != tileUnused) return false;
                }

                //if we're still here, let's start building
                for (ytemp = y; ytemp > (y - len); ytemp--) {
                    setCell(xtemp, ytemp, floor);
                }
                break;

            case 1: //east
                ytemp = y;

                for (xtemp = x; xtemp < (x + len); xtemp++) {
                    if (xtemp < 0 || xtemp > xsize) return false;
                    if (getCell(xtemp, ytemp) != tileUnused) return false;
                }

                for (xtemp = x; xtemp < (x + len); xtemp++) {
                    setCell(xtemp, ytemp, floor);
                }
                break;

            case 2: // south
                xtemp = x;

                for (ytemp = y; ytemp < (y + len); ytemp++) {
                    if (ytemp < 0 || ytemp > ysize) return false;
                    if (getCell(xtemp, ytemp) != tileUnused) return false;
                }

                for (ytemp = y; ytemp < (y + len); ytemp++) {
                    setCell(xtemp, ytemp, floor);
                }
                break;

            case 3: // west
                ytemp = y;

                for (xtemp = x; xtemp > (x - len); xtemp--) {
                    if (xtemp < 0 || xtemp > xsize) return false;
                    if (getCell(xtemp, ytemp) != tileUnused) return false;
                }

                for (xtemp = x; xtemp > (x - len); xtemp--) {
                    setCell(xtemp, ytemp, floor);
                }
                break;
        }

        //woot, we're still here! let's tell the other guys we're done!!
        return true;
    }

    private boolean makeRoom(int x, int y, int xlength, int ylength, int direction) {
        //define the dimensions of the room, it should be at least 4x4 tiles (2x2 for walking on, the rest is walls)
        int xlen = MathUtils.random(4, xlength);
        int ylen = MathUtils.random(4, ylength);

        //the tile type it's going to be filled with
        int floor = tileDirtFloor; //jordgolv..
        int wall = tileDirtWall; //jordv????gg

        //choose the way it's pointing at
        int dir = 0;
        if (direction > 0 && direction < 4) dir = direction;

        switch (dir) {

            case 0: // north

                //Check if there's enough space left for it
                for (int ytemp = y; ytemp > (y - ylen); ytemp--) {
                    if (ytemp < 0 || ytemp > ysize) return false;
                    for (int xtemp = (x - xlen / 2); xtemp < (x + (xlen + 1) / 2); xtemp++) {
                        if (xtemp < 0 || xtemp > xsize) return false;
                        if (getCell(xtemp, ytemp) != tileUnused) return false; //no space left...
                    }
                }

                //we're still here, build
                for (int ytemp = y; ytemp > (y - ylen); ytemp--) {
                    for (int xtemp = (x - xlen / 2); xtemp < (x + (xlen + 1) / 2); xtemp++) {
                        //start with the walls
                        if (xtemp == (x - xlen / 2)) setCell(xtemp, ytemp, wall);
                        else if (xtemp == (x + (xlen - 1) / 2)) setCell(xtemp, ytemp, wall);
                        else if (ytemp == y) setCell(xtemp, ytemp, wall);
                        else if (ytemp == (y - ylen + 1)) setCell(xtemp, ytemp, wall);
                            //and then fill with the floor
                        else setCell(xtemp, ytemp, floor);
                    }
                }

                break;

            case 1: // east

                for (int ytemp = (y - ylen / 2); ytemp < (y + (ylen + 1) / 2); ytemp++) {
                    if (ytemp < 0 || ytemp > ysize) return false;
                    for (int xtemp = x; xtemp < (x + xlen); xtemp++) {
                        if (xtemp < 0 || xtemp > xsize) return false;
                        if (getCell(xtemp, ytemp) != tileUnused) return false;
                    }
                }

                for (int ytemp = (y - ylen / 2); ytemp < (y + (ylen + 1) / 2); ytemp++) {
                    for (int xtemp = x; xtemp < (x + xlen); xtemp++) {
                        if (xtemp == x) setCell(xtemp, ytemp, wall);
                        else if (xtemp == (x + xlen - 1)) setCell(xtemp, ytemp, wall);
                        else if (ytemp == (y - ylen / 2)) setCell(xtemp, ytemp, wall);
                        else if (ytemp == (y + (ylen - 1) / 2)) setCell(xtemp, ytemp, wall);
                        else setCell(xtemp, ytemp, floor);
                    }
                }

                break;

            case 2: // south

                for (int ytemp = y; ytemp < (y + ylen); ytemp++) {
                    if (ytemp < 0 || ytemp > ysize) return false;
                    for (int xtemp = (x - xlen / 2); xtemp < (x + (xlen + 1) / 2); xtemp++) {
                        if (xtemp < 0 || xtemp > xsize) return false;
                        if (getCell(xtemp, ytemp) != tileUnused) return false;
                    }
                }

                for (int ytemp = y; ytemp < (y + ylen); ytemp++) {
                    for (int xtemp = (x - xlen / 2); xtemp < (x + (xlen + 1) / 2); xtemp++) {
                        if (xtemp == (x - xlen / 2)) setCell(xtemp, ytemp, wall);
                        else if (xtemp == (x + (xlen - 1) / 2)) setCell(xtemp, ytemp, wall);
                        else if (ytemp == y) setCell(xtemp, ytemp, wall);
                        else if (ytemp == (y + ylen - 1)) setCell(xtemp, ytemp, wall);
                        else setCell(xtemp, ytemp, floor);
                    }
                }

                break;

            case 3: // west

                for (int ytemp = (y - ylen / 2); ytemp < (y + (ylen + 1) / 2); ytemp++) {
                    if (ytemp < 0 || ytemp > ysize) return false;
                    for (int xtemp = x; xtemp > (x - xlen); xtemp--) {
                        if (xtemp < 0 || xtemp > xsize) return false;
                        if (getCell(xtemp, ytemp) != tileUnused) return false;
                    }
                }

                for (int ytemp = (y - ylen / 2); ytemp < (y + (ylen + 1) / 2); ytemp++) {
                    for (int xtemp = x; xtemp > (x - xlen); xtemp--) {
                        if (xtemp == x) setCell(xtemp, ytemp, wall);
                        else if (xtemp == (x - xlen + 1)) setCell(xtemp, ytemp, wall);
                        else if (ytemp == (y - ylen / 2)) setCell(xtemp, ytemp, wall);
                        else if (ytemp == (y + (ylen - 1) / 2)) setCell(xtemp, ytemp, wall);
                        else setCell(xtemp, ytemp, floor);
                    }
                }

                break;
        }

        return true;
    }
}
