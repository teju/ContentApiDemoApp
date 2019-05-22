package demo.com.adapters;

/**
 * Created by Khushvinders on 15-Nov-16.
 */

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.contentful.java.cda.CDAAsset;
import com.contentful.java.cda.CDAClient;
import com.contentful.java.cda.CDAEntry;
import com.contentful.java.cda.CDAResource;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;

import demo.com.R;
import demo.com.RecipeDetail;
import demo.com.api.ResourceList;
import demo.com.helper.APISettings;
import demo.com.helper.DisplayItem;
import demo.com.helper.Helper;


public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.MyViewHolder> {
    private final Context context;
    private final ResourceList resourceList;
    private final int imageSize;
    private final CDAClient client;
    private String url = "";

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final ImageView display_image;
        private final LinearLayout root;


        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            display_image = (ImageView) view.findViewById(R.id.display_image);
            root = (LinearLayout) view.findViewById(R.id.root);
        }

    }

    public RecipeAdapter(Context context, ResourceList resourceList, CDAClient client) {
        this.context = context;
        this.client = client;
        this.resourceList = resourceList;
        this.imageSize = context.getResources().getDimensionPixelSize(R.dimen.gallery_image_size);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.receipe_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        try {
            holder.root.setTag(position);
            final CDAResource resource = resourceList.resources.get(position);

            String name = "";

            if (resource instanceof CDAEntry) {
                String displayField = Helper.getEntryDisplayField((CDAEntry) resource, resourceList);

                if (StringUtils.isNotBlank(displayField)) {
                    name = ((CDAEntry) resource).getField(displayField);
                }
            }

            if (StringUtils.isBlank(name)) {
                name = resource.id();
            }
            new GetImage((CDAEntry) resource,holder.display_image).execute();

            holder.name.setText(name);
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int index = (Integer) view.getTag();
                    final CDAResource res = resourceList.resources.get(index);
                    context.startActivity(new Intent(context, RecipeDetail.class)
                            .putExtra("EXTRA_ENTRY", (CDAEntry) res)
                            .putExtra("EXTRA_CONTENT_TYPES_MAP", (Serializable) resourceList.contentTypes));
                }
            });
        } catch (Exception e) {
        }
    }

    @Override
    public int getItemCount() {
        return resourceList.resources.size();
    }

    public class GetImage extends AsyncTask {
        private final CDAResource cdaResource;
        private ImageView imageView = null;
        String url = "";

        public GetImage(CDAResource cdaResource,ImageView imageView) {
            this.cdaResource = cdaResource;
            this.imageView = imageView;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            final List<DisplayItem> displayItems = APISettings.performItemLoad(resourceList.contentTypes, (CDAEntry) cdaResource);

            for (int i = 0; i < displayItems.size(); i++) {
                if (displayItems.get(i).key.contains("photo") && !StringUtils.isBlank(displayItems.get(i).resource.toString())) {
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
            try {
                if (!StringUtils.isBlank(url)) {
                    Picasso.with(context).load(url).fit()
                            .placeholder(R.drawable.no_image)
                            .error(R.drawable.no_image)
                            .centerCrop().into(imageView);
                }
            }catch (Exception e) {

            }
        }
    }

}