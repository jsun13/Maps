package junzhaosun.map.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.w3c.dom.Text;
import junzhaosun.map.fragments.SignUpFragment;
import junzhaosun.map.MainActivity;
import junzhaosun.map.R;

public class SignInFragment extends Fragment {
    private View contentView;
    private SignUpFragment helper;
    private TextInputEditText username;
    private TextInputEditText password;
    private TextInputLayout nameLayout;
    private TextInputLayout passwordLayout;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        if(contentView==null){
            contentView=inflater.inflate(R.layout.fragment_first, container, false);
        }
        return contentView;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        helper=new SignUpFragment();
        username=view.findViewById(R.id.sign_in_username);
        password=view.findViewById(R.id.sign_in_password);
        nameLayout=view.findViewById(R.id.login_username_layout);
        passwordLayout=view.findViewById(R.id.login_password_layout);

        view.findViewById(R.id.sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(helper.checkLen(username.getText(), nameLayout, 3) && helper.checkLetterOrDigit(username.getText(), nameLayout)){
                    if(helper.checkLen(password.getText(), passwordLayout, 6) && helper.checkLetterOrDigit(password.getText(), passwordLayout)){
                        ParseUser.logInInBackground(username.getText().toString(), password.getText().toString(), new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException e) {
                                if (e == null) {
                                    startActivity(new Intent(getActivity(), MainActivity.class));
                                } else {
                                    String message = e.getMessage();
                                    if (message.toLowerCase().contains("java")) {
                                        message = e.getMessage().substring(e.getMessage().indexOf(" "));
                                    }
                                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });


        view.findViewById(R.id.switch_sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SignInFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });
    }
}