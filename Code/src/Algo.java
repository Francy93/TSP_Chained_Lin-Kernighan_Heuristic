
import java.util.ArrayList;

public class Algo {
    
	private ArrayList<City>	 finalRoute;		// Array storing the final improved path
	private final double[][] costMatrix;		// Array of all the distances values
    private final City[]		 cities;		// Array of Cities
	private City			   baseCity;		// Starting point
	private final int		   N_CITIES;		// Total number of cities
	private final int			MAX_OPT;		// Maximum optimization levels (representing the number of "K-OPT" to carry out)
	private final int 			MAX_LKH;		// Maximum optimization attempts of each "K-OPT" level (Lin-Kernighan Heuristic)
	private final double MIN_GAIN = 0.00001;	// Optimiser (Lin-Kernighan) minimum acceptable gain

	/**
	 * Constructor
	 * @param cities
	 * @param kOpt max levels of K-Opt
	 * @param lkh max Lin Kernighan combinations
	 */
	public Algo(final City[] cities, final int kOpt, final int lkh){
		this.cities = cities;
		N_CITIES 	= cities.length;
		MAX_OPT 	= kOpt;
		MAX_LKH	 	= N_CITIES > lkh? lkh: N_CITIES > 3? N_CITIES-3: 0;

		// ......... starting the algorithms .........
		costMatrix = setMatrix();	// initialising the distances matrix
        kruskal();					// starting the greedy algorithm for the first tour
		optimiser();				// running the optimiser (Chained Lin-Kernighan Heuristic)
		settingRoute();				// setting the array of the final improved path
    }
    public Algo(final City[] c){
		// first parameter is the cities array
		// second one is the number of k-OPT
		// third one is the number of lkh cicles per level
		this(c, 3, 5);	// calling the main contructos
    }






	//................setters..............



	/**
     * Euclidean calculation
     * @param a
	 * @param b
     * @return distance
     */
	private double euclidean(final City a, final City b){
		return Math.sqrt( Math.pow((a.getX() - b.getX()),2) + Math.pow((a.getY() - b.getY()),2) );
	}

	/**
	 * Setting the costs (distances) matrix
	 * @return costMatrix
	 */
	private double[][] setMatrix(){
		double[][] matrix = new double[N_CITIES][N_CITIES];
		int cityA, cityB;

		for(int i=0; i<N_CITIES; i++){
			cityA = cities[i].getMatrixIndex();
			
			for(int j=0; j<N_CITIES; j++){
				cityB = cities[j].getMatrixIndex();
				matrix[cityA][cityB] = euclidean(cities[i], cities[j]);
			}
		}
		return matrix;
	}

	/**
	 * Pushing the result into the "route" array
	 */
	private void settingRoute(){
		finalRoute	= new ArrayList<>();
		baseCity 	= cities[0];
		City city	= baseCity, prevCity;

		// copying every city into the "finalRoute" array
		while(finalRoute.size() < N_CITIES){
			finalRoute.add(city);

			prevCity = finalRoute.size() > 1? finalRoute.get(finalRoute.size()-2): city;
			city = city.getNextCity(prevCity);
		}
	}










	// ............algorithms...........


	/**
	 * Generating first route with Kruskal Greedy algorithm (local tour)
	 */
    private void kruskal(){
		// providing a "closest cities array" to every city
        for(City cityA: cities)	cityA.closest = Util.quickSort(cities.clone(), (cityB) -> getDistance(cityA, cityB));
		// the way of sorting "cities" will determine if the Kruscal algorithm will run normal or reverse side
        Util.quickSort(cities, (city) -> getDistance(city, city.getClosest(1)));

		// starting the greedy algorithm cycle
        for(int i=0; !cities[i].routeComplete(); i = i==N_CITIES-1? 0: ++i)		cities[i].linkClosest();
	}


	/**
	 * local tour optimiser (Chained Lin-Kernighan)
	 */
	private void optimiser(){
		final short startLevel = 1, startScore = 0;	// setting strting parameters for LKH
		baseCity = cities[0];						// getting the first city for LKH

		if(N_CITIES > 3 && MAX_OPT > 0){
			for(int i=0; i<N_CITIES-1;){
				// condition for Chaining (attempting to improve again)
				if(linKernighan(startLevel, startScore));
				else baseCity = cities[++i];
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
		final Score[] scored= new Score[N_CITIES-3];

		// ------------------getting best candidates by LKH------------------
		final City prevBase = baseCity.getNeighbour2();
		final City nextBase = baseCity.getNeighbour1();
		City prevCity		= nextBase;
		City tempCity		= prevCity.getNextCity(baseCity);
		City prevHolder;

		// getting scores of all the cities
        for(int i=0;	tempCity != prevBase; i++){
			scored[i] = new Score(tempCity, prevCity, getDistance(prevCity,tempCity)-getDistance(nextBase,tempCity));
			
			prevHolder	= prevCity;
			prevCity	= tempCity;
			tempCity	= tempCity.getNextCity(prevHolder);
        }
		
		// sorting by score in a decreasing order (the last two paramenters are respectively "left start" and "right end")
        scoreSort(scored, 0, scored.length-1);


		//-----------------running the K-opt-------------------
		for(int i=0; i<MAX_LKH; i++){
			final City electedCity = scored[i].city;
			final City prevElected = scored[i].prevCity;
	
			final double old1 = getDistance(baseCity	,	nextBase   );
			final double old2 = getDistance(prevElected	,	electedCity);
			final double new1 = getDistance(baseCity	,	prevElected);
			final double new2 = getDistance(nextBase	,	electedCity);
	
			final double gain = old1 + old2 - new1 - new2 + PREV_GAIN;

			// flipping the cities
			flip(baseCity, nextBase, prevElected, electedCity);
			
			// condition to immediately exit the "LKH" function or generate a new "OPT" level (recursion)
			if(gain > MIN_GAIN || CURRENT_OPT < MAX_OPT && linKernighan(CURRENT_OPT+1, gain))	return true;
			else flip(baseCity, prevElected, nextBase, electedCity); // go back to previous state
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
		final double PIVOT = arr[(l + r) / 2].score;

		// partition 
		while (l <= r) {
			// loop left index if the current score is greater than the pivot one
			while (arr[l].score > PIVOT) l++;
			// loop right index if the current score is smaller than the pivot one
			while (arr[r].score < PIVOT) r--;

			if (l <= r) {
				final Score tmpNode = arr[l];
				arr[l++] = arr[r];
				arr[r--] = tmpNode;
			}
		}

		// sorting from right to left
		if (LEFT < r )					scoreSort(arr, LEFT,  r);
		// sorting from left to right and terminate the sorting algorithm at the "MAX_LKH"nth sorted element
		if (l < MAX_LKH && l < RIGHT)	scoreSort(arr, l, RIGHT);
	}

	




	




    //...........getter methods...........

	/**
	 * Getting the distance between two given cities
	 * @param a first city
	 * @param b second city
	 * @return euclidean distance
	 */
	public double getDistance(final City a, final City b){
		return costMatrix[a.getMatrixIndex()][b.getMatrixIndex()];
	}

	/**
	 * Getting the best route
	 * @return the final route arrayList
	 */
    public ArrayList<City> getRoute(){
        return new ArrayList<>(finalRoute);
    }
}