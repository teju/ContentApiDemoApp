package demo.com.adapters;

/**
 * Created by Khushvinders on 15-Nov-16.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.contentful.java.cda.CDAClient;

import org.apache.commons.lang3.StringUtils;
import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.ArrayList;
import java.util.List;

import demo.com.R;
import demo.com.helper.DisplayItem;


public class RecipeDetailAdapter extends RecyclerView.Adapter<RecipeDetailAdapter.MyViewHolder> {
    private final Context context;
    private List<DisplayItem> displayItems = new ArrayList<>();
    private final int imageSize;
    private final CDAClient client;
    private String url = "";

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView header_title;
        private final HtmlTextView name;
        private final ImageView display_image;
        private final LinearLayout root;


        public MyViewHolder(View view) {
            super(view);
            name = (HtmlTextView)view.findViewById(R.id.name);
            header_title = (TextView) view.findViewById(R.id.header_title);
            display_image = (ImageView) view.findViewById(R.id.display_image);
            root = (LinearLayout) view.findViewById(R.id.root);
        }

    }

    public RecipeDetailAdapter(Context context,  List<DisplayItem> displayItems, CDAClient client) {
        this.context = context;
        this.client = client;
        this.displayItems = displayItems;
        this.imageSize = context.getResources().getDimensionPixelSize(R.dimen.gallery_image_size);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.receipe_list_item_detail, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        try {
            holder.root.setTag(position);
            final DisplayItem resource = displayItems.get(position);

            if(!StringUtils.isBlank(resource.displayValue) && !StringUtils.isBlank(resource.key)) {
                holder.name.setText(resource.displayValue);
                holder.header_title.setText(resource.key);
                resource.displayValue = resource.displayValue.replaceAll("\"", " ");
                resource.displayValue = resource.displayValue.replaceAll("\\[", "").replaceAll("\\]","");

                holder.name.setHtml(resource.displayValue, new HtmlHttpImageGetter(holder.name));
            }
        } catch (Exception e) {
        }
    }

    @Override
    public int getItemCount() {
        return displayItems.size();
    }

}