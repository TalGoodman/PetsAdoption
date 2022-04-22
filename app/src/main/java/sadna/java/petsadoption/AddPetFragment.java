package sadna.java.petsadoption;

import android.app.Activity;
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


public class AddPetFragment extends Fragment implements View.OnClickListener {
    private FragmentAddPetBinding binding;

    private byte[] petImageByteArray;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentAddPetBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }




    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
                if (!DatabaseHandler.isConnected(AddPetFragment.this.getContext())) {
                Toast.makeText(getActivity(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
                return;
            }
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
            } else if (Name.length() > 15) {
                Toast.makeText(getActivity(), "Pet Name is too long",Toast.LENGTH_LONG).show();
                return;
            }

            Pet newPet = new Pet(petImageByteArray, Specie, fbUserID,Name,Identifier, Sex,
                    Vaccinated, Diet, Description);
            String petJSON = newPet.toJSON();

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
                    .navigate(R.id.action_AddPetFragment_to_WelcomeFragment);
            Toast.makeText(getActivity(), "The pet was successfully added",
                    Toast.LENGTH_LONG).show();
        }


        else if (view.getId() == R.id.btnSetPhoto) {
            if (!DatabaseHandler.isConnected(AddPetFragment.this.getContext())) {
                Toast.makeText(getActivity(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
                return;
            }
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 9002);
        }


        else if (view.getId() == R.id.ivPetImageAdd) {
            if (!DatabaseHandler.isConnected(AddPetFragment.this.getContext())) {
                Toast.makeText(getActivity(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
                return;
            }
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 9002);
        }

        else if (view.getId() == R.id.btnBackAdd) {
            if (!DatabaseHandler.isConnected(AddPetFragment.this.getContext())) {
                Toast.makeText(getActivity(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
                return;
            }
            NavHostFragment.findNavController(AddPetFragment.this)
                    .navigate(R.id.action_AddPetFragment_to_WelcomeFragment);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}