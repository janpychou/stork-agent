package com.hiido.stork.agent.api;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import com.hiido.stork.agent.SystemConfig;
import com.hiido.stork.agent.utils.LogUtil;
import com.hiido.stork.agent.utils.RunController;
import com.hiido.stork.agent.utils.StringUtil;
import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;

public class ApiServer implements Runnable {

    //    private HttpServer httpServer;

    private URI getBaseURI() {
        String ip = SystemConfig.getString(SystemConfig.API_SERVER_LISTEN_IP, "127.0.0.1");
        int port = SystemConfig.getInt(SystemConfig.API_SERVER_LISTEN_PORT);
        String rootPath = SystemConfig.getString(SystemConfig.PROJECT_NAME);

        String urlStr = StringUtil.formatByNumber("http://{0}:{1}/{2}/", ip, port, rootPath);

        URI baseUri = UriBuilder.fromUri(urlStr).build();
        return baseUri;
    }

    @Override
    public void run() {
        LogUtil.info("Starting Api Server using grizzly...");

        ResourceConfig resourceConfig = new PackagesResourceConfig("com.hiido.stork.agent.api.resource");
        resourceConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);

        URI baseUri = getBaseURI();

        try {
            GrizzlyServerFactory.createHttpServer(baseUri, resourceConfig);
            LogUtil.info("Api Server started, url is " + baseUri);
            RunController.blockApiServer();
        } catch (Exception e) {
            LogUtil.error(e);
            LogUtil.info("Start Api Server failed, System exit");
            System.exit(0);
        }
    }

}
