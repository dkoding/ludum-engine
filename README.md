# ludum-engine
The game engine I have created for Ludum Dare

Built on top of LibGDX (https://github.com/libgdx/libgdx)

WORK IN PROGRESS!

# Demo

A playable demo of the engine can be found here: http://dkit.no/ludum-engine/

# Capabilities

* Random map generation - Maze, Cave, Dungeon, Obstacles/Features (Spelunky), City, Universe
* 4 different game types (Top/Down walker, Top/Down vehicle, Top/Down space, Sidescroller)
* 3 different control schemes (newtonian, direct and vehicle)
* A lot of predefined yet customizable game objects as loot, enemies and weapons
* Travel from game world to game world
* Moving platforms, chasms, mines
* Start screen with level selector, splash and loading screen, upgrade screen
* Box2D physics
* Predefined shaders for a wide range of special effects 
* Different types of tiled backgrounds
* Easy masking of terrain 
* Lights, shadows and particle effects - and lasers! :-)
* Weapon upgrade system
* Quest system
* Smart and efficient update of game world based on device performance
* Artificial Intelligence (Single and Group behaviors)
* And much more... 

# How to use it

Clone from source, then modify it to your liking

# Prepare assets

FIRST OF ALL: THE CURRENT ASSETS INCLUDED ARE NOT SUPPOSED TO BE PART OF YOUR CONTRIBUTION TO THE LUDUM DARE!

They are just provided as placeholders, and most of them are modified versions of graphics retrieved from http://opengameart.org and 
http://freesound.org - or even Google Image Search!  

# Prepare assets using gradle

To create sprite sheets and place assets at correct locations, go to the assets project and run:

packManagedAssets - compress all images in the assets/managed catalog and create spritesheets, then copy them to the androd/assets catalog
copyUnmanagedAssets - copy all images and other assets from assets/unmanaged to android/assets catalog

# Run your game (development)

Desktop: Set working directory to /android/assets so assets will be found and then run DesktopLauncher

Android: Run AndroidLauncher on a connected device or the emulator

Web: Run gradle task :html:superDev and when finished point browser to http://localhost:8080/html/ (or your specified context path)
Sometimes you have to recompile (press the refresh button in the brower) to get the game to start properly. I don't know why this is...

IOS: Run IOSLauncher on a connected device or the emulator (requires Mac OS X!)

# Deploy your game

Windows/Mac/Linux

gradlew desktop:dist
This will create a runnable .jar-file in the desktop/build/libs-directory (desktop-1.0.jar is the default).
From this directory start the program with 'java -jar desktop-1.0.jar' (and even better, create a start.bat/start.sh file that does this for you)
Note that the java runtime environment has to be installed for this to work and java has to be on your classpath.

...or just use packr: https://github.com/libgdx/packr

HTML/WEB
You have to serve the contents of the war directory via an http server. Most browsers will not allow the async requests to load the additional files when running on the filesystem.

To generate a deployment via gradle, run gradlew html:dist. That will create a dist directory within your html/build directory and will contain everything you need to deploy (and more). The same rule about the http server applies.

# Forum and homepage

http://darkdroid.com

# License

1) Please give credit where credit is due.

2) As long as you release your game using this engine for a Ludum Dare abiding by all Ludum Dare rules you can afterwards
do with your game/product as you wish, including (of course) distributing your source code. Also, see 1)

# Thanks

The people at http://shadertoy.com (IQ is my hero), http://glslsandbox.com, all the good people involved in random map and noise generation,
Daniel Shiffman (The Nature of Code: http://natureofcode.com/book/), Lode (Random noise generation) http://lodev.org/cgtutor/randomnoise.html,
Rogue Basin (http://www.roguebasin.com/index.php?title=Dungeon-Building_Algorithm) and a lot of people on Stack Overflow and 
GameDev.net (http://www.gamedev.net/forum/11-game-programming/), and the LibGDX team (http://www.badlogicgames.com/forum)
