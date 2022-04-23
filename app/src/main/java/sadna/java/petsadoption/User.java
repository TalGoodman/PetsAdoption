package sadna.java.petsadoption;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.parse.ParseUser;

public class User extends ParseUser {
    private String userID;
    private String userEmail;
    private String userDisplayName;
    private String userPassword;

    /**
     * A User in the database
     * @param user
     */
    public User(FirebaseUser user) {
        this.userID = FirebaseAuth.getInstance().getCurrentUser().getProviderData().get(0).getUid();
        this.userEmail = FirebaseAuth.getInstance().getCurrentUser().getProviderData().get(0).getEmail();
        this.userDisplayName = FirebaseAuth.getInstance().getCurrentUser().getProviderData().get(0).getDisplayName();
        this.userPassword = FirebaseAuth.getInstance().getCurrentUser().getProviderId();
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
