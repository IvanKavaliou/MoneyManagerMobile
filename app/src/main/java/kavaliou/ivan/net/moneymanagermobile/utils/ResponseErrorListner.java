package kavaliou.ivan.net.moneymanagermobile.utils;

import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import kavaliou.ivan.net.moneymanagermobile.R;
import lombok.Data;

@Data
public class ResponseErrorListner  implements Response.ErrorListener {

    private TextView  errorTextView;

    @Override
    public void onErrorResponse(VolleyError error) {
        if(null != error.networkResponse && error.networkResponse.data!=null) {
            try {
                String statusCode = String.valueOf(error.networkResponse.statusCode);
                String body = new String(error.networkResponse.data,"UTF-8");
                try {
                    JSONObject jsonError = new JSONObject(body);
                    if (statusCode.equals("406") || statusCode.equals("404") || statusCode.equals("403")){
                        errorTextView.setText(jsonError.getString("message"));
                    } else {
                        JSONArray errors = jsonError.getJSONArray("errors");
                        errorTextView.setText("");
                        for (int i = 0; i < errors.length(); i++){
                            JSONObject jsonObject =  errors.getJSONObject(i);
                            errorTextView.setText(errorTextView.getText() + jsonObject.get("defaultMessage").toString() + System.getProperty("line.separator"));
                        }
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    errorTextView.setText(e.getMessage());
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                errorTextView.setText(e.getMessage());
            }
        } else {
            errorTextView.setText(R.string.service_unavalible);
        }
    }
}
