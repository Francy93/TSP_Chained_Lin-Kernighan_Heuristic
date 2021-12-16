
import java.util.Scanner;
import java.util.ArrayList;

/**
 * This class is a collection of various tools
 */
public class Util{
	public static Scanner cin = new Scanner(System.in);

	/**
	 * Lambda function with just a parameter
	 */ 
	public static interface Lambda1<T, V>{
        public V op(T a);
    }
	public static interface Lambda2<T, V>{
        public V op(V a, T b);
    }

	//determining whether the text get colored or not
	private static boolean colorState = true, timerOnOff;
	private static ArrayList<Long> compTime = new ArrayList<>();

	public static void setColor(boolean b){ colorState = b; }
	public static boolean colorState(){ return colorState;  }
    
	/**
	 * Benchmarking the computation time
	 * @param onOff
	 * @return current time
	 */
	public static double timeTrack(boolean onOff){
		long time = System.nanoTime();

		if(onOff && !timerOnOff)					compTime.add(time);
		else if(compTime.size() > 0 && timerOnOff)	compTime.set(compTime.size()-1, time - compTime.get(compTime.size()-1));
		else 							return 0;

		timerOnOff = onOff;
		return compTime.get(compTime.size()-1);
	}

	// getting the time collected so far
	public static long getTime(){
		long totalTime = 0;
		for(long n: compTime)	totalTime += n;
        return totalTime;
    }

	// setting back the time to zero 0
	public static void timeReset(){
		compTime.clear();
    }


	/**
	 * Generate a string repetition
	 * @param s
	 * @param n
	 * @return
	 */
	public static String StringRepeat(String s, long n){
		String str = "";
		for(long i=0; i<n; i++) 	str += s;
		return str;
	}


	/**
	 * detect if a string is a number
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str){ 
		try{  
			Double.parseDouble(str);  
			return true;
		}catch(NumberFormatException e){ return false; }
	}

	
	/**
	 * Sum values of array
	 * @param array
	 * @return sum
	 */
	public static class ArrayMath {

		public static					 double plus (int[]			array)	{ return sum(array, (a,b) -> a+b); }
		public static					 double minus(int[]			array)	{ return sum(array, (a,b) -> a-b); }
		public static					 double plus (double[]		array)	{ return sum(array, (a,b) -> a+b); }
		public static					 double minus(double[]		array)	{ return sum(array, (a,b) -> a-b); }
		public static					 double plus (long[]		array)	{ return sum(array, (a,b) -> a+b); }
		public static					 double minus(long[]		array)	{ return sum(array, (a,b) -> a-b); }
		public static <T extends Number> double plus (T[]			array)	{ return sum(array, (a,b) -> a+b.doubleValue()); }
		public static <T extends Number> double minus(T[]			array)	{ return sum(array, (a,b) -> a-b.doubleValue()); }
		public static <T extends Number> double plus (ArrayList<T>	array)	{ return sum(array, (a,b) -> a+b.doubleValue()); }
		public static <T extends Number> double minus(ArrayList<T>	array)	{ return sum(array, (a,b) -> a-b.doubleValue()); }
		public static double sum(int[] array, Lambda2<Integer, Double> comput){
			double result = array[0];
			for(int i=1; i<array.length; i++)   result = comput.op(result, array[i]);
			return result;
		}
		public static double sum(double[] array, Lambda2<Double, Double> comput ){
			double result = array[0];
			for(int i=1; i<array.length; i++)   result = comput.op(result, array[i]);
			return result;
		}
		public static double sum(long[] array, Lambda2<Long, Double> comput ){
			double result = array[0];
			for(int i=1; i<array.length; i++)   result = comput.op(result, array[i]);
			return result;
		}
		public static <T extends Number> double sum(ArrayList<T> array, Lambda2<T, Double> comput ){
			double result = array.get(0).doubleValue();
			for(int i=1; i<array.size(); i++)   result = comput.op(result, array.get(i));
			return result;
		}
		public static <T extends Number> double sum(T[] array, Lambda2<T, Double> comput ){
			double result = array[0].doubleValue();
			for(int i=1; i<array.length; i++)   result = comput.op(result, array[i]);
			return result;
		}
	}

