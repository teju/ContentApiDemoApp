package demo.com;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.contentful.java.cda.CDAAsset;
import com.contentful.java.cda.CDAClient;

import java.util.ArrayList;

import demo.com.adapters.RecipeAdapter;
import demo.com.api.ContentTypeWrapper;
import demo.com.api.ResourceList;
import demo.com.helper.APISettings;
import demo.com.helper.Loader;

public class MainActivity extends AppCompatActivity {


    private RecyclerView recipe_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    public void init() {

        recipe_list = (RecyclerView)findViewById(R.id.recipe_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recipe_list.setLayoutManager(mLayoutManager);
        recipe_list.setItemAnimator(new DefaultItemAnimator());
        new LoadList().execute();

    }


    public class LoadList extends AsyncTask {


        private ResourceList resourceList;
        private CDAClient client;
        private ArrayList<CDAAsset> data = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Loader.show(MainActivity.this);
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            client = APISettings.config(MainActivity.this);
            ArrayList<ContentTypeWrapper> contentTypeWrappers = APISettings.fetchItems(client);
            resourceList = APISettings.performLoad(client,contentTypeWrappers.get(1).getContentType());

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Loader.hide();
            RecipeAdapter recipeAdapter = new RecipeAdapter(MainActivity.this,resourceList,client);
            recipe_list.setAdapter(recipeAdapter);
        }
    }


}
