package demo.com.helper;

import android.content.Context;

import com.contentful.java.cda.CDAArray;
import com.contentful.java.cda.CDAAsset;
import com.contentful.java.cda.CDAClient;
import com.contentful.java.cda.CDAContentType;
import com.contentful.java.cda.CDAEntry;
import com.contentful.java.cda.CDAField;
import com.contentful.java.cda.CDAResource;
import com.contentful.java.cda.FetchQuery;

import org.markdownj.MarkdownProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import demo.com.R;
import demo.com.api.CFClient;
import demo.com.api.ContentTypeWrapper;
import demo.com.api.ResourceList;

/**
 * Created by tejaswini on 22/05/2019.
 */

public class APISettings {

    private static ResourceList resourceList;

    public static CDAClient config(Context context) {
        CDAClient client = null;
        try {
            client = CDAClient.builder()
                    .setSpace(context.getResources().getString(R.string.space_id))
                    .setToken(context.getResources().getString(R.string.access_token))
                    .build();
        } catch (Exception e){
        }
        return client;

    }

    public static ArrayList<ContentTypeWrapper> fetchItems(CDAClient client){
        List<CDAResource> items = client.fetch(CDAContentType.class).all().items();
        ArrayList<ContentTypeWrapper> tmp = new ArrayList<>();
        if (items.size() > 0) {
            for (CDAResource res : items) {
                CDAContentType cdaContentType = (CDAContentType) res;

                // Entries count
                CDAArray entries = client.fetch(CDAEntry.class)
                        .where("content_type", cdaContentType.id())
                        .all();

                ContentTypeWrapper ct = new ContentTypeWrapper(cdaContentType, entries.total());
                tmp.add(ct);
            }
        }
        return tmp;

    }

    public static ResourceList performLoad(CDAClient client,CDAContentType contentType) {
        ResourceList resourceList = new ResourceList();

        FetchQuery<CDAEntry> query =
                client.fetch(CDAEntry.class).where("content_type", contentType.id());

        // Set the locale if non-default locale is currently configured
        String locale = CFClient.getLocale();
        if (locale != null) {
            query.where("locale", locale);
        }

        // Make the request
        CDAArray cdaArray = query.all();
        resourceList.resources = new ArrayList<>();

        // Prepare the result
        resourceList.resources.addAll(cdaArray.entries().values());
        setContentTypes(client,resourceList);
        return resourceList;
    }

    public static void setContentTypes(CDAClient client,ResourceList resourceList) {
        HashMap<String, CDAContentType> map = new HashMap<>();
        CDAArray array = client.fetch(CDAContentType.class).all();

        for (CDAResource res : array.items()) {
            map.put(res.id(), (CDAContentType) res);
        }

        resourceList.contentTypes = map;
    }
    public static CDAAsset performImageLoad(CDAClient client, CDAResource resource) {
        try {
            CDAAsset asset = (CDAAsset)resource;
            String name = "";
            return client.fetch(CDAAsset.class)
                    .one(asset.id());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<DisplayItem> performItemLoad(Map<String, CDAContentType> contentType, CDAEntry entry) {
        List<DisplayItem> tmp = new ArrayList<>();
        CDAContentType contentTyp = Helper.getContentTypeForEntry(contentType, entry);


        List<CDAField> fields = contentTyp.fields();
        MarkdownProcessor processor = new MarkdownProcessor();

        // Iterate Entry fields
        for (CDAField f : fields) {
            String id = f.id();

            Object value = entry.getField(id);

            // Skip if the value is empty
            if (value == null) {
                continue;
            }

            // Construct a new DisplayItem
            DisplayItem displayItem = new DisplayItem();

            // CDAFieldType of this item
            displayItem.fieldType = f.type();

            // Original field ID (from Content Type)
            displayItem.key = id;
            if ("Link".equals(displayItem.fieldType)) {
                prepareLinkItem(displayItem, value,contentTyp);
            } else if ("Location".equals(displayItem.fieldType)) {
               // prepareLocationItem(displayItem, value);
            } else if ("Array".equals(displayItem.fieldType)) {
                prepareArrayItem(displayItem, value, f.items());
            } else {
                prepareDefaultItem(displayItem, value);
            }
            tmp.add(displayItem);
        }

        return tmp;
    }


    private static void prepareDefaultItem(DisplayItem displayItem, Object value) {
        displayItem.displayValue = value.toString();
    }

    @SuppressWarnings("unchecked")
    private static void prepareArrayItem(DisplayItem displayItem, Object value, Map arrayItems) {
        displayItem.arrayItemType = (String) arrayItems.get("type");

        if ("Link".equals(displayItem.arrayItemType)) {
            displayItem.arrayLinkType = (String) arrayItems.get("linkType");
        }

        displayItem.array = (List<Object>) value;
    }

    private static void prepareLinkItem(DisplayItem displayItem, Object value, CDAContentType contentTyp) {
        displayItem.resource = (CDAResource) value;

        if (value instanceof CDAEntry) {
            displayItem.displayValue = Helper.getTitleForEntry((CDAEntry) value, contentTyp);
        }
    }


  }
