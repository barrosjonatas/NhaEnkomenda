package cv.cvmovel.enacol_nhaenkomenda;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CriarPedidoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final String PREFS_NAME = "MoradaFile";

    // TODO: Rename and change types of parameters
    private String username;
    private String tokenid;
    private TextView status;
    private int produto;
    private int quantidade;
    private int valor_a_entregar;
    private int modo_pagamento;

    private UserCriarPedidoTask mAuthTask = null;

    private OnFragmentInteractionListener mListener;

    public CriarPedidoFragment() {
        // Required empty public constructor
    }

    public static CriarPedidoFragment newInstance(String param1, String param2) {
        CriarPedidoFragment fragment = new CriarPedidoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            username = getArguments().getString(ARG_PARAM1);
            tokenid = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_criar_pedido,
                container, false);

        status = (TextView)view.findViewById(R.id.textView2);

        final Spinner dropdown_prod = (Spinner) view.findViewById(R.id.spinner_produt);
        String[] item = new String[]{"Gaz Butano 3kg", "Gaz Butano 6kg", "Gaz Butano 12kg"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, item);
        dropdown_prod.setAdapter(adapter1);
        

        final EditText qtd = (EditText)view.findViewById(R.id.editText_qtd);


        Button minus = (Button)view.findViewById(R.id.button_minus);

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Integer.parseInt(qtd.getText().toString()) > 1){
                    int val = Integer.parseInt(qtd.getText().toString());
                    qtd.setText(""+(val-1));
                }

            }
        });

        Button plus = (Button)view.findViewById(R.id.button_plus);

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int val = Integer.parseInt(qtd.getText().toString());
                qtd.setText(""+(val+1));
            }
        });


        final Spinner dropdown_modo_pag = (Spinner) view.findViewById(R.id.spinner_modo_pag);
        String[] items2 = new String[]{"Dinheiro", "Vinti4", "Cheque"};
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, items2);
        dropdown_modo_pag.setAdapter(adapter3);

        final Spinner dropdown_morada = (Spinner) view.findViewById(R.id.spinner_morada);
        ArrayList<String> items3 = new ArrayList<String>();
        //String[] items3 = new String[]{"Casa 1", "Casa 2", "Casa 3"};

        SharedPreferences settings = this.getActivity().getSharedPreferences(PREFS_NAME, 0);

        int m = settings.getAll().size();

        if(m>0){
            m=m/2;
        }

        for (int i=0;i<m;i++) {
            String morada = settings.getString("morada"+i+1, "Default");
            String olc_morada = settings.getString("olc:" + morada, "");

            Toast.makeText(this.getActivity(), "" + olc_morada, Toast.LENGTH_LONG).show();

            items3.add(morada);
        }

        ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, items3);
        dropdown_morada.setAdapter(adapter4);

        Button button = (Button) view.findViewById(R.id.button_submeter);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                produto = dropdown_prod.getSelectedItemPosition()+1;
                modo_pagamento = dropdown_modo_pag.getSelectedItemPosition()+1;
                quantidade = Integer.parseInt(qtd.getText().toString());
                valor_a_entregar=0;
                attemptCriarPedido();
            }
        });
        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            //throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void attemptCriarPedido() {
        if (mAuthTask != null) {
            return;
        }

        boolean cancel = false;
        View focusView = null;

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask = new UserCriarPedidoTask(username, tokenid);
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
            });*/

            /*mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
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
           /* mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);*/
        }
    }

    public class UserCriarPedidoTask extends AsyncTask<Void, Void, Boolean> {

        private final String musername;
        private final String mtokenid;

        UserCriarPedidoTask(String username, String tokenid) {
            musername = username;
            mtokenid = tokenid;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            URL url = null;
            try {
                url = new URL("http://10.16.18.26/appnhaencomenda/mobile/pedido_mobile.php?username="+username+"" +
                        "&tokenid="+tokenid+"&prod="+produto+"&qtd="+quantidade+"&modo_pag="+modo_pagamento+"&valor_dinh="+valor_a_entregar+"");
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

                //urlConnection.disconnect();

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder result = new StringBuilder();
                String line;
                while((line = reader.readLine()) != null) {
                    result.append(line);
                }

                //return !result.toString().equals("-1");
                //System.out.println(result.toString());

                return !result.toString().equals("-1");

            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                status.setText("Pedido Solicitado");
                status.setTextColor(Color.GREEN);
            } else {
                status.setText("Erro ao criar pedido");
                status.setTextColor(Color.RED);
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
