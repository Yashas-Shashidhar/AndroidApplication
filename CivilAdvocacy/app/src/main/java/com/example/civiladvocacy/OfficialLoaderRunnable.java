//package com.example.civiladvocacy;
//
//
//
//import android.net.Uri;
//import android.util.Log;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.List;
//
//
//public class OfficialLoaderRunnable implements Runnable {
//
//    private static final String TAG = "OfficialLoaderRunnable";
//    private final MainActivity mainActivity;
//    private static final String DATA_URL = "https://www.googleapis.com/civicinfo/v2/representatives?key=AIzaSyBH_iwAxe69ILf6OtImYPvJ5IDAolJM4DU&address=1600 W North Ave, Chicago, IL";
//
//    OfficialLoaderRunnable(MainActivity mainActivity) {
//        this.mainActivity = mainActivity;
//    }
//
//    @Override
//    public void run() {
//        Uri dataUri = Uri.parse(DATA_URL);
//        String urlToUse = dataUri.toString();
//        Log.d(TAG, "run: " + urlToUse);
//
//        StringBuilder sb = new StringBuilder();
//        try {
//            URL url = new URL(urlToUse);
//
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");
//            conn.connect();
//
//            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
//                Log.d(TAG, "run: HTTP ResponseCode NOT OK: " + conn.getResponseCode());
//                handleResults(null);
//                return;
//            }
//
//            InputStream is = conn.getInputStream();
//            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));
//
//            String line;
//            while ((line = reader.readLine()) != null) {
//                sb.append(line).append('\n');
//            }
//
//            Log.d(TAG, "run: " + sb);
//
//        } catch (Exception e) {
//            Log.e(TAG, "run: ", e);
//            handleResults(null);
//            return;
//        }
//
//        handleResults(sb.toString());
//
//    }
//
//    private int official_picture(String icon) {
//        int iconID =
//                mainActivity.getResources().getIdentifier(icon, "drawable", mainActivity.getPackageName());
//        if (iconID == 0) {
//            Log.d(TAG, "parseCurrentRecord: CANNOT FIND ICON " + icon);
//        }
//        return iconID;
//    }
//
//
//    private void handleResults(String s) {
//
//        if (s == null) {
//            Log.d(TAG, "handleResults: Failure in data download");
//            mainActivity.runOnUiThread(mainActivity::downloadFailed);
//            return;
//        }
//
//        final ArrayList<Official> OfficialList = parseJSON(s);
//        if (OfficialList == null) {
//            mainActivity.runOnUiThread(mainActivity::downloadFailed);
//            return;
//        }
//
//        mainActivity.runOnUiThread(() -> mainActivity.updateData(OfficialList));
//    }
//
//    private ArrayList<Official> parseJSON(String s) {
//
//        ArrayList<Official> officialArrayList = new ArrayList<>();
//        try {
//
//            JSONObject jObj = new JSONObject(s);
//            //location
//            JSONObject jobj_loc=jObj.getJSONObject("normalizedInput");
//            String location =jobj_loc.getString("line1")+jobj_loc.getString("city")+ jobj_loc.getString("state");
//
//            JSONArray jr = jObj.getJSONArray("offices");
//            JSONArray jr_off=jObj.getJSONArray("officials");
//            for (int i = 0; i < jr.length(); i++) {
//                JSONObject jb1 = jr.getJSONObject(i);
//                String position = jb1.getString("name");
//
//                JSONArray jr2 = jb1.getJSONArray("officialIndices");
//                for (int j=0;j<jr2.length(); j++) {
//                    int val= jr2.getInt(j);
//                    JSONObject jo_off=jr_off.getJSONObject(val);
//
//                    //name
//                    String name=jo_off.getString("name");
//                    String address="";
//                    if (jo_off.has("address")){
//                        JSONArray addressarr=jo_off.getJSONArray("address");
//                        JSONObject address_obj=addressarr.getJSONObject(0);
//                        address=address_obj.getString("line1")+address_obj.getString("city")+address_obj.getString("state")+address_obj.getString("zip");
//                    }
//                    String party="";
//                    if (jo_off.has("party")){
//                        party=jo_off.getString("party");
//                    }
//                    String phnumber="";
//                    if (jo_off.has("phones")) {
//                        JSONArray phnumber_arr = jo_off.getJSONArray("phones");
//                        for (int k = 0; k < phnumber_arr.length(); k++) {
//                            phnumber =phnumber+ phnumber_arr.getString(k) +"\n";
//                        }
//                    }
//                    String imge_url=String.valueOf(official_picture("missing"));
//                    if (jo_off.has("photoUrl")) {
//                        imge_url = jo_off.getString("photoUrl");
//                    }
//                    String email_link="";
//                    if(jo_off.has("emails")){
//                       JSONArray emails=jo_off.getJSONArray("emails");
//                       email_link=emails.getString(0);
//                    }
//                    officialArrayList.add(new Official(imge_url,party,position,phnumber,name,address,email_link,location));
//                }
//
//
//            }
//            return officialArrayList;
//        } catch (Exception e) {
//            Log.d(TAG, "parseJSON: " + e.getMessage());
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//
//}