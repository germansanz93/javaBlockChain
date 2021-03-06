package com.gs.javablockchain.services;

import com.gs.javablockchain.utils.Configuration;
import com.gs.javablockchain.entities.Block;
import com.gs.javablockchain.entities.Blockchain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.util.Arrays;

@Service
@Slf4j
public class BlockService {

    private final TransactionService transactionService;

    private Blockchain blockchain = Blockchain.builder().build();

    @Autowired
    public BlockService (TransactionService transactionService){
        this.transactionService = transactionService;
    }

    public Blockchain getBlockchain(){
        return blockchain;
    }

    /**
     * Add block to the blockchain
     * @param block to be added
     * @return true if the block is added to the blockchain
     * */
    public synchronized boolean addBlock(Block block) {
        if(verifyBlock(block)){
            this.blockchain.addBlock(block);
            block.getTransactions().forEach(transactionService::removeTransaction);
            log.info("Block added to the chain");
            return true;
        } else return false;
    }

    /**
     * Get blockchain from another node
     * @param nodeURL
     * @param restTemplate RestTemplate to use
     * */
    public void getBlockChainFromAnotherNode(URL nodeURL, RestTemplate restTemplate){
        Blockchain chain = restTemplate.getForObject(nodeURL + "/chain", Blockchain.class);
        log.info("Chain obtained from {}", nodeURL);
        this.blockchain = chain;
    }

    /**
     * Validate a block
     * @param block to ve verified
     * @return true if the block is valid
     * */
    private boolean verifyBlock(Block block){
        if(!block.isValid()) return false;
        if(!blockchain.isEmpty()){
            byte[] lastBlockHash = blockchain.getLastBlock().getHash();
            if(!Arrays.equals(block.getPreviousBlockHash(), lastBlockHash)){
                log.error("Invalid previous Block Hash");
                return false;
            }
        } else {
            if(block.getPreviousBlockHash() != null) {
                log.error("Previous Hash invalid, must be null");
                return false;
            }
        }
        if(block.getTransactions().size() > Configuration.getInstance().getMaxTransactionsNumber()){
            log.error("Transactions qty is over limit");
            return false;
        }
        if(!transactionService.contains(block.getTransactions())){
            log.error("Some transactions are not in pool");
            return false;
        }
        if(block.getHashZeroesNumber() != Configuration.getInstance().getDifficult()) {
           log.error("Block difficult invalid");
            return false;
        }
        return true;
    }

}
