package cv.cvmovel.enacol_nhaenkomenda;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    String tokenid;
    String username;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(this.getIntent().hasExtra("tokenid")) {
            tokenid = this.getIntent().getExtras().getString("tokenid");
            username = this.getIntent().getExtras().getString("username");
        }
        final Intent op = this.getIntent();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Por implementar...->"+tokenid, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Intent myIntent = new Intent(getApplicationContext(), PopUpActivity.class);
                if(op.hasExtra("olc")) {
                    String olc = op.getExtras().getString("olc");
                    myIntent.putExtra("olc", olc);
                }
                startActivity(myIntent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header=navigationView.getHeaderView(0);

        TextView Username = (TextView)header.findViewById(R.id.UsernameID);

        Username.setText("Utilizador: "+username.substring(0,1).toUpperCase() + username.substring(1));

        ItemFragment historicos_pedidos = new ItemFragment();
        historicos_pedidos.user = username;
        historicos_pedidos.token = tokenid;
        FragmentManager manager = getFragmentManager();
        manager.beginTransaction().replace(R.id.relativelayout_for_fragment,historicos_pedidos).commit();

        fab.hide();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent myIntent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(myIntent);

            //return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            CriarPedidoFragment novo_pedido = CriarPedidoFragment.newInstance(username,tokenid);
            FragmentManager manager = getFragmentManager();
            manager.beginTransaction().replace(R.id.relativelayout_for_fragment,novo_pedido).commit();

            fab.hide();

        }else if (id == R.id.nav_slideshow) {
            ItemFragment historicos_pedidos = new ItemFragment();
            historicos_pedidos.user = username;
            historicos_pedidos.token = tokenid;
            FragmentManager manager = getFragmentManager();
            manager.beginTransaction().replace(R.id.relativelayout_for_fragment,historicos_pedidos).commit();

            fab.hide();

        } else if (id == R.id.nav_manage) {

            Intent myIntent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(myIntent);
            fab.hide();
        } else if (id == R.id.nav_share) {
            Fragment mapa = new MapViewFragment();
            FragmentManager manager = getFragmentManager();
            manager.beginTransaction().replace(R.id.relativelayout_for_fragment,mapa).commit();

            fab.show();
        } else if (id == R.id.nav_send) {

        }else if (id == R.id.logout) {
           this.finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
