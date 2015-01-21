import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;






public class metofficeCrawler {

	/**
	 * @param args
	 */
	
	private static String api_key = "";
	private static File log;
	private static crawlTypes crawlType;

	public static void main(String[] args) {
		
		long time = System.currentTimeMillis();
		
		String usage = "metOfficeCrawler [api-key] [crawlType]\n"+
				"api-key\t\tMet Office datapoint key\n"+
				"crawlType\t\t'forecast','observation'or'image'";
		
		DateFormat tsLogFormat = DateFormat.getDateTimeInstance();
		Date tsLog = new Date(time);
		log = new File("CrawlerLog.log");
		PrintWriter q;
		try {
			q = new PrintWriter(new BufferedWriter(new FileWriter(log, true)));
			q.println(tsLogFormat.format(tsLog)+" : "+"Crawler started");
			if (args.length == 0) {
					System.out.println(usage);
					System.exit(0);
			}
			for (int i=0;i<args.length;i++) {
				if (i==0) {
					api_key = args[i];
				} else if (i==1) {
					if (args[i].equals("forecast")) {
						crawlType = metofficeCrawler.crawlTypes.F;
					} else if (args[i].equals("observation")) {
						crawlType = metofficeCrawler.crawlTypes.O;
					} else if (args[i].equals("image")) {
						crawlType = metofficeCrawler.crawlTypes.I;
					} else {
						System.out.println("no valid crawlType given");
						System.exit(0);
					}
				}
			}
			q.println(tsLogFormat.format(tsLog)+" : "+"api_key="+api_key);
			q.println(tsLogFormat.format(tsLog)+" : "+"crawlType="+args[1]);
			csvWrapper forecastData;

			if ((forecastData = getForecastData.getForecast(crawlType)) != null) {
				q.println(tsLogFormat.format(tsLog)+" : "+args[1]+" data acquired");
			} else {
				q.println(tsLogFormat.format(tsLog)+" : "+args[1]+" data acquisition error");
				q.close();
				System.exit(0);
			}

			if (outputCSV.writetoCSV(forecastData,crawlType)==1) { 
				q.println(tsLogFormat.format(tsLog)+" : "+"output file created/updated");
			} else {
				q.println(tsLogFormat.format(tsLog)+" : "+"error creating/updating output file");
				q.close();
				System.exit(0);
			}
			q.println(tsLogFormat.format(tsLog)+" : "+"crawl finished");
			q.println(tsLogFormat.format(tsLog)+" : "+"execution time " + (System.currentTimeMillis()-time)/1000 +" Secs.");
			q.close();
		} catch (IOException e) {
			System.out.println(tsLogFormat.format(tsLog)+" : "+"Error creating/updating log file");
			System.out.println(e.getStackTrace());
		}

	}
	public static String getAPIkey() {
		return api_key;
	}
	public enum crawlTypes {
	    F,O,I 
	}
}
