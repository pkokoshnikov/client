package com.pkokoshnikov;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.client.Client;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import java.util.Date;
import java.util.Scanner;

public class MusicClient {
    public static void main(String[] args) throws IOException {

        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        WebResource service = client.resource(getBaseURI());
        Scanner scanner = new Scanner(System.in);

        printConsole("Hello");
        printConsole("Please enter your identifier");
        String id = scanner.nextLine();
        String response = login(service, id);

        if (response.contains("SUCCESS")){
            printConsole("Your are entered");
            printConsole("To exit press q");

            printConsole(doNextStateRequest(service, id, "one"));
            boolean play = false;
            long time = new Date().getTime();
            while (true){
                if (play && (new Date().getTime()) - time > 1000){
                    time = new Date().getTime();
                    response = doNextStateRequest(service, id, "play");
                    printConsole(response.replaceFirst("#play#","Melody "));
                }

                if (System.in.available() != 0){
                    int code = System.in.read();
                    if(code == 113){
                        logout(service, id);
                        break;
                    }
                    response = doNextStateRequest(service, id, matchKey(code));
                    System.in.skip(10);
                    play = response.contains("#play#");
                    printConsole(response.replaceFirst("#play#","Melody "));
                }
            }
        }
        else{
            printConsole(response);
        }
    }

    private static void logout(WebResource service, String line) {
        MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
        formData.add("id",line);
        service.path("rest").path("logout").type("application/x-www-form-urlencoded").post(ClientResponse.class, formData);
    }

    private static String login(WebResource service, String line) {
        MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
        formData.add("id",line);
        return service.path("rest").path("login").type("application/x-www-form-urlencoded").post(String.class, formData);
    }

    private static String matchKey(int codeOfSymbol) {
        String key;
        switch ( codeOfSymbol){
            case 49: key = "one"; break;
            case 50: key = "two"; break;
            case 51: key = "three"; break;
            case 42: key = "star"; break;
            case 35: key = "grid"; break;
            default: key = "one";
        }
        return key;
    }

    private static URI getBaseURI() {
        return UriBuilder.fromUri("http://localhost:8080/").build();
    }
    public static  void printConsole(String str){
        System.out.println(str);
    }
    public static String doNextStateRequest(WebResource service, String id, String key){
        MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
        formData.add("id", id);
        formData.add("key", key);
        return service.path("rest").path("nextstate").type("application/x-www-form-urlencoded").post(String.class, formData);
    }

} 