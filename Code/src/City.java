import java.util.ArrayList;

public class City {

	private static int counter =	0;						// object creation counter to assign a id/index
	private static int	 linkedCities;						// number of total linked cityes
	private final double   name, X, Y;						// this city coordinates
	private int			  matrixIndex;						// matrix index of this city to get computed distances
	private boolean corrupted  = false, fullEdges = false;	// mark this object as corrupted if the constructor does not get necessary args
	private ArrayList<City> neighbours = new ArrayList<>();	// this city neighbors (max 2)
	private City  clusterEnd   = this;						// last city of the current cluster of this city
	public City[] 			  closest;						// array of closest cities

	// constructor with correct parameters
	public City(final double n, final double x, final double y){
		name = n;
		X	 = x;
		Y	 = y;

		matrixIndex = counter++;	
		linkedCities = 0;
	}
	// constructor with string
	public City(final String str){
		final String delimiter = " ";
		String line = str.replaceAll("\\s+", delimiter).trim();

		ArrayList<Double> cityAttr = new ArrayList<>();
		for(String s: line.split(delimiter))	cityAttr.add(Double.parseDouble(s));

		if( cityAttr.size() > 2) {
			matrixIndex = counter++;
			name =	cityAttr.get(0);
			X	 =	cityAttr.get(1);
			Y	 =	cityAttr.get(2);
		}else{
			matrixIndex = -1;
			name 		= -1;
			X	 		= -1;
			Y	 		= -1;
			corrupted	= true;
		}

		linkedCities = 0;
	}





	// ....... setter methods ......


	/**
	 * Adding neighbour
	 * @param city
	 */
	private void neighbourAdd(final City that){
		neighbours.add(that);
		fullEdges = getNeighboursQty() >= 2;
		getClusterEnd().clusterEnd = this.getClusterEnd()==that.getClusterEnd()? that: that.getClusterEnd();
	}

	/**
	 * Setting neighbour of both cities
	 * @param city
	 * @return whether the city could be linked
	 */
	private boolean setNeighbours(final City city){
		if(canLinkTo(city)){
			this.neighbourAdd(city);
			city.neighbourAdd(this);
			linkedCities++;
			return true;
		}else return false;
	}

	/**
	 * Linking with the closest city
	 * @return operation status
	 */
	public boolean linkClosest(){
		if(isLinkable()){
			for(int i = 1; i<closest.length; i++){
				if(setNeighbours(getClosest(i))) return true;
			}
		}
		return false;
	}

	// setting neighbor 1
	private void setNeighbour1(final City c){
		neighbours.set(0,c);
	}
	// setting neighbor 2
	private void setNeighbour2(final City c){
		neighbours.set(1,c);
	}

	/**
	 * Replace existing neighbors
	 * @param old
	 * @param NEW
	 */
	public void replaceNeighbour(final City OLD, final City NEW){
		if	   (getNeighbour1() == OLD) setNeighbour1(NEW);
		else if(getNeighbour2() == OLD) setNeighbour2(NEW);
		else if(getNeighbour1() == NEW) setNeighbour1(OLD);
		else if(getNeighbour2() == NEW) setNeighbour2(OLD);
	}



	




	// ....... getter methods ........


	// getting edges status
	private boolean isLinkable(){
		return !fullEdges && !routeComplete();
	}

	/**
	 * Checking whether this city can be linked to the other one
	 * @param city
	 * @return linkable or not
	 */
	private boolean canLinkTo(final City that){
		return this != that && (this != that.getClusterEnd() || linkedCities == closest.length-1) && isLinkable() && that.isLinkable();
	}

	// getting the other end of this city claster
	private City getClusterEnd(){
		return clusterEnd;
	}

	// getting the name of the city at the edge 1
	public City getNeighbour1(){
		return neighbours.get(0);
	}

	// getting the name of the city at the edge 2
	public City getNeighbour2(){
		return neighbours.get(1);
	}

	// getting the city status after reading from the file
	public boolean isCorrupted(){
		return corrupted;
	}

	// getting city id
	public int getMatrixIndex(){
		return matrixIndex;
	}
	
	// getting city name
	public double getName(){
		return name;
	}

	// getting x axis
	public double getX(){
		return X;
	}

	// getting Y axis
	public double getY(){
		return Y;
	}

	// get amount of filled edges
	public int getNeighboursQty(){
		return neighbours.size();
	}

	/**
	 * Get the nearest city
	 * @param n get closest city by rank
	 * @return closest city
	 */
	public City getClosest(int n){
		n = n<1? 1: n;
		return closest[n] != this? closest[n]: closest[n-1];
	}
	
	// return true if the overal routing is complete
	public boolean routeComplete(){
		return linkedCities == closest.length;
	}

	/**
	 * Getting the other next city
	 * @param prevCity
	 * @return next City
	 */
	public City getNextCity(final City prevCity){
		return prevCity == getNeighbour1()? getNeighbour2(): getNeighbour1();
	}

	public void resetCounter(){
		counter = 0;
	}
}