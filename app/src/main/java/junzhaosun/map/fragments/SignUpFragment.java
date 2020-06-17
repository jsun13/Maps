package junzhaosun.map.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import junzhaosun.map.R;

public class SignUpFragment extends Fragment {
    private View contentView;
    //检测提交相关
    private boolean namevalid=true;
    private boolean passwordvalid=true;
    private TextInputEditText username;
    private TextInputEditText password;
    private TextInputLayout usernameLayout;
    private TextInputLayout passwordLayout;
    private EditText rePassword;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        if(contentView==null){
            contentView=inflater.inflate(R.layout.fragment_second, container, false);
        }
        return contentView;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toast.makeText(getContext(), "on view created", Toast.LENGTH_SHORT).show();

        //初始化控件
        username=view.findViewById(R.id.user_name_sign_up);
        password=view.findViewById(R.id.password_sign_up);
        rePassword=view.findViewById(R.id.reenter_password);
        usernameLayout=view.findViewById(R.id.username_layout);
        passwordLayout=view.findViewById(R.id.password_layout);
        usernameLayout.setErrorEnabled(true);
        passwordLayout.setErrorEnabled(true);
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                namevalid=true;
                checkUser(s);
            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                passwordvalid=true;
                checkPassword();
            }
        });

        view.findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUp();
            }
        });

    }

    /**
     * check whether username is at least 3 chars long
     * and letterOrDigit only
     * @param s
     */
    private void checkUser(CharSequence s){
        if(s.length()<3){
            usernameLayout.setError("!username.length >=3 ");
            namevalid=false;
        }
        for(int i=0; i<s.length(); ++i){
            if(!Character.isLetterOrDigit(s.charAt(i))){
                String error=usernameLayout.getError().toString();
                usernameLayout.setError(error+" must be digit or char");
                namevalid=false;
            }
        }
        if(namevalid==true){
            usernameLayout.setError(null);
        }
    }

    /**
     * check whether password is at least 6 chars long
     * contains letterOrDigit and at least one special
     *
     * passwords need to be matched
     */
    private void checkPassword(){
        if(password.getText().length()<6) {
            passwordLayout.setError("!password>=6");
            passwordvalid = false;
        }
        String s=password.getText().toString();
        boolean hasSpecial=false;
        for(int i=0; i<s.length(); ++i){
            if(!Character.isLetterOrDigit(s.charAt(i)) ) {
                Toast.makeText(getContext(), "must be digit/char", Toast.LENGTH_SHORT).show();
                passwordvalid = false;
            }
        }
        if(passwordvalid==true)
            passwordLayout.setError(null);
    }

    private void SignUp(){
        if(!rePassword.getText().toString().equals(password.getText().toString())){
            Toast.makeText(getContext(), "password does not match", Toast.LENGTH_SHORT).show();
            passwordvalid=false;
        }
        if(namevalid==true && passwordvalid==true) {
            ParseUser user=new ParseUser();
            user.setUsername(username.getText().toString());
            user.setPassword(password.getText().toString());
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.i("Info", "user signed up");
                    } else {
                        String message = e.getMessage();
                        if (message.toLowerCase().contains("java")) {
                            message = e.getMessage().substring(e.getMessage().indexOf(" "));
                        }
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            NavHostFragment.findNavController(SignUpFragment.this)
                    .popBackStack();
        }else{
            Log.e("sign up", "invalid sign up info");
        }

    }
}