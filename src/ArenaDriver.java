import java.io.*;
import java.util.Random;

public class ArenaDriver {
	private static String[] agents = {
										 "MAPRobotStrategy",
										 "MyRobotStrategy",
									 };

	
	public static void logEntry(String filename, String line)
	{
		boolean done = false;
		while(!done){
			try
			{
				FileWriter f = new FileWriter(filename,true);
				f.write(line+"\n");
				done=true;
				f.close();
			}
			catch(Exception e)
			{}
		}
	
	}
	public static void main(String argv[]) throws Exception{
		if(argv.length == 1)
		{
			tournament(Integer.parseInt(argv[0]));
			System.exit(0);
		}
		if(argv.length != 4)
		{
			System.out.println("Usage: java ArenaDriver <strategy1> <strategy2> <x> <y>");
			System.out.println("   Or: java ArenaDriver <number of tournament rounds>");
			System.exit(1);
		}
		
		Game g = new Game(argv[0],argv[1],Integer.parseInt(argv[2]),Integer.parseInt(argv[3]), true);
		Thread t = new Thread(g);
		t.start();
		System.out.println("Started Thread...\n");
		t.join();
		int winner = g.returnCode();

	
		if(winner==2) logEntry("log.txt","+1:"+argv[0]+"\n-1:"+argv[1]);
		if(winner==0) logEntry("log.txt","+1:"+argv[1]+"\n-1:"+argv[0]);

		g.close();
		System.exit(0);
	}
	
	public static void tournament(int rounds)
	{
		Random rand = new Random();
		int width = 1;
		int height = 1;
		final int V=width*height; // Number of venues
		int N=agents.length;
		int nextAgent = 0;
		int[] score = new int[N];
		int[] roundsPlayed = new int[N];
		Game[] games = new Game[V];
		Thread[] threads = new Thread[V];
		int[] player1 = new int[V];
		int[] player2 = new int[V];
		int toStart = rounds;
		while(rounds > 0)
		{
			try {
				Thread.sleep(50);
			} catch (final Exception e) {
			}
			for(int i=0;i<V;i++)
			{
				if(threads[i]!=null)
					if(!threads[i].isAlive())
					{
						roundsPlayed[player1[i]]++;
						roundsPlayed[player2[i]]++;
						rounds--;
						
						int winner = games[i].returnCode()+1;
						if(winner==2) {
							logEntry("tournament.txt","+1:"+games[i].getStrategyName(0)+"$"+agents[player1[i]]);
							logEntry("tournament.txt","-1:"+games[i].getStrategyName(1)+"$"+agents[player2[i]]);
							score[player1[i]]++;
							score[player2[i]]--;
						}
						if(winner==0) {
							logEntry("tournament.txt","-1:"+games[i].getStrategyName(0)+"$"+agents[player1[i]]);
							logEntry("tournament.txt","+1:"+games[i].getStrategyName(1)+"$"+agents[player2[i]]);
							score[player2[i]]++;
							score[player1[i]]--;
						}
						if(winner==1)
						{
							logEntry("tournament.txt","0:"+games[i].getStrategyName(0)+"$"+agents[player1[i]]);
							logEntry("tournament.txt","0:"+games[i].getStrategyName(1)+"$"+agents[player2[i]]);
						}
						threads[i]=null;
						games[i].destroy();
						
						// Disgustingly crude...
						for(int j=0;j<100;j++) System.out.println();
						for(int j=0;j<N;j++) 
						{
							String name = agents[j];
							if(name.length() > 32) name = name.substring(0,32);
							for(int k=agents[j].length();k<32;k++) System.out.print(" ");
							System.out.println(name+"\t"+score[j]+"\t/\t"+roundsPlayed[j]);
						}
					}
				
				if(threads[i]==null & toStart > 0)
				{
					player1[i] = nextAgent++;
					if(nextAgent==N) nextAgent=0;
					while(true)
					{
						player2[i] = rand.nextInt(N);
						int alt = rand.nextInt(N);
						if(roundsPlayed[alt] < roundsPlayed[player2[i]]) player2[i] = alt;
						alt = rand.nextInt(N);
						if(roundsPlayed[alt] < roundsPlayed[player2[i]]) player2[i] = alt;
						if(player2[i]!=player1[i]) break;
					}
					games[i] = new Game(agents[player1[i]],agents[player2[i]],i%width,i/width, true);
					threads[i] = new Thread(games[i]);
					threads[i].start();
					toStart--;
				}
			}
			
		}
	}
}
