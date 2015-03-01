# Mutibo
## A Coursera Android Capstone project

This was my submission to the capstone course of the first Android Specialization run by Coursera. 

For more information about the capstone please see https://class.coursera.org/androidcapstone-001 or https://class.coursera.org/androidcapstone-001/wiki/Mutibofor the requirements of the project.

A video demostration of my submission : https://www.youtube.com/watch?v=-nSTSuENN5M

## Building the beast

### Requirements

Google+ or Facebook is used for authentication. You will need to register the web service and android application with Google and create a Facebook application.

### Server
The server component of the project uses Gradle as a build system and was developed with Netbeans. The current version was designed to be run locally and requires a Mongo database server.

Some information was redacted from the public release and will need to be replaced to be able to run the server. 

In mutibo_server/src/main/resources/application.properties:

- tmdb.apikey : API key for www.themoviedb.org to retrieve movie information
- token.secret : base64 encoded hash to encrypt security tokens
- google.clientids : Google Client ID for the android application
- google.audience : Google Client ID of the web application
- google.gcm.key : Key for Google Cloud Messaging

### Client
The android client was developed with Android Studio (0.8.9).

Some information was redacted from the public release and will need to be replace to run the app. 

In app/src/main/res/values/settings.xml :
- facebook_app_id : The registered Facebook Application for the Android app.
- google_token_scope : The audience for the token generated by Google.