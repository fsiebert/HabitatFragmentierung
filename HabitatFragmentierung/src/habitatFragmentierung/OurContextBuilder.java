package habitatFragmentierung;

import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.RandomGridAdder;

public class OurContextBuilder implements ContextBuilder<Object> {

	@Override
	public Context<Object> build(Context<Object> context) {
		int xdim = 50;   // The x dimension of the physical space
		int ydim = 50;   // The y dimension of the physical space

		// Create a new 2D grid to model the discrete patches of grass.  The inputs to the
		// GridFactory include the grid name, the context in which to place the grid,
		// and the grid parameters.  Grid parameters include the border specification,
		// random adder for populating the grid with agents, boolean for multiple occupancy,
		// and the dimensions of the grid.
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Object> grid = gridFactory.createGrid("Simple Grid", context,
				new GridBuilderParameters<Object>(new repast.simphony.space.grid.WrapAroundBorders(),
						new RandomGridAdder<Object>(), true, xdim, ydim));
		
		//SimpleGridAdder + doppelte For-Schleife
		
		

		// Create a new 2D continuous space to model the physical space on which the sheep
		// and wolves will move.  The inputs to the Space Factory include the space name, 
		// the context in which to place the space, border specification,
		// random adder for populating the grid with agents,
		// and the dimensions of the grid.
		ContinuousSpaceFactory spaceFactory = ContinuousSpaceFactoryFinder.createContinuousSpaceFactory(null);
		ContinuousSpace<Object> space = spaceFactory.createContinuousSpace("Continuous Space", context, new RandomCartesianAdder<Object>(),
				new repast.simphony.space.continuous.WrapAroundBorders(), xdim, ydim, 1);

		// The environment parameters contain the user-editable values that appear in the GUI.
		//  Get the parameters p and then specifically the initial numbers of agents.
		Parameters p = RunEnvironment.getInstance().getParameters();
		int numAgents = (Integer)p.getValue("initialNumberOfAgents");
		
		
		
		
		
		/*
		 * Hier muessen wir ueberlegen, welche Parameter wir zum Erzeugen der Agenten brauchen
		 * bzw, ob wir diese Vorgeben, oder ob wir diese zufaellig erzeugen wollen.
		 */
				
		double comfortRadius = (Double) p.getValue("comfortRadius");
		double sightRange = (Double) p.getValue("sightRange");
		double maxMoveRange = (Double) p.getValue("maxMoveRange");
		double gainFromFood = (Double) p.getValue("gainFromFood");

		// Populate the root context with the initial agents
		// Iterate over the number of wolves
		for (int i = 0; i < numAgents; i++) {
			Agent agent = new Agent(sightRange, maxMoveRange, gainFromFood, comfortRadius);  // create a new agent
			context.add(agent);                  	// add the new agent to the root context
		}
		
		// Populate the patch grid with grass
		// Iterate over the dimensions of the patch grid
		for (int i=0; i<xdim; i++){
			for (int j=0; j<ydim; j++){
				Grass grass = new Grass();				// create a new grass
				context.add(grass);
				grid.moveTo(grass, i, j);
				space.moveTo(grass, i+0.5, j+0.5, 0);
			}
		}
		
		context.add(new GrassFinder());

		return context;                       

	}

}
