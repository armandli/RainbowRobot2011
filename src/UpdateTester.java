import java.util.Random;

/**
 * Class used test the correctness of belief state updates.
 * 
 * @author vasanth
 *
 */
public class UpdateTester {
	
	public static Random rand;
	public static Robot[] robots;
	public static RobotStrategy[] robotStrategies;
	
	
	public static RobotStrategy getStrategy(String name)
	{
		ClassLoader loader = ClassLoader.getSystemClassLoader();
		try
		{
			return (RobotStrategy) (loader.loadClass(name).newInstance());
		}
		catch(Exception e)
		{
			System.err.println("Class not found:" + name);
			return null;
		}
	}

	
	public static void main(String argv[]) throws Exception{
		
		rand = new Random();
		robotStrategies = new RobotStrategy[2];
		robots = new Robot[2];
		
		// initialize strategies
		robotStrategies[0] = getStrategy("GradingStrategy");
		robotStrategies[1] = getStrategy("MAPRobotStrategy");
		
		// create random robot positions
		int startX1, startX2, startY1, startY2;
		while(true)
		{
			startX1 = rand.nextInt(6);
			startX2 = rand.nextInt(6);
			startY1 = rand.nextInt(6);
			startY2 = rand.nextInt(6);
			if((Math.abs(startX1-startX2)+Math.abs(startY1-startY2))>=1) break;
		}
		robotStrategies[0].initializeBeliefState(startX1, startY1);
		robotStrategies[1].initializeBeliefState(startX2, startY2);


		// intialize robots
		robots[0] = new Robot(1, startX1, startY1);
		robots[1] = new Robot(1, startX2, startY2);
		
		// compute manhattan distances
		int deltaX = robots[1].getX() - robots[0].getX();
		int deltaY = robots[1].getY() - robots[0].getY();
		double hypo = Math.abs(deltaX) + Math.abs(deltaY);
		boolean[][] sensors = new boolean[2][4]; /* robot, sensor */

		// run belief state updates for both robots
		for (int r = 0; r < 2; r++) {
			
			// calculate sensors
			int i = -2 * r + 1; /* 1 if r == 0, -1 if r == 1 */
			sensors[r][Robot.SOUTH] = rand.nextDouble() <= Math.max(0, Math
					.min((i) * deltaY / hypo, 1));
			sensors[r][Robot.WEST] = rand.nextDouble() <= Math.max(0, Math
					.min((-i) * deltaX / hypo, 1));
			sensors[r][Robot.NORTH] = rand.nextDouble() <= Math.max(0, Math
					.min((-i) * deltaY / hypo, 1));
			sensors[r][Robot.EAST] = rand.nextDouble() <= Math.max(0, Math
					.min((i) * deltaX / hypo, 1));
			
			// update belief state
			robotStrategies[r].updateBeliefState(sensors[r], robots[r]
					.getX(), robots[r].getY());
			
			// Catch Errors
			try{
				robotStrategies[r].getBeliefState();
			} catch(Exception e) {}
		}
		
		// evaluate beliefs of MyRobotStrategy
		GradingStrategy gStrat = (GradingStrategy) robotStrategies[0];
		gStrat.compareBeliefStates();	
		
	}
}
