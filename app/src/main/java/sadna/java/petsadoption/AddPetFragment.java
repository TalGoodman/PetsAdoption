package sadna.java.petsadoption;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;

import sadna.java.petsadoption.databinding.FragmentAddPetBinding;
import sadna.java.petsadoption.databinding.FragmentWatchPetsBinding;

public class AddPetFragment extends Fragment {
    private FragmentAddPetBinding binding;

    private ImageView image;
    private Spinner spGenus;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddPetBinding.inflate(inflater, container, false);
        spGenus = binding.sGenusContentAdd;
        spGenus.setSelection(1);

        return inflater.inflate(R.layout.fragment_add_pet, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}