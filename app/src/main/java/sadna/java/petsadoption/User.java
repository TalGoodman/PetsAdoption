package sadna.java.petsadoption;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class User {
    private String userID;
    private String userEmail;
    private String userDisplayName;

    public User(FirebaseUser user) {
        this.userID = FirebaseAuth.getInstance().getCurrentUser().getProviderData().get(0).getUid();
        this.userEmail = FirebaseAuth.getInstance().getCurrentUser().getProviderData().get(0).getEmail();
        this.userDisplayName = FirebaseAuth.getInstance().getCurrentUser().getProviderData().get(0).getDisplayName();
    }

    public String getUserID() {
        return userID;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }
}
