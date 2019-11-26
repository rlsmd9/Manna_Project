package com.example.manna_project;

import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.manna_project.MainAgreementActivity_Util.NaverMapUtil.NaverMapPlaceSearch;
import com.example.manna_project.MainAgreementActivity_Util.NaverMapUtil.NaverMapReverseGeocoding;
import com.example.manna_project.MainAgreementActivity_Util.NaverMapUtil.Place;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;

import java.util.ArrayList;

public class SearchPlaceActivity extends AppCompatActivity implements OnMapReadyCallback, SearchView.OnQueryTextListener, SearchPlaceAdapter.OnItemClickListener,
        SearchView.OnCloseListener, Overlay.OnClickListener {
    SearchView searchView;
    NaverMapPlaceSearch naverMapPlaceSearch;
    ArrayList<Place> placesList;
    RecyclerView recyclerView;
    SearchPlaceAdapter adapter;
    LatLng latLng;
    String selectedAddress;
    MapFragment mapFragment;
    Marker marker;

    public final static int SEARCHPLACE_ACTIVITY_REQUEST_CODE = 1121;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_place);

        searchView = findViewById(R.id.activity_search_place_search);
        searchView.bringToFront();
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);

        recyclerView = findViewById(R.id.activity_search_place_list);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        placesList = new ArrayList<>();

        adapter = new SearchPlaceAdapter(placesList, this);
        adapter.setOnItemClickListener(this);

        recyclerView.setAdapter(adapter);

        naverMapPlaceSearch = new NaverMapPlaceSearch();

        // naver map
        FragmentManager fm = getSupportFragmentManager();
        mapFragment = (MapFragment) fm.findFragmentById(R.id.activity_search_place_map);

        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.activity_search_place_map, mapFragment).commit();
        }

        // 기본 숭실대 호수 좌표
        latLng = new LatLng(37.49630160214827, 126.9574464751917);
        marker = new Marker();
        mapFragment.getMapAsync(this);
        getNaverReverseGeocoding(latLng);
    }

    @Override
    public boolean onClose() {
        adapter.getPlaceList().clear();
        this.placesList.clear();
        adapter.notifyDataSetChanged();
        return false;
    }

    @Override
    public void onItemClick(View v, int pos) {
        latLng = placesList.get(pos).getLatLng();
        selectedAddress = (placesList.get(pos).getLoad_address().isEmpty()?placesList.get(pos).getLoad_address():placesList.get(pos).getJibun_address()) + " " + placesList.get(pos).getName();
        mapFragment.getMapAsync(this);
        Log.d("JS", "onItemClick: " + selectedAddress);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        adapter.getPlaceList().clear();
        this.placesList.clear();
        adapter.notifyDataSetChanged();

        if (query == null) return true;

        getNaverMapSearch(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        return false;
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        naverMap.setCameraPosition(new CameraPosition(latLng, 16));
        marker.setPosition(latLng);
        marker.setMap(naverMap);

        naverMap.setOnMapClickListener(new NaverMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) {
                marker.setPosition(latLng);
                getNaverReverseGeocoding(marker.getPosition());
            }
        });

        marker.setOnClickListener(this);
    }

    @Override
    public boolean onClick(@NonNull Overlay overlay) {
        Intent intent = getIntent();

        intent.putExtra("SelectedAddress", selectedAddress);
        intent.putExtra("Location", marker.getPosition());

        setResult(RESULT_OK, intent);

        finish();

        return true;
    }

    private void getNaverReverseGeocoding(final LatLng latLng) {
        new Thread() {
            @Override
            public void run() {

                NaverMapReverseGeocoding nmrg = new NaverMapReverseGeocoding();

                selectedAddress = nmrg.reversePlace(latLng);
            }
        }.start();
    }

    private void getNaverMapSearch(final String keyword) {
        new Thread() {
            @Override
            public void run() {
                naverMapPlaceSearch.searchPlace(keyword, adapter);

            }
        }.start();

    }
}
