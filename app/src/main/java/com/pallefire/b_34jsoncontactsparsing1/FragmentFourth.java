package com.pallefire.b_34jsoncontactsparsing1;


import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
public class FragmentFourth extends Fragment {


    Button button;
    Cursor cursor;
    RecyclerView recyclerView;
    MyDatabase myDatabase;
   MyRecyclerViewAdapter myRecyclerViewAdapter;
    MyTask myTask;

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
                   myDatabase.insert(name,email,mobile);

                }


                myRecyclerViewAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("B_34","Json Parsing Error");
            }
            super.onPostExecute(s);
        }
    }

    //7b. create an inner class f0r custom Adapter

    public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>
    {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //load row xml
            View v=getActivity().getLayoutInflater().inflate(R.layout.row,parent,false);
            //create the view holder
            ViewHolder viewHolder=new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            cursor.moveToPosition(position);
            int sno=cursor.getInt(0);
            String name=cursor.getString(1);
            String email=cursor.getString(2);
            String mobile=cursor.getString(3);
            holder.tv1.setText(""+sno);
            holder.tv2.setText(name);
            holder.tv3.setText(email);
            holder.tv4.setText(mobile);
            //get data from the arraylist based on position

            //apply data on viewholder--using Setters


        }

        @Override
        public int getItemCount() {
         return cursor.getCount();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView tv1,tv2,tv3,tv4;
            public ViewHolder(View itemView) {
                super(itemView);
                tv1= (TextView) itemView.findViewById(R.id.textView1);
                tv2= (TextView) itemView.findViewById(R.id.textView2);
                tv3= (TextView) itemView.findViewById(R.id.textView3);
                tv4= (TextView) itemView.findViewById(R.id.textView4);
            }
        }
    }
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



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //9. initialize all variables here
        View v= inflater.inflate(R.layout.fragment_fragment_two, container, false);
        button= (Button) v.findViewById(R.id.click);
        cursor=myDatabase.queryContacts();
        recyclerView= (RecyclerView) v.findViewById(R.id.recyclerView1);
        myRecyclerViewAdapter=new MyRecyclerViewAdapter();
        recyclerView.setAdapter(myRecyclerViewAdapter);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        myTask=new MyTask();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


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
