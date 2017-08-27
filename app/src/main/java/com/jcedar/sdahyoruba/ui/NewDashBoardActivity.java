package com.jcedar.sdahyoruba.ui;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jcedar.sdahyoruba.R;
import com.jcedar.sdahyoruba.app.SdahYoruba;
import com.jcedar.sdahyoruba.helper.PrefUtils;
import com.jcedar.sdahyoruba.io.model.Hymn;
import com.jcedar.sdahyoruba.provider.DataContract;
import com.jcedar.sdahyoruba.provider.DatabaseHelper;
import com.jcedar.sdahyoruba.ui.PagerFragment.OnFragmentInteractionListener;
import com.jcedar.sdahyoruba.ui.section.HymnSectionFragment;
import com.jcedar.sdahyoruba.ui.section.HymnSectionFragment1;
import com.jcedar.sdahyoruba.ui.section.HymnSectionFragment2;
import com.jcedar.sdahyoruba.ui.section.HymnSectionFragment3;
import com.jcedar.sdahyoruba.ui.section.HymnSectionFragment4;
import com.jcedar.sdahyoruba.ui.section.HymnSectionFragment5;
import com.jcedar.sdahyoruba.ui.section.HymnSectionFragment6;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Afolayan on 27/05/2016.
 */
public class NewDashBoardActivity extends AppCompatActivity implements
        HymnsFragment.Listener, OnFragmentInteractionListener,
         PagerFragment.FabListener, FavoriteListFragment.Listener,
        TopicalIndexFragment.OnFragmentInteractionListener, HymnSectionFragment.Listener,
        HymnSectionFragment1.Listener,
        HymnSectionFragment2.Listener,
        HymnSectionFragment3.Listener,
        HymnSectionFragment4.Listener,
        HymnSectionFragment5.Listener,
        HymnSectionFragment6.Listener
{
    private static final String TAG = NewDashBoardActivity.class.getName();
    private static final String FAVORITE_ID = "favorite_id";
    private static final String FAVORITE_ID_BUNDLE = "favorite_bundle";
    //Defining Variables
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    private Set<Fragment> mHomeFragments = new HashSet<>();
    private ActionBarDrawerToggle drawerToggle;

    View view;

    PagerFragment pagerFragment = new PagerFragment();
    private Fragment mContent;
    private boolean isHome;
    public static ArrayList<Hymn> allHymns;

    public Toolbar getToolbar() {
        return toolbar;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_dashboard);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View headerView = navigationView.inflateHeaderView(R.layout.drawer_header);

        view = headerView;

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        drawerToggle = setupDrawerToggle();

        // Tie DrawerLayout events to the ActionBarToggle
        drawerLayout.setDrawerListener(drawerToggle);


        //initialize elements of the drawer
        ImageView profilePhoto = (ImageView) headerView.findViewById(R.id.profile_image);
        TextView username = (TextView) headerView.findViewById(R.id.username);
        TextView email = (TextView) headerView.findViewById(R.id.email);
        TextView roleString = (TextView) headerView.findViewById(R.id.roleString);
        roleString.setVisibility(View.GONE);


        //set UserName
        final String user = PrefUtils.getPersonal(this);
        username.setText("");
        username.setTextColor(getResources().getColor(R.color.white));

        //set User Email
        String mailTxt = PrefUtils.getEmail(this);
        email.setText("");
        email.setTextColor(getResources().getColor(R.color.white));


        roleString.setText(R.string.member);
        roleString.setTextColor(getResources().getColor(R.color.white));

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ft.replace(R.id.frame, pagerFragment, "HOME")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();

        allHymns = new DatabaseHelper(this).getHymns();

        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.getMenu().getItem(4).setTitle("Help & Feedback");

        SdahYoruba.getInstance().trackScreenView("Main Hymn Screen");
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                //Checking if the item is in checked state or not, if not make it in checked state
                if(item.isChecked()) item.setChecked(false);
                else item.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();

                Intent intent;
                Fragment fragment = null;
                Class fragmentClass = PagerFragment.class;

                FragmentManager fragmentManager;
                FragmentTransaction fragmentTransaction;

                //Check to see which item was being clicked and perform appropriate action

                switch (item.getItemId()){

                    case R.id.nav_hymn_list:

                        fragmentClass = PagerFragment.class;
                        break;
                    case R.id.nav_favourite:
                        fragmentClass = FavoriteListFragment.class;

                        break;

                    case R.id.nav_index:
                        fragmentClass = TopicalIndexFragment.class;
                        break;


                    case R.id.nav_settings:
                        intent = new Intent(NewDashBoardActivity.this, Settings.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_help:
                        sendMail();
                        break;

                    default:
                        fragmentClass = HymnsFragment.class;
                        Toast.makeText(getApplicationContext(),"Somethings Wrong",Toast.LENGTH_SHORT).show();
                        break;

                }
                try{
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception  e) {
                    e.printStackTrace();
                }

                /*if( item.getItemId() == R.id.nav_index){
                    final Fragment finalFragment = fragment;
                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            FragmentManager fm = getSupportFragmentManager();
                            if( getSupportFragmentManager().findFragmentByTag("HOME") != null){
                                fm.beginTransaction().remove(pagerFragment).commit();
                            }
                            fm.beginTransaction()
                                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                                    .replace(R.id.frame, finalFragment).commit();
                        }
                    }, 0);
                } else {*/
                FragmentManager fm = getSupportFragmentManager();
                if (getSupportFragmentManager().findFragmentByTag("HOME") != null) {
                    fm.beginTransaction().remove(pagerFragment).commit();
                }
                fm.beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.frame, fragment)
                        .commit();
                //}

                return true;
            }
        });
    }

    /*@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save the fragment state
        getSupportFragmentManager().putFragment( outState, "fragment", mContent );
    }*/

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void sendMail() {
        ShareCompat.IntentBuilder.from(this)
                .setType("message/rfc822")
                .addEmailTo("jcedarng@gmail.com")
                .setSubject("SDAH Yoruba")
                .setChooserTitle("Send Mail")
                .startChooser();
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, drawerLayout, R.mipmap.ic_menu, R.string.drawer_open,  R.string.drawer_close);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

