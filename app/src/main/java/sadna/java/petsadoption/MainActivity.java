package sadna.java.petsadoption;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.parse.ParseObject;

import java.util.Date;
import java.util.List;

import sadna.java.petsadoption.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private GoogleSignInOptions gso;

    private String welcomeFragmentName;
    private String watchPetsFragmentName;
    private String offerToAdoptionFragmentName;
    private String petDetailsFragmentName;
    private String addPetFragmentName;
    private String watchMessagesFragmentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Parse.com Object
        ParseObject entity = new ParseObject("FirstClass");

        entity.put("message", (new Date()).toString());

        // Saves the new object.
        // Notice that the SaveCallback is totally optional!
        entity.saveInBackground(e -> {
            if (e==null){
                Log.v("SAVE", "Save was done");
            }else{
                //Something went wrong
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        FirebaseApp.initializeApp(this);
        /*gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);*/


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        welcomeFragmentName = WelcomeFragment.class.getName();
        watchPetsFragmentName = WatchPetsFragment.class.getName();
        offerToAdoptionFragmentName = OfferToAdoptionFragment.class.getName();
        petDetailsFragmentName = PetDetailsFragment.class.getName();
        addPetFragmentName = AddPetFragment.class.getName();
        watchMessagesFragmentName = WatchMessagesFragment.class.getName();
        //TODO:
        //if GoogleSignInAccount returns null, then the user is not signed in already
        //else, the user is signed in
        //update ui accordingly
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navigateBack();
        /*NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();*/
    }

    @Override
    public void onBackPressed() {
        navigateBack();
    }

    private boolean navigateBack(){
        Fragment currentFragment = getSupportFragmentManager().getFragments().get(0).getChildFragmentManager()
                .getFragments().get(0);
        String currentFragmentName = currentFragment.getClass().getName();
        if (watchPetsFragmentName.equals(currentFragmentName)) {
            NavHostFragment.findNavController(currentFragment)
                    .navigate(R.id.action_WatchPetsFragment_to_WelcomeFragment);
            return true;
        } else if (offerToAdoptionFragmentName.equals(currentFragmentName)) {
            NavHostFragment.findNavController(currentFragment)
                    .navigate(R.id.action_OfferToAdoptionFragment_to_WelcomeFragment);
            return true;
        } else if (addPetFragmentName.equals(currentFragmentName)) {
            AddPetFragment addPetFragment = (AddPetFragment)currentFragment;
            addPetFragment.startShowingProgressDialog();
            NavHostFragment.findNavController(currentFragment)
                    .navigate(R.id.action_AddPetFragment_to_OfferToAdoptionFragment);
            return true;
        } else if (watchMessagesFragmentName.equals(currentFragmentName)) {
            NavHostFragment.findNavController(currentFragment)
                    .navigate(R.id.action_WatchMessagesFragment_to_WelcomeFragment);
            return true;
        } else if (petDetailsFragmentName.equals(currentFragmentName)){
            PetDetailsFragment petDetailsFragment = (PetDetailsFragment)currentFragment;
            petDetailsFragment.startShowingProgressDialog();
            PetDetailsFragment currentPetDetailsFragment = (PetDetailsFragment)currentFragment;
            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String owner_id = currentPetDetailsFragment.getArguments().getString("ownerId");
            if (!currentUserId.equals(owner_id)){
                NavHostFragment.findNavController(currentFragment)
                        .navigate(R.id.action_PetDetailsFragment_to_WatchPetsFragment);
            } else {
                NavHostFragment.findNavController(currentFragment)
                        .navigate(R.id.action_PetDetailsFragment_to_OfferToAdoptionFragment);
            }
            return false;
        } else if (welcomeFragmentName.equals(currentFragmentName)){
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Closing Application")
                    .setMessage("Are you sure you want to close the application?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            System.exit(0);
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        }
        return false;
    }

}