package cv.cvmovel.enacol_nhaenkomenda;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RegistarActivity extends AppCompatActivity {

    public String tokenid="";
    private UserRegistarTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mUsername;
    private EditText mPasswordView;
    private EditText mNomeView;
    private EditText mLocalidadeView;
    private EditText mTelefoneView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registar);

        mUsername = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mNomeView = (EditText) findViewById(R.id.nomeCliente);
        mLocalidadeView = (EditText) findViewById(R.id.localidadeCliente);
        mTelefoneView = (EditText) findViewById(R.id.telefoneCliente);

        Button mEmailSignUpButton = (Button) findViewById(R.id.email_sign_up_button);
        mEmailSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegistar();
            }
        });

        TextView nUsernameSignInButton = (TextView) findViewById(R.id.link_signin);

        nUsernameSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(RegistarActivity.this, LoginActivity.class);
                RegistarActivity.this.startActivity(myIntent);


            }
        });

    }
    private void attemptRegistar() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUsername.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mUsername.getText().toString();
        String password = mPasswordView.getText().toString();

        String localidade = mLocalidadeView.getText().toString();
        String nome = mNomeView.getText().toString();

        String telefone = mTelefoneView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mUsername.setError(getString(R.string.error_field_required));
            focusView = mUsername;
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }else if (TextUtils.isEmpty(nome)) {
            mNomeView.setError(getString(R.string.error_field_required));
            focusView = mNomeView;
            cancel = true;
        }else if (TextUtils.isEmpty(telefone)) {
            mTelefoneView.setError(getString(R.string.error_field_required));
            focusView = mTelefoneView;
            cancel = true;
        }else if (TextUtils.isEmpty(localidade)) {
            mLocalidadeView.setError(getString(R.string.error_field_required));
            focusView = mLocalidadeView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //showProgress(true);
            mAuthTask = new UserRegistarTask(email, password, nome, localidade, telefone);
            mAuthTask.execute((Void) null);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            /*mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });*/
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class UserRegistarTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUser;
        private final String mPassword;
        private final String mNome;
        private final String mLocalidade;
        private final String mTelefone;

        UserRegistarTask(String user, String password,String Nome,String Localidade, String Telefone ) {
            mUser = user;
            mPassword = password;
            mLocalidade=Localidade;
            mNome = Nome;
            mTelefone=Telefone;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            URL url = null;

            try {
                url = new URL("http://10.16.18.26/appnhaencomenda/mobile/registo_mobile.php" +
                        "?nome="+mNome.replaceAll(" ","%20")+"&telefone="+mTelefone+
                        "&localidade="+mLocalidade.replaceAll(" ","%20")+
                        "&user="+ mUser.replace(" ","%20")+"&senha="+mPassword+
                        "");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                urlConnection.disconnect();

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder result = new StringBuilder();
                String line;
                while((line = reader.readLine()) != null) {
                    result.append(line);
                }

                tokenid = result.toString();

                return !result.toString().equals("-1");

                //System.out.println(result.toString());

            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Intent myIntent = new Intent(RegistarActivity.this, MainActivity.class);
                myIntent.putExtra("tokenid", tokenid); //Optional parameters
                myIntent.putExtra("username", mUser);
                RegistarActivity.this.startActivity(myIntent);
            } else {
                mUsername.setError("Utilizador já existe, escolha outro");
                mUsername.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
