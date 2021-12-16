
import java.util.ArrayList;

/**
 * This class is a algorithms container which generates the final solution:
 * Kruskal-TSP, Chained Lin-Kernighan and Quick-Sort (mono pivot).
 */
public class Algo {
    
	private ArrayList<City>	 finalRoute;		// Array storing the final improved path
	private City[] 		 		 cities;		// Array of Cities
	private City			   baseCity;		// Starting point
    private final Map				MAP;		// Map object, container of cities
	private final int		   N_CITIES;		// Total number of cities
	private final int			MAX_OPT;		// Maximum optimization levels (representing the number of "K-OPT" to carry out)
	private final int 			MAX_LKH;		// Maximum optimization attempts of each "K-OPT" level (Lin-Kernighan Heuristic)
	private final double MIN_GAIN = 0.00001;	// Optimiser (Lin-Kernighan) minimum acceptable gain

	/**
	 * Constructor
	 * @param map
	 * @param kOpt max levels of K-Opt
	 * @param lkh max Lin Kernighan combinations
	 */
	public Algo(final Map map, final int kOpt, final int lkh){
		MAP 		= map;
		cities 		= MAP.getCities();
		N_CITIES 	= MAP.getQuantity();
		MAX_OPT 	= kOpt;
		MAX_LKH	 	= N_CITIES > lkh? lkh: N_CITIES > 3? N_CITIES-3: 0;

		// ......... starting the algorithms .........
        kruskal();		// starting the greedy algorithm for the first tour
		optimiser();	// running the optimiser (Chained Lin-Kernighan Heuristic)
		settingRoute();	// setting the array of the final improved path
    }
    public Algo(final Map m){
		// first parameter is the cities array
		// second one is the number of k-OPT
		// third one is the number of lkh cycles per level
		this(m, 3, 5);	// calling the main contructos
    }






	//................setters..............




	/**
	 * Pushing the result into the "route" array
	 */
	private void settingRoute(){
		this.finalRoute	= new ArrayList<>();
		this.baseCity 	= this.cities[0];
		City city		= this.baseCity, prevCity;

		// copying every city into the "finalRoute" array
		while(this.finalRoute.size() < this.N_CITIES){
			this.finalRoute.add(city);

			prevCity = this.finalRoute.size() > 1? this.finalRoute.get(this.finalRoute.size()-2): city;
			city = city.getNextCity(prevCity);
		}
	}







	// ............algorithms...........


	
	/**
	 * Generating first route with Kruskal Greedy algorithm (local tour)
	 */
	private void kruskal(){
		final ArrayList<City[]>			VERSIONS = new ArrayList<>();										// container of versions
		final Util.Lambda1<City, Double> CENTRAL = (city) ->  MAP.getDistance(city,MAP.getCenterPoint());	// lambda to create a normal kruskal version
		final Util.Lambda1<City, Double> REVERSE = (city) -> -MAP.getDistance(city,	 city.getClosest(1));	// lambda to create a reverse kruskal version

		// generating two baseic path versions to be elaborated by the Kruskal
		VERSIONS.add(Util.quickSort(this.cities.clone()	, CENTRAL	));										// getting a normal kruskal version
		VERSIONS.add(Util.quickSort(this.cities			, REVERSE	));										// getting a reverse kruskal version

		// generating the local tour versions
		for(int i=0; i<VERSIONS.size(); i++){
			// starting the actual greedy algorithm cycle
			for(int j=0; !VERSIONS.get(i)[j].routeComplete(); j = j==this.N_CITIES-1? 0: j+1)	VERSIONS.get(i)[j].linkClosest();
			if(i < VERSIONS.size()-1)  MAP.setNewVersion();													// creating a new version
		}


		// cycling over the versions to choose the best one
		for(double currentVer=VERSIONS.size()-1, bestDist=getRouteDistance(), bestVer=currentVer+1;	currentVer>0;	currentVer--){
			MAP.setVersion((int)currentVer);
			double currentDist = getRouteDistance();

			if(currentDist	< bestDist){
				bestVer		= currentVer;
				bestDist	= currentDist;
			}
			if(currentVer == 1){
				MAP.setVersion((int)bestVer);
				this.cities = VERSIONS.get((int)bestVer-1);
			}
		}
	}


	/**
	 * local tour optimiser (Chained Lin-Kernighan)
	 */
	private void optimiser(){
		final short START_LEVEL = 1, START_SCORE = 0;	// setting strting parameters for LKH
		this.baseCity = this.cities[0];					// getting the first city for LKH

		if(this.N_CITIES > 3 && this.MAX_OPT > 0){
			for(int i=0; i<this.N_CITIES-1;){
				// condition for Chaining (attempting to improve again)
				if(linKernighan(START_LEVEL, START_SCORE));
				else this.baseCity = this.cities[++i];
			}
		}
	}


