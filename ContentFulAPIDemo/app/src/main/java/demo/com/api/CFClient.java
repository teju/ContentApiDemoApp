package demo.com.api;

import android.content.Context;

import com.contentful.java.cda.CDAClient;

import org.apache.commons.lang3.StringUtils;

import demo.com.R;

public class CFClient {
  private static CDAClient sInstance;
  private static String locale;

  private CFClient() {
  }

  /**
   * Initialize this client.
   *
   * @param space String representing the Space key.
   * @param token String representing the access token required to log in to the Space.
   * @return {@link com.contentful.java.cda.CDAClient} instance.
   */
  public synchronized static CDAClient init(String space, String token) {
    sInstance = CDAClient.builder().setSpace(space).setToken(token).build();

    locale = null;

    return sInstance;
  }

  public synchronized static CDAClient getClient(Context context) {
    if (sInstance == null) {

      String space = context.getResources().getString(R.string.space_id);
      String token = context.getResources().getString(R.string.access_token);

      if (StringUtils.isBlank(space) || StringUtils.isBlank(token)) {
        throw new IllegalStateException("Uninitialized client.");
      }

      return init(space, token);
    }

    return sInstance;
  }

  /**
   * Set the locale for this client.
   *
   * @param locale String representing the Locale code.
   */
  public synchronized static void setLocale(String locale) {
    CFClient.locale = locale;
  }

  /**
   * Gets the current locale defined for this client.
   *
   * @return String representing the current configured locale, null in case the default
   * locale is being used.
   */
  public static String getLocale() {
    return locale;
  }
}
