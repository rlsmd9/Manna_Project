package com.example.manna_project.MainAgreementActivity_Util.NaverMapUtil;

import android.util.Log;

import com.example.manna_project.MainAgreementActivity;
import com.example.manna_project.SearchPlaceAdapter;
import com.naver.maps.geometry.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class NaverMapPlaceSearch {
    StringBuffer response;
    String lastKeyword;
    final static String clientId = "ozkoxla3i2";//애플리케이션 클라이언트 아이디값";
    final static String clientSecret = "aGP81IuN7XpDA9FyMbVu8ZF9tlDcDrj0Z17bFyyv";//애플리케이션 클라이언트 시크릿값 공용 키 만들어서 사용해야함;
    int responseCode = 0;
    String responseJson;

    public NaverMapPlaceSearch() {
    }

    public void searchPlace(String keyword, SearchPlaceAdapter adapter) {
        lastKeyword = keyword;
        responseJson = getNaverPlaceSearch(lastKeyword);

        adapter.getPlaceList().clear();

        Log.d("book_js", "run: " + responseJson);
        if (responseJson == null) {
            Log.d("book_js", "run: API Error");
        } else {
            // GET한 책 정보 파싱
            try {
                JSONObject jsonObject = new JSONObject(responseJson);
                Log.d("JS", "searchPlace: " + jsonObject.getJSONObject("meta").getInt("totalCount"));
                JSONArray jsonArray;
                if (jsonObject.getJSONObject("meta").getInt("count") != 0) {
                    jsonArray = jsonObject.getJSONArray("places");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject item = jsonArray.getJSONObject(i);

                        Log.d("JS", "searchBook: "  + item.toString());

                        adapter.getPlaceList().add(new Place(item.getString("name"), item.getString("road_address"),
                                item.getString("jibun_address"), new LatLng(Double.parseDouble(item.getString("y")), Double.parseDouble(item.getString("x")))));

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("book_js", "run: API Error");
            }
        }

    }

    private String getNaverPlaceSearch(String keyword) {
        try {
            String encodingKeyword = URLEncoder.encode(keyword, "UTF-8");
            String apiURL = "https://naveropenapi.apigw.ntruss.com/map-place/v1/search?query=" + encodingKeyword + "&coordinate=127.1054328,37.3595963";
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
