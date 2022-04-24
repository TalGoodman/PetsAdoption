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
import com.google.firebase.auth.FirebaseUser;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.boltsinternal.Task;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;


public class DatabaseHandler {
    private final static int MAX_RUNNING_TIME_SECONDS = 10;

    /** Create User in the database
     *
     * @param user_id
     * @param email
     * @param user_name
     */
    public static void createUser(String user_id, String email,String user_name) {
        ParseUser user = new ParseUser();
        user.put("firebase_id", user_id);
        user.setUsername(user_name);
        user.setPassword("my pass");
        user.setEmail(email);

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


    /**
     * Creates a message in the database
     * @param pet_id
     * @param owner_id
     */
    public static void createMessage(String pet_id, String owner_id) {
        ParseObject message = new ParseObject("messages");
        String message_id = Long.toString(System.currentTimeMillis(), 32).toUpperCase();

        message.put("pet_id", pet_id);
        message.put("message_id", message_id);
        message.put("owner_id", owner_id);

        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser == null) {
            return;
        }
        String sender_id = fbUser.getUid();
        String sender_email = fbUser.getEmail();
        message.put("sender_id", sender_id);
        message.put("sender_email", sender_email);

        // Saves the new object
        message.saveInBackground(e -> {
            if (e==null){
                //Log.d("createMessage", "createObject: "+message.toString());
            }else{
                Log.d("createMessage", "createObject: "+e.getMessage());
            }
        });

    }

