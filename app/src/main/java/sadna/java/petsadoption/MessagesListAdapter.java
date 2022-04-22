package sadna.java.petsadoption;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

public class MessagesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private List<ParseObject> messagesList;
    private WatchMessagesFragment fragment;

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView tvMessage;
        public Button btnCopyEmail;
        public Button btnCopyName;
        public Button btnSendEmail;
        public Button btnDelete;

        public ItemViewHolder(View v) {
            super(v);
            tvMessage = (TextView) v.findViewById(R.id.tvMessage);
            btnCopyEmail = (Button) v.findViewById(R.id.btnCopyEmail);
            btnCopyName = (Button) v.findViewById(R.id.btnCopyName);
            btnSendEmail = (Button) v.findViewById(R.id.btnSendEmail);
            btnDelete = (Button) v.findViewById(R.id.btnDelete);
        }
    }

    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.pbItemLoading);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        View itemView;
        if (viewType == VIEW_TYPE_ITEM) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.messages_list_item, parent, false);
            return new MessagesListAdapter.ItemViewHolder(itemView);
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_loading, parent, false);
            return new MessagesListAdapter.LoadingViewHolder(itemView);
        }
    }

    public MessagesListAdapter(List<ParseObject> messagesList, WatchMessagesFragment fragment) {
        this.messagesList = messagesList;
        this.fragment = fragment;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MessagesListAdapter.LoadingViewHolder) {
            return;
        }
        MessagesListAdapter.ItemViewHolder itemHolder = (MessagesListAdapter.ItemViewHolder)holder;
        String sender_id = messagesList.get(position).get("sender_id").toString();
        String pet_id = messagesList.get(position).get("pet_id").toString();
        ParseUser userParseUser = DatabaseHandler.getUserByID(sender_id);
        ParseObject petParseObject = DatabaseHandler.getPetByID(pet_id);

        int position_in_messages_list = position;
        String user_name = userParseUser.getUsername();
        String user_email = messagesList.get(position).get("sender_email").toString();
        String pet_name = petParseObject.get("pet_name").toString();
        String message_id = messagesList.get(position).get("message_id").toString();

        String message_text = "Hi! " + user_name + " wants to adopt " + pet_name +
                ".\n" + user_name + "\'s Emails is: " + user_email + ".";

        fragment.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                itemHolder.tvMessage.setText(message_text);

                itemHolder.btnCopyEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ClipboardManager clipboard = (ClipboardManager) fragment.getActivity().
                                getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText("user_email", user_email);
                        clipboard.setPrimaryClip(clipData);
                        Toast.makeText(fragment.getContext(), "Email Copied",Toast.LENGTH_SHORT).show();
                    }
                });

                itemHolder.btnCopyName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ClipboardManager clipboard = (ClipboardManager) fragment.getActivity().
                                getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText("user_name", user_name);
                        clipboard.setPrimaryClip(clipData);
                        Toast.makeText(fragment.getContext(), "Name Copied",Toast.LENGTH_SHORT).show();
                    }
                });

                itemHolder.btnSendEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!DatabaseHandler.isConnected(fragment.getContext())) {
                            Toast.makeText(fragment.getContext(), "No Internet Connection",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }
                        final Intent emailIntent = new Intent( Intent.ACTION_SENDTO).setType("plain/text")
                                .setData(Uri.parse("mailto:"));
                        emailIntent.putExtra(Intent.EXTRA_EMAIL,
                                new String[] { user_email });
                        String subject = "Hello " + user_name + " I would like to adopt " + pet_name;
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT,
                                subject);
                        emailIntent.setPackage("com.google.android.gm");
                        try {
                            fragment.startActivity(Intent.createChooser(
                                    emailIntent, "Send mail..."));
                        } catch (android.content.ActivityNotFoundException e) {
                            Toast.makeText(fragment.getContext(), "There is no gmail client installed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                itemHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!DatabaseHandler.isConnected(fragment.getContext())) {
                            Toast.makeText(fragment.getContext(), "No Internet Connection",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }
                        ProgressDialog progress;
                        Handler handler = new Handler();
                        try {
                            DatabaseHandler.deleteMessageByID(message_id);
                            progress = ProgressDialog.show(fragment.getContext(), "Deleting Message", "Wait a second...");
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    if (progress != null && progress.isShowing()){
                                        progress.dismiss();
                                    }
                                }
                            }, 1500);
                            Toast.makeText(fragment.getContext(), "Message Deleted",Toast.LENGTH_SHORT).show();
                            fragment.messageDeleted(position_in_messages_list);
                        } catch (Exception e) {
                            Toast.makeText(fragment.getContext(), "Something went wrong",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (messagesList.get(position) == null) {
            return VIEW_TYPE_LOADING;
        }
        return VIEW_TYPE_ITEM;
    }
}
