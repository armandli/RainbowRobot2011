import java.util.Random;

/**
 * This is the stub code for what you need to implement. All your code should go
 * into this file. Look at the abstract class implementation in RobotStategy.java
 * and read the comments thoroughly. 
 * 
 */
public class MyRobotStrategy extends RobotStrategy {
	Random rand = new Random();
		
	/**
	 * Rename your bot as you please. This name will show up in the GUI.
	 */
	public String getName() { 
		return "RandoBot"; 
	}

	/** 
	 * In this function, we provide you with the sensor information (in the form
	 * of 4 binary values), and the grid coordinates of your robot.
	 * 
	 * The grid is a 6x6 grid, starting at (x=0,y=0) in the top left corner.
	 * We represent this as a flat array "beliefs" with 36 probabilities.
	 * index(x,y) is the index of position (x,y) into the array beliefs  
	 * 
	 * The sensors are in the following order: SWNE, so sensor[1] is West. The
	 * sensors are probabilistic. The vector between your robot and the other
	 * robot is mapped into probabilities of your sensor firing. So if the other
	 * robot is two squares South, and a single square West, then there is a 2/3
	 * chance of the South sensor firing, and a 1/3 chance of the West sensor
	 * firing. Note if the other robot was four squares South and 2 squares
	 * West, your robot's sensors would fire with the same probability.
	 * 
	 * @param sensor
	 *            This round's sensor information
	 * @param xPos
	 *            Robot's X coord
	 * @param yPos
	 *            Robot's Y coord
	 */
	public void updateBeliefState(boolean[] sensor, int xPos, int yPos) {
		// RandoBot keeps a uniform belief state. Your bot should update the actual belief state. 
		for (int x = 0; x < 6; x++) {
			for (int y = 0; y < 6; y++) {
				beliefState[x][y] = 1.0 / 36.0;
			}
		}
		
	}
	
	public Order giveOrder(){
		// Randobot fires the green laser at a random board position. Your bot should be able to do a lot better.
		return new Order(Order.GREEN_CANNON, rand.nextInt(6), rand.nextInt(6));
	}
}
