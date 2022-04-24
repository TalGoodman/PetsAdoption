package sadna.java.petsadoption;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
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
import com.google.firebase.auth.FirebaseUser;

import sadna.java.petsadoption.databinding.ActivityMainBinding;

//Main Activity of the application
public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private String welcomeFragmentName;
    private String watchPetsFragmentName;
    private String myPetsFragmentName;
    private String petDetailsFragmentName;
    private String addPetFragmentName;
    private String watchMessagesFragmentName;

    public static ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //Initializes the default FirebaseApp instance using
        FirebaseApp.initializeApp(this);

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

        //get fragments names
        welcomeFragmentName = WelcomeFragment.class.getName();
        watchPetsFragmentName = WatchPetsFragment.class.getName();
        myPetsFragmentName = MyPetsFragment.class.getName();
        petDetailsFragmentName = PetDetailsFragment.class.getName();
        addPetFragmentName = AddPetFragment.class.getName();
        watchMessagesFragmentName = WatchMessagesFragment.class.getName();
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

        if (id == R.id.action_to_welcome_fragment) {
            //Navigate to Home(WelcomeFragment)
            Fragment currentFragment = getSupportFragmentManager().getFragments().get(0).getChildFragmentManager()
                    .getFragments().get(0);
            String currentFragmentName = currentFragment.getClass().getName();
            if (watchPetsFragmentName.equals(currentFragmentName)) {
                NavHostFragment.findNavController(currentFragment)
                        .navigate(R.id.action_WatchPetsFragment_to_WelcomeFragment);
                return true;
            } else if (myPetsFragmentName.equals(currentFragmentName)) {
                NavHostFragment.findNavController(currentFragment)
                        .navigate(R.id.action_MyPetsFragment_to_WelcomeFragment);
                return true;
            } else if (addPetFragmentName.equals(currentFragmentName)) {
                AddPetFragment addPetFragment = (AddPetFragment)currentFragment;
                NavHostFragment.findNavController(currentFragment)
                        .navigate(R.id.action_AddPetFragment_to_WelcomeFragment);
                return true;
            } else if (watchMessagesFragmentName.equals(currentFragmentName)) {
                NavHostFragment.findNavController(currentFragment)
                        .navigate(R.id.action_WatchMessagesFragment_to_WelcomeFragment);
                return true;
            } else if (petDetailsFragmentName.equals(currentFragmentName)){
                NavHostFragment.findNavController(currentFragment)
                        .navigate(R.id.action_PetDetailsFragment_to_WelcomeFragment);
                return true;
            } else if (welcomeFragmentName.equals(currentFragmentName)){
                return false;
            }
        } else if (id == R.id.action_exit) {
            //Exit from the application
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        //Clear the Activity's bundle of the subsidiary fragments' bundles.
        bundle.clear();
    }

    @Override
    public boolean onSupportNavigateUp() {
        //Handle the "Back" button click in toolbar
        try {
            if (!DatabaseHandler.isConnected(this)) {
                Toast.makeText(this, "No Internet Connection",
                        Toast.LENGTH_LONG).show();
                return false;
            }
            return navigateBack();
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong",
                    Toast.LENGTH_LONG).show();
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        //Handle phone's "Back" button click
        try {
            if (!DatabaseHandler.isConnected(this)) {
                Toast.makeText(this, "No Internet Connection",
                        Toast.LENGTH_LONG).show();
                return;
            }
            navigateBack();
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong",
                    Toast.LENGTH_LONG).show();
            return;
        }
    }

    private boolean navigateBack(){
        //Navigates back
        Fragment currentFragment = getSupportFragmentManager().getFragments().get(0).getChildFragmentManager()
                .getFragments().get(0);
        String currentFragmentName = currentFragment.getClass().getName();
        if (watchPetsFragmentName.equals(currentFragmentName)) {
            NavHostFragment.findNavController(currentFragment)
                    .navigate(R.id.action_WatchPetsFragment_to_WelcomeFragment);
            return true;
        } else if (myPetsFragmentName.equals(currentFragmentName)) {
            NavHostFragment.findNavController(currentFragment)
                    .navigate(R.id.action_MyPetsFragment_to_WelcomeFragment);
            return true;
        } else if (addPetFragmentName.equals(currentFragmentName)) {
            AddPetFragment addPetFragment = (AddPetFragment)currentFragment;
            NavHostFragment.findNavController(currentFragment)
                    .navigate(R.id.action_AddPetFragment_to_WelcomeFragment);
            return true;
        } else if (watchMessagesFragmentName.equals(currentFragmentName)) {
            NavHostFragment.findNavController(currentFragment)
                    .navigate(R.id.action_WatchMessagesFragment_to_WelcomeFragment);
            return true;
        } else if (petDetailsFragmentName.equals(currentFragmentName)){
            PetDetailsFragment petDetailsFragment = (PetDetailsFragment)currentFragment;
            MainActivity.startShowingProgressDialog(petDetailsFragment.getContext());
            PetDetailsFragment currentPetDetailsFragment = (PetDetailsFragment)currentFragment;
            FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
            String currentUserId = null;
            if (fbUser != null) {
                currentUserId = fbUser.getUid();
            }
            String owner_id = currentPetDetailsFragment.getArguments().getString("ownerId");
            if (!owner_id.equals(currentUserId)){
                NavHostFragment.findNavController(currentFragment)
                        .navigate(R.id.action_PetDetailsFragment_to_WatchPetsFragment);
            } else {
                NavHostFragment.findNavController(currentFragment)
                        .navigate(R.id.action_PetDetailsFragment_to_MyPetsFragment);
            }
            return false;
        } else if (welcomeFragmentName.equals(currentFragmentName)){
            //If the user navigates back from WelcomeFragment
            //The "Closing Application" dialog is shown
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

    //Method for handling usage of ProgressDialog
    //The methods are static because it allows better dismiss timing
    public static void startShowingProgressDialog(Context c){
        progress = ProgressDialog.show(c, "Loading", "Just a few moments...");
    }

    //Method for handling usage of ProgressDialog
    //The methods are static because it allows better dismiss timing
    public static void dismissProgressDialog(){
        if (progress != null && progress.isShowing()){
            progress.dismiss();
        }
    }
}