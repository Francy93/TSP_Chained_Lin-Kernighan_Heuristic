
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Cities {

	private final ArrayList<City> cities= new ArrayList<>(); // list of cities
	private ArrayList<City>	  	 route	= new ArrayList<>(); // generated route
	private int		    corruptedLines	= 0;				 // counter of unreadable lines 
	private final double   COMPUT_TIME;						 // total computation time
	private final boolean	FILE_FOUND;						 // found file checker
	private Algo 			  routeGen;						 // route generator

	/**
	 * Cities constructor
	 * @param fileName
	 */
	public Cities(final String fileName){
		// starting the timer
		Util.timeTrack(true);

		FILE_FOUND = fileReader(fileName);

		if(FILE_FOUND){
			// generating an optimal path
			routeGen = new Algo(citiesToArray());
			setRoute();

			// resetting the static cities counter
			cities.get(0).resetCounter();
		}

		// stopping the timer
		COMPUT_TIME = Util.timeTrack(false);
		Util.timeReset();
	}




	// ..... METHODs .....

	/**
	 * Converting the cities list to an array of cities
	 * @return converted array
	 */
	private City[] citiesToArray(){
		City[] citiesArray = new City[cities.size()];

		for(int i=0; i<cities.size(); i++)	citiesArray[i] = cities.get(i);
		return citiesArray;
	}

	/**
	 * Txt file reader
	 * @param fileName
	 * @return whether the file has been found or not
	 */
	private boolean fileReader(final String fileName){
        try{
			BufferedReader scann = new BufferedReader(new FileReader(fileName));
			String line = "";

			while((line = scann.readLine()) != null){
				if(!line.trim().matches("^\\s*([+-]?\\d+([.]\\d+)?\\s+){2}([+-]?\\d+([.]\\d+)?)\\s*$")) continue;
				City city = new City(line);
	
				if(!city.isCorrupted())	cities.add(city);
				else corruptedLines++;
			}
	
			scann.close();

        }catch (IOException e){
            System.out.println(Util.colorText("No file found!", "red"));
			return false;
        }
        
		return cities.size() > 0;
    }

	// setting the route array
	private void setRoute(){
		route = routeGen.getRoute();
		route.add(route.get(0));
	}




	//.....	GETTER METHODs .....

	// getting size
	public int getQuantity(){
		return cities.size();
	}

	// get state
	public boolean fileFound(){
		return FILE_FOUND;
	}

	// get corrupted lines
	public int getCorrupted(){
		return corruptedLines;
	}

	// get route
	public ArrayList<Long> getRoute(){
		ArrayList<Long> intRoute = new ArrayList<>();

		for(City city: route){
			intRoute.add((long)city.getName());
		}

		return intRoute;
	}

	// get total distance
	public double getRouteDistance(){
		double distance = 0;

		for(int i=0; i<route.size()-1; i++){
			distance += routeGen.getDistance(route.get(i), route.get(i+1));
		}

		return distance;
	}

	// get compilation time in milliseconds
	public double getCompTime(){
		return (double)COMPUT_TIME/1000000;
	}

	// print results
	public void printResult(){
		String color = "cyan";

		System.out.println();
		System.out.println(Util.colorText("Cities route:		",	color)	+ getRoute());
		System.out.println(Util.colorText("Route distance:		",	color) 	+ getRouteDistance());
		System.out.println(Util.colorText("Computation (ms):	",	color)	+ getCompTime());
		System.out.println(Util.colorText("Number of cities:	",	color)	+ getQuantity());
		System.out.println();
	}
}