/*    private void signOutUser() {
        if (mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
//            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_settings:
//                startActivity(new Intent(this, AndroidDatabaseManager.class));
                startActivity(new Intent(this, Settings.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }






    @Override
    public void onHymnSelected(final long hymnId) {
        /*Intent detailIntent = new Intent(Intent.ACTION_VIEW);
        Uri uri = DataContract.Hymns.buildHymnUri(hymnId);
        detailIntent.setData(uri);
        Log.e(TAG, "hymn id == " + hymnId);
        startActivity(detailIntent);*/
        goToSelectedHymn(hymnId);

    }

    @Override
    public void onFavoriteSelected(final long hymnId) {
        /*Intent intent = getIntent();
        Bundle bundle = new Bundle();
        bundle.putString(FAVORITE_ID, hymnId+"");
        intent.putExtras(bundle);
        Log.e(TAG, "fav id == " + hymnId);
        finish();
        startActivity(intent);*/

        goToSelectedHymn(hymnId);


    }

    public int goToSelectedHymn(long hymnId) {
        FragmentManager fm = getSupportFragmentManager();
        if( getSupportFragmentManager().findFragmentById(R.id.frame) != null){

            Log.e(TAG, "fragment isnt null");
            fm.beginTransaction()
                    .remove(getSupportFragmentManager()
                            .findFragmentById(R.id.frame))
                    .commit();
        }

        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.add(R.id.frame, PagerFragment.newInstance(hymnId), "HOME")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
        navigationView.getMenu().getItem(0).setChecked(true);

        return 0;
    }

    @Override
    public void onFragmentDetached(Fragment fragment) {
        mHomeFragments.remove(fragment);
    }

    @Override
    public void onFragmentAttached(Fragment fragment) {
        mHomeFragments.add(fragment);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onHymnSharedFab(int position) {
        String[] hymn = new DatabaseHelper(this).getItemAtPosition(position);

        String text = hymn[2].replaceAll("(\r\n|\n\r|\r|\n)", "<br />");
        Spanned toPresent = Html.fromHtml(text.trim());

        final String stringToShare = "Hymn " + hymn[0] + "\n" + hymn[1] + "\n" + toPresent + "\nSDAH Yoruba (c) 2016";

        AlertDialog.Builder builder = new AlertDialog.Builder( this ).setMessage(stringToShare);
               builder.setPositiveButton("Share", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain")
                                .putExtra(Intent.EXTRA_TEXT, stringToShare);

                        startActivity(Intent.createChooser(intent,
                                getResources().getString(R.string.action_share_hymn)));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();

        builder.setMessage(stringToShare);
        builder.setTitle("Share Hymn ");
        builder.show();


    }

    @Override
    public void onHymnLikedFab(int position) {
        if( isAlreadyFavorite(position) ){
            Snackbar.make(view, R.string.already_added, Snackbar.LENGTH_SHORT).show();

        } else {
            String[] hymn = new DatabaseHelper(this).getItemAtPosition(position);
            Log.e(TAG, "hymn no = " + hymn[0] + " \nHymn name = " + hymn[1]);

            try {
                ContentValues values = new ContentValues();
                values.put(DataContract.FavoriteHymns.SONG_ID, hymn[0]);
                values.put(DataContract.FavoriteHymns.SONG_NAME, hymn[1]);
                values.put(DataContract.FavoriteHymns.SONG_TEXT, hymn[2]);
                values.put(DataContract.FavoriteHymns.ENGLISH_VERSION, hymn[3]);

                getContentResolver().insert(DataContract.FavoriteHymns.CONTENT_URI, values);
                //Toast.makeText(this, R.string.added_successfully, Toast.LENGTH_SHORT).show();

                Snackbar.make(view, R.string.added_successfully, Snackbar.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, R.string.generic_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isAlreadyFavorite(int position){
        Cursor cursor = getContentResolver().query(DataContract.FavoriteHymns.CONTENT_URI,
                null, DataContract.FavoriteHymns.SONG_ID+"=?", new String[]{position+""}, null);

        if (cursor.moveToFirst()) {
                return true;
        }
        return false;
    }

    public static ArrayList<Hymn> getAllHymns(){
        return allHymns;
    }
}
