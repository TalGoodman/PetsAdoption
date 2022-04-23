package sadna.java.petsadoption;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import sadna.java.petsadoption.databinding.FragmentWelcomeBinding;

/*
WelcomeFragment is the Home page fragment
It contains buttons for navigation to other fragments like:
- "WatchPetsFragment" for watching pets added by other users
- "MyPetsFragment" for watching pets added by the current user
- "WatchMessagesFragment" for watching pets adoption requests messages sent to the current user
- "AddPetFragment" for posting a pet of the current user so other users
    can ask to adopt it
Another button in WelcomeFragment is the Google Sign In button
It is required to sign in in order to make the "Watch Messages", "Offer Pet For Adoption"
and "My Pets" buttons active
 */
public class WelcomeFragment extends Fragment implements OnCompleteListener<AuthResult>, View.OnClickListener {

    private FragmentWelcomeBinding binding;

    private FirebaseAuth mAuth;
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;

    private SignInButton signInButton;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentWelcomeBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!DatabaseHandler.isConnected(this.getContext())) {
            //If there is no connection, shows this dialog and then exits
            new AlertDialog.Builder(this.getContext())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("No Internet Connection")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getActivity().finish();
                            System.exit(0);
                        }

                    })
                    .show();
        }
        //initialize GoogleSignInOptions
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id2))
                .requestEmail()
                .build();

        //initialize GoogleSignInClient
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        //initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        //initialize SignInButton
        signInButton = binding.btnLogin;
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        //initialize btnWatchPets background color
        binding.btnWatchPets.setBackgroundColor(Color.argb(190, 50, 25, 150));

        FirebaseUser user = mAuth.getCurrentUser();
        updateUI(user);     //a method that updates the UI

        //initialize OnCLickListener for the buttons
        binding.btnLogin.setOnClickListener(this);
        binding.btnWatchPets.setOnClickListener(this);
        binding.btnWatchMessages.setOnClickListener(this);
        binding.btnMyPets.setOnClickListener(this);
        binding.btnOfferToAdoption.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    //Handles sign in with google account
    @Deprecated
    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK && data !=null) {
            return;
        }
        if (requestCode == 9001) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                updateUI(null);
            }
        }
    }

    //Handles user authentication with FireBase
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("GoogleActivity", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), this);
    }

    //Updates the UI according to whether the user is already Signed In or not
    private void updateUI(FirebaseUser user){
        if(user != null) {
            String user_name = user.getDisplayName();
            binding.tvWelcome.setText("Logged in as "+user_name);
            TextView textView = (TextView) signInButton.getChildAt(0);
                textView.setText("Sign out");
            binding.btnWatchMessages.setClickable(true);
            binding.btnWatchMessages.setBackgroundColor(Color.argb(190, 50, 25, 150));
            binding.btnOfferToAdoption.setClickable(true);
            binding.btnOfferToAdoption.setBackgroundColor(Color.argb(190, 50, 25, 150));
            binding.btnMyPets.setClickable(true);
            binding.btnMyPets.setBackgroundColor(Color.argb(190, 50, 25, 150));
            binding.tvSignInInfo.setVisibility(View.INVISIBLE);
        } else {
            binding.tvWelcome.setText("Welcome to Pets Adoption!");
            TextView textView = (TextView) signInButton.getChildAt(0);
            textView.setText("Sign in");
            binding.btnWatchMessages.setClickable(false);
            binding.btnWatchMessages.setBackgroundColor(Color.argb(120, 90, 90, 90));
            binding.btnOfferToAdoption.setClickable(false);
            binding.btnOfferToAdoption.setBackgroundColor(Color.argb(120, 90, 90, 90));
            binding.btnMyPets.setClickable(false);
            binding.btnMyPets.setBackgroundColor(Color.argb(120, 90, 90, 90));
            binding.tvSignInInfo.setVisibility(View.VISIBLE);
        }
    }

    //Handles user authentication with FireBase
    //and adds to user to the Database
    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        if (task.isSuccessful()) {
            // Sign in success, update UI with the signed-in user's information
            Log.d("GoogleActivity", "signInWithCredential:success");
            FirebaseUser user = mAuth.getCurrentUser();
            if (user == null) {
                return;
            }
            Toast.makeText(getActivity(), "Signed in successfully",
                    Toast.LENGTH_LONG).show();
            String email = user.getEmail();
            //Adds the user to the Database
            DatabaseHandler.createUser(user.getUid(), email, user.getDisplayName()); //Create A User on login
            updateUI(user);
        } else {
            Exception err = task.getException();
            Toast.makeText(getActivity(), err.getMessage(), Toast.LENGTH_SHORT).show();
            updateUI(null);
        }
    }

    public void onResume() {
        super.onResume();
        MainActivity.dismissProgressDialog();
    }

    @Override
    public void onClick(View view) {
        //initialize buttons OnClick listeners
        if (view.getId() == R.id.btnLogin) {
            if (!DatabaseHandler.isConnected(WelcomeFragment.this.getContext())) {
                Toast.makeText(getActivity(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
                return;
            }
            if(mAuth.getCurrentUser() == null){
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 9001);
            } else {
                mAuth.signOut();
                mGoogleSignInClient.signOut();
                Toast.makeText(getActivity(), "Signed Out",
                        Toast.LENGTH_LONG).show();
                updateUI(null);
            }
        }

        if (view.getId() == R.id.btnWatchPets) {
            if (!DatabaseHandler.isConnected(WelcomeFragment.this.getContext())) {
                Toast.makeText(getActivity(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
                return;
            }
            MainActivity.startShowingProgressDialog(getContext());
            NavHostFragment.findNavController(WelcomeFragment.this)
                    .navigate(R.id.action_WelcomeFragment_to_WatchPetsFragment);
        }

        if (view.getId() == R.id.btnWatchMessages) {
            if (!DatabaseHandler.isConnected(WelcomeFragment.this.getContext())) {
                Toast.makeText(getActivity(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
                return;
            }
            if(mAuth.getCurrentUser() != null) {
                MainActivity.startShowingProgressDialog(getContext());
                NavHostFragment.findNavController(WelcomeFragment.this)
                        .navigate(R.id.action_WelcomeFragment_to_WatchMessagesFragment);
            }
        }

        if (view.getId() == R.id.btnOfferToAdoption) {
            if (!DatabaseHandler.isConnected(WelcomeFragment.this.getContext())) {
                Toast.makeText(getActivity(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
                return;
            }
            if(mAuth.getCurrentUser() != null) {
                NavHostFragment.findNavController(WelcomeFragment.this)
                        .navigate(R.id.action_WelcomeFragment_to_AddPetFragment);
            }
        }

        if (view.getId() == R.id.btnMyPets) {
            if (!DatabaseHandler.isConnected(WelcomeFragment.this.getContext())) {
                Toast.makeText(getActivity(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
                return;
            }
            if(mAuth.getCurrentUser() != null) {
                MainActivity.startShowingProgressDialog(getContext());
                NavHostFragment.findNavController(WelcomeFragment.this)
                        .navigate(R.id.action_WelcomeFragment_to_MyPetsFragment);
            }
        }
    }
}