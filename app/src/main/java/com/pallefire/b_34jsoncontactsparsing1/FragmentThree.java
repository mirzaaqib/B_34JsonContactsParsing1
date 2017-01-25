package com.pallefire.b_34jsoncontactsparsing1;


import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentThree extends Fragment {


    Button button;
    ListView listView;

    MainActivityFragment.MyAdapter myAdapter;
     MyTask myTask;
    MyDatabase myDatabase;
    Cursor cursor;
    SimpleCursorAdapter simpleCursorAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDatabase=new MyDatabase(getActivity());
        myDatabase.open();
    }

    @Override
    public void onDestroy() {
        myDatabase.close();
        super.onDestroy();
    }
    //7a. create an inner class f0r asynctask

    public boolean checkInternet(){

        ConnectivityManager manager= (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        //b. from network manger & get active network information

        //NetworkInfo networkInfo=manager.getActiveNetworkInfo();
        NetworkInfo networkInfo=manager.getActiveNetworkInfo();

        //c.check if network connected or not
        if(networkInfo==null  ||networkInfo.isConnected()==false) {

            //means there is no internet
            //webview.loadData("<h1>No Internet check internet<h1>", "text/html", null);


            return  false;
        }

        return true;


    }


    public class MyTask extends AsyncTask<String,Void,String> {
        URL myUrl;
        HttpURLConnection connection;
        InputStream inputStream;
        InputStreamReader inputStreamReader;
        BufferedReader bufferedReader;
        String line;
        StringBuilder result;

        @Override
        protected String doInBackground(String... strings) {

            //12.a write logic for connecting to server & get json data

            try {
                myUrl= new URL(strings[0]);
                connection= (HttpURLConnection) myUrl.openConnection();
                inputStream=connection.getInputStream();
                inputStreamReader=new InputStreamReader(inputStream);
                bufferedReader=new BufferedReader(inputStreamReader);
                line=bufferedReader.readLine();
                result=new StringBuilder();
                while (line!=null){
                    result.append(line);
                    line=bufferedReader.readLine();
                }
                return result.toString();//return final result json data to onpost execute
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.d("B_34","URL is Improper");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("B_34","Network Problem");
            }
            return "Something went wrong";
        }

        @Override
        protected void onPostExecute(String s) {

            //12.b reverse json parsing
            try {
                JSONObject j=new JSONObject(s);
                JSONArray k=j.getJSONArray("contacts");
                for(int i=0; i<k.length();i++){
                    JSONObject m=k.getJSONObject(i);
                    String name=m.getString("name");
                    String email=m.getString("email");
                    JSONObject phone=m.getJSONObject("phone");
                    String mobile=phone.getString("mobile");
//
                    //now push conatct data to database
                    myDatabase.insert(name,email,mobile);


                }
                cursor.requery();//for reflecting for first time
                //here we use so that optimize the cpu time so that use after for loop

            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("B_34","Json Parsing Error");
            }
            super.onPostExecute(s);
        }
    }






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //9. initialize all variables here
        View v= inflater.inflate(R.layout.fragment_main, container, false);
        button= (Button) v.findViewById(R.id.click);
        listView= (ListView) v.findViewById(R.id.lv);
        cursor=myDatabase.queryContacts();//this will read data from database
        simpleCursorAdapter=new SimpleCursorAdapter(getActivity(),R.layout.row,cursor,
                new String[]{"_id","name","email","mobile"},new int[]{R.id.textView1,R.id.textView2,R.id.textView3,R.id.textView4});

        listView.setAdapter(simpleCursorAdapter);
        myTask=new MyTask();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //11.check internet, if available start async task
                if(checkInternet()){
                    myTask.execute("http://api.androidhive.info/contacts/");

                }
                else{
                    Toast.makeText(getActivity(), "No Internet", Toast.LENGTH_SHORT).show();

                }}
        });

        return v;
    }
}
