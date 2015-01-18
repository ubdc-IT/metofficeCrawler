import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;


public class outputCSV {

	static File out = null;
	static int hflag = 0;

	public static int writetoCSV(csvWrapper c) {
		Date ts = new Date();
		Timestamp ts_string = new Timestamp(ts.getTime());
		ArrayList<String> headers = c.headers;
		ArrayList<ArrayList<String>> rows = c.data;
		out = new File("outputForecast_test.csv");
		if (out.exists())
			hflag = 1;
		PrintWriter q;
		try {
			q = new PrintWriter(new BufferedWriter(new FileWriter(out, true)));
			int j;
			// add headers to a new file
			if (hflag==0) {
				for (int i = 0; i < headers.size(); i++) {
					if (i == 0)
						q.print("Crawl Time"+",");
					if (i != 0) 
						q.print(",");
					q.print(headers.get(i));
				}
				q.println();
			}
			// add data
			for (int i = 0; i < rows.size(); i++) {
				for (j = 0; j < headers.size(); j++) {
					if (j == 0)
						q.print(ts+",");
					if (j != 0)
						q.print(",");
					q.print(rows.get(i).get(j)); // this might break TODO
				}
				q.println();
			}
			q.close();
		} catch (IOException e) {
			// give warning
			System.out.println("cannot create output file: " + out.getName());
			return 0;
		}
		return 1;
	} 
}
