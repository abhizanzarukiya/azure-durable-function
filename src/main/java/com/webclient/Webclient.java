package com.webclient;

import org.springframework.web.reactive.function.client.WebClient;

public class Webclient {

    WebClient client = WebClient.create();    

    public static String getResponse(String url) {
        WebClient client = WebClient.create(url);

        String response = client.get()
                .retrieve()
                .bodyToMono(String.class)
                .block();   


        return response;
    }     
}
