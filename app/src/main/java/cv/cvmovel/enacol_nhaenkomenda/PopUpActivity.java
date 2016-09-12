package cv.cvmovel.enacol_nhaenkomenda;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PopUpActivity extends Activity {

    public static final String PREFS_NAME = "MoradaFile";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up);

        if(this.getIntent().hasExtra("olc")) {
            String txt = this.getIntent().getExtras().get("olc").toString();
            //Toast.makeText(PopUpActivity.this, "OLC: " +txt , Toast.LENGTH_SHORT).show();
            final TextView olcTxt = (TextView) findViewById(R.id.textViewOlc);
            Button gravar = (Button)findViewById(R.id.button_olc);
            final EditText morada = (EditText)findViewById(R.id.editText_olc);

            gravar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("morada"+(settings.getAll().size()/2)+1,morada.getText().toString());
                    editor.putString("olc:"+morada.getText().toString(),olcTxt.getText().toString());
                    editor.commit();
                    finish();
                }
            });

            olcTxt.setText(""+txt);
        }
    }
}
