package sadna.java.petsadoption;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.parse.ParseException;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import sadna.java.petsadoption.databinding.FragmentAddPetBinding;

//AddPetFragment is a fragment for offering a pet for adoption by other users
//The fragment has a button adding a picture of the pet
//Input fields: EditTexts, spinners, and a checkbox
//for adding details about the pet
//A button to add the pet to the system
//and a button to navigate back
public class AddPetFragment extends Fragment implements View.OnClickListener {
    private FragmentAddPetBinding binding;

    private byte[] petImageByteArray;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddPetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }




    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Spinner element
        Spinner spinner = binding.spSpecieContentAdd;

        // Spinner Drop down elements
        List<String> categories = DatabaseHandler.getSpeciesNames();

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>( spinner.getContext(), android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        petImageByteArray = null;

        //initialize OnClickListener for buttons and image view
        binding.btnAddPet.setOnClickListener(this);
        binding.btnBackAdd.setOnClickListener(this);
        binding.btnSetPhoto.setOnClickListener(this);
        binding.ivPetImageAdd.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    //Handles the picture input
    @Deprecated
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK && data !=null) {
            return;
        }

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 9002){
                final Uri imageUri;
                InputStream imageStream = null;
                ByteArrayOutputStream byteStream = null;
                try {
                    imageUri = data.getData();
                    imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                    byteStream = new ByteArrayOutputStream();
                    final Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteStream);
                    petImageByteArray = byteStream.toByteArray();
                    if (petImageByteArray.length > 15000000) {
                        //if the picture is too heavy, notify the user
                        Toast.makeText(getActivity(), "File size must be at most 15MB", Toast.LENGTH_LONG).show();
                    } else {
                        binding.ivPetImageAdd.setImageBitmap(bitmap);
                        Toast.makeText(getActivity(), "Picture was set successfully",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
                } finally {
                    if (imageStream != null){
                        try {
                            imageStream.close();
                        } catch (IOException ioException) {
                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                    if (byteStream != null) {
                        try {
                            byteStream.close();
                        } catch (IOException ioException) {
                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                }
            }
        } else {
            Toast.makeText(getActivity(), "You haven't picked An Image",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnAddPet) {
            //The button clicked was "Add Pet"
            if (!DatabaseHandler.isConnected(AddPetFragment.this.getContext())) {
                Toast.makeText(getActivity(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
                return;
            }

            if (petImageByteArray == null) {
                Toast.makeText(getActivity(), "Please Choose a picture of the pet",
                        Toast.LENGTH_LONG).show();
                return;
            }

            //Assign the pet details from the input to the variables
            String specie = binding.spSpecieContentAdd.getSelectedItem().toString();
            String name = binding.etPetNameContentAdd.getText().toString();
            String sex = binding.spSexContentAdd.getSelectedItem().toString();
            Boolean vaccinated = binding.cbVaccinatedAdd.isChecked();
            String diet = binding.spDietContentAdd.getSelectedItem().toString();
            String description = binding.etDescriptionContentAdd.getText().toString();
            FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
            String fbUserID;
            if (fbUser != null) {
                fbUserID = fbUser.getUid();
            } else {
                Toast.makeText(getActivity(), "Something went wrong",Toast.LENGTH_LONG).show();
                return;
            }

            if (name.equals("")) {
                //Pet name should not be empty
                Toast.makeText(getActivity(), "Invalid Pet Name",Toast.LENGTH_LONG).show();
                return;
            } else if (name.length() > 15) {
                //Pet Name cannot be too long
                Toast.makeText(getActivity(), "Pet Name is too long",Toast.LENGTH_LONG).show();
                return;
            }

            //add the new pet to the database
            try {
                DatabaseHandler.createPet(
                        fbUserID,
                        specie,
                        sex,
                        vaccinated ,
                        diet,
                        name,
                        description,
                        petImageByteArray);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //navigate back to WelcomeFragment
            NavHostFragment.findNavController(AddPetFragment.this)
                    .navigate(R.id.action_AddPetFragment_to_WelcomeFragment);
            Toast.makeText(getActivity(), "The pet was successfully added",
                    Toast.LENGTH_LONG).show();
        }


        else if (view.getId() == R.id.btnSetPhoto) {
            //The button clicked was "Set Picture"
            if (!DatabaseHandler.isConnected(AddPetFragment.this.getContext())) {
                Toast.makeText(getActivity(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
                return;
            }

            //open an activity for choosing a picture for the pet
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 9002);
        } else if (view.getId() == R.id.ivPetImageAdd) {
            //The image view was clicked
            if (!DatabaseHandler.isConnected(AddPetFragment.this.getContext())) {
                Toast.makeText(getActivity(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
                return;
            }
            //open an activity for choosing a picture for the pet
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 9002);
        } else if (view.getId() == R.id.btnBackAdd) {
            //The button "Back" was clicked
            if (!DatabaseHandler.isConnected(AddPetFragment.this.getContext())) {
                Toast.makeText(getActivity(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
                return;
            }
            //navigate back to WelcomeFragment
            NavHostFragment.findNavController(AddPetFragment.this)
                    .navigate(R.id.action_AddPetFragment_to_WelcomeFragment);
        }
    }
}