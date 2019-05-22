package demo.com.api;

import android.content.Context;

import com.contentful.java.cda.CDAClient;

import demo.com.CFApp;
import demo.com.R;


public class CFDiscoveryClient {
  private static CDAClient sInstance;

  private CFDiscoveryClient() {
  }

  public static CDAClient getClient() {
    if (sInstance == null) {
      Context context = CFApp.getInstance();

      sInstance =
          CDAClient.builder().setSpace(context.getString(R.string.space_id))
              .setToken(context.getString(R.string.access_token))
              .build();
    }

    return sInstance;
  }
}
