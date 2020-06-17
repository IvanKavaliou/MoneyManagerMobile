package kavaliou.ivan.net.moneymanagermobile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.com.sapereaude.maskedEditText.MaskedEditText;
import kavaliou.ivan.net.moneymanagermobile.Adapters.AccountsListAdapter;
import kavaliou.ivan.net.moneymanagermobile.forms.AccountForm;
import kavaliou.ivan.net.moneymanagermobile.forms.LoginForm;
import kavaliou.ivan.net.moneymanagermobile.forms.TransactionCategoryForm;
import kavaliou.ivan.net.moneymanagermobile.forms.TransactionForm;
import kavaliou.ivan.net.moneymanagermobile.model.User;
import kavaliou.ivan.net.moneymanagermobile.utils.AuthUtils;
import kavaliou.ivan.net.moneymanagermobile.utils.ResponseErrorListner;
import kavaliou.ivan.net.moneymanagermobile.utils.enums.CurrencyType;
import kavaliou.ivan.net.moneymanagermobile.utils.enums.OpenTransActivMode;
import kavaliou.ivan.net.moneymanagermobile.utils.enums.TransactionType;

public class TransactionActivity extends AppCompatActivity {

    private RequestQueue queue;

    private User user;
    private OpenTransActivMode mode;
    private TransactionForm transactionForm = null;
    private TransactionType type;
    private ArrayList<AccountForm> accounts;
    private ArrayList<TransactionCategoryForm> categorys;

    private MaskedEditText editDate;
    private EditText editDescription;
    private EditText editValue;
    private TextView errorTextView;
    private Spinner accountSpinner;
    private Spinner categorySpinner;
    private Button buttonDeleteTrans;
    private Button buttonSaveTrans;

    private ResponseErrorListner responseErrorListner;

    private static final String URL_GET_CATEGORYS = "http://192.168.0.101:8080/rest/category/";
    private static final String URL_DELETE_TRANS = "http://192.168.0.101:8080/rest/trnsactions/delete/";
    private static final String URL_SAVE_TRANS = "http://192.168.0.101:8080/rest/transactions";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        queue = Volley.newRequestQueue(this);

        editDate = (MaskedEditText) findViewById(R.id.editDate);
        editDescription = (EditText) findViewById(R.id.editDescription);
        editValue = (EditText) findViewById(R.id.editValue);
        accountSpinner = (Spinner) findViewById(R.id.accountSpinner);
        categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
        errorTextView = (TextView) findViewById(R.id.errorTextView);
        buttonDeleteTrans = (Button) findViewById(R.id.buttonDeleteTrans);
        buttonSaveTrans = (Button) findViewById(R.id.buttonSaveTrans);


        user = (User) getIntent().getSerializableExtra("user");
        mode = (OpenTransActivMode) getIntent().getSerializableExtra("mode");
        type = (TransactionType) getIntent().getSerializableExtra("type");
        accounts = (ArrayList<AccountForm>) getIntent().getSerializableExtra("accounts");
        if (mode == OpenTransActivMode.EDIT) {
            transactionForm = (TransactionForm) getIntent().getSerializableExtra("transactionForm");
            buttonDeleteTrans.setVisibility(View.VISIBLE);

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            deleteTrans(transactionForm.getId());
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener);

            buttonDeleteTrans.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.show();
                }
            });
        }

        responseErrorListner = new ResponseErrorListner();
        responseErrorListner.setErrorTextView(errorTextView);

        initAccounts();
        initCategorys();

        if (mode == OpenTransActivMode.EDIT){
            editDate.setText(transactionForm.getDate());
            editDescription.setText(transactionForm.getName());
            editValue.setText(transactionForm.getValue().toString());
        }

        buttonSaveTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransactionForm f = new TransactionForm();
                if (mode == OpenTransActivMode.EDIT){
                    f.setId(transactionForm.getId());
                }
                f.setDate(editDate.getText().toString());
                f.setName(editDescription.getText().toString());
                if (StringUtils.isBlank(editValue.getText().toString())){
                    f.setValue(null);
                } else {
                    f.setValue(BigDecimal.valueOf(Long.parseLong(editValue.getText().toString())));
                }
                //get id trans category
                f.setIdTransactionCategory(categorys.get(categorySpinner.getSelectedItemPosition()).getId());
                //get Currency
                f.setCurrencyType(CurrencyType.valueOf(accountSpinner.getSelectedItem().toString()));

                saveTrans(f);
            }
        });

    }

    private void initAccounts(){
        String[] items = new String[accounts.size()];
        int i = 0;
        for (AccountForm a : accounts){
            items[i] = a.getCurrencyType().name();
            i++;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        accountSpinner.setAdapter(adapter);
        if (mode == OpenTransActivMode.EDIT){
            int spinnerPosition = adapter.getPosition(transactionForm.getCurrencyType().name());
            accountSpinner.setSelection(spinnerPosition);
        }
    }

    private void initCategorys(){
        categorys = new ArrayList<>();
        JsonArrayRequest jRequest = new JsonArrayRequest(Request.Method.GET, URL_GET_CATEGORYS + type.name().toLowerCase(), null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Gson gson = new Gson();
                        for (int i =0; i < response.length(); i++){
                            try {
                                TransactionCategoryForm category =  gson.fromJson(response.getJSONObject(i).toString(),TransactionCategoryForm.class);
                                categorys.add(category);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        String[] items = new String[categorys.size()];
                        int i = 0;
                        for (TransactionCategoryForm t : categorys){
                            items[i] = t.getName();
                            i++;
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, items);
                        categorySpinner.setAdapter(adapter);
                        if (mode == OpenTransActivMode.EDIT){
                            int spinnerPosition = adapter.getPosition(transactionForm.getTransactionCategory().getName());
                            categorySpinner.setSelection(spinnerPosition);
                        }
                    }
                }, responseErrorListner)

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AuthUtils.authUser(user);
            }
        };
        Volley.newRequestQueue(this).add(jRequest);
        queue.start();
    }

    private void deleteTrans(Integer id){

        StringRequest request = new StringRequest(StringRequest.Method.GET, URL_DELETE_TRANS + id.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                setResult(100);
                finish();
            }
        }, responseErrorListner) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AuthUtils.authUser(user);
            }
        };

        Volley.newRequestQueue(this).add(request);
        queue.start();
    }

    private void saveTrans(TransactionForm form){
        Gson gson = new Gson();
        JSONObject parameters = null;
        try {
            parameters = new JSONObject(gson.toJson(form));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, URL_SAVE_TRANS, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                setResult(100);
                finish();
            }
        }, responseErrorListner){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AuthUtils.authUser(user);
            }
        };
        Volley.newRequestQueue(this).add(jsonRequest);
        queue.start();
    }

}