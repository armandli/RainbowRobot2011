/**
 * This is the abstract class for the Robot strategy. You should put all your
 * code in the provided stub file
 * 
 * @author epz
 * 
 */
public abstract class RobotStrategy {
	public static final int SOUTH = 0;
	public static final int WEST = 1;
	public static final int NORTH = 2;
	public static final int EAST = 3;

	/* Width w and height h of the board. */ 
	int w,h;

	/* Belief state representation: beliefs[x][y] denotes the probability with which the opponent is at position (x,y). */
	/**
	 * Your robot'ss belief distribution about the opponent. beliefState[x][y] is your 
	 * robot's probability that the other robot is in square (x,y)
	 */
	protected double[][] beliefState;
	
	public double[][] getBeliefState() {
		return beliefState;
	}

	public RobotStrategy() {
		//=== Default size: 6*6.
		w = 6;
		h = 6;
		beliefState = new double [w][h];
	}
	
	/**
	 * Called when the robot is first placed, describing its initial position.
	 * This initializes the belief state of the other robot's location.
	 * @param x your own initial x coordinate
	 * @param y your own initial y coordinate
	 */
	public void initializeBeliefState(int x, int y) {
		beliefState = new double[w][h];

		for(int scanX=0;scanX<w;scanX++){
			for(int scanY=0;scanY<h;scanY++){
				beliefState[scanX][scanY] = 1.0;
				int dist = Math.abs(scanX-x)+Math.abs(scanY-y);
				if(dist < 4) beliefState[scanX][scanY] = 0.0;
			}
		}
		
		normalize();
	}

	/* Normalization of the belief state to sum to 1. */
	void normalize() {
		double sum = 0.0;
		for(int scanX=0;scanX<w;scanX++)
			for(int scanY=0;scanY<h;scanY++)
				sum += beliefState[scanX][scanY];
		
		for(int scanX=0;scanX<w;scanX++)
			for(int scanY=0;scanY<h;scanY++)
				beliefState[scanX][scanY] /= sum;
		
		for(int scanX=0;scanX<w;scanX++){
			for(int scanY=0;scanY<h;scanY++){
				if (Double.isNaN(beliefState[scanX][scanY])){
					System.err.println("ERROR in normalize: all entries are zero, resulting in NaN entries.!");
				}
			}
		}

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
	 * @return
	 */
	public abstract void updateBeliefState(boolean[] sensor, int xPos, int yPos);
	
	/**
	 * Specify which action to take. 
	 */
	public abstract Order giveOrder();	
	
	/**
	 * Receive sensor information and own position, and decide which action to take. 
	 * @param sensor
	 *            This round's sensor information
	 * @param xPos
	 *            Robot's X coord
	 * @param yPos
	 *            Robot's Y coord
	 * @return
	 */
	public Order integrateEvidenceAndGiveOrder(boolean[] sensor, int xPos, int yPos) {
		updateBeliefState(sensor, xPos, yPos);	
		return giveOrder();
	}

	/**
	 * Specify your robot's name as it should appear in-game 
	 */
	public abstract String getName();
}