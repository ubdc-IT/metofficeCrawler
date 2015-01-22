/*
 * getForecastData.java
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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class getForecastData {

	static String api_key = metofficeCrawler.getAPIkey();
	static String res = "daily";
	static ArrayList<String> dayheaders = new ArrayList<String>();
	static ArrayList<String> nightheaders = new ArrayList<String>();
	static ArrayList<String> headersmap = new ArrayList<String>();
	static ArrayList<String> outputheaders = new ArrayList<String>();
	static ArrayList<ArrayList<String>> rows = new ArrayList<ArrayList<String>>(); 
	private static ArrayList<String> location_ids = null;
	private static metofficeCrawler.crawlTypes ct;

	public static csvWrapper getForecast(metofficeCrawler.crawlTypes cT) {

		ct = cT;
		String url = null;
		switch (ct) {
		case F:	url = "http://datapoint.metoffice.gov.uk/public/data/val/wxfcs/all/json/";
		break;
		case O: url = "http://datapoint.metoffice.gov.uk/public/data/val/wxobs/all/json/";
		res = "hourly";
		break;
		case I:	url = "http://datapoint.metoffice.gov.uk/public/data/val/wxfcs/all/json/";
		break;
		}

		String charset = "UTF-8";
		String param1 = res;
		String param2 = api_key;
		csvWrapper dataout = null;

		outputheaders.add("Data Timestamp");
		outputheaders.add("Location ID");
		outputheaders.add("Location Name");
		switch (ct) {
		case F:	
			outputheaders.add("Forecast For Date");
			outputheaders.add("Forecast Type");
			break;
		case O:	
			outputheaders.add("Obs For Date");	
			outputheaders.add("Obs mins since midnight");
			break;
		case I:	
			outputheaders.add("Images For Date");
			outputheaders.add("Images Type");
			break;
		}

		location_ids = getForecastData.setLocation_ids();

		for (int i=0;i<location_ids.size();i++) {
			String id = location_ids.get(i);
			//String id = location_ids.get(i);
			//System.out.println("id "+id);
			try {
				String query = String.format("res=%s&key=%s", 
						URLEncoder.encode(param1, charset), URLEncoder.encode(param2, charset));

				URLConnection connection = new URL(url + id + "?" + query).openConnection();
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

				JSONObject obj = (JSONObject)resultObject;

				JSONObject SiteRep = (JSONObject)obj.get("SiteRep");
				JSONObject headers = (JSONObject)SiteRep.get("Wx");
				//System.out.println(headers.keySet());
				JSONArray params = (JSONArray)headers.get("Param");
				if (i==0) {
					for (int m=0;m<params.size();m++) {
						JSONObject param = (JSONObject) params.get(m);
						outputheaders.add((String)param.get("name"));
						//System.out.println(outputheaders.get(m));
					}
				}
				JSONObject dv = (JSONObject)SiteRep.get("DV");
				String forecastTime = (String)dv.get("dataDate");
				//System.out.println(String.format("data ts = %s", forecastTime));
				Object location = null;
				String loc_name = "";
				switch (ct) {
				case F:	location = (JSONObject)dv.get("Location");
				break;
				case O: location = (JSONArray)dv.get("Location");
				break;
				case I: location = (JSONObject)dv.get("Location");
				break;
				}
				switch (ct) {
				case F:	
					loc_name = (String)((JSONObject)location).get("name");
					JSONArray forecasts = (JSONArray)((JSONObject)location).get("Period");

					for (int j=0;j<forecasts.size();j++) {
						JSONObject forecast = (JSONObject)forecasts.get(j);
						//System.out.println(forecast.keySet());
						String forecastFor = (String)forecast.get("value");
						//System.out.println("Forecast for date = "+forecastFor);
						JSONArray report = (JSONArray)forecast.get("Rep");
						//System.out.println("report size "+report.size());
						for (int k=0;k<report.size();k++) {
							JSONObject rep = (JSONObject) report.get(k);
							ArrayList<String> datarow = new ArrayList<String>();
							datarow.add(forecastTime);
							datarow.add(id);
							datarow.add(loc_name);
							datarow.add(forecastFor);
							//System.out.println(rep.keySet());
							//System.out.println(rep.get("$"));
							if (rep.get("$").equals("Day")) {
								datarow.add("Day");
								if (dayheaders.isEmpty()) {
									dayheaders = new ArrayList<String>(rep.keySet());
								}
								for (int n=5;n<outputheaders.size();n++) {
									//System.out.println("n="+n+" "+dayheaders.contains(outputheaders.get(n)));
									if (dayheaders.contains(outputheaders.get(n))) {
										datarow.add((String)rep.get(outputheaders.get(n)));
										//System.out.println("unit = "+outputheaders.get(n)+"\tvalue = "+datarow.get(n));
									} else {
										datarow.add("");
									}
								}
								rows.add(datarow);
							} else {
								datarow.add("Night");
								if (nightheaders.isEmpty()) {
									nightheaders = new ArrayList<String>(rep.keySet());
								}
								for (int n=5;n<outputheaders.size();n++) {
									//System.out.println("n="+n+" "+nightheaders.contains(outputheaders.get(n)));
									if (nightheaders.contains(outputheaders.get(n))) {
										datarow.add((String)rep.get(outputheaders.get(n)));
										//System.out.println("unit = "+outputheaders.get(n)+"\tvalue = "+datarow.get(n));
									} else {
										datarow.add("");
									}
								}
								rows.add(datarow);
							}

						}
					}
					break;
				case O:
					for (int o=0;o<((JSONArray)location).size();o++) {
						loc_name = (String)((JSONObject)((JSONArray)location).get(o)).get("name");
						String loc_id = (String)((JSONObject)((JSONArray)location).get(o)).get("i");
						//System.out.println(((JSONObject)((JSONArray)location).get(o)).get("Period"));
						Object observations = ((JSONObject)((JSONArray)location).get(o)).get("Period");
						if (observations instanceof JSONArray) {
							for (int j=0;j<((JSONArray)observations).size();j++) {
								JSONObject observation = (JSONObject)((JSONArray)observations).get(j);
								//System.out.println(forecast.keySet());
								String observationsFor = (String)observation.get("value");
								//System.out.println("Forecast for date = "+forecastFor);
								//System.out.println(observation.keySet());
								//System.out.println(observation.get("Rep"));
								Object report = ((JSONObject)observation).get("Rep");
								if (report instanceof JSONArray) {
								//System.out.println("report size "+report.size());
								for (int k=0;k<((JSONArray)report).size();k++) {
									JSONObject rep = (JSONObject) (((JSONArray)report).get(k));
									ArrayList<String> datarow = new ArrayList<String>();
									datarow.add(forecastTime);
									datarow.add(loc_id);
									datarow.add(loc_name);
									datarow.add(observationsFor);
									//System.out.println(rep.keySet());
									//System.out.println(rep.get("$"));
									datarow.add((String)rep.get("$"));
									for (int n=5;n<outputheaders.size();n++) {
										datarow.add((String)rep.get(outputheaders.get(n)));
										//System.out.println("unit = "+outputheaders.get(n)+"\tvalue = "+datarow.get(n));
									}
									rows.add(datarow);
								}
								} else if (report instanceof JSONObject) {
									ArrayList<String> datarow = new ArrayList<String>();
									datarow.add(forecastTime);
									datarow.add(loc_id);
									datarow.add(loc_name);
									datarow.add(observationsFor);
									//System.out.println(rep.keySet());
									//System.out.println(rep.get("$"));
									datarow.add((String)((JSONObject)report).get("$"));
									for (int n=5;n<outputheaders.size();n++) {
										datarow.add((String)((JSONObject)report).get(outputheaders.get(n)));
										//System.out.println("unit = "+outputheaders.get(n)+"\tvalue = "+datarow.get(n));
									}
									rows.add(datarow);
								}
							}
						} else if (observations instanceof JSONObject) { 
							//System.out.println(forecast.keySet());
							String observationsFor = (String)((JSONObject)observations).get("value");
							//System.out.println("Forecast for date = "+forecastFor);
							//System.out.println(observation.keySet());
							//System.out.println(observation.get("Rep"));
							//System.out.println("report size "+report.size());
							Object report = ((JSONObject)observations).get("Rep");
							if (report instanceof JSONArray) {
							//System.out.println("report size "+report.size());
							for (int k=0;k<((JSONArray)report).size();k++) {
								JSONObject rep = (JSONObject) (((JSONArray)report).get(k));
								ArrayList<String> datarow = new ArrayList<String>();
								datarow.add(forecastTime);
								datarow.add(loc_id);
								datarow.add(loc_name);
								datarow.add(observationsFor);
								//System.out.println(rep.keySet());
								//System.out.println(rep.get("$"));
								datarow.add((String)rep.get("$"));
								for (int n=5;n<outputheaders.size();n++) {
									datarow.add((String)rep.get(outputheaders.get(n)));
									//System.out.println("unit = "+outputheaders.get(n)+"\tvalue = "+datarow.get(n));
								}
								rows.add(datarow);
							}
							} else if (report instanceof JSONObject) {
								ArrayList<String> datarow = new ArrayList<String>();
								datarow.add(forecastTime);
								datarow.add(loc_id);
								datarow.add(loc_name);
								datarow.add(observationsFor);
								//System.out.println(rep.keySet());
								//System.out.println(rep.get("$"));
								datarow.add((String)((JSONObject)report).get("$"));
								for (int n=5;n<outputheaders.size();n++) {
									datarow.add((String)((JSONObject)report).get(outputheaders.get(n)));
									//System.out.println("unit = "+outputheaders.get(n)+"\tvalue = "+datarow.get(n));
								}
								rows.add(datarow);
							}
						}
					}
					break;
				case I:
					break;
				}
			} catch (IOException | ParseException e) {
				e.printStackTrace();
				return dataout;
			}
		}
		dataout = new csvWrapper(outputheaders,rows);
		return dataout;
	}

	public static ArrayList<String> getLocation_ids() {
		return location_ids;
	}

	static ArrayList<String> setLocation_ids() {
		ArrayList<String>loc_ids = null; 
		switch (ct) {
		case F: loc_ids = siteLocations.getScotSiteids();
		break;
		case O: loc_ids = siteLocations.getObsIds();
		break;
		case I: loc_ids = siteLocations.getScotSiteids();
		break;
		}
		return loc_ids;
	}
}

class csvWrapper {
	ArrayList<String> headers;
	ArrayList<ArrayList<String>> data;

	public csvWrapper(ArrayList<String> h, ArrayList<ArrayList<String>> d) {
		headers = h;
		data = d;
	}
}