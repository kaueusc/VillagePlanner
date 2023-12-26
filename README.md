# Village Planner

## Getting started (running the application):
- **Make sure that you choose a pixel 2 device that allows google play services integration**
- Download a virtual Pixel 2 device with an API level of 24.
- Use Nougat as the Android OS version
- Run the application
- When first starting the application from android studio, the emulator may ask you to enable google play services, accept this message so that Andriod will redirect you to the Google Play store to download and enable Google Play Services for your device.
- Note that installing google play services may require you to sign into your google account on the emulator.
- Finally, sign up using the sign up page on the onboarding pages of the application or log in using an existing account.
- Now the app should be running! 🥳

## Basic feature
- Sign in and sign up using the screens that appear upon launching the app
- The profile tab on the bottom right contains all the user information
- To sign out, go to the profile tab and click “sign out” 

## Feature 1: Maps feature
- Click on the marker corresponding to the place within the village that you would like to navigate to. 
- Click the popup button near the bottom half of the screen prompting you to “navigate to X” where X is the name of the place that you want to travel to.
- Once this button is clicked a route will be drawn on the map from your current location to the destination.

## Feature 2: Wait time estimation
- When on the maps page of the application, you will see marker icons showing the estimated wait time for each place on the map.
- This wait time is estimated based on the current number of users at the current place along with a unique multiplier for each place for a more accurate estimation.

## Feature 3: Reminders
- On the bottom navigation bar of the application, you will see a reminders tab which will lead you to the reminders page
- In here you will see all the listed reminders for your personal account
- If you would like to create a new reminder, click on the plus button near the bottom half of the screen to navigate to a new popup where you can input additional information to create a new reminder.
- Once the time for the notification has been reached (notification time minus wait time), a dialog will show reminding you to start heading over to the given location 

## Improvements:
- Show delete confirmation message when deleting a message
- Users having a default profile picture if a user does not select an image
- Being able to change a user’s profile picture
- Improve UI/UX of the app
- Update layout of CreateReminderActivity
- Update button colors in ViewReminders
- Update Reminders in ViewReminders to CardViews (gives shadow to each reminder view)
- Immediately show route on marker click
