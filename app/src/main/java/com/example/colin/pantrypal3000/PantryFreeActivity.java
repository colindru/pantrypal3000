package com.example.colin.pantrypal3000;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

public class PantryFreeActivity extends AppCompatActivity {

    public static final String BASE_URL = "http://10.0.2.2:8080/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry_free);
    }

    public void submit(View view){
        final String userName = getIntent().getStringExtra(MainActivity.USERNAME_KEY);
        final String building = getIntent().getStringExtra(MainActivity.BUILDING_KEY);

        final String buildingToSend = BuildingMapper.mapBuilding(building);

        final EditText foodMessage = (EditText)findViewById(R.id.foodMessage);
        final String messageToSend = foodMessage.getText().toString();

        final String toastMessage = "Username: " + userName +
                "\nBuilding Entered: " + building +
                "\nBuilding to Send: " + buildingToSend +
                "\nMessage: " + messageToSend;
        Toast.makeText(view.getContext(), toastMessage, Toast.LENGTH_LONG).show();

        final String url = BASE_URL + "get/"
                + buildingToSend + "/"
                + messageToSend + "/"
                + userName;
        final RestTemplate restTemplate = new RestTemplate();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    String response = restTemplate.getForObject(url, String.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    public class GetMessage {
        private String userName;
        private String message;
        private Date dateTime;
    }

    public void refresh(View view){
        final String building = getIntent().getStringExtra(MainActivity.BUILDING_KEY);
        final String buildingToSend = BuildingMapper.mapBuilding(building);

        final String url = BASE_URL + "post/" + buildingToSend;

        final RestTemplate restTemplate = new RestTemplate();
        final Context context = view.getContext();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    ResponseEntity<List<GetMessage>> response = restTemplate
                            .exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<GetMessage>>() {
                            });
                    List<GetMessage> getMessagesBro = response.getBody();
                    boolean good = !getMessagesBro.isEmpty();
                    if(good){
                        Toast.makeText(context, "Got something", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "Didn't get anything", Toast.LENGTH_LONG).show();
                    }

                    TextView colinsFantasies = (TextView)findViewById(R.id.textView);

                    String textToDisplay = getTextToDisplay(getMessagesBro);

                    colinsFantasies.setText(textToDisplay);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    private String getTextToDisplay(List<GetMessage> responseMessages){
        StringBuilder sb = new StringBuilder();

        for(GetMessage message : responseMessages){
            sb.append(message.userName + "\n");
            sb.append(message.message + "\n");
            sb.append(message.dateTime.toString() + "\n\n");
        }

        return sb.toString();
    }
}