	/**
	 * Optimising a first local tour with the "Lin Kerninghan Heuristic" (LKH)
	 * @see https://bit.ly/3EpQ6zT
	 * @param currentOPT
	 * @param previousGain
	 * @return whether it found a better route or not
	 */
	private boolean linKernighan(final int CURRENT_OPT, final double PREV_GAIN){
		final Score[] SCORED= new Score[this.N_CITIES-3];

		// ------------------getting best candidates by LKH------------------
		final City PREV_BASE= this.baseCity.getNeighbour2();
		final City NEXT_BASE= this.baseCity.getNeighbour1();
		City prevCity		= NEXT_BASE;
		City tempCity		= prevCity.getNextCity(this.baseCity);
		City prevHolder;

		// getting scores of all the cities
        for(int i=0;	tempCity != PREV_BASE; i++){
			SCORED[i] = new Score(tempCity, prevCity, MAP.getDistance(prevCity,tempCity)-MAP.getDistance(NEXT_BASE,tempCity));
			
			prevHolder	= prevCity;
			prevCity	= tempCity;
			tempCity	= tempCity.getNextCity(prevHolder);
        }
		
		// sorting by score in a decreasing order (the last two paramenters are respectively "left start" and "right end")
        scoreSort(SCORED, 0, SCORED.length-1);


		//-----------------running the K-opt-------------------
		for(int i=0; i<this.MAX_LKH; i++){
			final City ELECTED_CITY = SCORED[i].CITY;
			final City PREV_ELECTED = SCORED[i].PREV_CITY;
	
			final double old1 = MAP.getDistance(this.baseCity,	NEXT_BASE   );
			final double old2 = MAP.getDistance(PREV_ELECTED ,	ELECTED_CITY);
			final double new1 = MAP.getDistance(this.baseCity,	PREV_ELECTED);
			final double new2 = MAP.getDistance(NEXT_BASE	 ,	ELECTED_CITY);
	
			final double GAIN = old1 + old2 - new1 - new2 + PREV_GAIN;

			// flipping the cities
			flip(this.baseCity, NEXT_BASE, PREV_ELECTED, ELECTED_CITY);
			
			// condition to immediately exit the "LKH" function or generate a new "OPT" level (recursion)
			if(GAIN > this.MIN_GAIN || CURRENT_OPT < this.MAX_OPT && linKernighan(CURRENT_OPT+1, GAIN))	return true;
			else flip(this.baseCity, PREV_ELECTED, NEXT_BASE, ELECTED_CITY); // go back to previous state
		}

		return false;
	}


	/**
	 * Flipping the cities
	 * @param prevA
	 * @param a
	 * @param b
	 * @param nextB
	 */
	private void flip(final City prevA, final City a, final City b, final City nextB){
		prevA.replaceNeighbour(a, b);		// exchange the link between prevA and a with prevA and b
		a.replaceNeighbour(prevA, nextB);	// exchange the link between a and prevA with a and prevB

		nextB.replaceNeighbour(b, a);		// exchange the link between nextB and b with nextB and a
		b.replaceNeighbour(nextB, prevA);	// exchange the link between b and nextB with b and prevA
	}


    /**
     * (QuickSort) sorting cities according to their LKH score
     * @see https://bit.ly/3GGQU4W
     * @param arr
     * @param mode (SortMode)
     * @return City[]
     */
	private void scoreSort(final Score[] arr, final int LEFT, final int RIGHT){
		int l	= LEFT, r = RIGHT;

		// getting the pivot (LKH score) from a calculated mid point
		final double PIVOT = arr[(l + r) / 2].SCORE;

		// partition 
		while (l <= r) {
			// loop left index if the current score is greater than the pivot one
			while (arr[l].SCORE > PIVOT) l++;
			// loop right index if the current score is smaller than the pivot one
			while (arr[r].SCORE < PIVOT) r--;

			if (l <= r) {
				final Score tmpNode = arr[l];
				arr[l++] = arr[r];
				arr[r--] = tmpNode;
			}
		}

		// sorting from right to left
		if (LEFT < r )						scoreSort(arr, LEFT,  r);
		// sorting from left to right and terminate the sorting algorithm at the "MAX_LKH"nth sorted element
		if (l < this.MAX_LKH && l < RIGHT)	scoreSort(arr, l, RIGHT);
	}

	




	




    //...........getter methods...........


	/**
	 * Getting the best route
	 * @return the final route arrayList
	 */
    public ArrayList<City> getRoute(){
        return new ArrayList<>(finalRoute);
    }


	// get total distance
	private double getRouteDistance(){
		double distance = 0;

		this.baseCity	= this.cities[0];
		City city		= this.baseCity;
		City prevCity	= this.baseCity.getNeighbour2();
		City prevHolder;

		// copying every city into the "finalRoute" array
		for(int i=0; i< this.N_CITIES; i++){
			distance	+= MAP.getDistance(city, prevCity);

			prevHolder	= prevCity;
			prevCity	= city;
			city		= city.getNextCity(prevHolder);
		}

		return distance;
	}
}