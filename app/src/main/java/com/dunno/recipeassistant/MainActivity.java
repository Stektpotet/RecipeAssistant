package com.dunno.recipeassistant;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getName();

    private TabPagerAdapter mTabPagerAdapter;
    private ProgressBar     mProgressBar;
    private LockedViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupUI();
    }

    private void setupUI() {

        mViewPager = findViewById(R.id.main_viewPager);
        BottomNavigationView navigation = findViewById(R.id.main_navigationBar);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mTabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mTabPagerAdapter);
        navigation.setSelectedItemId(R.id.menu_navigation_fridge);
    }

    public void setActionBarTitle(String newTitle) {
        getSupportActionBar().setTitle(newTitle);
    }
    public void setActionBarTitle(CharSequence newTitle) {
        getSupportActionBar().setTitle(newTitle);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            mViewPager.setCurrentItem(item.getItemId());
            setActionBarTitle(mViewPager.getAdapter().getPageTitle(item.getItemId()));
            return true;
        }
    };


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class TabPagerAdapter extends FragmentPagerAdapter {

        public TabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case R.id.menu_navigation_fridge:
                    return FridgeFragment.newInstance(0);
                case R.id.menu_navigation_recipes:
                default:
                    return RecipeListFragment.newInstance(0);
                case R.id.menu_navigation_shoppingList:
                    return ShoppingListFragment.newInstance(0);
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case R.id.menu_navigation_fridge:
                    return getBaseContext().getResources().getString(R.string.nav_fridge); //TODO: put these into @values/strings
                case R.id.menu_navigation_recipes:
                    return getBaseContext().getResources().getString(R.string.nav_recipes);
                case R.id.menu_navigation_shoppingList:
                    return getBaseContext().getResources().getString(R.string.nav_shopping);
            }
            return "How did you get here?";
        }
    }
}
