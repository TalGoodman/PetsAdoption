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

import androidx.recyclerview.widget.RecyclerView;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

/*
    Adapter for recycler views of messages list items
 */
public class MessagesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;       //Message item
    private final int VIEW_TYPE_LOADING = 1;       //Loading item

    private List<ParseObject> messagesList;     //list of messages
    private WatchMessagesFragment fragment;     //The fragment which setAdapter was called from

    //Inner class for message list item view
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView tvMessage;
        public Button btnCopyEmail;
        public Button btnCopyName;
        public Button btnSendEmail;
        public Button btnDelete;

        //constructor for inner class
        public ItemViewHolder(View v) {
            super(v);
            tvMessage = (TextView) v.findViewById(R.id.tvMessage);      //view is in messages_list_item.xml
            btnCopyEmail = (Button) v.findViewById(R.id.btnCopyEmail);  //view is in messages_list_item.xml
            btnCopyName = (Button) v.findViewById(R.id.btnCopyName);    //view is in messages_list_item.xml
            btnSendEmail = (Button) v.findViewById(R.id.btnSendEmail);  //view is in messages_list_item.xml
            btnDelete = (Button) v.findViewById(R.id.btnDelete);        //view is in messages_list_item.xml
        }
    }

    //Inner class for loading item view
    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.pbItemLoading);   //view is in item_loading.xml
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        View itemView;
        //determine if the view should be a load item or message item
        //viewType is determined by the method getItemViewType
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

    @Override
    public int getItemViewType(int position) {
        //null item in the list indicated there should be a loading item
        if (messagesList.get(position) == null) {
            return VIEW_TYPE_LOADING;
        }
        return VIEW_TYPE_ITEM;
    }

    //constructor for MessagesListAdapter
    public MessagesListAdapter(List<ParseObject> messagesList, WatchMessagesFragment fragment) {
        this.messagesList = messagesList;
        this.fragment = fragment;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MessagesListAdapter.LoadingViewHolder) {
            //if the item is loading item there is nothing else to set
            return;
        }
        //item is message item:
        MessagesListAdapter.ItemViewHolder itemHolder = (MessagesListAdapter.ItemViewHolder)holder;
        //retrieve details about the message from the list of messages
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

        //set the UI of message item on UI thread
        fragment.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                itemHolder.tvMessage.setText(message_text);

                //copies the email address of the message sender to clipboard
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

                //copies the name of the message sender to clipboard
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

                //opens gmail for answering the message sender
                //also adds the sender email address and a default subject text
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
                        String subject = "Hello " + user_name + ", do you still want to adopt " + pet_name + "?";
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

                //deletes the message from both the database and the list
                //then calls messageDeleted in fragment WatchMessagesFragment
                //in order to update the recycler view and relevant fields
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
                            //calls messageDeleted of WatchMessagesFragment
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
}
