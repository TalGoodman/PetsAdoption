package sadna.java.petsadoption;

import android.os.AsyncTask;
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

import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import sadna.java.petsadoption.databinding.FragmentWatchMessagesBinding;

/*
WatchMessagesFragment is a fragment holding a list of the messages
sent to the current user. The messages are sent automatically when
another user requests to adopt a pet posted by the current user
 */
public class WatchMessagesFragment extends Fragment {
    public static final int ITEMS_TO_LOAD = 1;      //Number of items to load at scrolling

    private FragmentWatchMessagesBinding binding;

    //the recycler view's items are "messages_list_item" layouts used for displaying messages
    private RecyclerView recyclerView;
    private MessagesListAdapter recyclerViewAdapter;

    //holds a list of ParseObjects of messages obtained from the database
    private List<ParseObject> messages_list;
    private List<ParseObject> messages_to_load;


    //the id of the current user
    private String currentUserId;
    private boolean isLoading;  //indicates whether new messages are currently loaded
    private int itemsLoaded;    //counts the number of messages already loaded
    private int numberOfMessages;   //holds the number of relevant messages in the database

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

        isLoading = false;  //initializing
        itemsLoaded = 0;    //initializing

        //initialize currentUserId
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            currentUserId = firebaseUser.getUid();
        } else {
            return;
        }
        //initialize numberOfMessages
        numberOfMessages = DatabaseHandler.getNumberOfMessagesByUser(currentUserId);

        //initialize recyclerView and recyclerViewAdapter
        populateData();
        initAdapter();
        initScrollListener();

        Toast.makeText(getActivity(), "Messages Sent to you",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /*
        updates the relevant fields and notifies the MessagesListAdapter
        when a message was deleted
     */
    public void messageDeleted(int position) {
        messages_list.remove(position);
        recyclerViewAdapter.notifyItemRemoved(position);
        itemsLoaded--;
        numberOfMessages--;
    }

    //read the first items from the database
    private void populateData() {
        messages_list = DatabaseHandler
                .getMessagesByKeyAndValueWithLimit("owner_id", currentUserId, 4, itemsLoaded);
        itemsLoaded = 4;
    }

    //initialize the recyclerview adapter
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

    //initialize scroll listener for the recycler view
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
                    //don't try to load more items if all of the messages were already loaded
                    return;
                }

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                final int lastCompletelyVisibleItemPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (!isLoading) {
                    if (linearLayoutManager != null && lastCompletelyVisibleItemPosition == itemsLoaded - 1) {
                        if (!DatabaseHandler.isConnected(WatchMessagesFragment.this.getContext())) {
                            Toast.makeText(getActivity(), "No Internet Connection",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }
                        isLoading = true;
                        loadMore();
                    }
                }
            }
        });


    }

    //load more items
    private void loadMore() {
        //add a temporary null item to the end of messages_list and recycler view
        messages_list.add(null);
        recyclerViewAdapter.notifyItemInserted(messages_list.size() - 1);

        //read more messages from the database and add them to messages_list
        messages_to_load = DatabaseHandler
                .getMessagesByKeyAndValueWithLimitAsync("owner_id", currentUserId, ITEMS_TO_LOAD, itemsLoaded);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //remove the temporary null at the end of messages_list and notify the adapter
                messages_list.remove(messages_list.size() - 1);
                int scrollPosition = messages_list.size();
                recyclerViewAdapter.notifyItemRemoved(scrollPosition);

                //read more messages from the database and add them to messages_list
                messages_list.addAll(messages_to_load);
                itemsLoaded += ITEMS_TO_LOAD;
                if (itemsLoaded > numberOfMessages) {
                    itemsLoaded = numberOfMessages;
                }

                //update the recycler view
                recyclerViewAdapter.notifyItemRangeInserted(messages_list.size() - messages_to_load.size(), messages_to_load.size());
            }
        }, 150);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //move the scroll position and allow to load more items
                recyclerView.scrollToPosition(itemsLoaded - 4);
                isLoading = false;
            }
        }, 500);
    }

}