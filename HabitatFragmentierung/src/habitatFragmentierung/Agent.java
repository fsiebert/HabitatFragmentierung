package habitatFragmentierung;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.collections.IndexedIterable;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;

public class Agent {
	
	private static final int ESSEN = 0;
	private static final int PAARUNG = 1;
	private static final int KAMPF = 2;
	
	private int nextAktion;
	private int age;
	private int maxEnergie = 10;
	private int energieLevel; //Anfangsverteilung? alles gleiches level oder zufaellig
	private int brunftCountdown = 5;
	
	private boolean gender;
	private boolean brunft;
	
	private double sightRange;		//Sicht- und Bewegungsfeld
	private double maxMoveRange;	//Aendern in: maxMoves - Maximale Anzahl der Schritte pro Step
	private double gainFromFood;
	private double comfortRadius;	//Aendern in: comfortLevel - Ist Level niedrig, wird gekaempft. Bei hohem Level hohe Toleranz.
	private double heading;
	
	public Agent(double sR, double mMR, double gFF, double cR){
		sightRange = sR;
		maxMoveRange = mMR;
		gainFromFood = gFF;
		comfortRadius = cR;
		age = RandomHelper.nextIntFromTo(1, 51);
		energieLevel = RandomHelper.nextIntFromTo(1, 11);
		if(RandomHelper.nextDoubleFromTo(0, 1) < 0.5) gender = true;
		else gender = false;
		heading = RandomHelper.nextDoubleFromTo(0, Math.PI*2);
	}
	
	@ScheduledMethod(start = 1, interval = 1)
	public void step(){
		brunftZeit();
		//if(brunft) nextAktion = partnerSuche();
		//else nextAktion = nahrungsSuche();
		move();
	}
	
	/**
	 * Prueft, ob bereits <>brunftCountdown<> Zeitschirtte 
	 * seit der letzten Paarung vergangen sind und setzt
	 * <>brunft<> gegebenenfalls auf true
	 */
	private void brunftZeit(){
		if(brunftCountdown == 0) brunft = true;		//Methode paarung() muss brunft = false setzten
		else brunftCountdown--;
	}

	/**
	 * Sucht das optimale Nahrungsfeld in der Umgebung
	 * Wird ein passendes gefunden, geht es essen,
	 * sont wird gekaempft
	 * 
	 * @return naechsteAktion - Integerwert fuer naechste Aktion
	 */
	private int nahrungsSuche(){
		//eigene Position auf dem Grid bestimmen
		Context<Object> context = ContextUtils.getContext(this);
		Grid<Object> grid = (Grid<Object>) context.getProjection("Simple Grid");
		ContinuousSpace<Object> space = (ContinuousSpace<Object>) context.getProjection("Continuous Space");
		NdPoint ownLocation = space.getLocation(this);
		double xOwn = ownLocation.getX();
		double yOwn = ownLocation.getY();
		
		IndexedIterable<Object> whoat =  context.getObjects(GrassFinder.class);
		GrassFinder grassFinder = (GrassFinder) whoat.get(0);
		NdPoint gFLocation;
		
		Grass[] bestGrass = new Grass[5];
		
		//Suche beste Nahrungsfelder
		//In die Entfehrung schauen
		for(double i=0; i<sightRange; i+= 0.7){
			//Von links nach rechts schauen
			for(double j=this.getHeading()-Math.PI/4; j<=this.getHeading()+Math.PI/4; j+=Math.PI/(4*((int)i+1))){
				//Graskoordinaten bestimmen
				space.moveByVector(grassFinder, i, j);
				gFLocation = space.getLocation(grassFinder);
				int checkForX = (int)gFLocation.getX();
				int checkForY = (int)gFLocation.getY();
				
				//Grassfeld anschauen und Energielevel bestimmen
				Grass g = (Grass)grid.getObjectAt(checkForX, checkForY);
				bestGrass = betterGrass(bestGrass, g);
			}
		}
		
		/*
		 * Jetzt hab ich alle potentiellen Grassstellen
		 * Als naechstes muss die beste Stelle herausgesucht werden, um dann
		 * deren Umgebung zu pruefen. Ist diese ok, soll eat() aufgerufen werden.
		 * Ist diese nicht ok, soll sie im (hilfs)array genullt werden, um unter
		 * den restlichen Plaetzen den besten zu bestimmt, solange noch welche
		 * uebrig sind. Sind keine mehr uebrig, soll er kaempfen.
		 */
		
		
		return KAMPF;
	}

	/**
	 * Bekommt die Koordinaten oder den Zielpunkt und bewegt den Agenten
	 * 
	 */
	private void move(){
		Context<Object> context = ContextUtils.getContext(this);
		
		Grid<Object> grid = (Grid<Object>) context.getProjection("Simple Grid");
		
		ContinuousSpace<Object> space = (ContinuousSpace<Object>) context.getProjection("Continuous Space");
		
		// Randomly change the current heading plus or minus PI/4 radians (45 degree)
		this.setHeading(this.getHeading() + RandomHelper.nextDoubleFromTo(- Math.PI/4 , Math.PI/4));
				
		// Move the agent on the space by one unit according to its new heading
		space.moveByVector(this, 1, this.getHeading(),0,0);		
	}
	
	private void setHeading(double hd){
		this.heading = hd;
	}
	
	private double getHeading(){
		return this.heading;
	}
	
	private Grass[] betterGrass(Grass[] array, Grass g){
		int min=array[0].getEnergieLevel();
		int index=0;
		//suche kleinsten energieLevel
		for(int i=1; i<array.length; i++){
			if(array[i].getEnergieLevel() < min && !g.equals(array[i])){
				min = array[i].getEnergieLevel();
				index = i;
			}
		}
		//Wenn kleinster kleiner ist als der neue, besseres Grass gefunden
		if(min < g.getEnergieLevel()) array[index] = g;
		return array;
	}
}
