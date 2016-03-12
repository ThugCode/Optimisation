package Commun;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GoogleDistanceRequest {

	private static final String path = "https://maps.googleapis.com/maps/api/distancematrix/json?";
	private static final String API_KEY = "AIzaSyCxblp5Zfls3lmmnfz1pVNwkaE_PxCpJYc";

	OkHttpClient client = new OkHttpClient();

	public String run(String long1, String lat1, String long2, String lat2) throws IOException {
		String url_request = path;
		url_request += "origins="+lat1+","+long1;
		url_request += "&destinations="+lat2+","+long2;
		url_request += "&mode=driving";
		url_request += "&language=fr-FR";
		url_request += "&key=" + API_KEY;
		
		Request request = new Request.Builder().url(url_request).build();
		Response response = client.newCall(request).execute();
		return response.body().string();
	}

	public float getDistance(float longitude, float latitude, float longitude2, float latitude2) {
		
		float distance;
		String response;
		try {
			response = this.run(longitude+"", latitude+"", longitude2+"",latitude2+"");
			
			JSONObject json = new JSONObject(response);
			json.get("rows");
			JSONArray arr = json.getJSONArray("rows");
			distance = Float.parseFloat(arr.getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("distance").getString("value"));
			distance = distance/1000;
			
			return distance;
			
		} catch (IOException e) {
			System.out.println("Erreur IO");
			return 0;
			//e.printStackTrace();
		} catch (JSONException e) {
			System.out.println("Probleme JSON");
			return 0;
			//e.printStackTrace();
		}
	}
}