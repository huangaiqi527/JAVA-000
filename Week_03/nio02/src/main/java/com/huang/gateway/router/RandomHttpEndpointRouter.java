package com.huang.gateway.router;

import java.util.List;
import java.util.Random;

public class RandomHttpEndpointRouter implements HttpEndpointRouter{

    /**
     * 随机转发至其中一个后台服务
     * @param endpoints
     * @return
     */
    @Override
    public String route(List<String> endpoints) {
        int  index = new Random().nextInt(endpoints.size());
        return endpoints.get(index);
    }
}
