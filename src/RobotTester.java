/**
 * Class used test the performance of MyRobotStrategy against MAPRobotStrategy.
 * 
 */
public class RobotTester {
	
	public static void main(String argv[]) throws Exception{
		
		//=== First, run 100 games to assess how well the distribution is tracked.
		System.out.println("Checking error in tracking the distribution ... ");
		int rounds = 1;
		double sumError = 0;
		for (int i = 0; i < rounds; i++) {
			Game g = new Game("GradingStrategy", "MAPRobotStrategy", 0, 0, false);
			g.run();
			
			GradingStrategy gStrat = (GradingStrategy) (g.getRobotStrategies()[0]);
			sumError += gStrat.getAvgError();
		}
		double avgTrackingError = sumError/rounds;
		System.out.println("Error in tracking the distribution: " + avgTrackingError + ".");


		Game game;		
		
		//=== Next, run a lot of games without graphics to assess the agent's performance against the MAPRobot.
		int wins = 0;
		int losses = 0;
		rounds = 10000; // more rounds give higher confidence... at 10000 things start to stabilize
		for (int k = 0; k < rounds; k++) {
			game = new Game("MyRobotStrategy", "MAPRobotStrategy", 0, 0, false);
			game.run();
			if (game.returnCode() > 0) wins++;
			else if (game.returnCode() < 0) losses++;
			
			if ((k+1) % 1000 == 0) System.out.println(k+1 + "/" + rounds + " games played; " + wins*100/(wins+losses+0.0) + "% wins");
		}
		System.out.println("Error in tracking the distribution (copied from above): " + avgTrackingError + ".");
		
		
		//=== Visualize a game against the MAPRobot. You can comment this out for debugging.
		game = new Game("MyRobotStrategy", "MAPRobotStrategy", 0, 0, true);
		game.run();
	}
}
