# 🌊 Word Wave

## 📜 Description

Welcome to Word Wave! This is an Android-based chatting application developed using Java and XML in Android Studio, with Firebase handling the database management and ZegoCloud enabling audio and video calling functionality.

## 🛠️ Development Tools

Developed using:
- 📱 Android Studio
- ☕ Java
- 🖌️ XML
- 💾 Firebase

## 📂 Sections

Word Wave is devided into 5 sections

1. **🔒 Authentication**
2. **🧑‍💼 Profile Edit and Initialization**
3. **🏠 Main Activity**
   - 👤 Profile Info
   - 📸 Photo Status
   - 💬 Chat List
   - 🔍 Search User
4. **🗨️ Individual Chat**
5. **ℹ️ Individual User Info**

## 📋 Details of Word Wave

### 🔒 Authentication

- **Sign Up:** Users can register yourself using Google 🌐, phone number 📱, or email-password authentication 📧.
- **Sign In:** Secure sign-in process using Google 🌐, phone number 📱, or email-password 📧.
- **Google:** Integration with Google accounts for seamless access.
- **Email-Password:** Standard email and password registration.
  - **🔑 Forget Password:** Users can reset their password by entering their registered email. A reset link is sent to the email address provided, allowing users to securely reset their password.
- **Phone Number:** Integrate your account with a phone number 📱.
  - **🔢 OTP Verfication:** OTP verification is required every time for sign-in and sign-up.
- **🆔Individual Accounts:** Each user has a distinct account.
  
   **Note:** Authentication functionalities are implemented using Firebase Authentication services and you do not need to sign in yourself every time.

### 🧑‍💼 Profile Initialization and Edit

- **Profile Initialization:** When a user first time signs up, they need to initialize their profile by setting a profile photo 📷, username 🆔, fullname ✍️, email 📧, and phone number 📱.
- **Edit Profile:** Users can edit their profile information of already exists account.

   **Note:** 
     1. Firebase Storage is used for storing profile photos of users and Firebase Firestore is used for storing user's other information.
     2. Usernames must be unique; you cannot set a username that is already taken by another user.
        
### 🏠 Main Activity

After completing the sign-up/sign-in process and initializing/editing their profile, users navigate to the Main Activity, which is divided into four parts: Chat List, Photo Status, Profile Info, and Search User.

1. **💬 Chat List**
   - Displays a list of users you have previously chatted with.
   - Each list item shows the other user's profile photo 📷, username 🆔, full name ✍️, and icons for audio 🎧 and video 📹 calls.
   - Click on a user's list item to start a chat 💬. Clicking on the profile photo brings up a dialog popup showing the photo 📷 and username 🆔, with two buttons: one to chat with the user 💬 and the other to view the user's info ℹ️.

2. **📸 Photo Status**
   - Users can set a photo as their status 📸 and view the statuses of others.
   - Displays your status 📸 and the statuses of users in your chat list.
   - Shows the time 🕒 when each status was set.
   - Click on a status to view it in full screen 📺.

3. **👤 Profile Info**
   - Shows all your account information ℹ️.
   - Allows you to edit ✏️ your profile information and log out 🔒.

4. **🔍 Search User**
   - A search bar is provided at the top of the main activity 🔍.
   - Search for users by their username 🆔.
   - Displays all users who have previously created an account in Word Wave.
   - Each list item shows the other user's profile photo 📷, username 🆔, full name ✍️, and icons for audio 🎧 and video 📹 calls.
   - Click on a user's list item to start a chat 💬. Clicking on the profile photo brings up a dialog popup showing the photo 📷 and username 🆔, with two buttons: one to chat with the user 💬 and the other to view the user's info ℹ️.

   **Note:** 
     1. Firbase Realtime Database and RecyclerView is used for implimentation of Chat List and Search User.
     2. For retrive and show user's info into Profile Info section Firebase Firestore is used.
     3. Photo Status is implimented using Firebase Realtime Database and Firebase Storage.
        
### 🗨️ Individual Chat

In the Individual Chat section, you can chat with a particular user. This activity includes a RecyclerView and a toolbar. 

- **Toolbar:** 
  - Displays the particular user's profile photo 📷, username 🆔, and user status.
  - **User status shows "online" if the particular user is online, otherwise it shows the last time and date when the user was online.**
  - Options for audio 🎧 and video 📹 calls.
  - Clicking on the profile photo brings up a dialog popup showing the profile photo 📷 and username 🆔, with two buttons: one to chat with the user 💬 and the other to view the user's info ℹ️.
  - Clicking on the toolbar navigates to the Individual User Info, where you can see the info of the particular user.

- **RecyclerView:**
  - Displays a list of messages or chats with the particular user 📋.
  - Each message shows the content and the time it was sent 🕒.
  - Options to **Delete for Me** 🗑️ and **Delete for Everyone** 🚫 for every message.
  - Easily send new messages 💬 to the particular user directly from this screen.

**Note:** The implementation of user status 🟢🔴 and chat 💬 functionality is made possible by Firebase Realtime Database.

### ℹ️ Individual User Info

- Displays the particular user's  profile photo 📷, fullname ✍️, username 🆔, phone number 📱, email 📧, etc.
- Options for audio 🎧 /video 📹 calls and sharing user info via other platforms 🌐.
  
### 📌 Additional Featuers

1. **🔑 Unique Username:** We can uniquly identify each user with their username.
2. **🕒 Message History:** View all previous chats upon logging in.
3. **🗨️ Message Yourself:** Send messages to yourself for notes or reminders.

## 📸 Screenshots

You can see screenshots of this app in the [screenshots](https://github.com/Raj-Pavara/WordWave/tree/main/screenshots) folder.

## 💡 Feedback

We appreciate your feedback! If you have any suggestions or find any issues, please open an issue or reach out to us.

## 🔗 LinkedIn Profile

You can watch a demo video of the Word Wave app on my LinkedIn profile: [My LinkedIn Profile](https://www.linkedin.com/in/raj-pavara-6b65262aa?utm_source=share&utm_campaign=share_via&utm_content=profile&utm_medium=android_app)

Feel free to connect with me on LinkedIn for more updates and discussions!

## 🚀 Getting Started

1. **Clone the repository:**
   ```bash
   git clone https://github.com/Raj-Pavara/Word-Wave.git
