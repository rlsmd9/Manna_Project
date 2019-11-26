package com.example.manna_project.MainAgreementActivity_Util.NaverMapUtil;

import android.util.Log;

import com.naver.maps.geometry.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NaverMapReverseGeocoding {
    StringBuffer response;
    String lastKeyword;
    final static String clientId = "ozkoxla3i2";//애플리케이션 클라이언트 아이디값";
    final static String clientSecret = "aGP81IuN7XpDA9FyMbVu8ZF9tlDcDrj0Z17bFyyv";//애플리케이션 클라이언트 시크릿값 공용 키 만들어서 사용해야함;
    int responseCode = 0;
    String responseJson;

    public NaverMapReverseGeocoding() {
    }

    public String reversePlace(LatLng latLng) {
        responseJson = getNaverReverseGeocoding(String.valueOf(latLng.latitude), String.valueOf(latLng.longitude));

        Log.d("book_js", "run: " + responseJson);
        if (responseJson == null) {
            Log.d("book_js", "run: API Error");
        } else {
            // GET한 책 정보 파싱
            try {
                JSONObject jsonObject = new JSONObject(responseJson);

                JSONArray jsonResult = jsonObject.getJSONArray("results");

                JSONObject jsonRegion = jsonResult.getJSONObject(0).getJSONObject("region");
                JSONObject jsonLand = jsonResult.getJSONObject(0).getJSONObject("land");


                StringBuilder address = new StringBuilder();

                address.append(jsonRegion.getJSONObject("area1").getString("name") + " ");
                address.append(jsonRegion.getJSONObject("area2").getString("name") + " ");
                address.append(jsonRegion.getJSONObject("area3").getString("name") + " ");
                address.append(jsonRegion.getJSONObject("area4").getString("name"));

                address.append(jsonLand.getJSONObject("addition0").getString("value"));
//                address.append(jsonLand.getJSONObject("addition2").getString("value") + " ");

                Log.d("JS", "searchPlace: " + address.toString().trim());

                return address.toString().trim();

//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject item = jsonArray.getJSONObject(i);
//
//                    Log.d("JS", "searchBook: "  + item.toString());
//                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.d("book_js", "run: API Error");
            }
        }

        return "";
    }

    private String getNaverReverseGeocoding(String x, String y) {
        try {
            String apiURL = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?request=coordsToaddr&coords=" + y + "," + x + "&sourcecrs=epsg:4326&output=json&orders=roadaddr";
            Log.d("book_js", "getNaverPlaceSearch: " + apiURL);
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
            con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);

            responseCode = con.getResponseCode();
            BufferedReader br;
            if(responseCode==200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();

            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
