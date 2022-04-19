package sadna.java.petsadoption;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;


public class DatabaseHandler {

//Create User
    public static void createUser(String user_id, String email,String user_name) {
        ParseUser user = new ParseUser();
        user.put("firebase_id", user_id);
        user.setUsername(user_name);
        user.setPassword("my pass");
        user.setEmail(email);

        // Other fields can be set just like any other ParseObject,
        // using the "put" method, like this: user.put("attribute", "its value");
        // If this field does not exists, it will be automatically created

        user.signUpInBackground(e -> {
            if (e == null) {
                // Hooray! Let them use the app now.
            } else {
                // Sign up didn't succeed. Look at the ParseException
                // to figure out what went wrong
                //Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //create user by tal
    public static void createUserTal(String user_id, String user_email, String user_name) {
        ParseObject user = new ParseObject("users");
        user.put("user_id", user_id);
        user.put("user_email",user_email);
        user.put("user_name", user_name);
        user.saveInBackground();
    }


    //ToDo: add the nececeary users info
    public static void createMessage(String pet_id, String owner_id) {
        ParseObject message = new ParseObject("messages");
        String message_id = Long.toString(System.currentTimeMillis(), 32).toUpperCase();

        message.put("pet_id", pet_id);
        message.put("message_id", message_id);
        message.put("owner_id", owner_id);

        String sender_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String sender_email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        message.put("sender_id", sender_id);
        message.put("sender_email", sender_email);

        // Saves the new object
        message.saveInBackground(e -> {
            if (e==null){
                //message.setObjectId(message_id);
                Log.d("createMessage", "createObject: "+message.toString());
            }else{
                Log.d("createMessage", "createObject: "+e.getMessage());
            }
        });

    }

    //https://parse-dashboard.back4app.com/apps/2e930e57-26cb-49bf-a16a-38c9effd1503/browser/pets
    public static void createPet(String owner_id,
                                 String species,
                                 String gender,
                                 Boolean vaccinated,
                                 String diet,
                                 String pet_name,
                                 String description,
                                 byte[] pet_image) throws ParseException {
        String pet_id = Long.toString(System.currentTimeMillis(), 32).toUpperCase();
        //ParseObject pet = ParseObject.createWithoutData("pets", pet_id);//new ParseObject("pets");
        ParseObject pet = new ParseObject("pets");
        pet.put("pet_id", pet_id);
        pet.put("owner_id",owner_id);
        pet.put("pet_name", pet_name);
        pet.put("species",  species);
        pet.put("vaccinated",  vaccinated);
        pet.put("diet",  diet);
        //pet.put("species", new ParseObject("Species")); //How do i set it to be a specific class?
        pet.put("gander", gender);
        pet.put("description", description);
        pet.put("pet_image", new ParseFile(pet_name+".png", pet_image)); //Will Be The Pet Image
        pet.saveInBackground(e -> {
            if (e==null){
                //Save was done
                Log.d("PetCreated",pet.getObjectId());
                pet.setObjectId(pet_id);
                Log.d("PetUpdated",pet.getObjectId());
                pet.saveInBackground();
            }else{
                //Something went wrong
                Log.d("PetCreationError",e.getMessage());
            }
        });
    }

    public static List<ParseObject> getUserPets() {
        String user_id = FirebaseAuth.getInstance().getCurrentUser().getProviderData().get(0).getUid();

        //This find function works synchronously.
        ParseQuery<ParseObject> query = new ParseQuery<>("pets").whereContains("owner_id", user_id);
        try {
            List<ParseObject> pets_list = query.find();
            //Log.d("Finding Pets", "List: " + pets_list.listIterator(1));
            pets_list.forEach(
                    (pet) -> {
                        Log.d("Finding User Pets", (String) pet.get("pet_name"));

                    }
            );
            return pets_list;
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<ParseObject> getPetsByKeysAndValues(String user_id, Map<String, Object> filterMap) {
        //This find function works synchronously.
        ParseQuery<ParseObject> query = new ParseQuery<>("pets").whereNotEqualTo("owner_id", user_id);;
        for (Map.Entry<String, Object> entry : filterMap.entrySet()) {
            query = query.whereEqualTo(entry.getKey(), entry.getValue());
        }
        try {
            List<ParseObject> pets_list = query.find();
            return pets_list;
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<ParseObject> getPetsByKeysAndValuesWithLimit(String user_id, Map<String, Object> filterMap, int limit, int skip) {
        //This find function works synchronously.
        ParseQuery<ParseObject> query = new ParseQuery<>("pets").setLimit(limit).setSkip(skip);
        if (user_id != null) {
            query = query.whereNotEqualTo("owner_id", user_id);
        }
        for (Map.Entry<String, Object> entry : filterMap.entrySet()) {
            query = query.whereEqualTo(entry.getKey(), entry.getValue());
        }
        try {
            List<ParseObject> pets_list = query.find();
            return pets_list;
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getNumberOfPetsByKeysAndValue(String user_id, Map<String, Object> filterMap) {
        ParseQuery<ParseObject> query = new ParseQuery<>("pets");
        if (user_id != null) {
            query = query.whereNotEqualTo("owner_id", user_id);
        }
        for (Map.Entry<String, Object> entry : filterMap.entrySet()) {
            query = query.whereEqualTo(entry.getKey(), entry.getValue());
        }
        int number = 0;
        try {
            number = query.count();
        } catch (com.parse.ParseException e) {
            return number;
        }
        return number;
    }

    public static ArrayList<String> getSpeciesNames() {
        ArrayList<String> species_names = new ArrayList<String>();

        //This find function works synchronously.
        ParseQuery<ParseObject> query = new ParseQuery<>("Species");
        try {
            List<ParseObject> species = query.find();
            //Log.d("Finding Pets", "List: " + pets_list.listIterator(1));
            species.forEach(
                    (specie) -> {
                        species_names.add(specie.getString("species_name").toString());
                    }
            );
            return species_names;
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
    public static ParseObject getUserByID(String user_id) {
        ParseQuery<ParseObject> query = new ParseQuery<>("users").whereEqualTo("user_id", user_id);
        try {
            List<ParseObject> user =  query.find();
            Log.d("Finding User", (String) user.get(0).get("user_name"));
            return user.get(0);
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    */

    public static ParseUser getUserByID(String user_id) {
        ParseQuery<ParseUser> query = ParseUser.getQuery().whereEqualTo("firebase_id", user_id);
        try {
            List<ParseUser> user =  query.find();
            Log.d("getUserByID", (String) user.get(0).get("firebase_id"));
            return user.get(0);
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    //Synchronously Gets A Pet By ID.
    public static ParseObject getPetByID(String pet_id) {
       ParseQuery<ParseObject> query = new ParseQuery<>("pets").whereEqualTo("pet_id", pet_id);
        try {
            List<ParseObject> pet =  query.find();
            Log.d("Finding User Pets", (String) pet.get(0).get("pet_name"));
            return pet.get(0);
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ParseObject getMessageByID(String message_id) {
        ParseQuery<ParseObject> query = new ParseQuery<>("messages").whereEqualTo("message_id", message_id);
        try {
            List<ParseObject> message =  query.find();
            Log.d("Finding Message", (String) message.get(0).get("message_id"));
            return message.get(0);
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<ParseObject> getMessagesByPetID(String pet_id) {
        ParseQuery<ParseObject> query = new ParseQuery<>("messages").whereEqualTo("pet_id", pet_id);
        try {
            List<ParseObject> messages =  query.find();
            return messages;
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void deleteMessageByID(String message_id) {
        ParseObject message_to_remove = getMessageByID(message_id);
        if (message_to_remove != null) {message_to_remove.deleteInBackground(e -> {
            if (e == null) {
                //ToDo: make a toast somehow | Toast.makeText(this, "Delete Successful", Toast.LENGTH_SHORT).show();
                Log.d("deleteMessage", message_to_remove+" delete successfuly");
            }
            ;
        });
        }
    }

    public static String deletePetByID(String pet_id) {
        AtomicReference<String> deletage_message = new AtomicReference<>("");
        deleteMessagesByKeyAndValue("pet_id", pet_id);
        ParseObject pet_to_remove = getPetByID(pet_id);
        if (pet_to_remove != null) {pet_to_remove.deleteInBackground(e -> {
            if (e == null) {
                //ToDo: make a toast somehow | Toast.makeText(this, "Delete Successful", Toast.LENGTH_SHORT).show();
                deletage_message.set(pet_id + " delete successfuly");
                Log.d("deletePetByID", deletage_message.get());
            }
            ;
        });
        }
        return deletage_message.get();
    }

    public static void deleteMessagesByKeyAndValue(String key, String value) {
        List<ParseObject> messages_list = getMessagesByKeyAndValue(key, value);
        for (ParseObject parseObj:messages_list
             ) {
            parseObj.deleteInBackground();
        }
    }

    public static List<ParseObject> getPetsOfOtherUsers(String user_id) {
        //This find function works synchronously.
        ParseQuery<ParseObject> query = new ParseQuery<>("pets").whereNotEqualTo("owner_id", user_id);
        try {
            List<ParseObject> pets_list = query.find();
            pets_list.forEach(
                    (pet) -> {
                        Log.d("Finding Other Users Pets", (String) pet.get("pet_name"));
                    }
            );
            return pets_list;
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<ParseObject> getPetsOfOtherUsersWithLimit(String user_id, int limit, int skip) {
        //This find function works synchronously.
        ParseQuery<ParseObject> query = new ParseQuery<>("pets").setLimit(limit).setSkip(skip).whereNotEqualTo("owner_id", user_id);
        try {
            List<ParseObject> pets_list = query.find();
            return pets_list;
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean findIfPetRequested(String pet_id, String user_id) {
        if (user_id == null) {
            return false;
        }
        ParseQuery<ParseObject> query = new ParseQuery<>("messages").whereEqualTo("pet_id", pet_id).whereEqualTo("sender_id", user_id);
        try {
            List<ParseObject> message =  query.find();
            if (message.size() > 0) {
                if (pet_id.equals(message.get(0).getString("pet_id"))){
                    return true;
                }
            }
            return false;
        } catch (com.parse.ParseException e) {
            return false;
        }
    }

    public static List<ParseObject> getMessagesByKeyAndValue(String key, String value) {
        //This find function works synchronously.
        ParseQuery<ParseObject> query = new ParseQuery<>("messages").whereEqualTo(key, value);
        try {
            List<ParseObject> messages_list = query.find();
            return messages_list;
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<ParseObject> getMessagesByKeyAndValueWithLimit(String key, String value, int limit, int skip) {
        //This find function works synchronously.
        ParseQuery<ParseObject> query = new ParseQuery<>("messages").setLimit(limit).setSkip(skip).whereEqualTo(key, value);
        try {
            List<ParseObject> messages_list = query.find();
            return messages_list;
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getNumberOfMessagesByUser(String user_id) {
        ParseQuery<ParseObject> query = new ParseQuery<>("messages");
        if (user_id == null) {
            return 0;
        }
        query = query.whereEqualTo("owner_id", user_id);
        int number = 0;
        try {
            number = query.count();
        } catch (com.parse.ParseException e) {
            return number;
        }
        return number;
    }

    public static List<ParseObject> getNotRequestedPets(String user_id) {
        List<ParseObject> messages_list = getMessagesByKeyAndValue("sender_id", user_id);
        Set<String> requested_pets_ids = new HashSet<>();
        for(int i = 0; i < messages_list.size(); i++) {
            String pet_id = messages_list.get(i).get("pet_id").toString();
            requested_pets_ids.add(pet_id);
        }
        ParseQuery<ParseObject> query = new ParseQuery<>("pets").whereNotContainedIn("pet_id", requested_pets_ids);
        try {
            List<ParseObject> pets_list = query.find();
            pets_list.forEach(
                    (pet) -> {
                        Log.d("getNotRequestedPets", (String) pet.get("pet_name"));
                    }
            );
            return pets_list;
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Set<String> getRequestedPetsIds(String user_id) {
        List<ParseObject> messages_list = getMessagesByKeyAndValue("sender_id", user_id);
        Set<String> requested_pets_ids = new HashSet<>();
        for (int i = 0; i < messages_list.size(); i++) {
            String pet_id = messages_list.get(i).get("pet_id").toString();
            requested_pets_ids.add(pet_id);
        }
        return requested_pets_ids;
    }

    public static int getNumberOfPets() {
        ParseQuery<ParseObject> query = new ParseQuery<>("pets");
        int number = 0;
        try {
            number = query.count();
        } catch (com.parse.ParseException e) {
            return number;
        }
        return number;
    }

    public static int getNumberOfPetsOfOtherUsers(String user_id) {
        //This find function works synchronously.
        ParseQuery<ParseObject> query = new ParseQuery<>("pets").whereNotEqualTo("owner_id", user_id);
        int number = 0;
        try {
            number = query.count();
        } catch (com.parse.ParseException e) {
            return number;
        }
        return number;
    }

    public static List<ParseObject> getNotRequestedPetsWithLimit(Set<String> requested_pets_ids, int limit, int skip) {
        ParseQuery<ParseObject> query = new ParseQuery<>("pets").setLimit(limit).setSkip(skip).whereNotContainedIn("pet_id", requested_pets_ids);
        try {
            List<ParseObject> pets_list = query.find();
            return pets_list;
        } catch (com.parse.ParseException e) {
            return null;
        }
    }

    //getPetsByFilter
    public static List<ParseObject> getPetsByFilter(            String specie,
                                                                String gander,
                                                                Boolean vaccinated,
                                                                String diet) {
        //This find function works synchronously.
        ParseQuery<ParseObject> query = new ParseQuery<>("pets");
        query.whereMatches("specie", specie);
        query.whereMatches("vaccinated", vaccinated.toString());
        query.whereMatches("gander", gander);
        query.whereMatches("diet", diet);
        try {
            List<ParseObject> pets_list = query.find();
            return pets_list;
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
            return null;
        }
    }



    public static List<ParseObject> getAllPets() {
        //This find function works synchronously.
        ParseQuery<ParseObject> query = new ParseQuery<>("pets");
        try {
            List<ParseObject> pets_list = query.find();
            //Log.d("Finding Pets", "List: " + pets_list.listIterator(1));
            pets_list.forEach(
                    (pet) -> {
                                Log.d("Finding Pets", (String) pet.get("pet_name"));
                             }
            );
//            Log.d("Pet: ", (String) pets_list.get(1).get("pet_name"));
            return pets_list;
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<ParseObject> getAllPetsWithLimit(int limit, int skip) {
        //This find function works synchronously.
        ParseQuery<ParseObject> query = new ParseQuery<>("pets").setLimit(limit).setSkip(skip);
        try {
            List<ParseObject> pets_list = query.find();
            return pets_list;
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    //This find function works synchronously.
    public static List<ParseObject> find(String className) {
        ParseQuery<ParseObject> query = new ParseQuery<>(className);
        try {
            List<ParseObject> list = query.find();
            //ToDo: לעבור על הרשימה עם list.listIterator(i)
            Log.d("Finding "+className+"objects", "List: " + list.listIterator(1));
            //forEach(ParseObject pet in list);
//            Log.d("Pet: ", (String) list.get(1).get("pet_name"));
            return list;
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    //Get Pet Image From Parse Object
    public static Bitmap getPetImage(ParseObject petObject, PetsListAdapter.ItemViewHolder holder)
    {
        final Bitmap[] bmp = new Bitmap[1];
        ParseFile thumbnail = (ParseFile) petObject.get("pet_image");
        thumbnail.getDataInBackground
                (
                    new GetDataCallback()
                    {
                        public void done(byte[] data, ParseException e)
                        {
                            if (e == null)  {
                                bmp[0] = BitmapFactory.decodeByteArray(data, 0,data.length);
                                holder.petImage.setImageBitmap(bmp[0]);
                            }
                        };
                    }
                );
        return bmp[0];
    }

    public static boolean isConnected(Context context) {
        boolean result = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (cm != null) {
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                        result = true;
                    }
                }
            }
        } else {
            if (cm != null) {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if (activeNetwork != null) {
                    // connected to the internet
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI ||
                            activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE ||
                            activeNetwork.getType() == ConnectivityManager.TYPE_VPN) {
                        result = true;
                    }
                }
            }
        }
        return result;
    }
}