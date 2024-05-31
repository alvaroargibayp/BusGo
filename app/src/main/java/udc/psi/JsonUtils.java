package udc.psi;

import android.content.Context;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
public class JsonUtils {

    // Function to read JSON file from raw resource and return it as a String
    public static String readJsonFromRaw(Context context, int resourceId) {
        StringBuilder jsonString = new StringBuilder();
        try {
            InputStream is = context.getResources().openRawResource(resourceId);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            reader.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return jsonString.toString();
    }

    // Function to read JSON file from internal storage and return it as a String
    public static String readJsonFromFile(Context context, String fileName) {
        StringBuilder jsonString = new StringBuilder();
        FileInputStream fis = null;
        try {
            fis = context.openFileInput(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            reader.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return jsonString.toString();
    }

    // Function to read JSON file from internal storage and return it as a String
    public static String readJsonFromFile2(Context context, String fileName) {
        StringBuilder jsonString = new StringBuilder();
        FileInputStream fis = null;
        try {
            fis = context.openFileInput(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            reader.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return jsonString.toString();
    }

    // Function to save JSONObject to a file in internal storage
    public static boolean saveJsonToFile(Context context, String fileName, JSONObject jsonObject) {
        String jsonString = jsonObject.toString();
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(jsonString.getBytes());
            fos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
