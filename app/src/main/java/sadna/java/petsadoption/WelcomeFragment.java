package sadna.java.petsadoption;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;

import java.util.concurrent.Executor;

import sadna.java.petsadoption.databinding.FragmentWelcomeBinding;

public class WelcomeFragment extends Fragment implements OnCompleteListener<AuthResult> {

    private FragmentWelcomeBinding binding;

    private FirebaseAuth mAuth;
    private GoogleSignInOptions gso;

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

        //TODO: fix default_web_client_id2 string resource
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id2))
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        mAuth = FirebaseAuth.getInstance();

        signInButton = binding.btnLogin;
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        updateUI(mAuth.getCurrentUser());


        /*ActivityResultLauncher<Intent> launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        //handleSignInResult(data);
                    }
                });*/

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                //launcher.launch(signInIntent);
                if(mAuth.getCurrentUser() == null){
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, 9001);
                } else {
                    mAuth.signOut();
                    updateUI(null);
                }
            }
        });

        binding.btnWatchPets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(WelcomeFragment.this)
                        .navigate(R.id.action_WelcomeFragment_to_WatchPetsFragment);
            }
        });

        binding.btnOfferToAdoption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(WelcomeFragment.this)
                        .navigate(R.id.action_WelcomeFragment_to_OfferToAdoptionFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == 9001) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                //if(progressBar.isShowing()){
                //    progressBar.dismiss();
                //}
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }
    /*private void handleSignInResult(Intent data) {
        try {
            SignInCredential credential = Identity.getSignInClient(getActivity()).getSignInCredentialFromIntent(data);
            // Signed in successfully
            binding.tvWelcome.setText("Logged in");
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            binding.tvWelcome.setText("Error");
        }
    }*/

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("GoogleActivity", "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        //showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), this);
    }

    private void updateUI(FirebaseUser user){
        if(user != null) {
            binding.tvWelcome.setText("Logged in");
            TextView textView = (TextView) signInButton.getChildAt(0);
            textView.setText("Sign out");
        } else {
            binding.tvWelcome.setText("Welcome to Pets Adoption!");
            TextView textView = (TextView) signInButton.getChildAt(0);
            textView.setText("Sign in");
        }
    }

    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        if (task.isSuccessful()) {
            // Sign in success, update UI with the signed-in user's information
            Log.d("GoogleActivity", "signInWithCredential:success");
            FirebaseUser user = mAuth.getCurrentUser();
            //progressBar.dismiss();
            Toast.makeText(getActivity(), "Login was successful",
                    Toast.LENGTH_SHORT).show();
            updateUI(user);
            Intent intent = new Intent(getActivity(),MainActivity.class);
            startActivity(intent);
            //Intent intent = new Intent(this, MainActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //this.startActivity(intent);
            //finish();
        } else {
            //progressBar.dismiss();
            Exception err = task.getException();
            Toast.makeText(getActivity(), err.getMessage(),
                    Toast.LENGTH_SHORT).show();
            updateUI(null);
        }
        // [START_EXCLUDE]
        //hideProgressDialog();
        // [END_EXCLUDE]
    }
}