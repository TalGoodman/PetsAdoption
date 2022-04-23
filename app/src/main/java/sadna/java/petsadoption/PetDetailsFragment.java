package sadna.java.petsadoption;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.parse.ParseUser;

import sadna.java.petsadoption.databinding.FragmentPetDetailsBinding;

//PetDetailsFragment is a fragment showing the details of a pet
//The fragment has a button for requesting to adopt the pet
//and a button to navigate back
public class PetDetailsFragment extends Fragment implements View.OnClickListener {
    private FragmentPetDetailsBinding binding;

    private String ownerId;     //the id of the pet's owner
    private String petId;       //the id of the pet

    private FirebaseUser firebaseUser;
    private String currentUserId;       //the id of the current user

    //indicates whether the current user has already requested to adopt the pet
    private boolean isRequested;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPetDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //initialize ownerId, the id is passed to the current fragment in Bundle savedInstanceState
        //the method getArguments() returns the value for a specific key in the bundle
        ownerId = getArguments().getString("ownerId");

        //initialize currentUserId
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = null;
        if (firebaseUser != null) {
            currentUserId = firebaseUser.getUid();
        }

        //initialize isRequested with data from the bundle
        isRequested = getArguments().getBoolean("isRequested");

        //initialize some pet details with data from the bundle
        petId = getArguments().getString("petId");
        String petName = getArguments().getString("name");
        String petSpecie = getArguments().getString("specie");
        String petSex = getArguments().getString("sex");
        boolean petVaccinated = getArguments().getBoolean("vaccinated");
        String petDiet = getArguments().getString("diet");
        String petDescription = getArguments().getString("description");

        //read a ParseUser object of the owner user from the database
        ParseUser ownerParseObject = DatabaseHandler.getUserByID(ownerId);
        String ownerName = ownerParseObject.getUsername();

        binding.tvPetNameContent.setText(petName);
        binding.tvSpecieContent.setText(petSpecie);
        binding.tvSexContent.setText(petSex);
        binding.tvVaccinatedContent.setText(Boolean.toString(petVaccinated));
        binding.tvDietContent.setText(petDiet);
        binding.tvDescriptionContent.setText(petDescription);
        binding.tvOwnerNameContent.setText(ownerName);

        //the "Request To Adopt" button is available only if the user is already signed in
        if (currentUserId == null) {
            binding.btnRequestToAdopt.setEnabled(false);
        } else if (ownerId.equals(currentUserId)) {
            //if the current user is the pet owner, change the button to be "Delete Pet"
            //it is possible only if the prior fragment was MyPetsFragment
            binding.btnRequestToAdopt.setText(R.string.bttnTextDeletPet);
        } else {
            //the current user is signed in and the pet owner is another user
            if(isRequested) {
                //if the pet was requested by the current user
                //disable the button and change it's text to "Already Requested"
                binding.btnRequestToAdopt.setText(R.string.bttnAlreadyRequested);
                binding.btnRequestToAdopt.setTextColor(Color.GREEN);
                binding.btnRequestToAdopt.setEnabled(false);
            }
        }

        //initialize OnClickListener for buttons
        binding.btnBackPetDetails.setOnClickListener(this);
        binding.btnRequestToAdopt.setOnClickListener(this);

        //set pet image by getting data from the bundle
        if(getArguments()!=null)
        {
            byte[] data = getArguments().getByteArray("image_data");
            if (data != null)
            {
                final Bitmap bmp = BitmapFactory.decodeByteArray(data, 0,data.length);
                binding.ivPetImage.setImageBitmap(bmp);
            }
        }
    }

    @Override
    public void onClick(View view) {
        if(!ownerId.equals(currentUserId)) {
            if(firebaseUser != null) {
                if (view.getId() == R.id.btnRequestToAdopt) {
                    //the user requested to adopt the pet
                    if (!DatabaseHandler.isConnected(PetDetailsFragment.this.getContext())) {
                        Toast.makeText(getActivity(), "No Internet Connection",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    DatabaseHandler.createMessage(petId, ownerId);  //adds a new message to the database
                    //Log.d("btnRequestToAdopt.onClick", "RequestToAdaptHere");
                    ProgressDialog progress = ProgressDialog.show(getContext(), "Requesting", "Wait a second...");
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            if (progress != null && progress.isShowing()){
                                progress.dismiss();
                            }
                        }
                    }, 1500);
                    Toast.makeText(getContext(), "Pet Requested",Toast.LENGTH_LONG).show();
                    //navigate back to WatchPetsFragment
                    NavHostFragment.findNavController(PetDetailsFragment.this)
                            .navigate(R.id.action_PetDetailsFragment_to_WatchPetsFragment);
                }
            }

            //the current user is not the pet owner or the current user is not signed in
            if (view.getId() == R.id.btnBackPetDetails) {
                //navigate back to WatchPetsFragment
                if (!DatabaseHandler.isConnected(PetDetailsFragment.this.getContext())) {
                    Toast.makeText(getActivity(), "No Internet Connection",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                MainActivity.startShowingProgressDialog(getContext());
                NavHostFragment.findNavController(PetDetailsFragment.this)
                        .navigate(R.id.action_PetDetailsFragment_to_WatchPetsFragment);
            }
        } else if (ownerId.equals(currentUserId)){
            if (view.getId() == R.id.btnRequestToAdopt) {
                //in this case the pet's owner is the current user
                //so now the button btnRequestToAdopt functions as a delete button
                if (!DatabaseHandler.isConnected(PetDetailsFragment.this.getContext())) {
                    Toast.makeText(getActivity(), "No Internet Connection",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    DatabaseHandler.deletePetByID(petId);   //delete the pet from the database
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error",Toast.LENGTH_LONG).show();
                    return;
                }
                //Log.d("btnRequestToAdopt.onClick", "deletePetByID");
                Handler handler = new Handler();
                ProgressDialog progress = ProgressDialog.show(getContext(), "Deleting", "Wait a second...");
                handler.postDelayed(new Runnable() {
                    public void run() {
                        if (progress != null && progress.isShowing()){
                            progress.dismiss();
                        }
                    }
                }, 1500);
                Toast.makeText(getContext(), "Pet Deleted",Toast.LENGTH_LONG).show();
                //navigate back to MyPetsFragment
                NavHostFragment.findNavController(PetDetailsFragment.this)
                        .navigate(R.id.action_PetDetailsFragment_to_MyPetsFragment);
            }
            else if (view.getId() == R.id.btnBackPetDetails) {
                //navigate back to MyPetsFragment
                if (!DatabaseHandler.isConnected(PetDetailsFragment.this.getContext())) {
                    Toast.makeText(getActivity(), "No Internet Connection",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                MainActivity.startShowingProgressDialog(getContext());
                NavHostFragment.findNavController(PetDetailsFragment.this)
                        .navigate(R.id.action_PetDetailsFragment_to_MyPetsFragment);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}