/**
* DungeonHunterParallel.java
* This is a parallel driver for the Dungeon hunter PCP1 Assignment
* It Mirrors the serial version, DungeonHunter.java but a parallel version
*/

import java.util.Random;

public class DungeonHunterParallel {
    static final boolean DEBUG = false;

    static long startTime = 0;
    static long endTime = 0;
    private static void tick() { startTime = System.currentTimeMillis(); }
    private static void tock() { endTime = System.currentTimeMillis(); }

    public static void main(String[] args) throws Exception {
        double xmin, xmax, ymin, ymax;
        DungeonMapParallel dungeon;

        int numSearches = 10;
        int gateSize = 10;
        Random rand = new Random();
        int randomSeed = 0;
