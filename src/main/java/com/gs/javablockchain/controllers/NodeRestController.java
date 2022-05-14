package com.gs.javablockchain.controllers;

import com.gs.javablockchain.services.NodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/node")
public class NodeRestController {

    private final NodeService nodeService;

    public NodeRestController(NodeService nodeService){
        this.nodeService = nodeService;
    }

    /**
     * Get neighbour node list
     * @return URLs list
     * */
    @GetMapping
    Set getNeighbourNodes(){
       log.info("Neighbour Nodes request");
       return nodeService.getNeighbourNodes();
    }

    /**
     * Post new node
     * @param nodeUrl
     * */
    @PostMapping
    void newNode(@RequestBody String nodeUrl, HttpServletResponse response){
        log.info("new node request: {}", nodeUrl);
        try{
            nodeService.addNode(new URL(nodeUrl));
            response.setStatus(HttpServletResponse.SC_OK);
        }catch (MalformedURLException e){
            log.error("Add node error: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    /**
     * Remove a node
     * @param nodeUrl
     * */
    @DeleteMapping
    void removeNode(@RequestBody String nodeUrl, HttpServletResponse response){
        log.info("Request to remove node: {}", nodeUrl);
        try{
            nodeService.removeNode(new URL(nodeUrl));
            response.setStatus(HttpServletResponse.SC_OK);
        }catch (MalformedURLException e){
            log.error("error removing node {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Endpoint to get the public ip of a node
     * @return publicIp
     * */
    @GetMapping
    String getPublicIp(HttpServletRequest request){
        log.info("ip public request");
        return request.getRemoteAddr();
    }
}
