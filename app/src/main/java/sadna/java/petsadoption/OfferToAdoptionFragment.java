package sadna.java.petsadoption;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sadna.java.petsadoption.databinding.FragmentOfferToAdoptionBinding;

public class OfferToAdoptionFragment extends Fragment {

    private FragmentOfferToAdoptionBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOfferToAdoptionBinding.inflate(inflater, container, false);
        return binding.getRoot();
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