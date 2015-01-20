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

	public static void main(String[] args) {

		long time = System.currentTimeMillis();
		DateFormat tsLogFormat = DateFormat.getDateInstance();
		Date tsLog = new Date(time);
		log = new File("CrawlerLog.log");
		PrintWriter q;
		try {
			q = new PrintWriter(new BufferedWriter(new FileWriter(log, true)));
			q.println(tsLogFormat.format(tsLog)+" : "+"Crawler started");
			for (int i=0;i<args.length;i++) {
				api_key = args[i];
			}
			q.println(tsLogFormat.format(tsLog)+" : "+"api_key="+api_key);
			csvWrapper forecastData;

			if ((forecastData = getForecastData.getForecast()) != null) {
				q.println(tsLogFormat.format(tsLog)+" : "+"forecast data acquired");
			} else {
				q.println(tsLogFormat.format(tsLog)+" : "+"forecast data acquisition error");
				q.close();
				System.exit(0);
			}

			if (outputCSV.writetoCSV(forecastData)==1) { 
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
}
