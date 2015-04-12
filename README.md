# ludum-engine
The game engine I have created for Ludum Dare

Built on top of LibGDX (https://github.com/libgdx/libgdx)

# Prepare assets using gradle:

packManaged - compress all images in the assets/managed catalog and create spritesheets
copyUnmanaged - copy all images and other assets from assets/unmanaged to android/assets catalog

# Run your game (development)

Desktop: Set working directory to /android/assets so assets will be found and then run DesktopLauncher
Android: Run AndroidLauncher on a connected device or the emulator
Web: Run gradle task :html:superDev and when finished point browser to http://localhost:8080/html/
IOS: Open the project in XCode and run it from there

# Deploy your game

Work in progress...

# License

1) Please give credit where credit is due.

2) As long as you release your game using this engine for a Ludum Dare abiding by all Ludum Dare rules you can afterwards
do with your game/product as you wish, including (of course) distributing your source code. Also, see 1)

