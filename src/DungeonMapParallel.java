/**
 * DungeonMapParallel.java by Mpumelelo Mpanza
 *
 *
 * M. Kuttel (2025) – adapted for parallel assignment
 */

import java.util.Random;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

public class DungeonMapParallel {

    public static final int PRECISION = 10000;
    public static final int RESOLUTION = 5;

    private int rows, columns;
    private double xmin, xmax, ymin, ymax;
    private int [][] manaMap;
    private int [][] visit;
    private int dungeonGridPointsEvaluated;
    private double bossX;
    private double bossY;
