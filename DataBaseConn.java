package app.uf.example.com.uf;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
/**
 * Created by prasa on 12/2/2017.
 */

public class DataBaseConn extends SQLiteOpenHelper {
    public static final String dataBaseName = "UFCoursesDB.db";
    public static final String TABLE_NAME = "courses";
    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String CODE = "code";
    public static final String INSTRUCTOR = "instructor";
    public static final String LEVEL = "level";
    public static final String DAY = "day";
    public static final String TIME = "time";

    public DataBaseConn(Context context){
        super(context,dataBaseName,null,1);
    }

    public void onCreate(SQLiteDatabase db){
//        db.execSQL(
//                "CREATE TABLE courses " +
//                        "(id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, code TEXT, instructor TEXT, level TEXT, day TEXT, time TEXT)"
//        );

        db.execSQL(
                "CREATE TABLE watchlist "+
                        "(id INTEGER,  title TEXT, code TEXT, instructor TEXT, level TEXT, day TEXT, time TEXT)"
        );

        db.execSQL(
                "CREATE TABLE userlogging "+
                        "(id INTEGER PRIMARY KEY AUTOINCREMENT, time TEXT, errors INTEGER)"
        );
    }

    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
        db.execSQL("DROP TABLE IF EXISTS watchlist");
        onCreate(db);
    }

    public ArrayList<String> getCoursesByName(String courseName){
        ArrayList<String> courses = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        courseName = courseName.replace("\"", "");
        Cursor res = db.rawQuery("select * from courses where title like \"%"+courseName+"%\"",null);
        //Cursor res = db.query("courses", new String[] {"title"},"title LIKE '?'", new String[]{"%"+courseName+"%"}, null, null, null);

        res.moveToFirst();
        while(!res.isAfterLast()){
            String course = "ID:"+res.getString(res.getColumnIndex(ID))+"\n";
            course += "Title:"+res.getString(res.getColumnIndex(TITLE))+"\n";
            course += "Course Number:"+res.getString(res.getColumnIndex(CODE))+"\n";
            course += "Instructor:"+res.getString(res.getColumnIndex(INSTRUCTOR))+"\n";
            course += "Program Level:"+res.getString(res.getColumnIndex(LEVEL))+"\n";
            String[] timeArr = res.getString(res.getColumnIndex(TIME)).split(",");
            String[] dayArr = res.getString(res.getColumnIndex(DAY)).split(",");
            if(timeArr.length > 1){
                course += "Day:"+dayArr[0]+"   "+timeArr[0]+"\n";
                course += dayArr[1]+"   "+timeArr[1];
            } else{
                course += "Day:"+res.getString(res.getColumnIndex(DAY))+"   "+res.getString(res.getColumnIndex(TIME));
            }
            courses.add(course);
            res.moveToNext();
        }

        return courses;
    }

    public ArrayList<String> getCoursesByInstructor(String instructor){
        ArrayList<String> courses = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        instructor = instructor.replace("\"", "");
        Log.d("INSTRUCTOR", instructor);
        Cursor res = db.rawQuery("select * from courses where instructor like '%"+instructor+"%'",null);
        res.moveToFirst();
        while(!res.isAfterLast()){
            String temp = "ID:"+res.getString(res.getColumnIndex(ID))+"\n";
            temp += "Title:"+res.getString(res.getColumnIndex(TITLE))+"\n";
            temp += "Course Number:"+res.getString(res.getColumnIndex(CODE))+"\n";
            temp += "Instructor:"+res.getString(res.getColumnIndex(INSTRUCTOR))+"\n";
            temp += "Program Level:"+res.getString(res.getColumnIndex(LEVEL))+"\n";
            String[] timeArr = res.getString(res.getColumnIndex(TIME)).split(",");
            String[] dayArr = res.getString(res.getColumnIndex(DAY)).split(",");
            if(timeArr.length > 1){
                temp += "Day:"+dayArr[0]+"   "+timeArr[0]+"\n";
                temp += dayArr[1]+"   "+timeArr[1];
            } else{
                temp += "Day:"+res.getString(res.getColumnIndex(DAY))+"   "+res.getString(res.getColumnIndex(TIME));
            }
            courses.add(temp);
            res.moveToNext();
        }

        return courses;
    }

    public ArrayList<String> getCourseByCode(String code){
        ArrayList<String> allCourses = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from courses where code = "+code,null);
        res.moveToFirst();
        while(!res.isAfterLast()){
            String course = "ID:"+res.getString(res.getColumnIndex(ID))+"\n";
            course += "Title:"+res.getString(res.getColumnIndex(TITLE))+"\n";
            course += "Course Number:"+res.getString(res.getColumnIndex(CODE))+"\n";
            course += "Instructor:"+res.getString(res.getColumnIndex(INSTRUCTOR))+"\n";
            course += "Program Level:"+res.getString(res.getColumnIndex(LEVEL))+"\n";
            String[] timeArr = res.getString(res.getColumnIndex(TIME)).split(",");
            String[] dayArr = res.getString(res.getColumnIndex(DAY)).split(",");
            if(timeArr.length > 1){
                course += "Day:"+dayArr[0]+"   "+timeArr[0]+"\n";
                course += dayArr[1]+"   "+timeArr[1];
            } else{
                course += "Day:"+res.getString(res.getColumnIndex(DAY))+"   "+res.getString(res.getColumnIndex(TIME));
            }
            allCourses.add(course);
            res.moveToNext();
        }

        return allCourses;
    }

    public ArrayList<String> getAllCourses(String level){
        ArrayList<String> allCourses = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from courses where level = '"+level+"'",null);
        res.moveToFirst();
        while(!res.isAfterLast()){
            String temp="ID:"+res.getString(res.getColumnIndex(ID))+"\n";
            temp += "Title:"+res.getString(res.getColumnIndex(TITLE))+"\n";
            temp += "Course Number:"+res.getString(res.getColumnIndex(CODE))+"\n";
            temp += "Instructor:"+res.getString(res.getColumnIndex(INSTRUCTOR))+"\n";
            temp += "Program Level:"+res.getString(res.getColumnIndex(LEVEL))+"\n";
            String[] timeArr = res.getString(res.getColumnIndex(TIME)).split(",");
            String[] dayArr = res.getString(res.getColumnIndex(DAY)).split(",");
            if(timeArr.length > 1){
                temp += "Day:"+dayArr[0]+"   "+timeArr[0]+"\n";
                temp += dayArr[1]+"   "+timeArr[1];
            } else{
                temp += "Day:"+res.getString(res.getColumnIndex(DAY))+"   "+res.getString(res.getColumnIndex(TIME));
            }

            allCourses.add(temp);
            res.moveToNext();
        }
        return allCourses;
    }

    public long insertLogging(String time, Integer errors){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("time", time);
        contentValues.put("errors", errors);
        long test=db.insert("userlogging",null,contentValues);
        return test;
    }

    public ArrayList<String> insertInWatchlist(String title){
        SQLiteDatabase dbr = this.getReadableDatabase();
        title = title.replace("\"", "");
        Log.d("TITLE", title);
        Cursor res = dbr.rawQuery("select * from courses where title like '%"+title+"%'",null);
        Log.d("COLUMNNAME", res.getColumnName(0));
        ArrayList<String> allCourses = new ArrayList<String>();
        if(res.getCount() == 0){
            return allCourses;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        res.moveToFirst();
        ContentValues contentValues=new ContentValues();
        while(!res.isAfterLast()){
            contentValues.put(ID, res.getString(res.getColumnIndex(ID)));
            contentValues.put(TITLE,res.getString(res.getColumnIndex(TITLE)));
            contentValues.put(CODE,res.getString(res.getColumnIndex(CODE)));
            contentValues.put(INSTRUCTOR,res.getString(res.getColumnIndex(INSTRUCTOR)));
            contentValues.put(LEVEL,res.getString(res.getColumnIndex(LEVEL)));
            contentValues.put(DAY,res.getString(res.getColumnIndex(DAY)));
            contentValues.put(TIME,res.getString(res.getColumnIndex(TIME)));
            res.moveToNext();
        }

        long test=db.insert("watchlist",null,contentValues);

        SQLiteDatabase dbw = this.getReadableDatabase();
        Cursor resw = db.rawQuery("select * from watchlist",null);
        resw.moveToFirst();
        Log.d("WATCHLISTSIZE", ""+resw.getCount());
        while(!resw.isAfterLast()){
            String temp="ID:"+resw.getString(resw.getColumnIndex(ID))+"\n";
            temp += "Title:"+resw.getString(resw.getColumnIndex(TITLE))+"\n";
            temp += "Course Number:"+resw.getString(resw.getColumnIndex(CODE))+"\n";
            temp += "Instructor:"+resw.getString(resw.getColumnIndex(INSTRUCTOR))+"\n";
            temp += "Program Level:"+resw.getString(resw.getColumnIndex(LEVEL))+"\n";
            String[] timeArr = resw.getString(resw.getColumnIndex(TIME)).split(",");
            String[] dayArr = resw.getString(resw.getColumnIndex(DAY)).split(",");
            if(timeArr.length > 1){
                temp += "Day:"+dayArr[0]+"   "+timeArr[0]+"\n";
                temp += dayArr[1]+"   "+timeArr[1];
            } else{
                temp += "Day:"+resw.getString(resw.getColumnIndex(DAY))+"   "+resw.getString(resw.getColumnIndex(TIME));
            }
            allCourses.add(temp);
            resw.moveToNext();
        }
        return allCourses;
    }

    public int clearWatchList(){
        SQLiteDatabase db=getWritableDatabase();
        int delete=db.delete("watchlist",null,null);
        return delete;
    }

    public long insertSampleData(String title, String code, String instructor, String level,
                                String day, String time){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();

        contentValues.put(TITLE, title);
        contentValues.put(CODE, code);
        contentValues.put(INSTRUCTOR, instructor);
        contentValues.put(LEVEL, level);
        contentValues.put(DAY, day);
        contentValues.put(TIME, time);
        long test=db.insert("courses",null,contentValues);
        return test;
    }
}
