# Social Lives

Android Application that permits to retrieve the public photo of Flickr users in the surroundings and display them in a Gallery, showing also a map with pins that locate the publication location of each of them. 

## Setup Project

1) Clone this project
2) Follow [this](https://developers.google.com/maps/documentation/android-sdk/signup) guide in order to obtain a Google Maps API Key
3) Follow [this](https://www.flickr.com/services/api/misc.api_keys.html) link to request a Flickr API Key
4) Create a secrets.xml file under the res/values folder and add the following:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="google_play_maps_api_key">Your Google Maps API Key</string>
    <string name="flickr_api_key">Your Flickr API Key</string>
</resources>
```

You're all set!

## Google Playstore Link
[Social Lives App](https://play.google.com/store/apps/details?id=omar.mohamed.socialphotoneighbour)

## Notes
The Map functionality is temporary disabled in the master, develop and consequently the production version of the app due to Flickr APIs that seems that, in certain case are not providing anymore the geo-coordinates of public photos; Working on a workaround in the version-with-map branch.
