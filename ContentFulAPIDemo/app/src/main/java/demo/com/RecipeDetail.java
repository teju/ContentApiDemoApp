package demo.com;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.contentful.java.cda.CDAAsset;
import com.contentful.java.cda.CDAClient;
import com.contentful.java.cda.CDAContentType;
import com.contentful.java.cda.CDAEntry;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import demo.com.adapters.RecipeDetailAdapter;
import demo.com.helper.APISettings;
import demo.com.helper.DisplayItem;
import demo.com.helper.Loader;

public class RecipeDetail extends AppCompatActivity {

    private CDAEntry entry;
    private Map<String, CDAContentType> contentTypesMap;
    private RecyclerView recipe_list;
    private CDAClient client;
    private ImageView display_image;
    private List<DisplayItem> displayItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_recipe_detail);
        init();
    }

    public void init() {
        entry = (CDAEntry) getIntent().getSerializableExtra("EXTRA_ENTRY");
        contentTypesMap = (Map<String, CDAContentType>) getIntent().getSerializableExtra(
                "EXTRA_CONTENT_TYPES_MAP");
        recipe_list = (RecyclerView)findViewById(R.id.recipe_list);
        display_image = (ImageView)findViewById(R.id.display_image);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recipe_list.setLayoutManager(mLayoutManager);
        recipe_list.setItemAnimator(new DefaultItemAnimator());
        new LoadData().execute();
        new GetImage().execute();



    }

    public class LoadData extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Loader.show(RecipeDetail.this);
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            client = APISettings.config(RecipeDetail.this);
            displayItems = APISettings.performItemLoad(contentTypesMap,entry);
            displayItems.removeAll(Collections.singleton(null));
            return null;
        }
        @Override
        protected void onPostExecute(Object o) {
            Loader.hide();
            super.onPostExecute(o);
            removeNullFromList();
            RecipeDetailAdapter recipeAdapter = new RecipeDetailAdapter(RecipeDetail.this,displayItems,client);
            recipe_list.setAdapter(recipeAdapter);
        }

        public void removeNullFromList() {
            for(int i =0;i<displayItems.size();i++) {
                if(StringUtils.isBlank(displayItems.get(i).displayValue) || StringUtils.isBlank(displayItems.get(i).key)) {
                    displayItems.remove(i);
                }
            }
        }
    }


    public class GetImage extends AsyncTask {
        String url = "";

        @Override
        protected Object doInBackground(Object[] objects) {
            for (int i = 0; i < displayItems.size(); i++) {
                if (displayItems.get(i).key.contains("photo")) {

                    final int finalI = i;
                    CDAAsset asset1 = APISettings.performImageLoad(client, displayItems.get(finalI).resource);

                    url = "http:" + asset1.url();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            if(!StringUtils.isBlank(url)) {
                Picasso.with(RecipeDetail.this).load(url).fit()
                        .placeholder(R.drawable.no_image)
                        .error(R.drawable.no_image)
                        .centerCrop().into(display_image);
            }
        }
    }
}
