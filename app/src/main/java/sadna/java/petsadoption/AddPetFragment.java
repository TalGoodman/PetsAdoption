package sadna.java.petsadoption;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.parse.ParseException;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import sadna.java.petsadoption.databinding.FragmentAddPetBinding;


public class AddPetFragment extends Fragment {
    private FragmentAddPetBinding binding;

    private byte[] petImageByteArray;
    private Pet newPet;

    private ProgressDialog progress;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentAddPetBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }




    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progress = null;
        /*DEBUGGING SPECIES VALUE
        Toast.makeText(getParentFragment().getActivity(), binding.spSexContentAdd.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
         */

        /**********************************************************/

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

    /*********************************************************/

        //Add Pet Button
        binding.btnAddPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Specie = binding.spSpecieContentAdd.getSelectedItem().toString();
                String Name = binding.etPetNameContentAdd.getText().toString();
                String Identifier = Long.toString(System.currentTimeMillis(), 32).toUpperCase();
                String fbUserID = FirebaseAuth.getInstance().getCurrentUser().getProviderData().get(0).getUid();
                String Sex = binding.spSexContentAdd.getSelectedItem().toString();
                Boolean Vaccinated = binding.cbVaccinatedAdd.isChecked();
                String Diet = binding.spDietContentAdd.getSelectedItem().toString();
                String Description = binding.etDescriptionContentAdd.getText().toString();

                if (Name.equals("")) {
                    Toast.makeText(getActivity(), "Invalid Pet Name",Toast.LENGTH_LONG).show();
                    return;
                } else if (Description.equals("")) {
                    Toast.makeText(getActivity(), "Please Add Description",Toast.LENGTH_LONG).show();
                    return;
                }

                Pet newPet = new Pet(petImageByteArray, Specie, fbUserID,Name,Identifier, Sex,
                        Vaccinated, Diet, Description);
                String petJSON = newPet.toJSON();
                Toast.makeText(getActivity(), petJSON, Toast.LENGTH_LONG).show();

                progress = ProgressDialog.show(getContext(), "Loading", "Wait a second...");
                try {
                    DatabaseHandler.createPet(
                            newPet.getOwnerID(),
                            newPet.getSpecies()+"",
                            newPet.getSex()+"",
                            newPet.getVaccinated() ,
                            newPet.getDiet(),
                            newPet.getName(),
                            newPet.getDescription(),
                            newPet.getImage());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                NavHostFragment.findNavController(AddPetFragment.this)
                        .navigate(R.id.action_AddPetFragment_to_OfferToAdoptionFragment);
            }
        });

        //Add Photo Button
        binding.btnSetPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 9002);
            }
        });

        //Tap Photo to upload
        binding.ivPetImageAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 9002);
            }
        });

        //Back Button
        binding.btnBackAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress = ProgressDialog.show(getContext(), "Loading", "Wait a second...");
                NavHostFragment.findNavController(AddPetFragment.this)
                        .navigate(R.id.action_AddPetFragment_to_OfferToAdoptionFragment);
            }
        });


    }

    @Override
    public void onDestroyView() {
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 9002){
                final Uri imageUri;
                InputStream imageStream = null;
                ByteArrayOutputStream byteStream = null;
                try {
                    imageUri = data.getData();
                    imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                    byteStream = new ByteArrayOutputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteStream);
                    petImageByteArray = byteStream.toByteArray();
                    if (petImageByteArray.length > 15000000) {
                        Toast.makeText(getActivity(), "File size must be at most 15MB", Toast.LENGTH_LONG).show();
                        return;
                    }
                    binding.ivPetImageAdd.setImageBitmap(bitmap);
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

    public void startShowingProgressDialog(){
        progress = ProgressDialog.show(getContext(), "Loading", "Wait a second...");
    }

    @Override
    public void onDestroy() {
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
        super.onDestroy();
    }

    public void onResume() {
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
        super.onResume();
    }
}