	/**
	 * Average finder
	 * @param array
	 * @return the average
	 */
	public static int arrayAvarage(double[] array){
		int index = 0;
		double difference = 0;
		double mean = ArrayMath.plus(array) / array.length;

		for(int i=0; i<array.length; i++){
			double sub = mean <= array[i]? array[i] - mean: mean - array[i];

			if(i==0 || difference > sub){
				difference = sub;
				index = i;
			}
		}
		return index;
	}


	/**
	 * Logic operator xor
	 * @param x
	 * @param y
	 * @return xor
	 */
	public static boolean xor(boolean x, boolean y){
		return ( ( x || y ) && ! ( x && y ) );
	}


	/**
	 * Round adjusting decimals
	 * @param n number to be rounded
	 * @param d quantity of decimals to be left
	 * @return rounded
	 */
	public static double round(double n, int d){
		double round = Math.pow(10, d>=0? d: 0);
		return Math.round(n*round)/round;
	}


	/**
	 * Get ANSI code for colored text
	 * @param color
	 * @return ASCII code
	 */
	public static String color(String color){
		if(colorState){
			color = color.toLowerCase();

			String[] colors = {"black","red","green","yellow","blue","magenta","cyan","white"};
			if(color.equals("reset"))	return "\u001B[0m";

			for(int i=0; i<colors.length; i++){
				if(color.equals(colors[i]))		return "\u001B[3"+i+"m";
			}
		}

		return "";
	}


	/**
	 * Get colored text
	 * @param text
	 * @param color
	 * @return colored string (ANSI)
	 */
	public static String colorText(String text, String color){
		return color(color) + text + color("reset");
	}

	/**
     * Checking whether a number exists already in the array
     * @param arr
     * @param b
     * @return boolean
     */
    public static boolean exists(int[] arr, int b){
        for(int a: arr){
            if(a==b) return true;
        }
        return false;
    }

	/**
     * Generate an integer within a range
     * @param min
     * @param max
     * @return int
     */
    public static double rangeRandom(double min, double max){
		min = min<max?min:max;
		max = min<max?max:min;
        return Math.round((Math.random() * (max - min)) + min);
    }

	// append arrays
	public static <T> T[] append(T[] destination, T[] array) {
		for(int i=0; i<array.length; i++) destination[i] = array[i];
		return destination;
	}

	/**
	 * Returns a string of enumerated options
	 * @param opts
	 * @param minSize
	 * @param colors
	 * @param print
	 * @return String[]
	 */
	public static String[] navOptions(long minSize, String colors, boolean print, String ... opts){
		String[] options = new String[opts.length + 2];
		append(options, opts);

        final long oSize = opts.length;
        long iSize = 2, i = 0, longest = 7;

        // getting the longest string size
        for(String o: opts){
            final long strSize = o.length();
            longest = strSize > longest? strSize: longest;
            if(++i == oSize) iSize = Long.toString(i).length() > iSize? Long.toString(i).length(): iSize;
        }

        String cStart = color(colors);					//yellow corresponds to: "\033[1;35m"
        String cEnd = cStart==""? "": color("reset");	//reset  corresponds to: "\033[0m"

        longest = longest+3>=(double)minSize-iSize? longest+3: minSize-iSize;
        i = 0;
        for(String o: opts){
            final long indexSize = iSize - Long.toString(++i).length();
            final long gap = longest - o.length();
            String index = cStart + Long.toString(i) + cEnd;

            options[(int)i-1] = o + StringRepeat(".", gap+indexSize) + index;
        }
        options[opts.length]	= "Go back" + StringRepeat(".",longest-7+iSize-1) + cStart+"0"+cEnd;
        options[opts.length+1]	= "Exit"    + StringRepeat(".",longest-4+iSize-2) + cStart+"00"+cEnd;

        // printing
        if(print) for(String line: options) System.out.println(line);
        return options;
    }


