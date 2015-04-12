package no.dkit.gamelib.util;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class Packer {
    public static void main(String[] args) {
        TexturePacker.Settings tpSettings = new TexturePacker.Settings();
        tpSettings.maxWidth = 1024;
        tpSettings.maxHeight = 1024;
        tpSettings.alias = true;
        tpSettings.combineSubdirectories = true;
        tpSettings.flattenPaths = true;
        tpSettings.paddingX=0;
        tpSettings.paddingY=0;
        tpSettings.pot=true;
        tpSettings.stripWhitespaceX=false;
        tpSettings.stripWhitespaceY=false;

        TexturePacker.process(tpSettings, args[0], args[1], args[2]);
    }
}