/**
 * HuntParallel.java by Mpumelelo Mpanza
 *
 *
 * M. Kuttel (2025) – adapted for parallel assignment
 */

import java.util.concurrent.RecursiveTask;

public class HuntParallel extends RecursiveTask<HuntParallel.Result> {

    public enum Direction {
        STAY, LEFT, RIGHT, UP, DOWN, UP_LEFT, UP_RIGHT, DOWN_LEFT, DOWN_RIGHT
    }

    public static class Result {
        public int bestMana = Integer.MIN_VALUE;
        public int bestRow  = -1;
        public int bestCol  = -1;

        public void merge(Result other) {
            if (other == null) return;
            if (other.bestMana > this.bestMana) {
                this.bestMana = other.bestMana;
                this.bestRow  = other.bestRow;
                this.bestCol  = other.bestCol;
            } else if (other.bestMana == this.bestMana) {
                if (other.bestRow < this.bestRow ||
                   (other.bestRow == this.bestRow && other.bestCol < this.bestCol)) {
                    this.bestRow = other.bestRow;
                    this.bestCol = other.bestCol;
                }
            }
        }
    }

    private static final int THRESHOLD = 256; 

    private final int[][] starts; 
    private final int from, to;     
    private final DungeonMapParallel dungeon;

    public HuntParallel(int[][] starts, int from, int to, DungeonMapParallel dungeon) {
        this.starts = starts;
        this.from   = from;
        this.to     = to;
        this.dungeon = dungeon;
    }

    @Override
    protected Result compute() {
        int count = to - from;
        if (count <= THRESHOLD) {
            return computeDirect();
        }
        int mid = from + (count / 2);

        HuntParallel left  = new HuntParallel(starts, from, mid, dungeon);
        HuntParallel right = new HuntParallel(starts, mid, to,   dungeon);

        left.fork();
        Result rightRes = right.compute();
        Result leftRes  = left.join();

        leftRes.merge(rightRes);
        return leftRes;
    }

    private Result computeDirect() {
        Result best = new Result();
        for (int i = from; i < to; i++) {
            int searchId = i + 1;
            int row = starts[i][0];
            int col = starts[i][1];

            int localMax = performSearch(searchId, row, col);

            Result r = new Result();
            r.bestMana = localMax;
            r.bestRow  = row;
            r.bestCol  = col;
            best.merge(r);
        }
        return best;
    }

    private int performSearch(int id, int row, int col) {
        int power = Integer.MIN_VALUE;
        Direction next = Direction.STAY;

        while (!dungeon.visited(row, col)) {
            power = dungeon.getManaLevel(row, col);
            dungeon.setVisited(row, col, id);

            next = dungeon.getNextStepDirection(row, col);
            switch (next) {
                case STAY:       return power;
                case LEFT:       row--;        break;
                case RIGHT:      row++;        break;
                case UP:         col--;        break;
                case DOWN:       col++;        break;
                case UP_LEFT:    row--; col--; break;
                case UP_RIGHT:   row++; col--; break;
                case DOWN_LEFT:  row--; col++; break;
                case DOWN_RIGHT: row++; col++; break;
            }
        }
        return power;
    }
}
