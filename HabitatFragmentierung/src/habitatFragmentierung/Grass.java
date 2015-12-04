package habitatFragmentierung;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;

public class Grass {
	
	private static int nextFreeIdentity = 0;
	private int identity;
	private int maxCapacity;
	private int regrowthRate;
	private int energieLevel;
	
	public Grass(){
		// Get the grass regrow time from the user parameters
		Parameters p = RunEnvironment.getInstance().getParameters();
		regrowthRate = (Integer)p.getValue("grassGrowRate");
		energieLevel = RandomHelper.nextIntFromTo(0, 10);
		identity = nextFreeIdentity++;
	}
	
	@ScheduledMethod(start = 1, interval = 1)
	public void step(){
		energieLevel = energieLevel + regrowthRate;
		if(energieLevel > maxCapacity){
			energieLevel = maxCapacity;
		}
	}
	
	public int eat(int dinner){
		if(dinner > energieLevel){
			dinner = energieLevel;
			energieLevel = 0;
			return dinner;
		} else {
			energieLevel = energieLevel - dinner;
			return dinner;
		}
	}
	
	public boolean isAlive(){
		if(energieLevel > 0) return true;
		else return false;
	}
	
	public int getEnergieLevel(){
		return this.energieLevel;
	}
	
	public int getMaxCapacity(){
		return this.maxCapacity;
	}
	
	public int getIdentity(){
		return this.identity;
	}
	
	public boolean equals(Grass g){
		if(this.getIdentity() == g.getIdentity()) return true;
		else return false;
	}

}
