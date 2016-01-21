package omar.mohamed.socialphotoneighbour.utility;

import javax.xml.parsers.ParserConfigurationException;

import com.gmail.yuyang226.flickr.Flickr;
import com.gmail.yuyang226.flickr.REST;
import com.gmail.yuyang226.flickr.RequestContext;
import com.gmail.yuyang226.flickr.oauth.OAuth;
import com.gmail.yuyang226.flickr.oauth.OAuthToken;

public final class FlickrHelper {

	private static FlickrHelper instance = null;
	public static final String API_KEY = "01bd8e557c0167f56bbc1d82e5e6370e"; //$NON-NLS-1$
	protected static final String API_SEC = "51d7478bbccb1dfb"; //$NON-NLS-1$
	

	public FlickrHelper() {

	}

	public static FlickrHelper getInstance() {
		if (instance == null) {
			instance = new FlickrHelper();
		}

		return instance;
	}

	public Flickr getFlickr() {
		try {
			Flickr f = new Flickr(API_KEY, API_SEC, new REST());
			return f;
		} catch (ParserConfigurationException e) {
			return null;
		}
	}
	
	

}
