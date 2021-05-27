<h1 align="center">WatchReadPlay</h1>

## About the project
*WatchReadPlay* is an mobile application written in Kotlin for saving movies, series, books and games that we have finished or haven't finished yet and are planning to do so. 

Users can create an account by entering an email or using a Google account using [Firebase](https://firebase.google.com/docs/auth).

## Technologies
- Kotlin 1.3.72-release-Studio4.1-5
- Android Studio, v4.1.2
- Android Gradle Plugin, v4.1.2
- Gradle, v6.5
- SDK API 30 (Android 11)
- Google Services, v4.3.5
- Firebase Database, v19.2.1
- Firebase Database ktx, v19.7.0
- Firebase Auth ktx, v20.0.4

## Config Firebase
Installation and configuration of Firebase modules from the original documentation: 
- [Realtime Database](https://firebase.google.com/docs/database/android/start)
- [Firebase Authentication](https://firebase.google.com/docs/auth/android/firebaseui)

## Features
- Account
  - Create account
  - Login using e-mail
  - Login using Google account
- Records
  - Add new record
  - Edit record
  - Delete record
  - Search for record by title or original title + category (optional)
  - Select (unselect) record as 'Finished' 
- List
  - Divide the list into categories:
    - Movie
    - Serie
    - Game
    - Book
    - All
  - Divide the list by status:
    - Finished
    - Wishlist
    - All  
