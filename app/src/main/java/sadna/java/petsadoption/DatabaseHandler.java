package sadna.java.petsadoption;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;


public class DatabaseHandler {
//Create Person
    public static void createPerson(String person_name) {
        //Saving your First data object on Back4App
        ParseObject person = new ParseObject("Person");
        person.put("name", person_name);
        person.put("age", 27);
        person.saveInBackground();

        //Reading your First Data Object from Back4App
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Person");
        query.getInBackground("mhPFDlCahj", new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    // object will be your person
                } else {
                    // something went wrong
                }
            }
        });
    }

    public static void createPet(String owner_id, String species, String gander, String pet_name, byte[] pet_image) throws ParseException {
        String pet_id = Long.toString(System.currentTimeMillis(), 32).toUpperCase();
        //ParseObject pet = ParseObject.createWithoutData("pets", pet_id);//new ParseObject("pets");
        ParseObject pet = new ParseObject("pets");
            pet.put("pet_id", pet_id);
            pet.put("owner_id",owner_id);
            pet.put("pet_name", pet_name);
            pet.put("species",  species);
            //pet.put("species", new ParseObject("Species")); //How do i set it to be a specific class?
            pet.put("gander", gander);
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

    //Gets A Pet By ID
    public static ParseObject getPetByID(String pet_id) {
        //This find function works synchronously.
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
