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

        if (args.length != 3) {
            System.out.println("Incorrect number of command line arguments provided.");
            System.exit(0);
        }

        try {
            gateSize = Integer.parseInt(args[0]);
            if (gateSize <= 0) throw new IllegalArgumentException("Grid size must be greater than 0.");

            numSearches = (int) (Double.parseDouble(args[1]) *
                                 (gateSize * 2) * (gateSize * 2) *
                                 DungeonMapParallel.RESOLUTION);

            randomSeed = Integer.parseInt(args[2]);
            if (randomSeed < 0) throw new IllegalArgumentException("Random seed must be non-negative.");
            else if (randomSeed > 0) rand = new Random(randomSeed);

        } catch (NumberFormatException e) {
            System.err.println("Error: All arguments must be numeric.");
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }

        xmin = -gateSize;
        xmax = gateSize;
        ymin = -gateSize;
        ymax = gateSize;

        dungeon = new DungeonMapParallel(xmin, xmax, ymin, ymax, randomSeed);

        int dungeonRows = dungeon.getRows();
        int dungeonColumns = dungeon.getColumns();

        int[][] starts = new int[numSearches][2];
        for (int i = 0; i < numSearches; i++) {
            starts[i][0] = rand.nextInt(dungeonRows);
            starts[i][1] = rand.nextInt(dungeonColumns);
        }

        int threads = Runtime.getRuntime().availableProcessors();
        Thread[] workers = new Thread[threads];
        HuntParallel[] jobs = new HuntParallel[threads];

        int chunk = (numSearches + threads - 1) / threads;

        tick();
        for (int t = 0; t < threads; t++) {
            int from = t * chunk;
            int to = Math.min(numSearches, from + chunk);
            if (from >= to) {
                workers[t] = null;
                continue;
            }
            jobs[t] = new HuntParallel(from + 1, starts, from, to, dungeon);
            workers[t] = new Thread(jobs[t], "hunter-" + t);
            workers[t].start();
        }
        
        for (int t = 0; t < threads; t++) {
            if (workers[t] != null) workers[t].join();
        }
        tock();

        int max = Integer.MIN_VALUE;
        int finderRow = -1, finderCol = -1;
        for (int t = 0; t < threads; t++) {
            if (jobs[t] == null) continue;
            int localMax = jobs[t].getBestMana();
            if (localMax > max) {
                max = localMax;
                finderRow = jobs[t].getBestRow();
                finderCol = jobs[t].getBestCol();
            }
        }

