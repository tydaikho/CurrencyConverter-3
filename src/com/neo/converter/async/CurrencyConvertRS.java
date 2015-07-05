package com.neo.converter.async;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

public class CurrencyConvertRS extends AsyncTask<String, Void, String> {

	private TextView tv;
	private String currencyTo;
	private Float valueToConvert;

	private final String LOG_CURRENCY_CONVERTER_RS = CurrencyConvertRS.class.getSimpleName();
	
	private final String JSON_RATE = "rates";
	private Activity activity;

	public CurrencyConvertRS(Activity a, TextView t) {
		tv = t;
		activity = a;
	}

	@Override
	protected String doInBackground(String... params) {
		// These two need to be declared outside the try/catch
		// so that they can be closed in the finally block.
		HttpURLConnection urlConnection = null;
		BufferedReader reader = null;

		// Will contain the raw JSON response as a string.
		String convertJsonStr = null;

		try {
			// Construct the URL for the OpenWeatherMap query
			// Possible parameters are available at OWM's forecast API page, at
			// http://openweathermap.org/API#forecast
			// URL url = new URL();

			// url address to change
			String urlAddress = "http://api.fixer.io/latest";

			Uri.Builder uri = Uri.parse(urlAddress).buildUpon();

			uri.appendQueryParameter("base", params[1].substring(0, 3));
			uri.appendQueryParameter("symbols", params[2].substring(0, 3));
			valueToConvert = Float.valueOf(params[0]);
			currencyTo = params[2].substring(0, 3);

			URL url = new URL(uri.build().toString());

			// Create the request to OpenWeatherMap, and open the connection
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.connect();

			Log.v("BUILT URI", uri.build().toString());

			// Read the input stream into a String
			InputStream inputStream = urlConnection.getInputStream();
			StringBuffer buffer = new StringBuffer();
			if (inputStream == null) {
				// Nothing to do.
				convertJsonStr = null;
			}
			reader = new BufferedReader(new InputStreamReader(inputStream));

			String line;
			while ((line = reader.readLine()) != null) {
				// Since it's JSON, adding a newline isn't necessary (it won't
				// affect parsing)
				// But it does make debugging a *lot* easier if you print out
				// the completed
				// buffer for debugging.
				buffer.append(line + "\n");
			}

			if (buffer.length() == 0) {
				// Stream was empty. No point in parsing.
				convertJsonStr = null;
			}
			convertJsonStr = buffer.toString();
			Log.d("JUST_RESULT", convertJsonStr);
		} catch (IOException e) {
			Log.e("Currency Convert Result", "Error ", e);
			showErrorDialog("Bad request");
			// If the code didn't successfully get the weather data, there's no
			// point in attempting
			// to parse it.
			convertJsonStr = null;
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (final IOException e) {
					Log.e("Currency Convert Result", "Error closing stream", e);
					showErrorDialog("Error on closing stream");
				}
			}
		}

		return convertJsonStr;
	}

	@Override
	protected void onPostExecute(String result) {
		if (result != null) {
			Log.d(LOG_CURRENCY_CONVERTER_RS, result);
			try {
				JSONObject convertJson = new JSONObject(result);
				JSONObject rates = convertJson.getJSONObject(JSON_RATE);
				Log.d(LOG_CURRENCY_CONVERTER_RS, "EUR-> " + rates.getString(currencyTo));
				Float resultValue = Float.valueOf(rates.getString(currencyTo));
				Float totalValue = valueToConvert * resultValue;
				tv.setText(totalValue.toString());
			} catch (JSONException e) {
				Log.d(LOG_CURRENCY_CONVERTER_RS, "Error in JSON Object: " + e.getMessage());
			}
		}
	}
	
	private void showErrorDialog(String error) {
		// dialog to show
		AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
		alertDialog.setTitle("Error");
		alertDialog.setMessage(error);
		alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
		    new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		            dialog.dismiss();
		        }
		    });
		alertDialog.show();
	}

}
