package sadna.java.petsadoption;

import android.content.DialogInterface;
import android.content.Intent;
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

public class WelcomeFragment extends Fragment implements OnCompleteListener<AuthResult> {

    private FragmentWelcomeBinding binding;

    private FirebaseAuth mAuth;
    private GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;

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
        //TODO: fix default_web_client_id2 string resource
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id2))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        mAuth = FirebaseAuth.getInstance();

        signInButton = binding.btnLogin;
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        FirebaseUser user = mAuth.getCurrentUser();
        updateUI(user);


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
                    updateUI(null);
                }
            }
        });

        binding.btnWatchPets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!DatabaseHandler.isConnected(WelcomeFragment.this.getContext())) {
                    Toast.makeText(getActivity(), "No Internet Connection",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                MainActivity.startShowingProgressDialog(getContext());
                NavHostFragment.findNavController(WelcomeFragment.this)
                        .navigate(R.id.action_WelcomeFragment_to_WatchPetsFragment);
            }
        });

        binding.btnWatchMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!DatabaseHandler.isConnected(WelcomeFragment.this.getContext())) {
                    Toast.makeText(getActivity(), "No Internet Connection",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if(mAuth.getCurrentUser() != null) {
                    MainActivity.startShowingProgressDialog(getContext());
                    NavHostFragment.findNavController(WelcomeFragment.this)
                            .navigate(R.id.action_WelcomeFragment_to_WatchMessagesFragment);
                } else {
                    Toast.makeText(getActivity(), "Please Sign In in order to watch messages",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        binding.btnOfferToAdoption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!DatabaseHandler.isConnected(WelcomeFragment.this.getContext())) {
                    Toast.makeText(getActivity(), "No Internet Connection",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if(mAuth.getCurrentUser() != null) {
                    NavHostFragment.findNavController(WelcomeFragment.this)
                            .navigate(R.id.action_WelcomeFragment_to_AddPetFragment);
                } else {
                    Toast.makeText(getActivity(), "Please Sign In in order to offer a pet",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        binding.btnMyPets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!DatabaseHandler.isConnected(WelcomeFragment.this.getContext())) {
                    Toast.makeText(getActivity(), "No Internet Connection",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if(mAuth.getCurrentUser() != null) {
                    MainActivity.startShowingProgressDialog(getContext());
                    NavHostFragment.findNavController(WelcomeFragment.this)
                            .navigate(R.id.action_WelcomeFragment_to_MyPetsFragment);
                } else {
                    Toast.makeText(getActivity(), "Please Sign In in order to watch your pets",
                            Toast.LENGTH_LONG).show();
                }
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
                updateUI(null);
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

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), this);
    }

    private void updateUI(FirebaseUser user){
        if(user != null) {
            String user_name = user.getDisplayName();
            binding.tvWelcome.setText("Logged in as "+user_name);
            TextView textView = (TextView) signInButton.getChildAt(0);
                textView.setText("Sign out");
            binding.btnWatchMessages.setEnabled(true);
            binding.btnOfferToAdoption.setEnabled(true);
            binding.btnMyPets.setEnabled(true);
            binding.tvSignInInfo.setVisibility(View.INVISIBLE);
            //ToDo: Check that it doesn't exist already
        } else {
            binding.tvWelcome.setText("Welcome to Pets Adoption!");
            TextView textView = (TextView) signInButton.getChildAt(0);
            textView.setText("Sign in");
            binding.btnWatchMessages.setEnabled(false);
            binding.btnOfferToAdoption.setEnabled(false);
            binding.btnMyPets.setEnabled(false);
            binding.tvSignInInfo.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        if (task.isSuccessful()) {
            // Sign in success, update UI with the signed-in user's information
            Log.d("GoogleActivity", "signInWithCredential:success");
            FirebaseUser user = mAuth.getCurrentUser();
            Toast.makeText(getActivity(), "Login was successful",
                    Toast.LENGTH_SHORT).show();
            String email = user.getEmail();
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


}