# Daily-Hero
Daily Hero is a useful Android application that teaches its users about tons of DC and Marvel comic characters for free. No internet connection required. [Learn more](https://andersonaddo.github.io/Daily-Hero/)

**Note** Even though you are allowed to do anything with Daily Hero, I wouldn't recommend using it with itc content. The app couldn't be published due to the copyright of the images and he text (from ComicVine, DC an Marvel). So, if you'd want to use Daily Hero for your own project, **don't use its database or /drawable images.**

Also, if you plan of updating the database that runs Daily Hero (either for the app itself or a project built on top of it, remember to do two things:

 - Update the variable in the `AlarmReceiver.java` class that dictates how many records the database has
 - Update the database version in `MyDatabase.java`
