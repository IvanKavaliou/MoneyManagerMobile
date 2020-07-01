package kavaliou.ivan.net.moneymanagermobile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import kavaliou.ivan.net.moneymanagermobile.forms.TransactionCategoryForm;
import kavaliou.ivan.net.moneymanagermobile.model.User;
import kavaliou.ivan.net.moneymanagermobile.utils.AuthUtils;
import kavaliou.ivan.net.moneymanagermobile.utils.ResponseErrorListner;
import kavaliou.ivan.net.moneymanagermobile.utils.enums.OpenEditActivMode;
import kavaliou.ivan.net.moneymanagermobile.utils.enums.TransactionType;

public class EditCategoryActivity extends AppCompatActivity {

    public EditText categoryName;
    public Button buttonCategorySave;
    public Button buttonCategoryDelete;
    public TextView errorTextView;

    public User user;
    public OpenEditActivMode mode;
    public TransactionCategoryForm form;
    public TransactionType type;

    private RequestQueue queue;
    private ResponseErrorListner responseErrorListner;
    private static final String URL_DELETE_CATEGORY = AuthUtils.SERVER_ADDRESS + "rest/category/delete/";
    private static final String URL_ADD_CATEGORY = AuthUtils.SERVER_ADDRESS + "rest/category/save";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue = Volley.newRequestQueue(this);
        setContentView(R.layout.activity_edit_category);
        categoryName = (EditText) findViewById(R.id.categoryName);
        buttonCategoryDelete = (Button) findViewById(R.id.buttonCategoryDelete);
        buttonCategorySave = (Button) findViewById(R.id.buttonCategorySave);

        errorTextView = (TextView) findViewById(R.id.errorTextView);
        responseErrorListner = new ResponseErrorListner();
        responseErrorListner.setErrorTextView(errorTextView);


        user = (User) getIntent().getSerializableExtra("user");
        mode = (OpenEditActivMode) getIntent().getSerializableExtra("mode");
        type = (TransactionType) getIntent().getSerializableExtra("type");
        if (mode == OpenEditActivMode.EDIT){
            form = (TransactionCategoryForm) getIntent().getSerializableExtra("form");
            categoryName.setText(form.getName());
            buttonCategoryDelete.setVisibility(View.VISIBLE);
        }

        buttonCategoryDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCategory(form.getId());
            }
        });

        buttonCategorySave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
    }

    private void save() {
        Gson gson = new Gson();
        JSONObject parameters = null;

        Integer id = null;
        if (mode == OpenEditActivMode.EDIT){
            id = form.getId();
        }

        TransactionCategoryForm tcf = TransactionCategoryForm.builder()
                .id(id)
                .name(categoryName.getText().toString())
                .transactionType(type)
                .build();
        try {
            parameters = new JSONObject(gson.toJson(tcf));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, URL_ADD_CATEGORY, parameters, new Response.Listener<JSONObject>() {
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

    private void deleteCategory(final Integer id) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        delete(id);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.are_you_sure).setPositiveButton(R.string.button_yes, dialogClickListener)
                .setNegativeButton(R.string.button_no, dialogClickListener);
        builder.show();
    }

    private void delete(Integer id) {
        StringRequest request = new StringRequest(StringRequest.Method.GET, URL_DELETE_CATEGORY + id.toString(), new Response.Listener<String>() {
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
}