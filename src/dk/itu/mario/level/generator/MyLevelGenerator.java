package dk.itu.mario.level.generator;

import dk.itu.mario.MarioInterface.GamePlay;
import dk.itu.mario.MarioInterface.LevelGenerator;
import dk.itu.mario.MarioInterface.LevelInterface;
import dk.itu.mario.level.MyLevel;

import java.util.Random;

public class MyLevelGenerator extends CustomizedLevelGenerator implements LevelGenerator {
    static GamePlay playerMetrics;
    static double current_fun = 0;
    static int fieldType = LevelInterface.TYPE_CASTLE;

    public static int SEED = 42;

    private double temperature = 100000.0;
    private double coolingRate = 0.9;
    private static Random generator = new Random(SEED);
    public static final double ABSOLUTE_TEMPERATURE = .00001;

    public LevelInterface generateLevel(GamePlay playerMetrics) {
        this.playerMetrics = playerMetrics;
        fieldType = generator.nextInt(3);
        MyLevel level = optimize(temperature, coolingRate);
        level.writeLevelSerial();
        return level;
    }

    @Override
    public LevelInterface generateLevel(String detailedInfo) {
        // TODO Auto-generated method stub
        return null;
    }


    // method to optimize using simulated annealing
    public static MyLevel optimize(double startingTemperature, double coolingRate) {
        double t = startingTemperature;
        MyLevel currentSolution = new MyLevel(320, 15, new Random().nextLong(), 7, fieldType, playerMetrics, 0.2, 0.2, 0.2, 0.2, 0.2);
        //mutate current solution and test fitness
        while (t > ABSOLUTE_TEMPERATURE) {
            double fitness = evaluate(currentSolution);
            MyLevel newSolution = mutate(currentSolution);
            double newFitness = evaluate(newSolution);
            //change best solution to current solution if current solution is better
            if (newFitness > fitness) {
                currentSolution = newSolution;
            }
            //change to a worse solution with certain probabilty
            else {
                Double randomValue = generator.nextDouble();
                if (randomValue < acceptanceProbability(fitness, newFitness, t)) {
                    currentSolution = newSolution;
                }
            }
            t = t * coolingRate;
        }
        printMapStatistics(currentSolution);
        return currentSolution;
    }

    private static void printMapStatistics(MyLevel solution) {
        System.out.println("probBuildJump: " + solution.probBuildJump);
        System.out.println("difficulty: " + solution.difficulty);
        System.out.println("probBuildCannons: " + solution.probBuildCannons);
        System.out.println("probBuildHillStraight: " + solution.probBuildHillStraight);
        System.out.println("probBuildTubes: " + solution.probBuildTubes);
        System.out.println("probBuildStraight: " + solution.probBuildStraight);
        System.out.println("fun: " + current_fun);
    }

    //method for calculating if a worse solution should be accepted
    public static double acceptanceProbability(double oldFitness, double newFitness, double temperature) {
        double val = (newFitness - oldFitness) / temperature;
        return Math.exp(val);
    }


    //method for mutating(changing a bit) the current solution
    public static MyLevel mutate(MyLevel old) {
        int[] signs = new int[6];
        double[] change = new double[6];
        double pickedSign = 0.5;
        for (int i = 0; i < 6; i++) {
            pickedSign = generator.nextDouble();
            signs[i] = pickedSign < 0.5 ? -1 : 1;
            change[i] = 0 + (0.2 - 0) * generator.nextDouble();
        }

        double difficulty = Math.min(Math.max(old.difficulty + (10 * change[0] * signs[0]), 1), 35);
        double jump = Math.max(old.probBuildJump + (change[1] * signs[1]), 0);
        double cannons = Math.max(old.probBuildCannons + (change[2] * signs[2]), 0);
        double hills = Math.max(old.probBuildHillStraight + (change[3] * signs[3]), 0);
        double tubes = Math.max(old.probBuildTubes + (change[4] * signs[4]), 0);
        double straight = Math.max(old.probBuildStraight + (change[5] * signs[5]), 0);
        double total = jump + cannons + hills + tubes + straight;
        return new MyLevel(320, 15, generator.nextLong(), (int) difficulty, fieldType, playerMetrics,
                jump / total, hills / total, straight / total, tubes / total, cannons / total);
    }


    //evaluate a solution based on our fun function, this is where ML comes in
    public static double evaluate(MyLevel solution) {

        double fun =


                2 * playerMetrics.emptyBlocksDestroyed +
                        0.6 * playerMetrics.enemyKillByKickingShell +
                        -0.3 * playerMetrics.totalTimeFireMode +
                        -2 * playerMetrics.percentageBlocksDestroyed +
                        0.1 * playerMetrics.totalEmptyBlocks +
                        0.3 * playerMetrics.totalCoinBlocks +
                        -0.1 * playerMetrics.totalCoins +
                        -0.2 * solution.difficulty +
                        5;
        current_fun = fun;
        return fun;
    }

    public static double getDifficulty() {
        //TODO: evaluate level difficulty
        return 0.0;
    }

}
