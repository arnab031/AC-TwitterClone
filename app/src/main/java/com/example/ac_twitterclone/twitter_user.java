package com.example.ac_twitterclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;

public class twitter_user extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView listView;
    private ArrayList<String> arrayList;
    private ArrayAdapter arrayAdapter;

    private String followedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_user);

        FancyToast.makeText(this,"Welcome "+ ParseUser.getCurrentUser().getUsername(),Toast.LENGTH_SHORT,FancyToast.INFO,true).show();


        listView = findViewById(R.id.listView);
        arrayList = new ArrayList();
        arrayAdapter=new ArrayAdapter(this, android.R.layout.simple_list_item_checked,arrayList);


        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(this);
        //listView.setOnItemLongClickListener(this);

        try {
            final TextView txtLoadingUsers = findViewById(R.id.txtLoadingUsers);

            ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();

            parseQuery.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());

            parseQuery.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> users, ParseException e) {
                    if (e == null && users.size() > 0) {
                            for (ParseUser user : users) {
                                arrayList.add(user.getUsername());
                            }

                            listView.setAdapter(arrayAdapter);
                            txtLoadingUsers.animate().alpha(0).setDuration(1000);
                            listView.setVisibility(View.VISIBLE);

                            for(String twitterUser:arrayList){
                                if(ParseUser.getCurrentUser().getList("fanOf")!=null) {
                                    if (ParseUser.getCurrentUser().getList("fanOf").contains(twitterUser)) {

                                        followedUser = followedUser + twitterUser +"\n";
                                        listView.setItemChecked(arrayList.indexOf(twitterUser), true);
                                        FancyToast.makeText(twitter_user.this, ParseUser.getCurrentUser().getUsername()+ " is following "+followedUser, Toast.LENGTH_SHORT,FancyToast.INFO,true).show();
                                    }
                                }
                            }
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.logoutUserItem:
                ParseUser.getCurrentUser().logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        Intent intent =new Intent(twitter_user.this,activity_login.class);
                        startActivity(intent);
                        finish();
                    }
                });
                break;
            case R.id.sendTweetItem:
                Intent intent=new Intent(this,SendTweetActivity.class);
                startActivity(intent);

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CheckedTextView checkedTextView = (CheckedTextView) view;

        if(checkedTextView.isChecked()){

            FancyToast.makeText(twitter_user.this, arrayList.get(position)+ " is followed", Toast.LENGTH_SHORT,FancyToast.INFO,true).show();
            ParseUser.getCurrentUser().add("fanOf",arrayList.get(position));
        }else {
            FancyToast.makeText(twitter_user.this, arrayList.get(position)+ " is not followed", Toast.LENGTH_SHORT,FancyToast.INFO,true).show();

            ParseUser.getCurrentUser().getList("fanOf").remove(arrayList.get(position));
            List currentUserFanOfList = ParseUser.getCurrentUser().getList("fanOf");
            ParseUser.getCurrentUser().remove("fanOf");
            ParseUser.getCurrentUser().put("fanOf", currentUserFanOfList);
        }

        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null){
                    FancyToast.makeText(twitter_user.this, "Saved", Toast.LENGTH_SHORT,FancyToast.SUCCESS,true).show();
                }
            }
        });
    }
}
