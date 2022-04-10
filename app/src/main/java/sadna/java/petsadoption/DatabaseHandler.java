package sadna.java.petsadoption;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


public class DatabaseHandler {
//Create User
    public static void createUser(String user_name, String email) {
        ParseUser user = new ParseUser();
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


    //ToDo: add the nececeary users info
    public static void createMessage(String pet_id) {
        ParseObject message = new ParseObject("messages");

        message.put("pet_id", pet_id);
        message.put("message_id", "A string");
        message.put("owner_id", "A string");
        message.put("sender_id", "A string");

        // Saves the new object
        message.saveInBackground(e -> {
            if (e==null){
                Log.d("createMessage", "createObject: "+message.toString());
            }else{
                Log.d("createMessage", "createObject: "+e.getMessage());
            }
        });

    }

    public static void createPet(String owner_id, String species, String gender, Boolean vaccinated, String pet_name, byte[] pet_image) throws ParseException {
        String pet_id = Long.toString(System.currentTimeMillis(), 32).toUpperCase();
        //ParseObject pet = ParseObject.createWithoutData("pets", pet_id);//new ParseObject("pets");
        ParseObject pet = new ParseObject("pets");
            pet.put("pet_id", pet_id);
            pet.put("owner_id",owner_id);
            pet.put("pet_name", pet_name);
            pet.put("species",  species);
            pet.put("vaccinated",  vaccinated);
            //pet.put("species", new ParseObject("Species")); //How do i set it to be a specific class?
            pet.put("gander", gender);
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

    //Synchronously Gets A Pet By ID.
    public static ParseObject getPetByID(String pet_id) {
       ParseQuery<ParseObject> query = new ParseQuery<>("pets").whereContains("pet_id", pet_id);
        try {
            ParseObject pet = (ParseObject) query.find();
            Log.d("Finding User Pets", (String) pet.get("pet_name"));
            return pet;
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
            //ToDo: לעבור על הרשימה עם pets_list.listIterator(i)
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
    public static Bitmap getPetImage(ParseObject petObject, ListAdapter.ViewHolder holder)
    {
        final Bitmap[] bmp = new Bitmap[1];
        ParseFile thumbnail = (ParseFile) petObject.get("pet_image");
        thumbnail.getDataInBackground
                (
                    new GetDataCallback()
                    {
                        public void done(byte[] data, ParseException e)
                        {
                            Log.d("Getting Pet Image", "done");
                            if (e == null)  {
                                bmp[0] = BitmapFactory.decodeByteArray(data, 0,data.length);
                                holder.petImage.setImageBitmap(bmp[0]);
                            }
                            Log.d("Getting Pet Image", "Pet Image Assigned: "+ bmp[0].toString());
                        };
                    }
                );
        return bmp[0];
    }
}