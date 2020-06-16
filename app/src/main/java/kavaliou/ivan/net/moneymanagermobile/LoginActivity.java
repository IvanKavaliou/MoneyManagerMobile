package kavaliou.ivan.net.moneymanagermobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import kavaliou.ivan.net.moneymanagermobile.forms.LoginForm;
import kavaliou.ivan.net.moneymanagermobile.model.User;
import kavaliou.ivan.net.moneymanagermobile.utils.ResponseErrorListner;
import kavaliou.ivan.net.moneymanagermobile.utils.enums.CurrencyType;

public class LoginActivity extends AppCompatActivity {

    private EditText editEmail;
    private EditText editPassword;
    private EditText editPasswordRepeat;
    private Button buttonSignIn;
    private CheckBox checkRegister;
    private TextView errorTextView;
    private ImageView imageLoginLogo;
    private Spinner dropdown;

    private static String URL_LOGIN = "http://192.168.0.101:8080/rest/login";
    private static String URL_REGISTRATION = "http://192.168.0.101:8080/rest/registration";

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        queue = Volley.newRequestQueue(this);
        initView();
    }

    public void initView(){
        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);
        editPasswordRepeat = (EditText) findViewById(R.id.editPasswordRepeat);
        errorTextView = (TextView) findViewById(R.id.errorTextView);

        dropdown = findViewById(R.id.currencyList);
        CurrencyType[] values = CurrencyType.values();
        String[] items = new String[values.length];
        int i = 0;
        for (CurrencyType c : values){
            items[i] = c.name();
            i++;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        imageLoginLogo = (ImageView) findViewById(R.id.imageLoginLogo);
        imageLoginLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editEmail.setText("user@user.ru");
                editPassword.setText("user");
            }
        });

        buttonSignIn = (Button) findViewById(R.id.buttonSignIn);
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (!editEmail.getText().toString().trim().isEmpty()){
                        if (!editPassword.getText().toString().isEmpty()){
                            if (!checkRegister.isChecked()){
                                login();
                                errorTextView.setText("");
                            } else {
                                if (editPassword.getText().toString().equals(editPasswordRepeat.getText().toString())){
                                    regisrtation();
                                    errorTextView.setText("");
                                } else {
                                    errorTextView.setText(R.string.password_can_equals);
                                }
                            }
                        } else {
                            errorTextView.setText(R.string.password_cannot_empty);
                        }
                    } else {
                        errorTextView.setText(R.string.email_cannot_empty);
                    }
                queue.start();
            }
        });


        checkRegister = (CheckBox) findViewById(R.id.checkRegister);
        checkRegister.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
               @Override
                public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                   if (isChecked){
                       editPasswordRepeat.setVisibility(View.VISIBLE);
                       dropdown.setVisibility(View.VISIBLE);
                       buttonSignIn.setText(getString(R.string.sign_up));
                   } else {
                       editPasswordRepeat.setVisibility(View.INVISIBLE);
                       dropdown.setVisibility(View.GONE);
                       buttonSignIn.setText(getString(R.string.sign_in));
                   }
                }
         });
    }

    private void regisrtation() {

    }

    private void login(){

        Gson gson = new Gson();


        JSONObject parameters = null;
        try {
            parameters = new JSONObject(gson.toJson(LoginForm.builder()
                    .email(editEmail.getText().toString())
                    .password(editPassword.getText().toString())
                    .build()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ResponseErrorListner errorListner = new ResponseErrorListner();
        errorListner.setErrorTextView(errorTextView);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, URL_LOGIN, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson = new Gson();
                User user = gson.fromJson(response.toString(),User.class);
                startMainActivity(user);
            }
        }, errorListner);

        Volley.newRequestQueue(this).add(jsonRequest);
    }

    private void startMainActivity(User user){
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("user", user);
        startActivity(i);
    }


}
