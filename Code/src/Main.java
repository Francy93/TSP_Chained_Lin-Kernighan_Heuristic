
public class Main {
	public static Cities cities;
	public static final short EXIT = -1;
	public static final short BACK =  0;
	public static final short AHEAD=  1;

	// setting the environment
	public static int envSet(){
		System.out.println(Util.colorText("Environment setting", "magenta"));

		switch(Util.navChoice(5,"Colors ON  (VScode)","Colors OFF (Eclipse)")){
			case EXIT: return EXIT;
			case BACK: return BACK;
			case	1: Util.setColor(true);
				break;
			case  2: Util.setColor(false);
		}
		return AHEAD;
	}

	// initialising the cities object
	public static int readCities(){

		for(boolean loop = false; !loop; loop = !loop){
			System.out.println(Util.colorText("Enter a File name!", "magenta"));
			Util.navOptions(5, "yellow", true);
			
			System.out.print("\r\nEneter here a value:> ");
			String choice = Util.cinln();
			
			switch (choice){
				case "00": return EXIT;
				case "0" : return BACK;
				default:
					cities = new Cities(choice);

					if(!cities.fileFound()){
						loop = true;
						System.out.println(Util.colorText("Please enter a valid file name!\r\n", "yellow"));
					}else if(cities.getCorrupted()>0){
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
		Util.setColor(false);
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
