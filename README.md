# Signify: Bridging Communication with Sign Language 🤟

Signify is an Android app designed to bridge the communication gap between hearing-impaired individuals and the hearing community. Using machine learning, it recognizes and interprets sign language gestures, enabling seamless real-time interaction.

## Background
According to The Central Statistics Agency, there were 923,941 Indonesians suffering from hearing impairments or total hearing loss in 2022. In many cases, people don't know how to communicate with them due to a lack of knowledge about sign language. This has motivated us to develop an app aimed at providing assistance and helping people learn sign language. 

Signify is a mobile application designed to bridge communication gaps between hearing-impaired individuals and the hearing community while providing an effective platform for learning sign language. Communication barriers due to limited accessibility to sign language tools pose challenges for individuals with hearing impairments, limiting their interactions and inclusivity in society. Signify addresses these issues by creating an easy-to-use application that helps users learn and communicate using sign language. 

This tool is valuable for individuals with hearing impairments, their families, and anyone interested in learning sign language to promote inclusive communication. Signify leverages machine learning to recognize and interpret sign language gestures, making it accessible and efficient for users who want to learn or interact with deaf individuals in real-time.

## Path Contributions
The success of this project was made possible through the collective contributions and collaboration of all involved teams:

- **Machine Learning**: Building models with TensorFlow Lite, using deep learning algorithms like CNN (Convolutional Neural Network), and utilizing MediaPipe to extract hand gestures for training models. From the training, we achieved 98% accuracy.
- **Mobile Development**: Deployment of TensorFlow Lite and creation of both a user and an admin app. The application features real-time connections using Firebase and a mechanism to buffer data in case of internet unavailability.
- **Cloud Computing**: Implementing an MQTT communication mechanism for backend and mobile apps. Also, creating a simple dashboard for monitoring service availability, logging, and housekeeping purposes.


![App](https://github.com/user-attachments/assets/4c23c729-7e65-4d07-b674-22e8f98351fe)

## Getting Started
To set up and run the Signify project, follow the steps below:

### Clone the Repository
```bash
git clone https://github.com/asapw/projectcapstone
cd projectcapstone
```

### Configure API Keys
Obtain the required API keys:
- **Gemini API**
- **News API**
- **Google Service Account Key**

Follow the instructions in the [API Keys Configuration](#api-keys-configuration) section to set up these keys.

### Sync the Project
1. Open the project in Android Studio.
2. Let Gradle sync and download the dependencies.
3. Wait for the sync to complete.

### Build and Run
1. Click the "Run" button in Android Studio.
2. Choose an emulator or connect a physical device.
3. Wait for the app to build and launch.

## API Keys Configuration

### Gemini API
1. Visit [Google AI Studio](https://ai.google/) and create a new API key.
2. Add the API key to the `local.properties` file:
   ```properties
   GEMINI_API_KEY=your_gemini_api_key_here
   ```

### News API
1. Go to [NewsAPI.org](https://newsapi.org/) and sign up to get your API key.
2. Add the API key to the `local.properties` file:
   ```properties
   NEWS_API_KEY=your_news_api_key_here
   ```

### Google Service Account Key
1. Go to the [Google Cloud Console](https://console.cloud.google.com/) and create a new project or select an existing one.
2. Navigate to **IAM & Admin** > **Service Accounts** and create a new service account.
3. Generate a JSON key file for the service account.
4. Create a `secrets` folder in your project root and place the JSON key file there, renaming it to `google-services.json`.

## Features
- **Learn Sign Language**: Interactive lessons and quizzes to help users master sign language.
- **Real-Time Gesture Recognition**: AI-powered detection for seamless communication.
- **Inclusive Design**: User-friendly interface for individuals, families, and educators.

## Development Roadmap
- **October**: Planning and UI/UX initial concept.
- **November**: Implementing Android app and core features.
- **December**: App finalization and refinement.

## Technology Stack
- **Frontend**: Android Native.
- **Backend**: Firebase, Google Cloud Services.
- **Machine Learning**: TensorFlow, MediaPipe.

## Contributors
We welcome contributions! Feel free to:
- Submit bug reports or feature requests.
- Fork the repository and submit a pull request.
- Share feedback to improve usability and accessibility.

## License
Signify is licensed under the [MIT License](./LICENSE).

## Acknowledgments
Thank you to everyone supporting inclusivity and accessibility for the hearing-impaired community.

## Contact
For questions or collaboration, reach out via [a195b4ky4107@bangkit.academy](mailto:a195b4ky4107@bangkit.academy).
