package com.hiido.stork.agent.api;


public class ApiServerHandler {

    private ApiServerHandler() {
    }

    public static void startApiServer() {
        Thread apiServerThread = new Thread(new ApiServer());
        apiServerThread.start();
    }

}
