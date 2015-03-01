package com.ar.ipsum.ipsumapp;


import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ar.ipsum.ipsumapp.Utils.AsyncHttpPost;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText user;
    private EditText password;
    private Button start;
    private boolean isEmailValid;
    private boolean isPasswordValid;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String name = "nameKey";
    public static final String pass = "passwordKey";
    public static final String tokenKey = "tokenKey";
    View view;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        sharedpreferences= getActivity().getSharedPreferences(MainActivity.MyPREFERENCES,
                Context.MODE_PRIVATE);

        view = inflater.inflate(R.layout.fragment_login,
                container, false);
        initView();
        return view;
    }

    //NETWORK
    private void onLoginClicked() {
        if(!isDataValid()) {
            Toast.makeText(getActivity().getBaseContext(),
                    "Login or password is incorrect", Toast.LENGTH_SHORT).show();
        } else {
            // do login request
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(name, user.getText().toString());
            editor.putString(pass, password.getText().toString());
            editor.commit();
            HashMap<String, String> data = new HashMap<String, String>();
            data.put("email", user.getText().toString());
            data.put("password", password.getText().toString());
            AsyncHttpPost asyncHttpPost = new AsyncHttpPost(data, getActivity());
            asyncHttpPost.execute("http://ipsumapi.herokuapp.com/login");
        }
    }

    public boolean isDataValid() {
        boolean isEmailValid = Patterns.EMAIL_ADDRESS.matcher(getEmail()).matches();
        boolean isPasswordValid = !getPassword().isEmpty();
        return isEmailValid && isPasswordValid;
    }

    public String getPassword() {
        return password.getText().toString().trim(); // mEditPassword - EditText
    }

    public String getEmail() {
        return user.getText().toString().trim(); // mEditEmail - EditText
    }

    private void initView() {
        user = (EditText) view.findViewById(R.id.user);
        password = (EditText) view.findViewById(R.id.password);

        String user1=sharedpreferences.getString(name,"");
        String pass1=sharedpreferences.getString(pass,"");
        String token1= sharedpreferences.getString(tokenKey,"");


        user.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            validateEmail(s.toString());
            updateLoginButtonState();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    });
        password.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            validatePassword(s.toString());
            updateLoginButtonState();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    });

    start = (Button) view.findViewById(R.id.start_button);
    start.setEnabled(false); // default state should be disabled
    start.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            onLoginClicked();
        }
    });

        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                boolean isValidKey = event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER;
                boolean isValidAction = actionId == EditorInfo.IME_ACTION_DONE;

                if (isValidKey || isValidAction) {
                    onLoginClicked();
                }
                return false;
            }
        });

        if (!user1.isEmpty() && !pass1.isEmpty()){
            user.setText(user1);
            password.setText(pass1);
        }
    }

    private void validatePassword(String text) {
        isPasswordValid = !text.isEmpty();
    }

    private void validateEmail(String text) {
        isEmailValid = Patterns.EMAIL_ADDRESS.matcher(text).matches();
    }

    private void updateLoginButtonState() {
        if(isEmailValid && isPasswordValid) {
            start.setEnabled(true);
        } else {
            start.setEnabled(false);
        }
    }



}
