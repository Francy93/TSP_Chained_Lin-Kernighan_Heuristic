
public class Score {
    public final City	   city;	// city owner of this score
    public final City  prevCity;	// city behind this one
    public final double   score;	// score assigned to this city

	/**
	 * Score assignment to a specified city by Lin-Kernighan algorithm
	 * @param c city
	 * @param p previous city
	 * @param sc assigned score
	 */
    public Score(final City c, final City p, final double sc){
		city 	  =  c;
		prevCity  =  p;
		score 	  = sc;
    }
}
