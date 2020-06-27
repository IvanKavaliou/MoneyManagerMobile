package kavaliou.ivan.net.moneymanagermobile;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kavaliou.ivan.net.moneymanagermobile.Adapters.AccountsListAdapter;
import kavaliou.ivan.net.moneymanagermobile.Adapters.TransactionsListAdapter;
import kavaliou.ivan.net.moneymanagermobile.forms.AccountForm;
import kavaliou.ivan.net.moneymanagermobile.forms.ChangeEmailForm;
import kavaliou.ivan.net.moneymanagermobile.forms.LoginForm;
import kavaliou.ivan.net.moneymanagermobile.forms.PasswordChangeForm;
import kavaliou.ivan.net.moneymanagermobile.forms.TransactionForm;
import kavaliou.ivan.net.moneymanagermobile.model.User;
import kavaliou.ivan.net.moneymanagermobile.utils.AuthUtils;
import kavaliou.ivan.net.moneymanagermobile.utils.ResponseErrorListner;
import kavaliou.ivan.net.moneymanagermobile.utils.enums.CurrencyType;
import kavaliou.ivan.net.moneymanagermobile.utils.enums.OpenEditActivMode;
import kavaliou.ivan.net.moneymanagermobile.utils.enums.TransactionType;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private User user;
    private TextView textErrors;
    private RequestQueue queue;

    private ListView accountsList;
    private ArrayList<AccountForm> accounts;
    private ListView transactionsIncomeList;
    private ArrayList<TransactionForm> transactionsIncomes;
    private ListView transactionsExpensesList;
    private ArrayList<TransactionForm> transactionsExpenses;
    private Button buttonAddExpenses;
    private Button buttonAddIncome;
    private Button buttonOpenAddAccounts;
    private boolean addAccountsOpen = false;
    private Button buttonAddAccount;
    private boolean fabOpen = false;
    private Spinner addAccountSpinner;
    private FloatingActionButton fab;
    private LinearLayout layoutSettings;
    private LinearLayout layoutPassword;
    private CheckBox checkboxEditPassword;
    private CheckBox checkboxEditEmail;
    private boolean passChangeOpen = false;
    private boolean emailChangeOpen = false;
    private EditText editOldPassword;
    private EditText editNewPassword;
    private EditText editNewPasswordRepeat;
    private EditText editEmail;
    private Button buttonSaveEmail;
    private Button buttonChangePassword;

    private ResponseErrorListner responseErrorListner;

    private static final String URL_GET_ACCOUNTS ="http://192.168.0.101:8080/rest/transactions/accounts";
    private static final String URL_GET_INCOMES ="http://192.168.0.101:8080/rest/trnsactions/income";
    private static final String URL_GET_EXPENSES ="http://192.168.0.101:8080/rest/trnsactions/expenses";
    private static final String URL_DELETE_CURRENCY ="http://192.168.0.101:8080/rest/currency/delete/";
    private static final String URL_ADD_CURRENCY ="http://192.168.0.101:8080/rest/currency/add/";
    private static final String URL_EDIT_EMAIL ="http://192.168.0.101:8080/rest/change/email";
    private static final String URL_CHANGE_PASSWORD ="http://192.168.0.101:8080/rest/change/password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        queue = Volley.newRequestQueue(this);

        addAccountSpinner = (Spinner) findViewById(R.id.addAccountSpinner);
        buttonAddExpenses = (Button) findViewById(R.id.buttonAddExpenses);
        buttonAddExpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTransactionActivity(TransactionType.EXPENSES, OpenEditActivMode.ADD, null);
            }
        });
        buttonAddIncome = (Button) findViewById(R.id.buttonAddIncome);
        buttonAddIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTransactionActivity(TransactionType.INCOME, OpenEditActivMode.ADD, null);
            }
        });

        buttonAddAccount = (Button) findViewById(R.id.buttonAddAccount);
        buttonAddAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addAccountSpinner.getCount() > 0){
                    addAccount(CurrencyType.valueOf(addAccountSpinner.getSelectedItem().toString()));
                }
            }
        });

        buttonOpenAddAccounts = (Button) findViewById(R.id.buttonOpenAddAccounts);
        buttonOpenAddAccounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addAccountsOpen){
                    buttonAddAccount.setVisibility(View.GONE);
                    addAccountSpinner.setVisibility(View.GONE);
                    buttonOpenAddAccounts.setText(R.string.add_accounts);
                    addAccountsOpen = false;
                } else {
                    buttonAddAccount.setVisibility(View.VISIBLE);
                    addAccountSpinner.setVisibility(View.VISIBLE);
                    buttonOpenAddAccounts.setText(R.string.close);
                    addAccountsOpen = true;
                }
            }
        });


        textErrors = (TextView) findViewById(R.id.textErrors);
        responseErrorListner = new ResponseErrorListner();
        responseErrorListner.setErrorTextView(textErrors);
        initUser();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fabOpen){
                    buttonAddExpenses.setVisibility(View.GONE);
                    buttonAddIncome.setVisibility(View.GONE);
                    fabOpen = false;
                } else {
                    buttonAddExpenses.setVisibility(View.VISIBLE);
                    buttonAddIncome.setVisibility(View.VISIBLE);
                    fabOpen = true;
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initAccounts();
        initTransactions();

        //queue = Volley.newRequestQueue(this);

    }

    private void addAccount(CurrencyType currencyType) {
        JsonObjectRequest jRequest = new JsonObjectRequest(Request.Method.GET, URL_ADD_CURRENCY + currencyType.name(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        buttonOpenAddAccounts.callOnClick();
                       update();
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

    private void update() {
        initAccounts();
        initTransactions();
    }

    private void openTransactionActivity(TransactionType type, OpenEditActivMode mode, TransactionForm form) {
        Intent i = new Intent(this, TransactionActivity.class);
        i.putExtra("user", user);
        i.putExtra("mode", mode);
        i.putExtra("type", type);
        i.putExtra("accounts", accounts);
        if (mode == OpenEditActivMode.EDIT){
            i.putExtra("transactionForm", form);
        }
        startActivityForResult(i, 100);
    }

    private void initUser() {
        user = (User) getIntent().getSerializableExtra("user");
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView textViewEmail = (TextView) headerView.findViewById(R.id.textViewEmail);
        textViewEmail.setText(user.getEmail());

        initSettings();
    }

    private void initSettings(){
        layoutSettings = (LinearLayout) findViewById(R.id.layoutSettings);
        layoutPassword = (LinearLayout) findViewById(R.id.layoutPassword);
        checkboxEditPassword = (CheckBox) findViewById(R.id.checkboxEditPassword);
        checkboxEditEmail = (CheckBox) findViewById(R.id.checkboxEditEmail);
        editOldPassword = (EditText) findViewById(R.id.editOldPassword);
        editNewPassword = (EditText) findViewById(R.id.editNewPassword);
        editNewPasswordRepeat = (EditText) findViewById(R.id.editNewPasswordRepeat);
        editEmail = (EditText) findViewById(R.id.editEmail);
        buttonSaveEmail = (Button) findViewById(R.id.buttonSaveEmail);
        buttonChangePassword = (Button) findViewById(R.id.buttonChangePassword);

        editEmail.setText(user.getEmail());

        checkboxEditEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emailChangeOpen){
                    editEmail.setFocusable(false);
                    buttonSaveEmail.setVisibility(View.GONE);
                    emailChangeOpen = false;
                } else {
                    editEmail.setFocusableInTouchMode(true);
                    buttonSaveEmail.setVisibility(View.VISIBLE);
                    emailChangeOpen = true;
                }
            }
        });

        checkboxEditPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(passChangeOpen){
                    layoutPassword.setVisibility(View.GONE);
                    editOldPassword.setText("");
                    editNewPassword.setText("");
                    editNewPasswordRepeat.setText("");
                    passChangeOpen = false;
                } else {
                    layoutPassword.setVisibility(View.VISIBLE);
                    passChangeOpen = true;
                }
            }
        });

        buttonSaveEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEmail(editEmail.getText().toString());
            }
        });

        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword(PasswordChangeForm.builder()
                        .oldPassword(editOldPassword.getText().toString())
                        .password(editNewPassword.getText().toString())
                        .passwordRepeat(editNewPasswordRepeat.getText().toString())
                        .build());
            }
        });
    }

    private void changePassword(PasswordChangeForm form){
        Gson gson = new Gson();
        JSONObject parameters = null;
        try {
            parameters = new JSONObject(gson.toJson(form));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(StringRequest.Method.POST, URL_CHANGE_PASSWORD, parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
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

    private void saveEmail(final String email) {
        Gson gson = new Gson();
        JSONObject parameters = null;
        try {
            parameters = new JSONObject(gson.toJson(new ChangeEmailForm(email)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(StringRequest.Method.POST, URL_EDIT_EMAIL, parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
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

    private void initAddAccountsSpiner(){
        ArrayList<CurrencyType> tmp = new ArrayList<CurrencyType>();
        for (AccountForm a : accounts){
            tmp.add(a.getCurrencyType());
        }

        String[] items = new String[CurrencyType.values().length-accounts.size()];
        int i = 0;
        for(CurrencyType c : CurrencyType.values()){
            if (!tmp.contains(c)){
                items[i] = c.name();
                i++;
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        addAccountSpinner.setAdapter(adapter);

    }

    private void initAccounts(){

        accounts = new ArrayList<>();
        accountsList = (ListView) findViewById(R.id.accountsList);
        initAddAccountsSpiner();
        accountsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteAccount(accounts.get(position).getCurrencyType());
                return false;
            }
        });
        JsonArrayRequest jRequest = new JsonArrayRequest(Request.Method.GET, URL_GET_ACCOUNTS, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Gson gson = new Gson();
                        for (int i =0; i < response.length(); i++){
                            try {
                                AccountForm account =  gson.fromJson(response.getJSONObject(i).toString(),AccountForm.class);
                                accounts.add(account);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        AccountsListAdapter accountsListAdapter = new AccountsListAdapter(getApplicationContext(), accounts, user);
                        accountsList.setAdapter(accountsListAdapter);
                        initAddAccountsSpiner();
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

    private void deleteAccount(final CurrencyType currencyType) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        deleteCurrency(currencyType);
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

    private void deleteCurrency(CurrencyType currencyType){

        StringRequest request = new StringRequest(StringRequest.Method.GET, URL_DELETE_CURRENCY + currencyType.name(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                update();
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

    private void initTransactions(){
        transactionsIncomes = new ArrayList<>();
        transactionsExpenses = new ArrayList<>();

        transactionsIncomeList = (ListView) findViewById(R.id.transactionsIncomeList);
        transactionsIncomeList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                openTransactionActivity(
                        TransactionType.INCOME,
                        OpenEditActivMode.EDIT,
                        transactionsIncomes.get(position));
                return false;
            }
        });

        transactionsExpensesList = (ListView) findViewById(R.id.transactionsExpensesList);
        transactionsExpensesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                openTransactionActivity(
                        TransactionType.EXPENSES,
                        OpenEditActivMode.EDIT,
                        transactionsExpenses.get(position));
                return false;
            }
        });

        JsonArrayRequest jRequestIncomes = new JsonArrayRequest(Request.Method.GET, URL_GET_INCOMES, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Gson gson = new Gson();
                        for (int i =0; i < response.length(); i++){
                            try {
                                TransactionForm trans =  gson.fromJson(response.getJSONObject(i).toString(),TransactionForm.class);
                                transactionsIncomes.add(trans);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        TransactionsListAdapter transactionsListAdapter = new TransactionsListAdapter(getApplicationContext(), transactionsIncomes, user);
                        transactionsIncomeList.setAdapter(transactionsListAdapter);
                    }
                }, responseErrorListner)

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AuthUtils.authUser(user);
            }
        };
        Volley.newRequestQueue(this).add(jRequestIncomes);

        JsonArrayRequest jRequestExpenses = new JsonArrayRequest(Request.Method.GET, URL_GET_EXPENSES, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Gson gson = new Gson();
                        for (int i =0; i < response.length(); i++){
                            try {
                                TransactionForm trans =  gson.fromJson(response.getJSONObject(i).toString(),TransactionForm.class);
                                transactionsExpenses.add(trans);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        TransactionsListAdapter transactionsListAdapter = new TransactionsListAdapter(getApplicationContext(), transactionsExpenses, user);
                        transactionsExpensesList.setAdapter(transactionsListAdapter);
                    }
                }, responseErrorListner)

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AuthUtils.authUser(user);
            }
        };
        Volley.newRequestQueue(this).add(jRequestExpenses);

        queue.start();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100){
            fab.callOnClick();
            update();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (addAccountsOpen){
            buttonOpenAddAccounts.callOnClick();
        }
        if (fabOpen){
            fab.callOnClick();
        }
        if(passChangeOpen){
            checkboxEditPassword.callOnClick();
        }

        accountsList.setVisibility(View.GONE);
        transactionsExpensesList.setVisibility(View.GONE);
        transactionsIncomeList.setVisibility(View.GONE);
        buttonOpenAddAccounts.setVisibility(View.GONE);
        textErrors.setVisibility(View.GONE);
        layoutSettings.setVisibility(View.GONE);

        if (id == R.id.nav_accounts) {
            accountsList.setVisibility(View.VISIBLE);
            buttonOpenAddAccounts.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_incomes) {
            transactionsIncomeList.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_cexpenses) {
            transactionsExpensesList.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_settings) {
            layoutSettings.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_logout) {
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
