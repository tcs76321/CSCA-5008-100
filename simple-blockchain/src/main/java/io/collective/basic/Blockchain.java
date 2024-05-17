package io.collective.basic;

import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

public class Blockchain {
    private final List<Block> blocks = new LinkedList<>();

    public boolean isEmpty() {
        return size() == 0;
    }

    public void add(Block block) throws NoSuchAlgorithmException {
        blocks.add(block);
    }

    public int size() {
        return blocks.size();
    }

    public boolean isValid() throws NoSuchAlgorithmException {
        for (int i = 0; i < blocks.size(); i++) {
            Block currentBlock = blocks.get(i);

            // Check mined blocks are mined
            if (!isMined(currentBlock)) {
                return false;
            }

            // Check previous hash matches
            if (i > 0) {
                Block previousBlock = blocks.get(i - 1);
                if (!currentBlock.getPreviousHash().equals(previousBlock.getHash())) {
                    return false;
                }
            }

            // Check hash is correctly calculated
            if (!currentBlock.getHash().equals(currentBlock.calculatedHash())) {
                return false;
            }
        }
        return true;
    }

    public static Block mine(Block block) throws NoSuchAlgorithmException {
        int nonce = block.getNonce();
        Block mined;
        do {
            mined = new Block(block.getPreviousHash(), block.getTimestamp(), nonce++);
        } while (!isMined(mined));
        return mined;
    }

    public static boolean isMined(Block minedBlock) {
        return minedBlock.getHash().startsWith("00");
    }
}