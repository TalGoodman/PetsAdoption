package sadna.java.petsadoption;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

public class MessagesListAdapter extends RecyclerView.Adapter<MessagesListAdapter.ViewHolder> {
    private List<ParseObject> messagesList;
    private WatchMessagesFragment fragment;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvMessage;
        public Button btnCopyEmail;
        public Button btnCopyName;
        public Button btnSendEmail;
        public Button btnDelete;

        public ViewHolder(View v) {
            super(v);
            tvMessage = (TextView) v.findViewById(R.id.tvMessage);
            btnCopyEmail = (Button) v.findViewById(R.id.btnCopyEmail);
            btnCopyName = (Button) v.findViewById(R.id.btnCopyName);
            btnSendEmail = (Button) v.findViewById(R.id.btnSendEmail);
            btnDelete = (Button) v.findViewById(R.id.btnDelete);
        }
    }


    @Override
    public MessagesListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.messages_list_item, parent, false);

        return new MessagesListAdapter.ViewHolder(itemView);
    }

    public MessagesListAdapter(List<ParseObject> messagesList, WatchMessagesFragment fragment) {
        this.messagesList = messagesList;
        this.fragment = fragment;
    }

    @Override
    public void onBindViewHolder(MessagesListAdapter.ViewHolder holder, int position) {
        String sender_id = messagesList.get(position).get("sender_id").toString();
        String pet_id = messagesList.get(position).get("pet_id").toString();
        ParseUser userParseUser = DatabaseHandler.getUserByID(sender_id);
        ParseObject petParseObject = DatabaseHandler.getPetByID(pet_id);

        String user_name = userParseUser.getUsername();
        String user_email = userParseUser.getEmail();
        String pet_name = petParseObject.get("pet_name").toString();

        String message_text = "Hi! " + user_name + " wants to adopt " + pet_name +
                ".\n" + user_name + "\'s Emails is: " + user_email + ".";

        holder.tvMessage.setText(message_text);

        holder.btnCopyEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) fragment.getActivity().
                        getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("user_email", user_email);
                clipboard.setPrimaryClip(clipData);
                Toast.makeText(fragment.getContext(), "Email Copied",Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnCopyName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) fragment.getActivity().
                        getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("user_name", user_name);
                clipboard.setPrimaryClip(clipData);
                Toast.makeText(fragment.getContext(), "Name Copied",Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent emailIntent = new Intent( android.content.Intent.ACTION_SEND);
                emailIntent.setType("plain/text");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                        new String[] { user_email });
                String subject = "Hello " + user_name + " I would like to adopt " + pet_name;
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                        subject);
                fragment.startActivity(Intent.createChooser(
                        emailIntent, "Send mail..."));
            }
        });

        String message_id = messagesList.get(position).get("message_id").toString();
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    DatabaseHandler.deleteMessageByID(message_id);
                    Toast.makeText(fragment.getContext(), "Message Deleted",Toast.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(fragment).navigate(R.id.action_WatchMessagesFragment_to_WatchMessagesFragment);
                } catch (Exception e) {
                    Toast.makeText(fragment.getContext(), "Something went wrong",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }
}
