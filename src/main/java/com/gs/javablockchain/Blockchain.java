package com.gs.javablockchain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Slf4j
public class Blockchain {

    private List<Block> blocks;

    /**
     * Add block to the blockchain
     * @param block to be added
     * */
    public void addBlock(Block block){
        this.blocks.add(block);
    }

    /**
     * validate if the blockchain has no blocks
     * @return true if blockchain has no blocks
     * */
    public boolean isEmpty(){
        return this.blocks == null || this.blocks.isEmpty();
    }

    /**
     * Get last block in chain
     * @return last block in chain
     * */
    public Block getLastBlock(){
        if(isEmpty()){
            return null;
        }
        return this.blocks.get(blocks.size() - 1);
    }

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Blockchain blockchain = (Blockchain) o;
        if(blocks.size() != blockchain.getBlocks().size()) return false;
        for(int i = 0; i<blocks.size(); i++){
            if(blocks.get(i) != blockchain.getBlocks().get(i)) return false;
        }
        return true;
    }

    @Override
    public String toString(){
        return blocks.toString();
    }
}
