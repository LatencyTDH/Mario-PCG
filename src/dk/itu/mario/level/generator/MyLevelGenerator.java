package dk.itu.mario.level.generator;

import java.util.Random;
import dk.itu.mario.MarioInterface.Constraints;
import dk.itu.mario.MarioInterface.GamePlay;
import dk.itu.mario.MarioInterface.LevelGenerator;
import dk.itu.mario.MarioInterface.LevelInterface;
import dk.itu.mario.level.CustomizedLevel;
import dk.itu.mario.level.MyLevel;

public class MyLevelGenerator extends CustomizedLevelGenerator implements LevelGenerator{
    static GamePlay playerMetrics;
    static double current_fun = 0;
    static int fieldType = LevelInterface.TYPE_CASTLE;
	public LevelInterface generateLevel(GamePlay playerMetrics) {
		this.playerMetrics = playerMetrics;
		return optimize(1.0,1000,0.9);
	}

	@Override
	public LevelInterface generateLevel(String detailedInfo) {
		// TODO Auto-generated method stub
		return null;
	}


  // method to optimize using simulated annealing
	public static MyLevel optimize(double startingTemperature, int numberOfIterations, double coolingRate) {
    double t = startingTemperature;
    MyLevel current_solution =  new MyLevel(320,15,new Random().nextLong(),7,fieldType,playerMetrics,0.2,0.2,0.2,0.2,0.2);
    double ti = t;
    //mutate current solution and test fitness
    for (int i = 0; i < numberOfIterations; i ++) {
      double fitness = evaluate(current_solution);
      MyLevel new_solution = mutate(current_solution);
      double new_fitness = evaluate(new_solution);
      //change best solution to current solution if current solution is better
      if (new_fitness > fitness) {
        current_solution = new_solution;
    }
    //change to a worse solution with certain probabilty
    else{
    	Random rand = new Random();
    	Double pickedNumber = rand.nextDouble();
    	if (acceptance_probability(fitness, new_fitness, t) < pickedNumber){
    		current_solution = new_solution;
    	}
    }
        ti = t * coolingRate;
      }
    System.out.println("probBuildJump: " + current_solution.probBuildJump);
    System.out.println("difficulty: " + current_solution.difficulty);
    System.out.println("probBuildCannons: " + current_solution.probBuildCannons);
    System.out.println("probBuildHillStraight: " + current_solution.probBuildHillStraight);
    System.out.println("probBuildTubes: " + current_solution.probBuildTubes);
    System.out.println("probBuildStraight: " + current_solution.probBuildStraight);
    System.out.println("fun: " + current_fun);
    return current_solution;
  }

  //method for calculating if a worse solution should be accepted
  public static double acceptance_probability(double old_fitness, double new_fitness,double temperature){
  	double val = (new_fitness- old_fitness)/temperature;
  	return Math.exp(val);
  }
  

  //method for mutating(changing a bit) the current solution
  public static MyLevel mutate(MyLevel old){
  	Random rand = new Random();
  	int[] signs = new int[6];
  	double[] change = new double[6];
  	double pickedSign = 0.5;
  	double pickedNumber = 0.5;
  	for (int i=0;i<6;i++){
    	pickedSign = rand.nextDouble();
      signs[i] = pickedSign < 0.5 ? -1 : 1;
      change[i] = 0 + (0.2 - 0) * rand.nextDouble();
    }

    double difficulty = Math.min(Math.max(old.difficulty + ( 10*change[0] * signs[0]), 1), 35);
  	double jump = Math.max(old.probBuildJump + (change[1] * signs[1]), 0);
  	double cannons = Math.max(old.probBuildCannons + (change[2] * signs[2]), 0);
  	double hills = Math.max(old.probBuildHillStraight + (change[3] * signs[3]), 0);
  	double tubes = Math.max(old.probBuildTubes + (change[4] * signs[4]), 0);
  	double straight = Math.max(old.probBuildStraight + (change[5] * signs[5]), 0);
  	double total = jump + cannons + hills + tubes + straight;
  	return new MyLevel(320, 15, new Random().nextLong(), (int) difficulty,fieldType,playerMetrics,jump/total,hills/total,straight/total,tubes/total,cannons/total);

  }
 

 //evaluate a solution based on our fun function, this is where ML comes in
  public static double evaluate(MyLevel solution){

    double fun = 

     
       2  * playerMetrics.emptyBlocksDestroyed +
       0.6 * playerMetrics.enemyKillByKickingShell +
      -0.3 * playerMetrics.totalTimeFireMode +
       -2  * playerMetrics.percentageBlocksDestroyed +
       0.1 * playerMetrics.totalEmptyBlocks +
       0.3 * playerMetrics.totalCoinBlocks +
      -0.1 * playerMetrics.totalCoins +
     -0.2 * solution.difficulty +
      5;
      current_fun = fun;
      return fun;
  }


}
