package com.gs.javablockchain.services;

import com.gs.javablockchain.utils.Configuration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PreDestroy;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
@Getter
public class NodeService implements ApplicationListener {

    private final BlockService blockService;
    private final TransactionService transactionService;

    private URL myNodeURL;

    private Set neighbourNodes = new HashSet<>();

    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    public NodeService(BlockService blockService, TransactionService transactionService){
        this.blockService = blockService;
        this.transactionService = transactionService;
    }

    /**
     * At node's initialization we have to get the net nodes list, get blockchain, get transactionPool,
     * reigster this node on the net
     *
     * @param webServiceInitializedEvent webServer to get the port
     * */

    public void onApplicationEvent(WebServerInitializedEvent webServiceInitializedEvent) {
        URL masterNodeUrl = getMasterNodeUrl();

        String host = getPublicIp(masterNodeUrl, restTemplate);
        int port = webServiceInitializedEvent.getWebServer().getPort();

        myNodeURL = getMyUrlNode(host, port);

        if(myNodeURL.equals(masterNodeUrl)){
            log.info("Running master node");
        } else {
            neighbourNodes.add(masterNodeUrl);

            getNeighbourNodes(masterNodeUrl, restTemplate);
            blockService.getBlockChainFromAnotherNode(masterNodeUrl, restTemplate);
            transactionService.getTransactionPool(masterNodeUrl, restTemplate);

            registerOnNeighbourNodes("node", myNodeURL);
        }
    }

    /**
     * Remove this node from the others in the net before destroy
     * */
    @PreDestroy
    public void shutdown(){
        log.info("stopping node...");
        deleteFromNeighbourNodes("node", myNodeURL);
    }

    /**
     * Obtain master node URL from configuration
     * */
    private URL getMasterNodeUrl(){
        try{
            return new URL(Configuration.getInstance().getMasterNodeURL());
        } catch (MalformedURLException e){
            log.error("Invalid master node URL");
            return null;
        }
    }

    /**
     * Get public ip with use to connect to the net
     * @param neighbourNodeUrl node to do the request
     * @param restTemplate restTemplate
     * */
    private String getPublicIp(URL neighbourNodeUrl, RestTemplate restTemplate){
        return restTemplate.getForObject(neighbourNodeUrl+"/node/ip", String.class);
    }

    /**
     * Build this node URL from host and port
     * @param host public host
     * @param port
     * */
    private URL getMyUrlNode(String host, int port){
        try {
            return new URL("http", host, port, "");
        }catch (MalformedURLException e){
            log.error("Invalid node URL");
            return null;
        }
    }

    /**
     * get neighbour nodes
     *
     * @param neighbourNodeUrl node to do the request
     * @param restTemplate restTemplate
     * */
    public void getNeighbourNodes(URL neighbourNodeUrl, RestTemplate restTemplate){
        URL[] nodes = restTemplate.getForObject(neighbourNodeUrl + "/node", URL[].class);
        Collections.addAll(neighbourNodes, nodes);
    }

    /**
     * Send a POST request to all nodes in the net
     * @param endpoint to this request
     * @param data to be sent
     * */
    public void registerOnNeighbourNodes(String endpoint, Object data){
        neighbourNodes.parallelStream().forEach(nodeUrl -> restTemplate.postForLocation(nodeUrl + "/" + endpoint, data));
    }

    /**
     * Send a DELETE request to all nodes in the net
     * @param endpoint to this request
     * @param data to be sent
     * */
    public void deleteFromNeighbourNodes(String endpoint, Object data){
        neighbourNodes.parallelStream().forEach(nodeUrl -> restTemplate.delete(nodeUrl + "/" + endpoint, data));
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) { //empty
    }
}
