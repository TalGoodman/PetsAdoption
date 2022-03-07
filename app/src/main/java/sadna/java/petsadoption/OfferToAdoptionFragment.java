package sadna.java.petsadoption;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import sadna.java.petsadoption.databinding.FragmentOfferToAdoptionBinding;
import sadna.java.petsadoption.databinding.FragmentWatchPetsBinding;

public class OfferToAdoptionFragment extends Fragment {
    public static final int MAX_LIST_SIZE = 50;

    private FragmentOfferToAdoptionBinding binding;

    private RecyclerView recyclerView;
    private ImageView[] imagesList;
    private String[] petsTextList;
    private Button[] buttonsList;

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