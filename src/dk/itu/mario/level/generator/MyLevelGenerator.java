package dk.itu.mario.level.generator;

import dk.itu.mario.MarioInterface.GamePlay;
import dk.itu.mario.MarioInterface.LevelGenerator;
import dk.itu.mario.MarioInterface.LevelInterface;
import dk.itu.mario.level.MyLevel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;


public class MyLevelGenerator extends CustomizedLevelGenerator implements LevelGenerator {
    static GamePlay playerMetrics;
    static int fieldType = LevelInterface.TYPE_CASTLE;

    public static long SEED = System.currentTimeMillis();

    private double temperature = 100000.0;
    private double coolingRate = 0.7;
    private static Random generator = new Random(SEED);
    public static final int DIFFICULTY_LEVELS = 5; //Don't change this
    public static final double ABSOLUTE_TEMPERATURE = .000001;

    //minimum number of user funness ratings before we ask sklearn to build the regression model
    public static final int TRAINING_MINIMUM = 10;
    private final String regression = "sv";
    private FileWrapper ratingsFile = new FileWrapper("ratings.arff");

    public LevelInterface generateLevel(GamePlay playerMetrics) {
        boolean predictWithPythonModel = false;
        if (ratingsFile.exists() && ratingsFile.countDataLines() >= TRAINING_MINIMUM) {
            System.out.println(String.format("Rebuilding %s-regression model on new training data...", regression));
            rebuildPythonModel(regression,ratingsFile.getFilename());
            System.out.println("Done!");
            predictWithPythonModel = true;
        }
        this.playerMetrics = playerMetrics;
        fieldType = generator.nextInt(3);
        System.out.println("Evaluating generated maps' funness...");
        MyLevel level = optimize(temperature, coolingRate, predictWithPythonModel);
        return level;
    }

    private void rebuildPythonModel(String regressionType, String filename) {
        executeFromCommandLine(String.format("python sv_regression.py --rebuild %s --file %s",
                regressionType, filename));
    }

    private static ArrayList<String> executeFromCommandLine(String command) {
        ArrayList<String> stringList = new ArrayList<>();
        Runtime run = Runtime.getRuntime();
        try {
            Process pr = run.exec(command);
            BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));

            String s = null;
            while ((s = in.readLine()) != null) {
                stringList.add(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringList;
    }
    @Override
    public LevelInterface generateLevel(String detailedInfo) {
        // TODO Auto-generated method stub
        return null;
    }


    // method to optimize using simulated annealing
    public static MyLevel optimize(double startingTemperature, double coolingRate, boolean usePythonModel) {
        double t = startingTemperature;
        MyLevel currentSolution = new MyLevel(320, 15, new Random().nextLong(), 7, fieldType, playerMetrics, 0.2, 0.2, 0.2, 0.2, 0.2);
        //mutate current solution and test fitness
        while (t > ABSOLUTE_TEMPERATURE) {
            double fitness = evaluate(currentSolution, usePythonModel);
            MyLevel newSolution = mutate(currentSolution);
            double newFitness = evaluate(newSolution, usePythonModel);
            //change best solution to current solution if current solution is better
            if (newFitness > fitness) {
                currentSolution = newSolution;
                System.out.println("Better: " + currentSolution.fun);
            }
            //change to a worse solution with certain probabilty
            else {
                Double randomValue = generator.nextDouble();
                if (randomValue < acceptanceProbability(fitness, newFitness, t)) {
                    currentSolution = newSolution;
                    System.out.println("Worse: " + currentSolution.fun);
                }
            }
            t = t * coolingRate;
        }
        printMapStatistics(currentSolution);
        return currentSolution;
    }

    private static void printMapStatistics(MyLevel solution) {
        System.out.println("probBuildJump: " + solution.probBuildJump);
        System.out.println("probBuildCannons: " + solution.probBuildCannons);
        System.out.println("probBuildHillStraight: " + solution.probBuildHillStraight);
        System.out.println("probBuildTubes: " + solution.probBuildTubes);
        System.out.println("probBuildStraight: " + solution.probBuildStraight);
        System.out.println("difficulty: " + solution.difficulty);
        System.out.println("blocksCoins: " + solution.BLOCKS_COINS);
        System.out.println("blocksEmpty: " + solution.BLOCKS_EMPTY);
        System.out.println("blocksPower: " + solution.BLOCKS_POWER);
        System.out.println("enemies: " + solution.ENEMIES);
        System.out.println("Predicted fun: " + solution.fun);
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

        double difficulty = Math.abs((old.difficulty + signs[0]) % DIFFICULTY_LEVELS);
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
        String featureVector = String.format("%f,%f,%f,%f,%f,%d,%d,%d,%d,%d",
                solution.probBuildJump,
                solution.probBuildCannons,
                solution.probBuildHillStraight,
                solution.probBuildTubes,
                solution.probBuildStraight,
                solution.difficulty,
                solution.BLOCKS_COINS,
                solution.BLOCKS_EMPTY,
                solution.BLOCKS_POWER,
                solution.ENEMIES);

        ArrayList<String> result = executeFromCommandLine("python sv_regression.py --fv " + featureVector);
        double fun = Double.valueOf(result.get(0));
        solution.fun = fun;
        return fun;
    }

    public static double evaluate(MyLevel solution, boolean hasRegressionModel) {
        if (hasRegressionModel) {
            return evaluate(solution);
        }
        return 0.0;
    }
}
