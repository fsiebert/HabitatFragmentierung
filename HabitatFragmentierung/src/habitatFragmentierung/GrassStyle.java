package habitatFragmentierung;

import java.awt.Color;

import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;
import saf.v3d.scene.VSpatial;


/*
 * Nur aus gegebenen Modell kopiert, um in etwa den Code zu haben, den wir brauchen
 * macht bisher aber noch keine sinn
 * Verschiedene gruenabstufungen denkbar, fuer die verschiedenen energieLevel der grasfelder
 */





public class GrassStyle extends DefaultStyleOGL2D {
	/*
	 * Method for coloring different states
	 */
	@Override
	public Color getColor(Object object) {
		
		if (object instanceof Grass){
			
			Grass g = (Grass) object;
			if( g.isAlive()){		
				return Color.GREEN;	//if alive -> green
			}else{
				return Color.GRAY;	//if dead -> grey
			}
		} else{
			return null;
		}
	}
	/*
	 * Method for the form and size of the grass agent
	 */
	@Override
	public VSpatial getVSpatial(Object agent, VSpatial spatial) {
		
		if (spatial == null) {
	      spatial = shapeFactory.createRectangle(15, 15);
	    }
	    return spatial;
	}

}
