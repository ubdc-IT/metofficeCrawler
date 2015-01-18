


public class metofficeCrawler {

	/**
	 * @param args
	 */
	private static String api_key = "";
	
	public static void main(String[] args) {
		for (int i=0;i<args.length;i++) {
			api_key = args[i];
		}
		
		long time = System.currentTimeMillis();
		csvWrapper forecastData;
		
		
		forecastData = getForecastData.getForecast();
		if (outputCSV.writetoCSV(forecastData)==1) { 
			System.out.println("output file created");
		}
		
		System.out.println("The execution time : " + (System.currentTimeMillis()-time)/1000 +" Secs.");
	}
	public static String getAPIkey() {
		return api_key;
	}
}
