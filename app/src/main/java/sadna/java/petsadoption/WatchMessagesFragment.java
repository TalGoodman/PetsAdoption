package sadna.java.petsadoption;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.parse.ParseObject;

import java.util.List;

import sadna.java.petsadoption.databinding.FragmentWatchMessagesBinding;


public class WatchMessagesFragment extends Fragment {
    private FragmentWatchMessagesBinding binding;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWatchMessagesBinding.inflate(inflater, container, false);
        recyclerView = binding.rvWatchMessagesList;

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        List<ParseObject> messages_list = DatabaseHandler.getMessagesByKeyAndValue("owner_id", currentUserId);

        MessagesListAdapter adapter = new MessagesListAdapter(messages_list, WatchMessagesFragment.this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}