    /**
     * Creates a Pet in the pets table on the database
     * @param owner_id
     * @param species
     * @param gender
     * @param vaccinated
     * @param diet
     * @param pet_name
     * @param description
     * @param pet_image
     * @throws ParseException
     */
    public static void createPet(String owner_id,
                                 String species,
                                 String gender,
                                 Boolean vaccinated,
                                 String diet,
                                 String pet_name,
                                 String description,
                                 byte[] pet_image) throws ParseException {
        String pet_id = Long.toString(System.currentTimeMillis(), 32).toUpperCase();
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
                //Log.d("PetCreated",pet.getObjectId());
                pet.setObjectId(pet_id);
                //Log.d("PetUpdated",pet.getObjectId());
                pet.saveInBackground();
            }else{
                //Something went wrong
                Log.d("PetCreationError",e.getMessage());
            }
        });
    }

    //reads from the database the pet with the given owner id
    //synchronously

    /**
     *
     * @param user_id
     * @return
     */
    public static List<ParseObject> getUserPets(String user_id) {
        ParseQuery<ParseObject> query = new ParseQuery<>("pets").whereContains("owner_id", user_id);
        try {
            //This find function works synchronously.
            List<ParseObject> pets_list = query.find();
            return pets_list;
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    //reads from the database the pet with the given owner id
    //asynchronously

    /**
     *
     * @param user_id
     * @return
     */
    public static List<ParseObject> getUserPetsAsync(String user_id) {
        List<ParseObject> pets_list = new ArrayList<>();
        if (user_id != null) {
            ParseQuery<ParseObject> query = new ParseQuery<>("pets").whereContains("owner_id", user_id);
            Task asyncTask;
            boolean success;
            do {
                try {
                    //This find function works asynchronously.
                    asyncTask = query.findInBackground();
                    asyncTask.waitForCompletion();
                    pets_list = (List<ParseObject>)asyncTask.getResult();
                    success = true;
                } catch (InterruptedException e) {
                    success = false;
                }
            } while (!success);
        }
        return pets_list;
    }

    //read from the database the pets with given keys and values
    //synchronously

    /**
     *
     * @param user_id
     * @param filterMap
     * @return
     */
    public static List<ParseObject> getPetsByKeysAndValues(String user_id, Map<String, Object> filterMap) {
        ParseQuery<ParseObject> query = new ParseQuery<>("pets").whereNotEqualTo("owner_id", user_id);;
        for (Map.Entry<String, Object> entry : filterMap.entrySet()) {
            query = query.whereEqualTo(entry.getKey(), entry.getValue());
        }
        try {
            //This find function works synchronously.
            List<ParseObject> pets_list = query.find();
            return pets_list;
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    //read from the database the pets with given keys and values
    //also set a limit to the number of maximum pets to read
    //also set a skip to the number of pets skip before starting to read
    //synchronously

    /**
     *
     * @param user_id
     * @param filterMap
     * @param limit
     * @param skip
     * @return
     */
    public static List<ParseObject> getPetsByKeysAndValuesWithLimit(String user_id, Map<String, Object> filterMap, int limit, int skip) {
        ParseQuery<ParseObject> query = new ParseQuery<>("pets").setLimit(limit).setSkip(skip);
        if (user_id != null) {
            query = query.whereNotEqualTo("owner_id", user_id);
        }
        for (Map.Entry<String, Object> entry : filterMap.entrySet()) {
            query = query.whereEqualTo(entry.getKey(), entry.getValue());
        }
        try {
            //This find function works synchronously.
            List<ParseObject> pets_list = query.find();
            return pets_list;
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    //read from the database the pets with given keys and values
    //also set a limit to the number of maximum pets to read
    //also set a skip to the number of pets skipbefore starting to read
    //asynchronously

    /**
     *
     * @param user_id
     * @param filterMap
     * @param limit
     * @param skip
     * @return
     */
    public static List<ParseObject> getPetsByKeysAndValuesWithLimitAsync(String user_id, Map<String, Object> filterMap, int limit, int skip) {
        ParseQuery<ParseObject> query = new ParseQuery<>("pets").setLimit(limit).setSkip(skip);
        if (user_id != null) {
            query = query.whereNotEqualTo("owner_id", user_id);
        }
        for (Map.Entry<String, Object> entry : filterMap.entrySet()) {
            query = query.whereEqualTo(entry.getKey(), entry.getValue());
        }
        List<ParseObject> pets_list = new ArrayList<>();
        Task asyncTask;
        boolean success;
        long startTime = System.currentTimeMillis();
        long difference = 0;
        do {
            try {
                //This find function works asynchronously.
                asyncTask = query.findInBackground();
                asyncTask.waitForCompletion();
                pets_list = (List<ParseObject>)asyncTask.getResult();
                success = true;
            } catch (InterruptedException e) {
                success = false;
                difference = (System.currentTimeMillis() - startTime)/1000;
            }
        } while (!success && difference < MAX_RUNNING_TIME_SECONDS);
        return pets_list;
    }

    //returns the number of pets in the database which suitable for the filtering requirements

    /**
     *
     * @param user_id
     * @param filterMap
     * @return
     */
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

    //returns an array list of species names read from the database
    //the options for possible species the users can select
    //are stored in the database
    //synchronously

    /**
     *
     * @return
     */
    public static ArrayList<String> getSpeciesNames() {
        ArrayList<String> species_names = new ArrayList<String>();
        ParseQuery<ParseObject> query = new ParseQuery<>("Species");
        try {
            //This find function works synchronously.
            List<ParseObject> species = query.find();
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

    //returns a user with a giver id from the database as ParseUser object
    //synchronously

    /**
     *
     * @param user_id
     * @return
     */
    public static ParseUser getUserByID(String user_id) {
        ParseQuery<ParseUser> query = ParseUser.getQuery().whereEqualTo("firebase_id", user_id);
        try {
            //This find function works synchronously.
            List<ParseUser> user =  query.find();
            return user.get(0);
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    //Synchronously Gets A Pet By ID.

    /**
     *
     * @param pet_id
     * @return
     */
    public static ParseObject getPetByID(String pet_id) {
       ParseQuery<ParseObject> query = new ParseQuery<>("pets").whereEqualTo("pet_id", pet_id);
        try {
            List<ParseObject> pet =  query.find();
            return pet.get(0);
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    //Synchronously Gets A Message By ID.

    /**
     *
     * @param message_id
     * @return
     */
    public static ParseObject getMessageByID(String message_id) {
        ParseQuery<ParseObject> query = new ParseQuery<>("messages").whereEqualTo("message_id", message_id);
        try {
            List<ParseObject> message =  query.find();
            return message.get(0);
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    //returns a list of messages about the pet with the given id
    //synchronously

    /**
     *
     * @param pet_id
     * @return
     */
    public static List<ParseObject> getMessagesByPetID(String pet_id) {
        ParseQuery<ParseObject> query = new ParseQuery<>("messages").whereEqualTo("pet_id", pet_id);
        try {
            //This find function works synchronously.
            List<ParseObject> messages =  query.find();
            return messages;
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    //deletes the message with the given id from the database

    /**
     *
     * @param message_id
     */
    public static void deleteMessageByID(String message_id) {
        ParseObject message_to_remove = getMessageByID(message_id);
        if (message_to_remove != null) {message_to_remove.deleteInBackground(e -> {
            if (e == null) {
                //ToDo: make a toast somehow | Toast.makeText(this, "Delete Successful", Toast.LENGTH_SHORT).show();
               //Log.d("deleteMessage", message_to_remove+" delete successfuly");
            }
            ;
        });
        }
    }



    /**
     * deletes the pet with the given id from the database
     * also deletes messages about this pet from the database
     * @param pet_id
     * @return a String, message about delete status
     */
    public static String deletePetByID(String pet_id) {
        AtomicReference<String> deletage_message = new AtomicReference<>("");
        deleteMessagesByKeyAndValue("pet_id", pet_id);
        ParseObject pet_to_remove = getPetByID(pet_id);
        if (pet_to_remove != null) {pet_to_remove.deleteInBackground(e -> {
            if (e == null) {
                deletage_message.set(pet_id + " delete successfuly");
                //Log.d("deletePetByID", deletage_message.get());
            }
            ;
        });
        }
        return deletage_message.get();
    }

    /**deletes messages with given keys and values from the database
     *
     * @param key
     * @param value
     */
    public static void deleteMessagesByKeyAndValue(String key, String value) {
        List<ParseObject> messages_list = getMessagesByKeyAndValue(key, value);
        for (ParseObject parseObj:messages_list
             ) {
            parseObj.deleteInBackground();
        }
    }

    /** returns from the database a list of pets with owner id different of the current user id
     *
     * @param user_id
     * @return
     */
    public static List<ParseObject> getPetsOfOtherUsers(String user_id) {
        ParseQuery<ParseObject> query = new ParseQuery<>("pets").whereNotEqualTo("owner_id", user_id);
        try {
            //This find function works synchronously.
            List<ParseObject> pets_list = query.find();
            return pets_list;
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** returns from the database a list of pets with owner id different of the current user id
     * also set a limit to the number of maximum pets to read
     * also set a skip to the number of pets skip before starting to read
     */
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

    /**
     * returns from the database a list of pets with owner id different of the current user id
     * also set a limit to the number of maximum pets to read
     * also set a skip to the number of pets skip before starting to read asynchronously
     * @param user_id
     * @param limit
     * @param skip
     * @return
     */
    public static List<ParseObject> getPetsOfOtherUsersWithLimitAsync(String user_id, int limit, int skip) {
        ParseQuery<ParseObject> query = new ParseQuery<>("pets").setLimit(limit).setSkip(skip).whereNotEqualTo("owner_id", user_id);
        List<ParseObject> pets_list = new ArrayList<>();
        Task asyncTask;
        boolean success;
        long startTime = System.currentTimeMillis();
        long difference = 0;
        do {
            try {
                asyncTask = query.findInBackground();
                asyncTask.waitForCompletion();
                pets_list = (List<ParseObject>)asyncTask.getResult();
                success = true;
            } catch (InterruptedException e) {
                success = false;
                difference = (System.currentTimeMillis() - startTime) / 1000;
            }
        } while (!success && difference < MAX_RUNNING_TIME_SECONDS);
        return pets_list;
    }

    //
    //

    /**
     * synchronously
     * @param pet_id
     * @param user_id
     * @return boolean true if the pet with id "pet_id" was requested by the user with id "user_id"
     */
    public static boolean findIfPetRequested(String pet_id, String user_id) {
        if (user_id == null || pet_id == null) {
            return false;
        }
        ParseQuery<ParseObject> query = new ParseQuery<>("messages").whereEqualTo("pet_id", pet_id).whereEqualTo("sender_id", user_id);
        try {
            //This find function works synchronously.
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

    //returns from the database a list of messages with the given key and value
    //synchronously

    /**
     *
     * @param key
     * @param value
     * @return
     */
    public static List<ParseObject> getMessagesByKeyAndValue(String key, String value) {
        ParseQuery<ParseObject> query = new ParseQuery<>("messages").whereEqualTo(key, value);
        try {
            //This find function works synchronously.
            List<ParseObject> messages_list = query.find();
            return messages_list;
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    //returns from the database a list of messages with the given key and value
    //also set a limit to the number of maximum messages to read
    //also set a skip to the number of messages skip before starting to read
    //synchronously

    /**
     *
     * @param key
     * @param value
     * @param limit
     * @param skip
     * @return
     */
    public static List<ParseObject> getMessagesByKeyAndValueWithLimit(String key, String value, int limit, int skip) {
        ParseQuery<ParseObject> query = new ParseQuery<>("messages").setLimit(limit).setSkip(skip).whereEqualTo(key, value);
        try {
            //This find function works synchronously.
            List<ParseObject> messages_list = query.find();
            return messages_list;
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    //returns from the database a list of messages with the given key and value
    //also set a limit to the number of maximum messages to read
    //also set a skip to the number of messages skip before starting to read
    //asynchronously

    /**
     *
     * @param key
     * @param value
     * @param limit
     * @param skip
     * @return
     */
    public static List<ParseObject> getMessagesByKeyAndValueWithLimitAsync
            (String key, String value, int limit, int skip) {
        ParseQuery<ParseObject> query = new ParseQuery<>("messages").setLimit(limit).setSkip(skip).whereEqualTo(key, value);
        List<ParseObject> messages_list = new ArrayList<>();
        Task asyncTask;
        boolean success;
        long startTime = System.currentTimeMillis();
        long difference = 0;
        do {
            try {
                //This find function works asynchronously.
                asyncTask = query.findInBackground();
                asyncTask.waitForCompletion();
                messages_list = (List<ParseObject>)asyncTask.getResult();
                success = true;
            } catch (InterruptedException e) {
                success = false;
                difference = (System.currentTimeMillis() - startTime)/1000;
            }
        } while (!success && difference < MAX_RUNNING_TIME_SECONDS);
        return messages_list;
    }

    //returns the number of messages sent to the user with id "user_id"

    /**
     *
     * @param user_id
     * @return
     */
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

    //returns a set of the pets requested by the user with id "user_id"

    /**
     *
     * @param user_id
     * @return
     */
    public static Set<String> getRequestedPetsIds(String user_id) {
        List<ParseObject> messages_list = getMessagesByKeyAndValue("sender_id", user_id);
        Set<String> requested_pets_ids = new HashSet<>();
        for (int i = 0; i < messages_list.size(); i++) {
            String pet_id = messages_list.get(i).get("pet_id").toString();
            requested_pets_ids.add(pet_id);
        }
        return requested_pets_ids;
    }

    //returns the number of allpets in the database

    /**
     *
     * @return
     */
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

    //returns the number of pets with owner id different of the given "user_id"

    /**
     *
     * @param user_id
     * @return
     */
    public static int getNumberOfPetsOfOtherUsers(String user_id) {
        ParseQuery<ParseObject> query = new ParseQuery<>("pets").whereNotEqualTo("owner_id", user_id);
        int number = 0;
        try {
            number = query.count();
        } catch (com.parse.ParseException e) {
            return number;
        }
        return number;
    }

    //returns from the database a list of all pets
    //synchronously

    /**
     *
     * @return
     */
    public static List<ParseObject> getAllPets() {
        ParseQuery<ParseObject> query = new ParseQuery<>("pets");
        try {
            //This find function works synchronously.
            List<ParseObject> pets_list = query.find();
            return pets_list;
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    //returns from the database a list pets without any restriction on their details
    //also set a limit to the number of maximum pets to read
    //also set a skip to the number of pets skip before starting to read
    //synchronously

    /**
     *
     * @param limit
     * @param skip
     * @return
     */
    public static List<ParseObject> getAllPetsWithLimit(int limit, int skip) {
        ParseQuery<ParseObject> query = new ParseQuery<>("pets").setLimit(limit).setSkip(skip);
        try {
            //This find function works synchronously.
            List<ParseObject> pets_list = query.find();
            return pets_list;
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    //returns from the database a list pets without any restriction on their details
    //also set a limit to the number of maximum pets to read
    //also set a skip to the number of pets skip before starting to read
    //asynchronously

    /**
     *
     * @param limit
     * @param skip
     * @return
     */
    public static List<ParseObject> getAllPetsWithLimitAsync(int limit, int skip) {
        ParseQuery<ParseObject> query = new ParseQuery<>("pets").setLimit(limit).setSkip(skip);
        List<ParseObject> pets_list = new ArrayList<>();
        Task asyncTask;
        boolean success;
        long startTime = System.currentTimeMillis();
        long difference = System.currentTimeMillis();
        do {
            try {
                //This find function works asynchronously.
                asyncTask = query.findInBackground();
                asyncTask.waitForCompletion();
                pets_list = (List<ParseObject>)asyncTask.getResult();
                success = true;
            } catch (InterruptedException e) {
                success = false;
                difference = (System.currentTimeMillis() - startTime) / 1000;
            }
        } while (!success && difference < MAX_RUNNING_TIME_SECONDS);
        return pets_list;
    }

    //This find function works synchronously.

    /**
     *
     * @param className
     * @return
     */
    public static List<ParseObject> find(String className) {
        ParseQuery<ParseObject> query = new ParseQuery<>(className);
        try {
            List<ParseObject> list = query.find();
            return list;
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    //Get Pet Image From Parse Object
    //Set the picture on PetsListAdapter.ItemViewHolder holder

    /**
     *
     * @param petObject
     * @param holder
     * @return
     */
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

    //returns true if the user has internet connection

    /**
     *
     * @param context
     * @return
     */
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