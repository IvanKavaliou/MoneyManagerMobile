package kavaliou.ivan.net.moneymanagermobile.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import kavaliou.ivan.net.moneymanagermobile.R;
import kavaliou.ivan.net.moneymanagermobile.forms.TransactionForm;
import kavaliou.ivan.net.moneymanagermobile.model.User;

public class TransactionsListAdapter extends BaseAdapter {

    private LayoutInflater LInflater;
    private ArrayList<TransactionForm> list;
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
        TransactionForm form = getTransactionForm(position);
        if ( v == null){
            v = LInflater.inflate(R.layout.transactions_list, parent, false);
        }

        TextView transDate = (TextView) v.findViewById(R.id.transDate);
        transDate.setText(form.getDate().substring(0,10));
        TextView transTime = (TextView) v.findViewById(R.id.transTime);
        transTime.setText(form.getDate().substring(11,16));

        TextView trnsCurrency = (TextView) v.findViewById(R.id.trnsCurrency);
        trnsCurrency.setText(form.getCurrencyType().name());

        TextView trnsValue = (TextView) v.findViewById(R.id.trnsValue);
        trnsValue.setText(form.getValue().toString());

        TextView transCategory = (TextView) v.findViewById(R.id.transCategory);
        transCategory.setText(form.getTransactionCategory().getName());

        TextView transDescription = (TextView) v.findViewById(R.id.transDescription);
        transDescription.setText(form.getName());

        return v;
    }

    public TransactionForm getTransactionForm(int position){
        return (TransactionForm) getItem(position);
    }

    public TransactionsListAdapter(Context context, ArrayList<TransactionForm> data, User user){
        this.user = user;
        list = data;
        LInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }
}
