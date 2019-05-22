package demo.com.helper;

import com.contentful.java.cda.CDAContentType;
import com.contentful.java.cda.CDAEntry;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import demo.com.CFApp;
import demo.com.R;
import demo.com.api.ResourceList;

/**
 * Created by tejaswini on 22/05/2019.
 */

public class Helper {
    public static  Map<String, CDAContentType> EXTRA_CONTENT_TYPE  ;
    public static CDAEntry EXTRA_CONTENT_ENTRY  ;

    private static HashMap<String, String> mimeTypesMap;

    public static CDAContentType getContentTypeForEntry(Map<String, CDAContentType> contentTypesMap,
                                                        CDAEntry entry) {


        Map contentType = entry.getAttribute("contentType");
        String contentTypeId = (String) ((Map) contentType.get("sys")).get("id");

        return contentTypesMap.get(contentTypeId);
    }

    public static String getTitleForEntry(CDAEntry entry, CDAContentType contentType) {
        String displayField = contentType.displayField();

        if (!StringUtils.isBlank(displayField)) {
            String result = entry.getField(displayField);

            if (StringUtils.isNotBlank(result)) {
                return result;
            }
        }

        return CFApp.getInstance().getString(R.string.app_name);
    }

    public static String getEntryDisplayField(CDAEntry entry, ResourceList resourceList) {
        return Helper.getContentTypeForEntry(resourceList.contentTypes, entry).displayField();
    }



}
