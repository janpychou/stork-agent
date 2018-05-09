package com.hiido.stork.agent;

import com.hiido.stork.agent.api.ApiServerHandler;
import com.hiido.stork.agent.utils.LogUtil;

public class HttpFile {

    public static void main(String[] args) throws Exception {
        printUsage();
        SystemConfig.tryLock(SystemConfig.getLockFile());
        LogUtil.info("get lock file:" + SystemConfig.getLockFile());
        SystemConfig.parseArgs(args);
        ApiServerHandler.startApiServer();
    }

    private static void printUsage() {
        LogUtil.info("----------------welcome to start stork agent-------------");
        LogUtil.info("----------------support args:");
        LogUtil.info("----------------        -bindip: agent api server will bind on the ip, default value: 127.0.0.1");
        LogUtil.info("----------------        -port: agent api server will listen on, default value: 2627");
        LogUtil.info("----------------        -line: get content line max number, default value: 100");
        LogUtil.info("----------------eg: -bindip 172.19.16.36 -port 2627 -line 50");
        System.out.println();
    }

}
