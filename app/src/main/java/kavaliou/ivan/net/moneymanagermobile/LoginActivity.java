package kavaliou.ivan.net.moneymanagermobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class LoginActivity extends AppCompatActivity {

    private EditText editEmail;
    private EditText editPassword;
    private EditText editPasswordRepeat;
    private Button buttonSignIn;
    private CheckBox checkRegister;
    private TextView errorTextView;
    private ImageView imageLoginLogo;

    private static String URL_LOGIN = "http://192.168.0.101:8080/login";
    private static String URL_REGISTRATION = "http://192.168.0.101:8080/registration";

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

        imageLoginLogo = (ImageView) findViewById(R.id.imageLoginLogo);
        imageLoginLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editEmail.setText("user@user.com");
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
                       buttonSignIn.setText(getString(R.string.sign_up));
                   } else {
                       editPasswordRepeat.setVisibility(View.INVISIBLE);
                       buttonSignIn.setText(getString(R.string.sign_in));
                   }
                }
         });
    }

    private void startMainActivity(){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    private void regisrtation() {

    }

    private void login(){
        startMainActivity();
    }


}
