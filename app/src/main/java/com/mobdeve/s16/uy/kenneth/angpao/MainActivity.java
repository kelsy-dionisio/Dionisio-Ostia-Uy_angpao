package com.mobdeve.s16.uy.kenneth.angpao;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;  // Added this import
import com.mobdeve.s16.uy.kenneth.angpao.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    BottomNavigationView bottomNavigationView;  // Corrected the import

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bottomNavigationView = binding.bottomNavigationView;
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.homeBtn) {
                replaceFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.ninongBtn) {
                replaceFragment(new NinongFragment());
            } else if (item.getItemId() == R.id.historyBtn) {
                replaceFragment(new HistoryFragment());
            }
            return true;
        });

        // Initially, set the HomeFragment
        replaceFragment(new HomeFragment());
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}
