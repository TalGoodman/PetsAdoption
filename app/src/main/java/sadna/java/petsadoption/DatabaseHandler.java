package sadna.java.petsadoption;

import android.util.Log;

import com.parse.GetCallback;
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

    public static void createPet(String owner_id,String species,String gander, String pet_name, byte[] pet_image) {
        String pet_id = Long.toString(System.currentTimeMillis(), 32).toUpperCase();
        ParseObject pet = new ParseObject("pets");
            pet.put("pet_id", pet_id);
            pet.put("owner_id",owner_id);
            pet.put("pet_name", pet_name);
            pet.put("species", species);
            pet.put("gander", gander);
            pet.put("pet_image", new ParseFile(pet_name+".png", pet_image)); //Will Be The Pet Image
        pet.saveInBackground(e -> {
            if (e==null){
                //Save was done
                Log.d("PetCreated:",pet.getObjectId());
                //pet.setObjectId(pet_id);
            }else{
                //Something went wrong
                Log.d("PetCreationError",e.getMessage());
            }
        });
    }

    public static List<ParseObject> findPets() {
        //This find function works synchronously.
        //ToDo: Add a filter to match user pets only by user id
        ParseQuery<ParseObject> query = new ParseQuery<>("pets");
        try {
            List<ParseObject> list = query.find();
            //ToDo: לעבור על הרשימה עם list.listIterator(i)
            //Log.d("Finding Pets", "List: " + list.listIterator(1));
            list.forEach(
                    (pet) -> {
                                Log.d("Finding Pets", (String) pet.get("pet_name"));
                             }
            );
//            Log.d("Pet: ", (String) list.get(1).get("pet_name"));
            return list;
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<ParseObject> find(String className) {
        //This find function works synchronously.
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
};


