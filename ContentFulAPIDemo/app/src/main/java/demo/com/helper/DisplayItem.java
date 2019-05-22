package demo.com.helper;

import com.contentful.java.cda.CDAResource;

import java.util.List;

public class DisplayItem {
  public String key;
  public String displayValue;
  public String fieldType;
  public CDAResource resource;
  public String imageURI;

  // Array
  public List<Object> array;
  public String arrayItemType;
  public String arrayLinkType;
}
