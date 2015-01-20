/*
 * siteLocations.java
 * 
 * Copyright 2015 Michael Comerford <michael.comerford@glasgow.ac.uk>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 * 
 * 
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class siteLocations {

	static String api_key = metofficeCrawler.getAPIkey();
	public static ArrayList<String> ids = new ArrayList<String>();
	
	public static ArrayList<String> getSiteids() {

		String url = "http://datapoint.metoffice.gov.uk/public/data/val/wxfcs/all/json/sitelist";
		String charset = "UTF-8";
		String param1 = api_key;
		//String param2 = "value2";

		try {
			String query = String.format("key=%s", 
					URLEncoder.encode(param1, charset)); 
			//URLEncoder.encode(param2, charset));

			URLConnection connection = new URL(url + "?" + query).openConnection();
			connection.setRequestProperty("Accept-Charset", charset);
			InputStream response = connection.getInputStream();

			HttpURLConnection conn = (HttpURLConnection)connection;
			int status = conn.getResponseCode();
			//System.out.println(status);
			for (Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
				//System.out.println(header.getKey() + "=" + header.getValue());
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(response, charset));
			String json = reader.readLine();

			JSONParser parser = new JSONParser();
			Object resultObject = parser.parse(json);

			JSONObject obj =(JSONObject)resultObject;
			JSONObject locs =(JSONObject)obj.get("Locations");
			JSONArray loc = (JSONArray)locs.get("Location");

			
			for (int i=0;i<loc.size();i++) {
				JSONObject location = (JSONObject)loc.get(i);
				String id = (String)location.get("id");
				ids.add(id);
			}
			//System.out.println(ids.size());
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		return ids;
	}
	
	public static ArrayList<String> getScotSiteids() {
			File input_ids = new File("MetOfficeForecastSitesScotland.csv");
			Scanner csv = null;
			try {
				csv = new Scanner(input_ids);
				String id;
				while (csv.hasNextLine()) {
					id = csv.nextLine();
					ids.add(id);
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			csv.close();
		return ids;
	}
}

