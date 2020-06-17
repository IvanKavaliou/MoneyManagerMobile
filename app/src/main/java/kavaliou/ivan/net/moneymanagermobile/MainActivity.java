package kavaliou.ivan.net.moneymanagermobile;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Base64;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

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
import kavaliou.ivan.net.moneymanagermobile.forms.TransactionForm;
import kavaliou.ivan.net.moneymanagermobile.model.User;
import kavaliou.ivan.net.moneymanagermobile.utils.AuthUtils;
import kavaliou.ivan.net.moneymanagermobile.utils.ResponseErrorListner;
import kavaliou.ivan.net.moneymanagermobile.utils.enums.OpenTransActivMode;
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



    private ResponseErrorListner responseErrorListner;

    private static final String URL_GET_ACCOUNTS ="http://192.168.0.101:8080/rest/transactions/accounts";
    private static final String URL_GET_INCOMES ="http://192.168.0.101:8080/rest/trnsactions/income";
    private static final String URL_GET_EXPENSES ="http://192.168.0.101:8080/rest/trnsactions/expenses";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        queue = Volley.newRequestQueue(this);

        buttonAddExpenses = (Button) findViewById(R.id.buttonAddExpenses);
        buttonAddExpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTransactionActivity(TransactionType.EXPENSES, OpenTransActivMode.ADD, null);
            }
        });
        buttonAddIncome = (Button) findViewById(R.id.buttonAddIncome);
        buttonAddIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTransactionActivity(TransactionType.INCOME, OpenTransActivMode.ADD, null);
            }
        });



        textErrors = (TextView) findViewById(R.id.textErrors);
        responseErrorListner = new ResponseErrorListner();
        responseErrorListner.setErrorTextView(textErrors);
        initUser();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

    private void update() {
        initAccounts();
        initTransactions();
    }

    private void openTransactionActivity(TransactionType type, OpenTransActivMode mode, TransactionForm form) {
        Intent i = new Intent(this, TransactionActivity.class);
        i.putExtra("user", user);
        i.putExtra("mode", mode);
        i.putExtra("type", type);
        i.putExtra("accounts", accounts);
        if (mode == OpenTransActivMode.EDIT){
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
    }

    private void initAccounts(){
        accountsList = (ListView) findViewById(R.id.accountsList);
        accounts = new ArrayList<>();
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

    private void initTransactions(){
        transactionsIncomes = new ArrayList<>();
        transactionsExpenses = new ArrayList<>();

        transactionsIncomeList = (ListView) findViewById(R.id.transactionsIncomeList);
        transactionsIncomeList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                openTransactionActivity(
                        TransactionType.INCOME,
                        OpenTransActivMode.EDIT,
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
                        OpenTransActivMode.EDIT,
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
        textErrors.setVisibility(View.GONE);
        if (id == R.id.nav_accounts) {
            accountsList.setVisibility(View.VISIBLE);
            transactionsExpensesList.setVisibility(View.GONE);
            transactionsIncomeList.setVisibility(View.GONE);
            buttonAddExpenses.setVisibility(View.GONE);
            buttonAddIncome.setVisibility(View.GONE);
        } else if (id == R.id.nav_incomes) {
            accountsList.setVisibility(View.GONE);
            transactionsExpensesList.setVisibility(View.GONE);
            transactionsIncomeList.setVisibility(View.VISIBLE);
            buttonAddExpenses.setVisibility(View.GONE);
            buttonAddIncome.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_cexpenses) {
            accountsList.setVisibility(View.GONE);
            transactionsExpensesList.setVisibility(View.VISIBLE);
            transactionsIncomeList.setVisibility(View.GONE);
            buttonAddExpenses.setVisibility(View.VISIBLE);
            buttonAddIncome.setVisibility(View.GONE);
        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_logout) {
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
