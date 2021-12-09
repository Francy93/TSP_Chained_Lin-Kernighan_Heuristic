

public class Main {
	public static Cities cities;			// object / collection of cities and generated best path
	public static final short EXIT = -1;  	// terminal navigator exit code
	public static final short BACK =  0;	// terminal navigator back code
	public static final short AHEAD=  1;	// terminal navigator ahead code

	// setting the environment
	public static int envSet(){
		System.out.println(Util.colorText("Environment setting", "magenta"));

		switch(Util.navChoice(5,"Colors ON  (VScode)","Colors OFF (Eclipse)")){
			case EXIT: return EXIT;
			case BACK: return BACK;
			case	1: Util.setColor(true);
				break;
			case  	2: Util.setColor(false);
		}
		return AHEAD;
	}

	// initialising the cities object
	public static int readCities(){

		for(boolean loop = false; !loop; loop = !loop){
			System.out.println(Util.colorText("Enter a File name!", "magenta"));
			Util.navOptions(5, "yellow", true);
			
			System.out.print("\r\nEnter here a value:> ");
			String choice = Util.cinln();				// getting the user input from the terminal
			
			switch (choice){
				case "00": return EXIT;
				case "0" : return BACK;
				default:
					cities = new Cities(choice);		// choice is a string and not a key word

					if(!cities.fileFound()){
						loop = true;
						System.out.println(Util.colorText("Please enter a valid file name!\r\n", "yellow"));
					}else if(cities.getCorrupted()>0){	// if any city corrupted found while reading the file
						System.out.println(Util.colorText("Found "+cities.getCorrupted()+" corrupted lines!\r\n", "red"));
					}/* else{
						double bestCompTime = cities.getCompTime();
						int cycles = 1000;
						for(int i=0; i<cycles; i++){
							cities = new Cities(choice);
							bestCompTime = cities.getCompTime() < bestCompTime? cities.getCompTime(): bestCompTime;
						}
						System.out.print(Util.colorText("\r\nBest computation time out of "+cycles+" is: "+bestCompTime+"\r\n", "green"));
					} */
			}
		}
		return AHEAD;
	}





	

	// The main
	public static void main(String[] args){
		Util.setColor(false); // starting without colors in the terminal
		System.out.println(Util.colorText("\r\n\r\nWelcome to theTravelling Salesman Problem\r\n","cyan"));

		// main loop
		for(int nav = envSet();	nav!=EXIT;	nav = nav!=BACK? nav: envSet()){
			nav = nav==BACK? nav: readCities();

			// getting result before and after cycling the generations
			if(nav == AHEAD){
				cities.printResult();
				System.out.println();
			}
		}

		// exiting the main loop
		System.out.println(Util.colorText("Successfully exited!", "green"));
	}
}
