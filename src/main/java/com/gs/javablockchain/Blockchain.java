package com.gs.javablockchain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Blockchain {

    private List<Block> blocks;

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
