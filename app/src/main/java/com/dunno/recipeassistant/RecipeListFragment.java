package com.dunno.recipeassistant;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecipeListFragment extends Fragment {

    public static final String PREF_SET_NAME = "RECIPE_LIST";


    private static final int SPAN_COUNT = 2;
    private static final int DATASET_COUNT = 60;
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static DbHelper dbHelper;

    protected RecyclerView                  mRecyclerView;
    protected RecipeListAdapter             mListAdapter;
    protected RecyclerView.LayoutManager    mLayoutManager;
    protected Set<Recipe>                   mDataSet = new HashSet<>();

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }
    protected LayoutManagerType mCurrentLayoutManagerType;

    public RecipeListFragment() {}

    public static RecipeListFragment newInstance(int entry) {
        RecipeListFragment fragment = new RecipeListFragment();
        Bundle args = new Bundle();
        //SET ARGS
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DbHelper(getContext());

        initDataset();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("PLES", "THANKS");
        if(mListAdapter != null) {
            mListAdapter.updateDataSet(mDataSet);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipelist, container, false);
        mRecyclerView = rootView.findViewById(R.id.fragment_recipelist_recyclerView);

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mLayoutManager = new LinearLayoutManager(getActivity());

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager(LayoutManagerType.LINEAR_LAYOUT_MANAGER);

        mListAdapter = new RecipeListAdapter(mDataSet);
        mListAdapter.updateDataSet(mDataSet);

        // Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mListAdapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener( getContext(),
                                                                            mRecyclerView,
                                                                            new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {

                        Intent intent = new Intent(getContext(), RecipeActivity.class);
                        intent.putExtra("recipeId", mListAdapter.getItemAt(position));
                        startActivity(intent);
                    }

                    @Override public void onLongItemClick(View view, int position) {

                    }
                })
        );


        return rootView;
    }

    /**
     * Set RecyclerView's LayoutManager to the one given.
     *
     * @param layoutManagerType Type of layout manager to switch to.
     */
    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case LINEAR_LAYOUT_MANAGER:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                break;
            default:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void initDataset() {
        mDataSet = new HashSet<>(dbHelper.getRecipelist());
    }

    public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.ViewHolder> {
        private Recipe[] mDataSet;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView textView;
            public TextView hasPercentage;
            public TextView time;

            public ViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.recipeList_item_image);
                textView = itemView.findViewById(R.id.recipeList_item_title);
                hasPercentage = itemView.findViewById(R.id.recipeList_item_txt_infridge);
                time = itemView.findViewById(R.id.recipeList_item_txt_time);
            }

        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public RecipeListAdapter(Set<Recipe> dataSet) { updateDataSet(dataSet); }

        public void updateDataSet(Set<Recipe> dataSet) {
            List<Recipe> temp = new ArrayList<>(dataSet);
            temp = dbHelper.updateAllHasValues(temp);
            this.mDataSet = temp.toArray(new Recipe[temp.size()]);
            notifyDataSetChanged();
        }

        // Create new views (invoked by the layout manager)
        @Override
        public RecipeListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view

            View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item, parent, false);

            return  new ViewHolder(item);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            // - get element from your dataset at this position
            // - replace the contents of the view with that element
//            holder.itemView.setText(mDataSet[position]);
            holder.textView.setText(this.mDataSet[position].title);
            int resID = getResources().getIdentifier(this.mDataSet[position].image,
                                                "drawable", "com.dunno.recipeassistant");
            holder.imageView.setImageResource(resID);
            holder.hasPercentage.setText(String.valueOf(this.mDataSet[position].hasPercentage*100.0f) + "%");
            holder.time.setText(this.mDataSet[position].time);
        }

        @Override
        public int getItemCount() {
            return this.mDataSet.length;
        }


        public int getItemAt(int position) {
            return mDataSet[position].id;
        }

    }


}
