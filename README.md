# VideoDownloader
A simple android app that just downloads videos shared to it by using youtube-dl as the backend (hosted on heroku)

There is an alpha version currently in the play store. Note that the app has been renamed to "Get Media!" as there is already a VideoDownloader app on the play store
To get it from the play Store, you'll need to first opt-into [the alpha test here](https://play.google.com/apps/testing/uk.me.gman.getmedia)

And then [download it from here](https://play.google.com/store/apps/details?id=uk.me.gman.getmedia)

Or get the [precompiled apk directly from the repo](https://github.com/zeronickname/VideoDownloader/raw/master/app/app-release.apk)


# Use
Share a URL (from any other app) to Get Media!. It'll hit a [youtube-dl-api-server](https://github.com/jaimeMF/youtube-dl-api-server/) hosted on heroku and parse the actual link to the media contained in the URL. If that works, it'll download the media using android's download manager


# Supported sites
Everything [supported by youtube-dl](https://rg3.github.io/youtube-dl/supportedsites.html)
## Youtube support
To get this approved on the play store, youtube downloads are disabled by default. To (re)enable support for youtube, open the app, overflow menu and hit about. Repeat this procedure a further 4 times. Now Youtube links work (note: private links still won't work)