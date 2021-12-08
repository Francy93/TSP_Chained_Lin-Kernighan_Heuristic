
import java.util.ArrayList;

public class Algo {
    
	private ArrayList<City>	 finalRoute;		// Array storing the final improved path
	private final double[][] costMatrix;		// Array of all the distances values
    private final City[]		 cities;		// Array of Cities
	private City			   baseCity;		// Starting point
	private final int		   N_CITIES;		// Total number of cities
	private final int			MAX_OPT;		// Maximum optimization recursions (representing the "K-OPT" of Lin-Kernighan Heuristic)
	private final int 			MAX_LKH;		// Maximum optimization attempts of a sequence (Lin-Kernighan Heuristic)
	private final double MIN_GAIN = 0.00001;	// Optimiser (Lin-Kernighan) minimum acceptable gain

	/**
	 * Constructor
	 * @param cities
	 * @param kOpt max levels of K-Opt
	 * @param lkh mex Lin Kernighan combinations
	 */
	public Algo(final City[] cities, final int kOpt, final int lkh){
		this.cities = cities;
		N_CITIES 	= cities.length;
		MAX_OPT 	= kOpt;
		MAX_LKH	 	= N_CITIES > lkh? lkh: N_CITIES > 3? N_CITIES-3: 0;

		// initialising the distances matrix
		costMatrix = setMatrix();
		
		// starting the greedy algorithm for the first tour
        localTour();
		baseCity = cities[0];

		// running the optimiser (Chained Lin-Kernighan Heuristic)
		optimiser();

		// setting the array of the final improved path
		settingRoute();
    }
    public Algo(final City[] c){
		this(c, 3, c.length-3 >= 5? 5: c.length-3);
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
	 * Setting the costes (distances) matrix
	 * @return costMatrix
	 */
	private double[][] setMatrix(){
		double[][] matrix = new double[N_CITIES][N_CITIES];
		int cityA, cityB;

		for(int i=0; i<N_CITIES; i++){
			cityA = cities[i].getMatrixIndex();
			for(int j=0; j<N_CITIES; j++){
				cityB = cities[j].getMatrixIndex();
				matrix[cityA][cityB] = euclidean(cities[i],	  cities[j]);
			}
		}
		return matrix;
	}

	//     ---   6 23 9 2 5 17 30 22 7 4 32 11 20 28 10 26 16 8 24 21 27 13 15 19 14 29 25 1 12 18 3 31
	/**
	 * Pushing the result into the "route" array
	 */
	private void settingRoute(){
		finalRoute = new ArrayList<>();
		City city = baseCity, prevCity;

		// copying every city into the "finalRoute" array
		while(finalRoute.size() < N_CITIES){
			finalRoute.add(city);

			prevCity = finalRoute.size() > 1? finalRoute.get(finalRoute.size()-2): city;
			city = city.getNextCity(prevCity);
		}
	}










	// ............algorithms...........


	/**
	 * Generating first route with a Greedy algorithm
	 */
    private void localTour(){
		// providing a "closest cities array" to every city
        for(City city: cities)	city.closest = Util.quickSort(cities.clone(), (c) -> getDistance(c, city));
        Util.quickSort(cities, (c) -> getDistance(c, c.getClosest(1)));

		// starting the greedy algorithm cicle
        for(int i=0; !cities[i].routeComplete(); i = i==N_CITIES-1? 0: ++i){
			cities[i].linkClosest();
        }
	}


	/**
	 * local tour optimiser (Chained Lin-Kernighan)
	 */
	private void optimiser(){
		final short startLevel = 1, startScore = 0;

		if(N_CITIES > 3 && MAX_OPT > 0){
			for(int i=0; i<N_CITIES-1;){
				// condition for Chaining (attempting to improve again)
				if(linKernighan(startLevel, startScore));
				else baseCity = cities[++i];
			}
		}
	}


	/**
	 * Optimising the first local tour with the "Lin Kerninghan Heuristic" (LKH)
	 * @see https://bit.ly/3EpQ6zT
	 * @param currentOPT
	 * @param previousGain
	 * @return whether it found a better route or not
	 */
	private boolean linKernighan(final int currentOPT, final double prevGain){
		final Score[] scored= new Score[N_CITIES-3];

		// ------------------getting best candidates by LKH------------------
		final City prevBase = baseCity.getNeighbour2();
		final City nextBase = baseCity.getNeighbour1();
		City prevCity		= nextBase;
		City tempCity		= prevCity.getNextCity(baseCity);
		City prevHolder;

		// getting scores of all the cities
        for(int i=0;	tempCity != prevBase;){
			scored[i++] = new Score(tempCity, prevCity, getDistance(prevCity,tempCity)-getDistance(nextBase,tempCity));
			
			prevHolder	= prevCity;
			prevCity	= tempCity;
			tempCity	= tempCity.getNextCity(prevHolder);
        }
		
		// sorting by score in a decreasing order
        scoreSort(scored, 0, scored.length-1);


		//-----------------running the K-opt-------------------
		for(int i=0; i<MAX_LKH; i++){
			final City electedCity = scored[i].city;
			final City prevElected = scored[i].prevCity;
	
			final double old1 = getDistance(baseCity,		nextBase);
			final double old2 = getDistance(prevElected,	electedCity);
			final double new1 = getDistance(baseCity,		prevElected);
			final double new2 = getDistance(nextBase,		electedCity);
	
			final double gain = old1 + old2 - new1 - new2 + prevGain;

			// flipping the cities
			flip(baseCity, nextBase, prevElected, electedCity);
			
			// condition to immediately exit the "LKH" function or genearete a new "OPT" level (recursion)
			if(gain > MIN_GAIN || currentOPT < MAX_OPT && linKernighan(currentOPT+1, gain))	return true;
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
		prevA.replaceNeighbour(a, b);
		a.replaceNeighbour(prevA, nextB);

		nextB.replaceNeighbour(b, a);
		b.replaceNeighbour(nextB, prevA);
	}


    /**
     * (QuickSort) sorting cities according to their LKH score
     * @see https://bit.ly/3GGQU4W
     * @param arr
     * @param mode (SortMode)
     * @return City[]
     */
	private void scoreSort(final Score[] arr, final int left, final int right){
		int l	= left, r = right;

		// getting the pivot (LKH score) from the mid point
		final double PIVOT = arr[(l + r) / 2].score;

		// partition 
		while (l <= r) {
			// loop left index if the current score is greater than the pivot one
			while (arr[l].score > PIVOT) l++;
			// loop right index if the current score is smoller than the pivot one
			while (arr[r].score < PIVOT) r--;

			if (l <= r) {
				final Score tmpNode = arr[l];
				arr[l++] = arr[r];
				arr[r--] = tmpNode;
			}
		}

		// sorting from right to left
		if (left < r )					scoreSort(arr, left,  r);
		// sorting from left to right and terminate the sorting algorithm at the "MAX_LKH"th sorted element
		if (l < MAX_LKH && l < right)	scoreSort(arr, l, right);
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