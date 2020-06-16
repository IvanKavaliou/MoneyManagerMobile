package kavaliou.ivan.net.moneymanagermobile.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import kavaliou.ivan.net.moneymanagermobile.MainActivity;
import kavaliou.ivan.net.moneymanagermobile.R;
import kavaliou.ivan.net.moneymanagermobile.forms.AccountForm;
import kavaliou.ivan.net.moneymanagermobile.model.User;

public class AccountsListAdapter extends BaseAdapter {

    private LayoutInflater LInflater;
    private ArrayList<AccountForm> list;
    private Context context;
    private User user;

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        AccountForm accountForm = getAccountForm(position);
        if ( v == null){
            v = LInflater.inflate(R.layout.accounts_list, parent, false);
        }

        TextView textAccountCurrency = (TextView) v.findViewById(R.id.textAccountCurrency);
        textAccountCurrency.setText(accountForm.getCurrencyType().name());
        TextView textAccountBalance = (TextView) v.findViewById(R.id.textAccountBalance);
        textAccountBalance.setText(accountForm.getIncome().getBalance());

        TextView expensesTextDay = (TextView) v.findViewById(R.id.expensesTextDay);
        expensesTextDay.setText(getStringFromRes(R.string.day) + accountForm.getExpenses().getDay());
        TextView expensesTextWeek = (TextView) v.findViewById(R.id.expensesTextWeek);
        expensesTextWeek.setText(getStringFromRes(R.string.week) + accountForm.getExpenses().getWeek());
        TextView expensesTextMonth = (TextView) v.findViewById(R.id.expensesTextMonth);
        expensesTextMonth.setText(getStringFromRes(R.string.month) + accountForm.getExpenses().getMonth());

        TextView incomesTextDay = (TextView) v.findViewById(R.id.incomesTextDay);
        incomesTextDay.setText(getStringFromRes(R.string.day) + accountForm.getIncome().getDay());
        TextView incomesTextWeek = (TextView) v.findViewById(R.id.incomesTextWeek);
        incomesTextWeek.setText(getStringFromRes(R.string.week) + accountForm.getIncome().getWeek());
        TextView incomesTextMonth = (TextView) v.findViewById(R.id.incomesTextMonth);
        incomesTextMonth.setText(getStringFromRes(R.string.month) + accountForm.getIncome().getMonth());

        return v;
    }

    private String getStringFromRes(Integer id){
        return context.getResources().getString(id);
    }

    public AccountForm getAccountForm(int position){
        return (AccountForm) getItem(position);
    }

    public AccountsListAdapter(Context context, ArrayList<AccountForm> data, User user){
        this.user = user;
        list = data;
        LInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }
}
