package ee364e.happs;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.JarURLConnection;
import java.net.URL;

/**
 * Created by Ali on 3/5/2017.
 */

public class Database {

    public static final String allUsersURLString = "https://teamhapps.herokuapp.com/api/users/?format=json";

    public static boolean isUser(String userID) throws Exception{
        JSONArray usersJSONArray = doGETRequest(allUsersURLString);
        int l = usersJSONArray.length();
        for(int i = 0;  i < l; i++){
            JSONObject userJSON = (JSONObject) usersJSONArray.get(i);
            String thisUserId = (String) userJSON.get("user_id");
            if(userID.equals(thisUserId)){
                return true;}
        }

        return  false;
    }

    public static JSONArray doGETRequest(String URLString){
        URL url = null;
        String newLine = "";
        String newJSON = "";
        try{
            url = new URL(URLString);
        }catch (Exception e){
            e.printStackTrace();
        }


        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
            while((newLine = reader.readLine()) != null){
                newJSON += newLine;
                System.out.println(newLine);
            }

            JSONArray jsonArray = new JSONArray(newJSON);


            System.out.println(newJSON);

            return jsonArray;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

    //testing main method
    public static void main(String[] args){
        JSONArray array;
        System.out.println("Starting Main Program:");
        array = doGETRequest(allUsersURLString);
        int length = 0;
        try{
            length = array.length();
        }catch (Exception e){
            e.printStackTrace();
        }

        System.out.println("Length is: "+length);
    }


}
