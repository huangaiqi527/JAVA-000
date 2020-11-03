package com.huang.gateway.mappool;

import com.huang.gateway.filter.HttpRequestFilter;
import com.huang.gateway.filter.filterimpl.AddHeaderFilter;

import java.util.*;

public class ServicesMappingPool {
    private Map<String, List<String>> endpointsPool = new HashMap<String, List<String>>();
    private Map<String, List<HttpRequestFilter>> filtersPool = new HashMap<String, List<HttpRequestFilter>>();

    /**
     * 初始化负载映射，服务/端口 负载映射的后端 节点池
     * 备注：后续可以考虑从配置文件中读取
     */
    private void initMappingPool(){
        endpointsPool.put("/hello",
                Arrays.asList("http://127.0.0.1:8801","http://127.0.0.1:8802","http://127.0.0.1:8803"));
        filtersPool.put("/hello",
                Arrays.asList(new AddHeaderFilter()));
    }
    public ServicesMappingPool(){
        initMappingPool();
    }

    /**
     * 根据请求URI获取负载的后端节点池
     * @param uri
     * @return
     */
    public List<HttpRequestFilter> getFilters(String uri){
        Iterator<Map.Entry<String, List<HttpRequestFilter>>> it = filtersPool.entrySet().iterator();
        Map.Entry<String, List<HttpRequestFilter>>  entry = null;
        List<HttpRequestFilter> filters = null;
        while(it.hasNext()){
            entry = it.next();
            if(uri.startsWith(entry.getKey())){
                filters = entry.getValue();
                break;
            }
        }
        return filters;
    }


    /**
     * 根据请求URI获取服务的过滤器集合
     * @param uri
     * @return
     */
    public List<String> getEndpoints(String uri){
        Iterator<Map.Entry<String, List<String>>> it = endpointsPool.entrySet().iterator();
        Map.Entry<String, List<String>>  entry = null;
        List<String> endpoints = null;
        while(it.hasNext()){
            entry = it.next();
            if(uri.startsWith(entry.getKey())){
                endpoints = entry.getValue();
                break;
            }
        }
        return endpoints;
    }
}
