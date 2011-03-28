import java.util.Random;

/**
 * This is the stub code for what you need to implement. All your code should go
 * into this file. Look at the abstract class implementation in RobotStategy.java
 * and read the comments thoroughly. 
 * 
 */
public class MyRobotStrategy extends RobotStrategy {
	// walking vector in the order of SWNE
	static final int walk_vector[][] = {{0,1},{-1,0},{0,-1},{1,0}};
	int num_red, num_yellow;
	public int red_shots, yellow_shots;
	double most_probable;
	
	Random rand = new Random();
		
	/**
	 * Rename your bot as you please. This name will show up in the GUI.
	 */
	public String getName() { 
		return "RandoBot"; 
	}
	
	public MyRobotStrategy(){
		super();
		num_red = 1;
		num_yellow = 2;
		most_probable = 0.0;
		red_shots = yellow_shots = 0;
	}
	
	void printBeliefState(){
		for (int i = 0; i < w; ++i){
			for (int j = 0; j < h; ++j)
				System.out.print(beliefState[i][j]+" ");
			System.out.println();
		}
	}
	
	void printPrbMatrix(double p_x[][], double cond[][][]){
		System.out.println("X Prob:");
		for (int i = 0; i < w; ++i){
			for (int j = 0; j < h; ++j)
				System.out.print(p_x[i][j]+" ");
			System.out.println();
		}
		for (int k = 0; k < 4; ++k){
			System.out.println("Y="+k);
			for (int i = 0; i < w; ++i){
				for (int j = 0; j < h; ++j)
					System.out.print(cond[k][i][j]+" ");
				System.out.println();
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
	 */
	public void updateBeliefState(boolean[] sensor, int xPos, int yPos) {
		double cond_s_p[][][] = new double[4][w][h];
		double prob_x[][] = new double[w][h];

		for (int i = 0; i < w; ++i)
			for (int j = 0; j < h; ++j){
				// calculate the probability of robot in location P in the next step
				prob_x[i][j] = calculateP(i, j);
				for (int k = 0; k < 4; ++k)
					// calculate the conditional probability of robot being in location P given sensor input S
					cond_s_p[k][i][j] = calculateCondSP(k, i, j, sensor[k], xPos, yPos);
			}
		
		// update the belief state
		for (int i = 0; i < w; ++i)
			for (int j = 0; j < h; ++j){
				beliefState[i][j] = prob_x[i][j];
				for (int k = 0; k < 4; ++k)
					beliefState[i][j] *= cond_s_p[k][i][j];
				if (beliefState[i][j] > most_probable)
					most_probable = beliefState[i][j];
			}

		//double sum = 0.0;
		//for (int i = 0; i < w; ++i)
		//	for (int j = 0; j < h; ++j)
		//		sum += beliefState[i][j];
		//System.out.println("Sensor:");
		//System.out.println("SOUTH:"+sensor[0]+" WEST:"+sensor[1]+" NORTH:"+sensor[2]+" EAST:"+sensor[3]);
		//System.out.println("X position:"+xPos+" Y Position:"+yPos);
		//printPrbMatrix(prob_x, cond_s_p);
		//System.exit(-1);

		normalize();
	}
	
	double calculateCondSP(int stype, int x, int y, boolean on, int xpos, int ypos){
		if (x == xpos && y == ypos)
			return 0.0;
		
		double p = 0.0; // assume 0 prob
		double dy = Math.abs(y - ypos), dx = Math.abs(x - xpos);
		switch(stype){
		case SOUTH:
			if (y > ypos){
				p = dy / (dy + dx);
				if (!on) 
					p = 1 - p;
			} else if (!on) p = 1.0;
			break;
		case WEST:
			if (x < xpos){
				p = dx / (dy + dx);
				if (!on)
					p = 1 - p;
			} else if (!on) p = 1.0;
			break;
		case NORTH:
			if (y < ypos){
				p = dy / (dy + dx);
				if (!on)
					p = 1 - p;
			} else if (!on) p = 1.0;
			break;
		case EAST:
			if (x > xpos){
				p = dx / (dy + dx);
				if (!on)
					p = 1 - p;
			} else if (!on) p = 1.0;
			break;
		}
		return p;
	}
	
	double calculateP(int x, int y){
		double p = beliefState[x][y] * 0.52;
		for (int i = 0; i < 4; ++i)
			if (!isOutOfBound(x+walk_vector[i][0], y+walk_vector[i][1]))
				p += beliefState[x+walk_vector[i][0]][y+walk_vector[i][1]] * 0.12;
			else
				p += beliefState[x][y] * 0.12;
		return p;
	}
	
	boolean isOutOfBound(int x, int y){
		if (x < 0 || x >= w || y < 0 || y >= h)
			return true;
		return false;
	}
	
	// strategy 1: take the MAP
	Order giveMAPOrder(){
		int bestx = 0, besty = 0;
		for (int i = 0; i < w; ++i)
			for (int j = 0; j < h; ++j)
				if (beliefState[i][j] > beliefState[bestx][besty]){
					bestx = i; besty = j;
				}
		return new Order(Order.GREEN_CANNON, bestx, besty);
	}
	
	//strategy 2: a variant of MAP, but we throw special attack when higher than x confidence
	Order giveMAPSpecialOrder(){
		double threshold_red = 0.75, threshold_yellow = 0.40;
		if (most_probable >= 0.87)
			red_shots++;
		else if (most_probable >= 0.73)
			yellow_shots++;
		int bestx = 0, besty = 0;
		for (int i = 0; i < w; ++i)
			for (int j = 0; j < h; ++j)
				if (beliefState[i][j] > beliefState[bestx][besty]){
					bestx = i; besty = j;
				}
		if (beliefState[bestx][besty] >= threshold_red && num_red > 0){
			num_red--;
			return new Order(Order.RED_CANNON, bestx, besty);
		} else if (beliefState[bestx][besty] >= threshold_yellow && num_yellow > 0){
			num_yellow--;
			return new Order(Order.YELLOW_CANNON, bestx, besty);
		}
		return new Order(Order.GREEN_CANNON, bestx, besty);
	}
	
	public Order giveOrder(){
		return giveMAPSpecialOrder();
	}
}
