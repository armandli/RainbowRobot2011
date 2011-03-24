import javax.management.RuntimeErrorException;

/**
 * Compares MyRobotStrategy to MAPRobotStrategy and measures the distance between their beliefs
 * 
 * @author daveth
 * 
 */
public class GradingStrategy extends RobotStrategy 
{
	private RobotStrategy mapChild;
	private RobotStrategy myChild;

	private double error = 0;
	private double avgError = 0;
	private int rounds = 0;

	public double getAvgError() {
		return avgError;
	}

	public GradingStrategy() 
	{
		mapChild = new MAPRobotStrategy();
		myChild = new MyRobotStrategy();
		error = 0;
	}

	public void updateBeliefState(boolean[] sensor, int xPos, int yPos){
		mapChild.updateBeliefState(sensor, xPos, yPos);
		myChild.updateBeliefState(sensor, xPos, yPos);

		for(int scanX=0;scanX<w;scanX++){
			for(int scanY=0;scanY<h;scanY++){
				if (Double.isNaN(myChild.getBeliefState()[scanX][scanY])){
					System.err.println("ERROR: student implementation returns belief state with NaN entries. Check division by zero!");
				}
			}
		}
		
		/*
		 * Compare MAP agent's believes to the ones of the other agent.
		 */
		double[][] mapBeliefs = mapChild.getBeliefState();
		double[][] myBeliefs = myChild.getBeliefState();

		for (int x = 0; x < 6; x++) {
			for (int y = 0; y < 6; y++) {
				error += Math.abs(mapBeliefs[x][y]-myBeliefs[x][y]);
			}
		}
		rounds++;
		avgError = error/rounds;
	}

	public void compareBeliefStates(){
		double[][] mapBeliefs = mapChild.getBeliefState();
		double[][] myBeliefs = myChild.getBeliefState();
		double[][] errBeliefs = new double[6][6];

		System.out.println("CORRECT BELIEF:\n");
		for (int x = 0; x < 6; x++) {
			for (int y = 0; y < 6; y++) {
				System.out.format("  %1.4f    ", mapBeliefs[x][y]);
			}
			System.out.println("\n");
		}
		
		System.out.println("YOUR BELIEF:\n");
		
		for (int x = 0; x < 6; x++) {
			for (int y = 0; y < 6; y++) {
				errBeliefs[x][y]= myBeliefs[x][y]-mapBeliefs[x][y];
				System.out.format("  %1.4f    ", myBeliefs[x][y]);
			}
			System.out.println();
			
			for (int y = 0; y < 6; y++) {
				System.out.format("[%+1.4f]   ", errBeliefs[x][y]);
			}
			System.out.println();
			System.out.println();	
		}
	}
	
	public Order giveOrder(){
		myChild.giveOrder();
		return mapChild.giveOrder();		
	}

	public String getName() { return "GradingStrategy!"; }
}