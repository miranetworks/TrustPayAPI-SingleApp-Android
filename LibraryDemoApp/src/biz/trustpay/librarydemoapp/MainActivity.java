package biz.trustpay.librarydemoapp;

import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;
import biz.trustpay.api.PricePointListener;
import biz.trustpay.objects.Request;
import biz.trustpay.ui.Payments;
import biz.trustpay.utils.Pricepoints;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class MainActivity extends Activity implements PricePointListener {
	Button pay = null;
	Button pricepoints = null;
	EditText notes = null;
	Context context=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context=this;
		pay = (Button) findViewById(R.id.pay);
		pay.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				StartPayment();
			}
		});
		pricepoints = (Button) findViewById(R.id.pricepoints);
		pricepoints.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				getPricePoints();
			}
		});
	}
    private void getPricePoints(){
    	Pricepoints pps = new Pricepoints("ap.d0a19fa2-f324-4df0-a4f4-7f3938b0e2c4",context,this);
    	pps.getPricePoints();
    }
	private void StartPayment() {
		EditText description = (EditText) findViewById(R.id.description);
		EditText amount = (EditText) findViewById(R.id.amount);
		EditText currency = (EditText) findViewById(R.id.currency);
		CheckBox istest = (CheckBox) findViewById(R.id.istest);
		UUID uuid = UUID.randomUUID();
		Request request = new Request();
		request.setAmount(amount.getText().toString());
		request.setApp_id("ap.d0a19fa2-f324-4df0-a4f4-7f3938b0e2c4");
		request.setApp_ref(uuid.toString());
		request.setApp_user("Demo User");
		request.setCurrency(currency.getText().toString());
		request.setTx_description(description.getText().toString());
		if (istest.isChecked()) {
			request.setIstest(true);
		} else {
			request.setIstest(false);
		}
		Intent intent = new Intent(this, Payments.class);
		Bundle mBundle = new Bundle();
		mBundle.putSerializable("request", request);
		intent.putExtras(mBundle);
		startActivityForResult(intent, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			notes = (EditText) findViewById(R.id.notes);
			if (resultCode == RESULT_OK) {
				notes.setText(notes.getText() + "\n" + "Successful transaction");
			} else if (resultCode == Activity.RESULT_CANCELED) {
				notes.setText(notes.getText() + "\n" + "Canceled transaction");
			} else {
				notes.setText(notes.getText() + "\n" + "Strange result ... ");
			}
		} else {
			notes.setText(notes.getText() + "\n" + "Unknown request code ... ");
		}
	}

	@Override
	public void onTrustPayPricePointsResult(final JSONObject pps) {
		this.runOnUiThread(new Runnable(){
		    public void run(){
		    	try {
		    		System.out.println(pps.toString(3));
		    		notes = (EditText) findViewById(R.id.notes);
		    		String result = pps.toString(3);
					notes.setText(notes.getText() + "\n" + result);
				} catch (JSONException e) {
					notes.setText(notes.getText() + "\nReturned bad data.");
				}
		    }
		});
	}
}