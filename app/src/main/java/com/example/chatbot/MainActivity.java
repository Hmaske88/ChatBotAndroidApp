package com.example.chatbot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private RecyclerView chatsRV;
    private RelativeLayout msgRL;
    private EditText userMsgEdt;
    private FloatingActionButton sendMsgFAB;
    private final String BOT_KEY="bot";
    private final String USER_KEY="user";
    private ArrayList<chatsModal> chatsModalArrayList;
    private chatRVadapter cRVadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chatsRV=findViewById(R.id.RVchats);
        msgRL=findViewById(R.id.RLmsg);
        userMsgEdt=findViewById(R.id.EdtMsg);
        sendMsgFAB=findViewById(R.id.FABsend);
        chatsModalArrayList=new ArrayList<>();
        cRVadapter=new chatRVadapter(chatsModalArrayList,this);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        chatsRV.setLayoutManager(manager);
        chatsRV.setAdapter(cRVadapter);

        sendMsgFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userMsgEdt.getText().toString().isEmpty())
                {
                    Toast.makeText(MainActivity.this, "Please Enter Your Message", Toast.LENGTH_SHORT).show();
                    return;
                }
                getResponse(userMsgEdt.getText().toString());
                userMsgEdt.setText("");
            }
        });
    }

    private void getResponse(String message)
    {
        chatsModalArrayList.add(new chatsModal(message,USER_KEY));
        cRVadapter.notifyDataSetChanged();
        String url="http://api.brainshop.ai/get?bid=171190&key=XvVQpuqVwO6hoBqu&uid=[uid]&msg="+message;
        String BASE_URL="http://api.brainshop.ai/";
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitApi rfApi=retrofit.create(retrofitApi.class);
        Call<msgModal> call=rfApi.getMessage(url);
        call.enqueue(new Callback<msgModal>() {
            @Override
            public void onResponse(Call<msgModal> call, Response<msgModal> response) {
                if(response.isSuccessful())
                {
                    msgModal modal=response.body();
                    chatsModalArrayList.add(new chatsModal(modal.getCnt(),BOT_KEY));
                    cRVadapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<msgModal> call, Throwable t) {
                chatsModalArrayList.add(new chatsModal("Please revert your question",BOT_KEY));
                cRVadapter.notifyDataSetChanged();
            }
        });
    }
}