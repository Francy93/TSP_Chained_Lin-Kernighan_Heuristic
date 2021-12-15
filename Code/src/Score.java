
/**
 * This class is a simple and small scores container.
 */
public class Score {
    public final City	   CITY;	// city owner of this score
    public final City PREV_CITY;	// city behind this one
    public final double   SCORE;	// score assigned to this city

	/**
	 * Score assignment to a specified city by Lin-Kernighan algorithm
	 * @param C city
	 * @param P previous city
	 * @param SC assigned score
	 */
    public Score(final City C, final City P, final double SC){
		CITY		=  C;
		PREV_CITY	=  P;
		SCORE		= SC;
    }
}
