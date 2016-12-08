package com.example.colin.pantrypal3000;

import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

public class PantryFreeActivity extends AppCompatActivity {

    public static final String BASE_URL = "http://localhost:8080/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry_free);
    }

    public void submit(View view){
        final String userName = getIntent().getStringExtra(MainActivity.USERNAME_KEY);
        final String building = getIntent().getStringExtra(MainActivity.BUILDING_KEY);

        final EditText foodMessage = (EditText)findViewById(R.id.foodMessage);
        final String messageToSend = foodMessage.getText().toString();

        final String toastMessage = "Username: " + userName +
                "\nBuilding: " + building +
                "\nMessage: " + messageToSend;
        Toast.makeText(view.getContext(), toastMessage, Toast.LENGTH_LONG).show();

        final String url = BASE_URL + "send/"
                + building + "/"
                + messageToSend + "/"
                + userName;

        RestTemplate restTemplate = new RestTemplate();
        // Can read and write string from the HTTP request/response
        //restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        String response = restTemplate.postForObject(url, null, String.class);
    }

    public class getMessage {
        private String userName;
        private String message;
        private Date dateTime;
    }
    public void refresh(View view){
        final String building = getIntent().getStringExtra(MainActivity.BUILDING_KEY);

        final String url = BASE_URL + "receive/" + building;

        RestTemplate restTemplate = new RestTemplate();
        // Can read and write string from the HTTP request/response
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

        Message message = new Message();

        ResponseEntity<List<getMessage>> response = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<getMessage>>() {
        });
        List<getMessage> getMessagesBro = response.getBody();

        TextView colinsFantasies = (TextView) findViewById(R.id.textView);
        colinsFantasies.setText(getMessagesBro.get(0).message);
    }

}
