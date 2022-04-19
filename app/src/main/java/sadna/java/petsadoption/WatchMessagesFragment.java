package sadna.java.petsadoption;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

import sadna.java.petsadoption.databinding.FragmentWatchMessagesBinding;


public class WatchMessagesFragment extends Fragment {
    public static final int ITEMS_TO_LOAD = 2;

    private FragmentWatchMessagesBinding binding;
    private RecyclerView recyclerView;
    private MessagesListAdapter recyclerViewAdapter;

    private List<ParseObject> messages_list;
    private String currentUserId;
    private boolean isLoading;
    private int itemsLoaded;
    private int numberOfMessages;

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

        isLoading = false;
        itemsLoaded = 0;

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            currentUserId = firebaseUser.getUid();
        } else {
            return;
        }
        numberOfMessages = DatabaseHandler.getNumberOfMessagesByUser(currentUserId);

        populateData();
        initAdapter();
        initScrollListener();
        //List<ParseObject> messages_list = DatabaseHandler.getMessagesByKeyAndValue("owner_id", currentUserId);


        //MessagesListAdapter adapter = new MessagesListAdapter(messages_list, WatchMessagesFragment.this);
        //recyclerView.setAdapter(adapter);
        //recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void messageDeleted(int position) {
        messages_list.remove(position);
        recyclerViewAdapter.notifyItemRemoved(position);
        itemsLoaded--;
        numberOfMessages--;
    }

    private void populateData() {
        messages_list = DatabaseHandler
                .getMessagesByKeyAndValueWithLimit("owner_id", currentUserId, 4, itemsLoaded);
        itemsLoaded = 4;
    }

    private void initAdapter() {
        recyclerViewAdapter = new MessagesListAdapter(messages_list, WatchMessagesFragment.this);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()){
            @Override
            public void onLayoutCompleted(RecyclerView.State state) {
                super.onLayoutCompleted(state);
                MainActivity.dismissProgressDialog();
            }
        });
    }

    private void initScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (itemsLoaded >= numberOfMessages) {
                    return;
                }

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int lastCompletelyVisibleItemPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (!isLoading) {
                    if (linearLayoutManager != null && lastCompletelyVisibleItemPosition == itemsLoaded - 1) {
                        if (!DatabaseHandler.isConnected(WatchMessagesFragment.this.getContext())) {
                            Toast.makeText(getActivity(), "No Internet Connection",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }
                        loadMore();
                        isLoading = true;
                    }
                }
            }
        });


    }

    private void loadMore() {
        messages_list.add(null);
        recyclerViewAdapter.notifyItemInserted(messages_list.size() - 1);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                messages_list.remove(messages_list.size() - 1);
                int scrollPosition = messages_list.size();
                recyclerViewAdapter.notifyItemRemoved(scrollPosition);
                int nextLimit = itemsLoaded + ITEMS_TO_LOAD;
                List<ParseObject> append_list = new ArrayList<>();

                if (itemsLoaded < nextLimit) {
                    append_list = DatabaseHandler
                            .getMessagesByKeyAndValueWithLimit("owner_id", currentUserId, ITEMS_TO_LOAD, itemsLoaded);
                    messages_list.addAll(append_list);
                    itemsLoaded += ITEMS_TO_LOAD;
                    if (itemsLoaded > numberOfMessages) {
                        itemsLoaded = numberOfMessages;
                    }
                }

                recyclerViewAdapter.notifyItemRangeInserted(messages_list.size() - append_list.size(), append_list.size());
                recyclerView.scrollToPosition(itemsLoaded - 5);
                isLoading = false;
            }
        }, 4000);
    }

}