	// getting console input
	public static String cinln(){
        String input = cin.nextLine();
        System.out.println();
        return input;
    }


    /**
     * @since getting user input
     * @param max
     * @return int
     */
    public static int getChoice(int options){
        options = options<2?1: options;
		
		//checking the choice
		String input ="";
		
		while(true){
			System.out.print("Enter a choice here :> ");
			input = cinln();
			
			if(!input.equals("0") && !input.equals("00")){
				for(int i=1; i<=options; i++){
					if(input.equals(Integer.toString(i))) return i;
				}
				System.out.println(colorText("WRONG SELECTION! Try again.", "yellow"));
			}else if(input.equals("0")) return 0;
			else return -1;
		}
	}

	/**
         * @since display options and return choice
         * @param options 
         * @param min
         * @return int 
         */
	public static int navChoice(long min, String ... options){
        
        //displaying options
		navOptions(min, "yellow", true, options);
		System.out.println();
		//getting the choice
		return getChoice(options.length);
    }


	/**
     * (QuickSort) sorting elements according to their specific values
     * @see https://bit.ly/3GGQU4W
     * @param arr
     * @param mode (SortMode)
     * @return City[]
     */
	public static <T	 extends Comparable<T>> T[] quickSort(final T[] arr){
        if(arr.length > 1) return new QuickSort<T,T>(arr, true, (a) -> a).getSorted();
		else return arr;
    }
	public static <T	 extends Comparable<T>> T[] quickSort(final T[] arr, final boolean mode){
        if(arr.length > 1) return new QuickSort<T,T>(arr, mode, (a) -> a).getSorted();
		else return arr;
    }
    public static <T, V  extends Comparable<V>> T[] quickSort(final T[] arr, final Lambda1<T, V> compare){
		if(arr.length > 1) return new QuickSort<T,V>(arr, true, compare).getSorted();
		else return arr;
    }

	private static class QuickSort<T, V extends Comparable<V>>{

		private final boolean MODE;				// this is the direction the array get sorted
		private final Lambda1<T, V> compare;	// this is what of the array has to be compared
		private final T[] array;				// this is the array to be sorted

		/**
		 * (QuickSort) sorting elements according to their specific values
     	 * @see https://bit.ly/3GGQU4W
		 * @param a array
		 * @param m mode
		 * @param c lambda method
		 */
		private QuickSort(final T[] a, final boolean m, final Lambda1<T, V> c){
			array	= a;
			MODE	= m;
			compare = c;
			quickSort(0, array.length-1);
		}

		private T[] quickSort(final int left, final int right){
			int l	= left, r = right; 

			// getting the pivot from a calculated mid point
			final var PIVOT = compare.op(array[(l + r) / 2]);

			// partition 
			while (l <= r) {
				// loop left index if the current element is smaller or greater than pivot
				while (MODE? compare.op(array[l]).compareTo(PIVOT) < 0: compare.op(array[l]).compareTo(PIVOT) > 0)	l++;
				// loop right index if the current element is greater or smaller than pivot
				while (MODE? compare.op(array[r]).compareTo(PIVOT) > 0: compare.op(array[r]).compareTo(PIVOT) < 0)	r--;

				if (l <= r) {
					final T tmpNode	= array[l];
					array[l++]	= array[r];
					array[r--]	= tmpNode;
				}
			}

			// recursion
			if (left < r ) quickSort(left,  r);
			if (l < right) quickSort(l, right);

			return array;
		}

		/**
		 * Getting the sorted array
		 * @return sorted array
		 */
		public T[] getSorted(){
			return array;
		}
	}
}