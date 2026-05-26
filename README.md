# What is InstantCoords?

InstantCoords is a lightweight, offline android utility designed for emergencies and off-grid navigation. Displaying your coordinates in a notification that refreshes every couple of seconds! 

Need to share your location during an emergency or when maps can't load? Just tap the notification, and your coordinates are copied!

## Features 

* *Entirely offline functionality:* The app uses GPS services which are built-in your device. And it obviously does not require internet connection. (only maps need connection)
* **Notification copying Functionality:* Tapping on the app's notification copies the latitude and longitude data to your clipboard for quick pasting into SMS, satellite messengers, or whichever you want. (It's your coordinates after all. Do what you want with it)
* *Privacy Centric:* Completely free from trackers, **no Internet permission (`android.permission.INTERNET`) declared in the manifest** proving there cannot be any trackers. Your location data never leaves your device. (Unless you obviously send it to someone)
* *Lightweight application:* The app is free from ads, bloat, and useless stuff. Making it entirely weightless. (Just in case of low storage.)

## the app requires the following system permissions:
* `ACCESS_FINE_LOCATION` & `ACCESS_COARSE_LOCATION`: To communicate directly with the phone's physical GPS sensor.
* `FOREGROUND_SERVICE` & `FOREGROUND_SERVICE_LOCATION`: Required by Android 14+ to prevent the background tracking engine from being suspended.
* `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`: Exempts the application from aggressive doze modes, ensuring background updates survive when the screen is turned off in a pocket.
* `POST_NOTIFICATIONS`: Required on Android 13+ to cleanly pin the coordinates widget to your status tray.

## Usage Terms & License

> This project is licensed under the GNU General Public License v3.0 (GPL-3.0).
Terms of Use:

    No Liability: This software is provided "as is" without warranty of any kind. The [author](https://github.com/cxcolor) is not responsible for any damage, data loss, or issues caused by the use or misuse of this software.

    Notification: Please notify the [author](https://github.com/cxcolor) before using or deploying this project in your own applications.

This project is open-source and available under the [MIT License](LICENSE). Feel free to audit, fork, or adapt the code for your own safety-critical applications.
