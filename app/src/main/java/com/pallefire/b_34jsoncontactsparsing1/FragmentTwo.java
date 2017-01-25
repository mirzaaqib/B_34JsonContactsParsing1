package com.pallefire.b_34jsoncontactsparsing1;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
public class FragmentTwo extends Fragment {


    //8. declare all required variables for your fragment

    Button button;
//    ListView listView;
    ArrayList<Contact> arrayList;
    RecyclerView recyclerView;
    MyRecyclerViewAdapter myRecyclerViewAdapter;
//    MainActivityFragment.MyAdapter myAdapter;
       MyTask myTask;

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
                    //let us push of the data into the arraylist<Contact>
                    Contact c=new Contact();
                    c.setName(name);
                    c.setEmail(email);
                    c.setMobile(mobile);
                    c.setSno(i+1);
                    //now push conatct object to arraylist

                    arrayList.add(c);
                }

                //here we notify to your recyler view  adapter
                //here we use so that optimize the cpu time so that use after for loop
               // myAdapter.notifyDataSetChanged();
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
            //get data from the arraylist based on position
            Contact c=arrayList.get(position);

            //apply data on viewholder--using Setters
            holder.tv1.setText(""+c.getSno());
            holder.tv2.setText(""+c.getName());
            holder.tv3.setText(""+c.getEmail());
            holder.tv4.setText(""+c.getMobile());

        }

        @Override
        public int getItemCount() {
            return arrayList.size();
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




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //9. initialize all variables here
        View v= inflater.inflate(R.layout.fragment_fragment_two, container, false);
        button= (Button) v.findViewById(R.id.click);
        recyclerView= (RecyclerView) v.findViewById(R.id.recyclerView1);
        myRecyclerViewAdapter=new MyRecyclerViewAdapter();
        recyclerView.setAdapter(myRecyclerViewAdapter);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        //listView= (ListView) v.findViewById(R.id.lv);
        arrayList=new ArrayList<Contact>();
       // myAdapter= new MainActivityFragment.MyAdapter();
       // listView.setAdapter(myAdapter);
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
