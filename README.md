### Authors: 
* Harshita Vuradi (University of Florida) 
* Sahab Prasad Lanka (University of Florida)

## Introduction
CourseUF is built as an Android application. The major part of the system is Dialog flow(API.AI), a conversational user experience platform that enables natural language
interactions for devices. The voice interface here is cross platform. Dialog flow comes with a wide range of SDKs to support this. The advantage of using Dialog
flow is that we just need to design the interaction scenarios just once and the agent will understand no matter what platform or device the user is using.
The voice input to the system is through an android device.  
SQLite is the database used for this application. The dialog flow has an agent created for the application. The agent has a list of predefined intents that direct a user’s 
dialogue flow once the user starts using the system. The response from dialog flow, is a json object that contains important values like input context, output context, parameters.
User initiates the conversation with the system by speaking to an android device. Android Text To Speech is used to convert speech to text initially and then text to speech in the final step. 
The client access token of the agent created in dialog flow is used, to establish a connection between our android backend and dialog flow. The text generated from speech is sent to the dialog flow, which
then matches this incoming text to an intent in dialog flow. Each intent has an action field which can be used to perform intent specific actions in android backend.The intents have two main fields that decide the
dialogue conversation order between user and system, input context and output context. Input context is the name of the current context which can be the value of
an output context of another intent. Dialog flow responds with a json object with several fields. Once the android backend receives response from dialog
flow, intent specific actions in android backend are performed as follows:
1. Parameters like instructor name, course name, course number are captured that are passed as part of query to fetch course list.      
1. DB actions are categorized based on the action field set for each intent. For example, an action ‘all_courses’ fetches all courses from database where as an action ‘instructor_name_value’   
   fetches only courses based on instructor name.
1. The response also has the text response set for the intent which the system outputs to the user. There is a default fallback intent that is called every
   time a user input does not match any of the intents created in our agent. In general, this intent has genericresponse that asks the user to repeat what he said or ask for help.

## Dependencies:
* Android Studio : https://developer.android.com/studio/index.html
* Dialogue Flow (API.AI) : https://dialogflow.com/

## To Run:
* Create an account in Dialogue Flow and create intents accordingly. 

