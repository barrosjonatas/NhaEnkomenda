package cv.cvmovel.enacol_nhaenkomenda;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemFragment extends ListFragment{
     String user;
     String token;
    private ArrayList<String> dados;
    TableLayout table;
    Activity activit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dados = new ArrayList<String>();

        table = (TableLayout)this.getActivity().findViewById(R.id.tab);
        //table.removeAllViews();

        activit = this.getActivity();

        AsyncTask<Void, Void, String> task = new AsyncTask<Void,Void,String>()
        {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    //Thread.sleep(1000);
                    URL url = null;

                    try {
                        url = new URL("http://10.16.18.26/appnhaencomenda/mobile/clientepedidos_mobile.php?user="+user+"&tokenid="+token+"");
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

                            if(!line.equals("-1")) {
                                try {
                                    JSONArray menuObject = new JSONArray(result.toString());

                                    for (int i = 0; i < menuObject.length(); i++) {
                                        String dt = menuObject.getJSONObject(i).getString("data").toString();
                                        String prod=menuObject.getJSONObject(i).getString("produto").toString();
                                        String qtd=menuObject.getJSONObject(i).getString("quantidade").toString();
                                        String val=menuObject.getJSONObject(i).getString("valor").toString();
                                        String moto=menuObject.getJSONObject(i).getString("motoboy").toString();

                                        dados.add(dt);
                                        dados.add(prod);
                                        dados.add(qtd);
                                        dados.add(val);
                                        if(moto.isEmpty()) {
                                            dados.add("n/a");
                                        }else{
                                            dados.add(moto);
                                        }
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }else{
                                //dados.add("Ainda não solicitou nenhum Pedido");
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
              }
               return null;
            }
            @Override
            protected void onPostExecute(String result) {
                List<Map<String, String>> list = new ArrayList<Map<String, String>>();
                Map<String, String> map;
                int count=0;

                if(!dados.isEmpty()){
                    count = dados.size()/5;
                }


                map = new HashMap<String, String>();
                map.put("data", "Data");
                map.put("produto", "Produto");
                map.put("quantidade", "Qtd");
                map.put("valor", "Valor");
                map.put("motoboy", "Motoboy");
                list.add(map);
                int x = 0;

                for(int i = 0; i < count; i++) {
                    map = new HashMap<String, String>();

                    String dt = dados.get(x);
                    x++;
                    String prodt = dados.get(x);
                    x++;
                    String qtds = dados.get(x);
                    x++;
                    String val = dados.get(x);
                    x++;
                    String moto = dados.get(x);
                    x++;

                    SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

                    try {

                        dt = myFormat.format(fromUser.parse(dt));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    map.put("data", dt );
                    map.put("produto", prodt );
                    map.put("quantidade",qtds );
                    map.put("valor", val);
                    map.put("motoboy", moto);
                    list.add(map);
                }

                SimpleAdapter adapter = new SimpleAdapter(activit, list,R.layout.tabela,new String[]{"data","produto","quantidade","valor","motoboy"},new int[] { R.id.data, R.id.produto,R.id.quantidade,R.id.valor,R.id.motoboy });
                setListAdapter(adapter);
            }
        };
        task.execute();
    }
}
