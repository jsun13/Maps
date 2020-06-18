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

import junzhaosun.map.LoginActivity;
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
                if(checkLen(s, usernameLayout, 3) && checkLetterOrDigit(s, usernameLayout)){
                    namevalid=true;
                    usernameLayout.setError(null);
                }else{
                    namevalid=false;
                }
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
                if(checkLen(s, passwordLayout, 6) && checkLetterOrDigit(s, passwordLayout)){
                    passwordvalid=true;
                    passwordLayout.setError(null);
                }else{
                    passwordvalid=false;
                }
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
     * shared method for both login and sign up fragments
     * check if the given string has met the required length
     * @param s
     * @param layout
     * @param l
     * @return
     */
    public static boolean checkLen(CharSequence s, TextInputLayout layout, int l){
        if(s.length()<l){
            layout.setError("!length >="+l+" ");
            return false;
        }
        return true;
    }

    /**
     * check if the given string consists only of letters or digits
     * @param s
     * @param layout
     * @return
     */
    public static boolean checkLetterOrDigit(CharSequence s, TextInputLayout layout){
        for(int i=0; i<s.length(); ++i){
            if(!Character.isLetterOrDigit(s.charAt(i))){
                if(layout.getError()!=null){
                    String error=layout.getError().toString();
                    layout.setError(error+" must be digit or char");
                }else{
                    layout.setError(" must be digit or char");
                }
                return false;
            }
        }
        return true;
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
                        NavHostFragment.findNavController(SignUpFragment.this)
                                .popBackStack();
                    } else {
                        String message = e.getMessage();
                        if (message.toLowerCase().contains("java")) {
                            message = e.getMessage().substring(e.getMessage().indexOf(" "));
                        }
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }else{
            Log.e("sign up", "invalid sign up info");
        }

    }
}