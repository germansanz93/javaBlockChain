package com.gs.javablockchain.controllers;

import com.gs.javablockchain.entities.Block;
import com.gs.javablockchain.entities.Blockchain;
import com.gs.javablockchain.services.BlockService;
import com.gs.javablockchain.services.NodeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("block")
public class BlockRestController {
    private final BlockService blockService;
    private final NodeService nodeService;

    public BlockRestController(BlockService blockService, NodeService nodeService) {
        this.blockService = blockService;
        this.nodeService = nodeService;
    }

    /**
     * Get block chain
     * @return JSON blocks list
     * */
    @RequestMapping(method = RequestMethod.GET)
    Blockchain getBlockChain(){
        log.info("Request block chain");
        return blockService.getBlockchain();
    }

    /**
     * Add block to the chain
     * @param block Block to be added
     * @param propagate if the block must be propagated
     * @param response 202 if the block is added and 406 in another case
     * */
    @RequestMapping(method = RequestMethod.POST)
    void addBlock(@RequestBody Block block, @RequestParam(required = false) Boolean propagate, HttpServletResponse response){
        log.info(Base64.encodeBase64String(block.getHash()));
        boolean isSuccess = blockService.addBlock(block);

        if(isSuccess){
            log.info("block added");
            response.setStatus(HttpServletResponse.SC_ACCEPTED);

            if(Objects.nonNull(propagate) && propagate){
                nodeService.registerOnNeighbourNodes("block", block);
            }
        } else {
            log.info("block will not be added");
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
        }
    }
}
