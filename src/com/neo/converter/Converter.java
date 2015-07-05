package com.neo.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.neo.converter.async.CurrencyConvertRS;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Converter extends Activity {
	
	private EditText et1;
	private TextView tv2;
	private ImageView im1;
	private ImageView im2;
	private Spinner spinner1;
	private Spinner spinner2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_converter);
		
		final Resources res = getResources();
		
		spinner1 = (Spinner) findViewById(R.id.spinner1);
		spinner2 = (Spinner) findViewById(R.id.spinner2);

		et1 = (EditText) findViewById(R.id.tv1);
		tv2 = (TextView) findViewById(R.id.tv2);

		im1 = (ImageView) findViewById(R.id.iv1);
		im2 = (ImageView) findViewById(R.id.iv2);

		ImageButton iButton = (ImageButton) findViewById(R.id.reverse);
		iButton.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onClick(View v) {
				// Reverse edit text
				String et1Text = et1.getText().toString();
				et1.setText(tv2.getText());
				tv2.setText(et1Text);

				// Reverse spinners
				String spinner1text = (String) spinner1.getSelectedItem();
				String spinner2text = (String) spinner2.getSelectedItem();
				spinner2.setSelection(((ArrayAdapter<String>) spinner1
						.getAdapter()).getPosition(spinner1text));
				spinner1.setSelection(((ArrayAdapter<String>) spinner2
						.getAdapter()).getPosition(spinner2text));

				// Reverse images
				Drawable idImage1 = im1.getDrawable();
				Drawable idImage2 = im2.getDrawable();
				im1.setImageDrawable(idImage2);
				im2.setImageDrawable(idImage1);
			}
		});

		String[] currencies = res.getStringArray(R.array.currencies);
		List<String> currenciesList = new ArrayList<String>();
		currenciesList.addAll(Arrays.asList(currencies));

		// Spinner click listener for first spinner
		spinner1.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// On selecting a spinner item
				String item = parent.getItemAtPosition(position).toString();

				// Showing selected spinner item
				Toast.makeText(parent.getContext(), "Selected: " + item,
						Toast.LENGTH_LONG).show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}

		});

		// Spinner click listener for first spinner
		spinner2.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// On selecting a spinner item
				String item = parent.getItemAtPosition(position).toString();

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}

		});

		// Creating adapter for spinner
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				R.layout.currency_spinner_item, R.id.spinner_item_text, currenciesList);

		// attaching data adapter to spinner
		spinner1.setAdapter(dataAdapter);
		spinner2.setAdapter(dataAdapter);
		
		Button convertButton = (Button) findViewById(R.id.calculate);
		convertButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// convert and show
				if (!et1.getText().toString().equals("")) {
					convertAndShow(v);
				} else {
					String warningMessage = "You have to type a value to convert";
					showAlertDialog(warningMessage);
				}
				
			}
		});

	}

	protected void convertAndShow(View v) {
		String valueToConvert = et1.getText().toString();
		String currencyFrom = (String) spinner1.getSelectedItem();
		String currencyTo = (String) spinner2.getSelectedItem();
		new CurrencyConvertRS(this, tv2).execute(valueToConvert, currencyFrom, currencyTo);
	}
	
	private void showAlertDialog(String warning) {
		// dialog to show
		AlertDialog alertDialog = new AlertDialog.Builder(Converter.this).create();
		alertDialog.setTitle("Alert");
		alertDialog.setMessage(warning);
		alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
		    new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		            dialog.dismiss();
		        }
		    });
		alertDialog.show();
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.converter, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
			case R.id.action_settings: {
				return true;
			}
	
			case R.id.action_graph: {
				return true;
			}
	
			default:
				break;
			}
		
		return super.onOptionsItemSelected(item);
	}
}
