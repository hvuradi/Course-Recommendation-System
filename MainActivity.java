package app.uf.example.com.uf;

import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIContext;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import ai.api.RequestExtras;
import com.google.gson.JsonElement;
import android.content.Intent;

import java.util.Locale;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ArrayAdapter;

public class MainActivity extends AppCompatActivity implements AIListener, TextToSpeech.OnInitListener{

    private Button listenButton;
    private TextView resultTextView;
    private AIService aiService;
    private TextToSpeech tts;
    private boolean listening;
    List<AIContext> contexts = new ArrayList<>();

    String programLevel = "";
    String instructor = "";
    String courseTitle = "";
    String courseNumber = "";
    String section = "";
    String day = "";
    String watchlistCourse = "";
    String logtime = "";
    int errorCount = 0;

    DataBaseConn dbo;
    private ListView listViewItems;
    private ArrayAdapter<String> itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listenButton = (Button) findViewById(R.id.listenButton);
        resultTextView = (TextView) findViewById(R.id.resultTextView);

        contexts.add(new AIContext("courses-next-sem"));
        contexts.add(new AIContext("graduate-level"));
        final AIConfiguration config = new AIConfiguration("3b7d3b0d34c44b1db5abfa31ef8e58c6 ",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(this, config);
        aiService.setListener(this);
        tts = new TextToSpeech(this, this);
        dbo = new DataBaseConn(this);
    }

    public void listenButtonOnClick(final View view) {
        //aiService.startListening();
        if(listening) {
            aiService.cancel();
        }
        else {
            if(tts.isSpeaking()) {
                tts.stop();
            }
            resultTextView.setText("Listening");
            aiService.startListening();
        }
    }

    public void onResult(final AIResponse response) {
        Result result = response.getResult();
        String customResponse = "";
        boolean customFlag = false;

        // Get parameters
        String parameterString = "";
        if (result.getParameters() != null && !result.getParameters().isEmpty()) {
            for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                parameterString += "(" + entry.getKey() + ", " + entry.getValue() + ") ";
                switch(entry.getKey()){
                    case "instructor":
                        instructor = "" + entry.getValue();
                        break;
                    case "title":
                        courseTitle = "" + entry.getValue();
                        break;
                    case "code":
                        courseNumber = "" + entry.getValue();
                        courseNumber = courseNumber.replaceAll("\\s", "");
                        break;
                    case "section":
                        section += "" + entry.getValue();
                        break;
                    case "day":
                        day = "" + entry.getValue();
                        break;
                    case "coursename":
                        watchlistCourse = "" + entry.getValue();
                        break;
                }
            }
        }
        Log.d("ACTION", result.getAction());
        switch (result.getAction()){
            case "start":
                listViewItems = (ListView) findViewById(R.id.listViewItems);
                listViewItems.setVisibility(ListView.VISIBLE);
                listViewItems.setAdapter(null);
                dbo.clearWatchList();
                errorCount = 0;
                break;
            case "fallback":
                errorCount++;
            case "graduate_level":
                programLevel = "graduate";
                break;
            case "undergraduate_level":
                programLevel = "undergraduate";
                break;
            case "allcourses":
                ArrayList<String> courses = dbo.getAllCourses(programLevel);
                listViewItems = (ListView) findViewById(R.id.listViewItems);
                listViewItems.setVisibility(ListView.VISIBLE);
                itemsAdapter = new ArrayAdapter<String>(this,R.layout.list,courses);
                listViewItems.setAdapter(itemsAdapter);
                break;
            case "course_name_val":
                ArrayList<String> coursesn = dbo.getCoursesByName(courseTitle);
                listViewItems = (ListView) findViewById(R.id.listViewItems);
                listViewItems.setVisibility(ListView.VISIBLE);
                itemsAdapter = new ArrayAdapter<String>(this,R.layout.list,coursesn);
                listViewItems.setAdapter(itemsAdapter);
                if(coursesn.size() == 0){
                    customFlag = true;
                    customResponse = "I could not find any courses with the name "+courseTitle+". Please say the name again";

                }
                Log.d("Length", ""+coursesn.size());
                break;
            case "course_number_val":
                ArrayList<String> coursesc = dbo.getCourseByCode(courseNumber);
                listViewItems = (ListView) findViewById(R.id.listViewItems);
                listViewItems.setVisibility(ListView.VISIBLE);
                itemsAdapter = new ArrayAdapter<String>(this,R.layout.list,coursesc);
                listViewItems.setAdapter(itemsAdapter);
                if(coursesc.size() == 0){
                    customFlag = true;
                    customResponse = "I could not find any courses with the course number "+courseNumber+". Please say the course number again";
                }
                break;
            case "instructor_name_val":
                ArrayList<String> coursesi = dbo.getCoursesByInstructor(instructor);
                listViewItems = (ListView) findViewById(R.id.listViewItems);
                listViewItems.setVisibility(ListView.VISIBLE);
                itemsAdapter = new ArrayAdapter<String>(this,R.layout.list,coursesi);
                listViewItems.setAdapter(itemsAdapter);
                if(coursesi.size() == 0){
                    customFlag = true;
                    customResponse = "I could not find any courses taken by the instructor "+instructor+" . Please say the instructor name again";
                }
                break;
            case "anything_no":
            case "allcourses_no_no":
                Intent intent2 = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent2);
                dbo.clearWatchList();
                dbo.insertLogging(logtime, errorCount);
                Log.d("ERRORCOUNT", ""+errorCount);
                logtime = "";
                errorCount = 0;
                break;
            case "watchlist_add":
                Log.d("WATCHLISTADD", watchlistCourse);
                ArrayList<String> watchlistcourses = dbo.insertInWatchlist(watchlistCourse);
                listViewItems = (ListView) findViewById(R.id.listViewItems);
                listViewItems.setVisibility(ListView.VISIBLE);
                itemsAdapter = new ArrayAdapter<String>(this,R.layout.list,watchlistcourses);
                listViewItems.setAdapter(itemsAdapter);
                if(watchlistcourses.size() == 0){
                    customFlag = true;
                    customResponse = "I could not find any courses with the name "+watchlistCourse+" . Please say the name again";
                }
                break;
            case "dummy_add":
                //dbo.insertSampleData("Theory and Practice of Multimedia Production","CAP3020","David T Small","undergraduate","W","5:10PM-8:10PM");
//                dbo.insertSampleData("Interactive Modeling and Animation 1","CAP3032","Rong Zhang","undergraduate","M,W,F","9:35 AM - 10:25 AM");
//                dbo.insertSampleData("Introduction to Computer-Aided Animation","CAP3034","Rong Zhang","undergraduate","M,W,F","8:30 AM - 9:20 AM");
//                dbo.insertSampleData("Artificial Intelligence for Computer Games","CAP4053","Lisa Anthony","undergraduate","M,W,F","12:50 PM - 1:40 PM");
//                dbo.insertSampleData("Computational Structures in Computer Graphics","CAP4730","Alireza Entezari","undergraduate","M,W,F","10:40 AM - 11:30 AM");
//                dbo.insertSampleData("Projects Data Science","CAP4773","Zhe Wang","undergraduate","","Web (100%)");
//                dbo.insertSampleData("Introduction to Computer Organization","CDA3101","Jihkwon Peir","undergraduate","T,R","3:00 PM - 4:55 PM,4:05 PM - 4:55 PM");
//                dbo.insertSampleData("EMBEDDED SYSTEMS","CDA4630","Prabhat Kumar Mishra","undergraduate","T,R","10:40AM-11:30,10:40 AM - 12:35 PM");
//                dbo.insertSampleData("Introduction to Software Engineering","CEN3031","Joshua E Fox","undergraduate","M,W,F","3:00 PM - 3:50 PM");
//                dbo.insertSampleData("Computer and Information Science and Engineering Design 1","CEN3913","Mark S Schmalz","undergraduate","T,R","1:55 PM - 2:45 PM");
//                dbo.insertSampleData("Software Testing and Verification","CEN4072","Stephen M Thebaut","undergraduate","M,W,F","1:55 PM - 2:45 PM");
//                dbo.insertSampleData("Human-Computer Interaction","CEN4721","Jaime Ruiz","undergraduate","M,W,F","9:35 AM - 10:25 AM");
//                dbo.insertSampleData("Computer and Information Science and Engineering Design 2","CEN4914","Mark S Schmalz","undergraduate","","Web (100%)");
//                dbo.insertSampleData("Legal and Social Issues in Computing","CGS3065","","undergraduate","T,R","8:30 AM - 10:25 AM, 8:30 AM - 9:20 AM");
//                dbo.insertSampleData("Information and Database Systems 1","CIS4301","Peter J Dobbins","undergraduate","M,W,F","9:35 AM - 10:25 AM");
//                dbo.insertSampleData("Individual Study in CISE","CIS4095","Richard E Newman","undergraduate","","Web (100%)");
//                dbo.insertSampleData("Integrated Product and Process Design 2","CIS4913C","Keith R Stanfill","undergraduate","T","3:00 PM - 6:00 PM");
//                dbo.insertSampleData("Senior Project","CIS4914","Mark S Schmalz","undergraduate","","Web (100%)");
//                dbo.insertSampleData("Special Topics in CISE","CIS4930","Joseph N Wilson","undergraduate","M,W,F","12:50 PM - 1:40 PM");
//                dbo.insertSampleData("Practical Work","CIS4940","Richard E Newman","undergraduate","","");
//                dbo.insertSampleData("Co-Op Work in CISE","CIS4949","Richard E Newman","undergraduate","","");
//                dbo.insertSampleData("Computer Network Fundamentals","CNT4007C","Jonathan C L Klavan","undergraduate","M,W,F","10:40 AM - 11:30 AM");
//                dbo.insertSampleData("Computer Programming Using JAVA","COP2800","Elizabeth A Matthews","undergraduate","M,W,F","11:45 AM - 12:35 PM");
//                dbo.insertSampleData("Computer Programming Using C","COP3275","STAFF","undergraduate","M,W,F","3:00 PM - 3:50 PM");
//                dbo.insertSampleData("Programming Fundamentals 1","COP3502","Jeremiah J Blanchard","undergraduate","T,R","8:30 AM - 10:25 AM,9:35 AM - 10:25 AM");
//                dbo.insertSampleData("Programming Fundamentals 2","COP3503","jonathan C L Klavan","undergraduate","M,W,F","8:30 AM - 9:20 AM");
//                dbo.insertSampleData("Data Structures and Algorithm","COP3530","Cheryl Resch","undergraduate","M,W,F","10:40 AM - 11:30 AM");
//                dbo.insertSampleData("Programming Language Concepts","COP4020","Manuel E Bermudez","undergraduate","M,W,F","12:50 PM - 1:40 PM");
//                dbo.insertSampleData("Object-oriented Programming","COP4331","David T Small","undergraduate","M,W,F","1:55 PM - 2:45 PM");
//                dbo.insertSampleData("Operating Systems","COP4600","Jeremiah J Blanchard","undergraduate","M,W,F","4:05 PM - 4:55 PM");
//                dbo.insertSampleData("Applications of Discrete Structures","COT3100","Christina A Boucher","undergraduate","M,W,F","3:00 PM - 3:50 PM");
//                dbo.insertSampleData("Numerical Analysis: A Computational Approach","COT4501","Jorg Peters","undergraduate","M,W,F","10:40 AM - 11:30 AM");
//                dbo.insertSampleData("Engineering Directed Independent Research","EGN4912","Richard E Newman","undergraduate","","Web (100%)");
//                dbo.insertSampleData("HUMAN-COMPUTER INTERACTION","CAP5100","Jaime Ruiz","graduate","M,W,F","9:35 AM - 10:25 AM");
//                dbo.insertSampleData("RESEARCH METHOD FOR HCC","CAP5108","Eakta Jain","graduate","T,R","3:00 PM - 4:55 PM,4:05 PM - 4:55 PM");
//                dbo.insertSampleData("MALWARE REVERSE ENGINEERING","CAP6137","Joseph N Wilson","graduate","M,W,F","12:50 PM - 1:40 PM");
//                dbo.insertSampleData("MACHINE LEARNING","CAP6610","Paul D Gader","graduate","","Web(100%)");
//                dbo.insertSampleData("ADVANCE COMPUTER GRAPHICS","CAP6701","Corey Theresa Toler-Franklin","graduate","M,W,F","10:40 AM - 11:30 AM");
//                dbo.insertSampleData("EMBEDDED SYSTEMS","CDA5636","Prabhat Kumar Mishra","graduate","","Web (100%)");
//                dbo.insertSampleData("SOFTWARE TEST/VERIFICATION","CEN6070","Stephen M Thebaut","graduate","M,W,F","1:55 PM - 2:45 PM");
//                dbo.insertSampleData("COMPUTER & INFORMATION SECURITY","CIS5370","Kevin Butler","graduate","M,W,F","12:50 PM - 1:40 PM");
//                dbo.insertSampleData("INTRODUCTION TO CRYPTOLOGY","CIS5371","Thomas Shrimpton","graduate","T,R","8:30 AM - 10:25 AM,9:35 AM - 10:25 AM");
//                dbo.insertSampleData("INDIVIDUAL STUDY","CIS6905","Ahmed Abdelghaffar Helmy","graduate","","");
//                dbo.insertSampleData("SUPERVISED RESEARCH","CIS6910","Ahmed Abdelghaffar Helmy","graduate","","");
//                dbo.insertSampleData("3D AUDIO INTERFACES","CIS6930","Kyla McMullen","graduate","T,R","3:00 PM - 4:55 PM,4:05 PM - 4:55 PM");
//                dbo.insertSampleData("ALGORITHMIC ECONOMICS","CIS6930","Meera Sitharam","graduate","T,R","1:55 PM - 2:45 PM,1:55 PM - 3:50 PM");
//                dbo.insertSampleData("APPROXIMATION QUERY PROCESSING","CIS6930","Alin Viorel Dobra","graduate","T,R","1:55 PM - 2:45 PM,1:55 PM - 3:50 PM");
//                dbo.insertSampleData("COMPUTATIONAL NEUROSCIENCE","CIS6930","Arunava Banerjee","graduate","T,R","1:55 PM - 2:45 PM,1:55 PM - 3:50 PM");
//                dbo.insertSampleData("DIGITAL CURRENCIES","CIS6930","Richard E Newman","graduate","M,W,F","11:45 AM - 12:35 PM");
//                dbo.insertSampleData("MOBILE NETWORKING","CIS6930","Ahmed Abdelghaffar Helmy","graduate","M,W,F","11:45 AM - 12:35 PM");
//                dbo.insertSampleData("NETWORK ALGORITHMS DATA STRUCTURE","CIS6930","Shigang Chen","graduate","M,W,F","9:35 AM - 10:25 AM");
//                dbo.insertSampleData("VR FOR SOCIAL GOOD","CIS6930","Benjamin Lok","graduate","T","5:10 PM - 7:05 PM");
//                dbo.insertSampleData("GRADUATE SEMINAR","CIS6935","STAFF","graduate","","");
//                dbo.insertSampleData(" MASTERS RESEARCH","CIS6971","Ahmed Abdelghaffar Helmy","graduate","","");
//                dbo.insertSampleData("ADVANCED RESEARCH","CIS7979","Ahmed Abdelghaffar Helmy","graduate","","");
//                dbo.insertSampleData("DOCTORAL RESEARCH","CIS7980","Ahmed Abdelghaffar Helmy","graduate","","");
//                dbo.insertSampleData("COMPUTER NETWORKS","CNT5106C","Ye Xia","graduate","T,R","1:55 PM - 3:50 PM,1:55 PM - 2:45 PM");
//                dbo.insertSampleData("ADVANCE DATA STRUCTURES","COP5536","Sartaj Kumar Sahni","graduate","M,W,F","1:55 PM - 2:45 PM");
//                dbo.insertSampleData("PROGRAM LANGUAGE PRINCIPLES","COP5556","Beverly A Sanders","graduate","T,R","4:05 PM - 4:55 PM,3:00 PM - 4:55 PM");
//                dbo.insertSampleData("DATA BASE MANAGEMENT SYS","COP5725","Markus Paul Schneider","graduate","T,R","8:30 AM - 9:20 AM,8:30 AM - 10:25 AM");
//                dbo.insertSampleData("DATABASE SYS IMPLEMENTATION","COP6726","Alin Viorel Dobra","graduate","T,R","8:30 AM - 10:25 AM,9:35 AM - 10:25 AM");
//                dbo.insertSampleData("ANALYSIS OF ALGORITHMS","COT5405","Alper Ungor","graduate","T,R","9:35 AM - 11:30 AM,10:40 AM - 11:30 AM");
//                dbo.insertSampleData("COMPUTATIONL GEOMETRY","COT5520","Alper Ungor","graduate","T,R","3:00 PM - 4:55 PM,4:05 PM - 4:55 PM");
//                dbo.insertSampleData("PRAC/INTERN/COOP WORK","EGN5949","Ahmed Abdelghaffar Helmy","graduate","","");
//                dbo.insertSampleData("ENGINEERING GRAD RSCH","EGN6913","Ahmed Abdelghaffar Helmy","graduate","","");

        }

        // Show results in TextView.
        resultTextView.setText("Query:" + result.getResolvedQuery());
//                "\nAction: " + result.getAction() +
//                "\nParameters: " + parameterString);
        if(customFlag){
            speakOut(customResponse);
        } else{
            speakOut(response.getResult().getFulfillment().getSpeech().toString());
        }

    }

    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This language is not supported");
            }
            else {

            }
        } else {
            Log.e("TTS", "Initialization failed");
        }
    }

    @Override
    public void onDestroy() {
        if(tts!= null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    private void speakOut(String text) {
        tts.speak(text,TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onError(final AIError error) {
        resultTextView.setText(error.toString());
    }

    @Override
    public void onListeningStarted() {}

    @Override
    public void onListeningCanceled() {
        resultTextView.setText("Click button to speak");
        listening = false;
    }

    @Override
    public void onListeningFinished() { listening = false; }

    @Override
    public void onAudioLevel(final float level) {}
}
