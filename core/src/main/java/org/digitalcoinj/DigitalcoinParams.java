package org.digitalcoinj;

import com.google.bitcoin.core.*;
import com.google.bitcoin.store.BlockStore;
import com.google.bitcoin.store.BlockStoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by HashEngineering on 1/11/15.
 */
public class DigitalcoinParams extends NetworkParameters {
    private static final Logger log = LoggerFactory.getLogger(AbstractBlockChain.class);

    public DigitalcoinParams() {
        super();
        interval = INTERVAL;
        targetTimespan = TARGET_TIMESPAN;
        proofOfWorkLimit = CoinDefinition.proofOfWorkLimit;
        dumpedPrivateKeyHeader = 128 + CoinDefinition.AddressHeader;
        addressHeader = CoinDefinition.AddressHeader;
        p2shHeader = CoinDefinition.p2shHeader;
        acceptableAddressCodes = new int[] { addressHeader, p2shHeader};

        port = CoinDefinition.Port;
        packetMagic = CoinDefinition.PacketMagic;
        genesisBlock.setDifficultyTarget(CoinDefinition.genesisBlockDifficultyTarget);
        genesisBlock.setTime(CoinDefinition.genesisBlockTime);
        genesisBlock.setNonce(CoinDefinition.genesisBlockNonce);
        id = ID_MAINNET;
        subsidyDecreaseBlockCount = CoinDefinition.subsidyDecreaseBlockCount;
        spendableCoinbaseDepth = CoinDefinition.spendableCoinbaseDepth;
        String genesisHash = genesisBlock.getHashAsString();
        checkState(genesisHash.equals(CoinDefinition.genesisHash),
                genesisHash);

        CoinDefinition.initCheckpoints(checkpoints);

        dnsSeeds = CoinDefinition.dnsSeeds;

    }
    private static DigitalcoinParams instance;
    public static synchronized DigitalcoinParams get() {
        if (instance == null) {
            instance = new DigitalcoinParams();
        }
        return instance;
    }

    public String getPaymentProtocolId() {
        return PAYMENT_PROTOCOL_ID_MAINNET;
    }

    @Override
    public boolean checkDifficultyTransitions(StoredBlock storedPrev, Block nextBlock, BlockStore blockStore) throws BlockStoreException, VerificationException {
        if (this.getId().equals(NetworkParameters.ID_TESTNET))
        {
            Block prev = storedPrev.getHeader();
            checkTestnetDifficulty(storedPrev, prev, nextBlock);
            return true;
        }
        else if(storedPrev.getHeight()+1 < CoinDefinition.V3_FORK)
        {
            checkDifficultyTransitionsV1(storedPrev,nextBlock, blockStore);
            return true;
        }
        else
        {
            checkDifficultyTransitionsV2(storedPrev, nextBlock, blockStore);
            return true;
        }


    }
    private void checkDifficultyTransitionsV1(StoredBlock storedPrev, Block nextBlock, BlockStore blockStore) throws BlockStoreException, VerificationException {

        Block prev = storedPrev.getHeader();

        int nDifficultySwitchHeight = 476280;
        int nInflationFixHeight = 523800;
        int nDifficultySwitchHeightTwo = 625800;

        boolean fNewDifficultyProtocol = ((storedPrev.getHeight() + 1) >= nDifficultySwitchHeight);
        boolean fInflationFixProtocol = ((storedPrev.getHeight() + 1) >= nInflationFixHeight);
        boolean fDifficultySwitchHeightTwo = ((storedPrev.getHeight() + 1) >= nDifficultySwitchHeightTwo /*|| fTestNet*/);

        int nTargetTimespanCurrent = fInflationFixProtocol? this.targetTimespan : (this.targetTimespan*5);
        int interval = fInflationFixProtocol? (nTargetTimespanCurrent / this.TARGET_SPACING) : (nTargetTimespanCurrent / (this.TARGET_SPACING / 2));

        // Is this supposed to be a difficulty transition point?
        if ((storedPrev.getHeight() + 1) % interval != 0 &&
                (storedPrev.getHeight() + 1) != nDifficultySwitchHeight)
        {

            // TODO: Refactor this hack after 0.5 is released and we stop supporting deserialization compatibility.
            // This should be a method of the NetworkParameters, which should in turn be using singletons and a subclass
            // for each network type. Then each network can define its own difficulty transition rules.
            /*if (this.getId().equals(NetworkParameters.ID_TESTNET) && nextBlock.getTime().after(testnetDiffDate)) {
                checkTestnetDifficulty(storedPrev, prev, nextBlock);
                return;
            }*/

            // No ... so check the difficulty didn't actually change.
            if (nextBlock.getDifficultyTarget() != prev.getDifficultyTarget())
                throw new VerificationException("Unexpected change in difficulty at height " + storedPrev.getHeight() +
                        ": " + Long.toHexString(nextBlock.getDifficultyTarget()) + " vs " +
                        Long.toHexString(prev.getDifficultyTarget()) + ", Interval: "+interval);
            return;
        }

        // We need to find a block far back in the chain. It's OK that this is expensive because it only occurs every
        // two weeks after the initial block chain download.
        long now = System.currentTimeMillis();
        StoredBlock cursor = blockStore.get(prev.getHash());

        int goBack = interval - 1;
        if (cursor.getHeight()+1 != interval)
            goBack = interval;

        for (int i = 0; i < goBack; i++) {
            if (cursor == null) {
                // This should never happen. If it does, it means we are following an incorrect or busted chain.
                throw new VerificationException(
                        "Difficulty transition point but we did not find a way back to the genesis block.");
            }
            cursor = blockStore.get(cursor.getHeader().getPrevBlockHash());
        }
        long elapsed = System.currentTimeMillis() - now;
        if (elapsed > 50)
            log.info("Difficulty transition traversal took {}msec", elapsed);

        // Check if our cursor is null.  If it is, we've used checkpoints to restore.
        if(cursor == null) return;

        Block blockIntervalAgo = cursor.getHeader();
        int timespan = (int) (prev.getTimeSeconds() - blockIntervalAgo.getTimeSeconds());
        // Limit the adjustment step.

        int nActualTimespanMax = fNewDifficultyProtocol? (nTargetTimespanCurrent*2) : (nTargetTimespanCurrent*4);
        int nActualTimespanMin = fNewDifficultyProtocol? (nTargetTimespanCurrent/2) : (nTargetTimespanCurrent/4);

        //new for v1.0.0
        if (fDifficultySwitchHeightTwo){
            nActualTimespanMax = ((nTargetTimespanCurrent*75)/60);
            nActualTimespanMin = ((nTargetTimespanCurrent*55)/73);
        }

        if (timespan < nActualTimespanMin)
            timespan = nActualTimespanMin;
        if (timespan > nActualTimespanMax)
            timespan = nActualTimespanMax;

        BigInteger newDifficulty = Utils.decodeCompactBits(prev.getDifficultyTarget());
        newDifficulty = newDifficulty.multiply(BigInteger.valueOf(timespan));
        newDifficulty = newDifficulty.divide(BigInteger.valueOf(nTargetTimespanCurrent));

        if (newDifficulty.compareTo(this.proofOfWorkLimit) > 0) {
            log.info("Difficulty hit proof of work limit: {}", newDifficulty.toString(16));
            newDifficulty = this.proofOfWorkLimit;
        }

        int accuracyBytes = (int) (nextBlock.getDifficultyTarget() >>> 24) - 3;
        BigInteger receivedDifficulty = nextBlock.getDifficultyTargetAsInteger();

        // The calculated difficulty is to a higher precision than received, so reduce here.
        BigInteger mask = BigInteger.valueOf(0xFFFFFFL).shiftLeft(accuracyBytes * 8);
        newDifficulty = newDifficulty.and(mask);

        if (newDifficulty.compareTo(receivedDifficulty) != 0)
            throw new VerificationException("Network provided difficulty bits do not match what was calculated: " +
                    receivedDifficulty.toString(16) + " vs " + newDifficulty.toString(16));
    }
    private StoredBlock GetLastBlockForAlgo(StoredBlock block, int algo, BlockStore blockStore)
    {

        for(;;)
        {
            if(block.getHeight() < CoinDefinition.V3_FORK && (algo == Block.ALGO_SHA256D||algo == Block.ALGO_X11))
            {
                return null;
            }
            if(block == null || block.getHeader().getPrevBlockHash().equals(Sha256Hash.ZERO_HASH))
                return null;
            if(block.getHeader().getAlgo() == algo)
                return block;
            try {
                block = block.getPrev(blockStore);
            }
            catch(BlockStoreException x)
            {
                return null;
            }
        }

    }
    //MultiAlgo Target updates
    final long multiAlgoTargetTimespan = 120; // 2 minutes (NUM_ALGOS(3) * 40 seconds)
    final long multiAlgoTargetSpacing = 120; // 2 minutes (NUM_ALGOS * 30 seconds)
    final long multiAlgoInterval = 1; // retargets every blocks

    final long nAveragingInterval = 10; // 10 blocks
    final long nAveragingTargetTimespan = nAveragingInterval * multiAlgoTargetSpacing; // 20 minutes

    final long nMaxAdjustDown = 40; // 40% adjustment down
    final long nMaxAdjustUp = 20; // 20% adjustment up

    final long nTargetTimespanAdjDown = multiAlgoTargetTimespan * (100 + nMaxAdjustDown) / 100;
    final long nLocalDifficultyAdjustment = 40; // 40% down, 20% up

    final long nMinActualTimespan = nAveragingTargetTimespan * (100 - nMaxAdjustUp) / 100;
    final long nMaxActualTimespan = nAveragingTargetTimespan * (100 + nMaxAdjustDown) / 100;

    private void checkDifficultyTransitionsV2(StoredBlock storedPrev, Block nextBlock, BlockStore blockStore) throws BlockStoreException, VerificationException {

        //Block prev = storedPrev.getHeader();
        int algo = nextBlock.getAlgo();
        BigInteger proofOfWorkLimit = CoinDefinition.getProofOfWorkLimit(algo);


        StoredBlock first = storedPrev;
        for (int i = 0; first != null && i < Block.NUM_ALGOS * nAveragingInterval; i++)
        {
            first = first.getPrev(blockStore);
        }
        if(first == null)
            return; //using checkpoints file
        StoredBlock lastBlockSolved = GetLastBlockForAlgo(storedPrev, algo, blockStore);
        if(lastBlockSolved == null)
        {
            //verifyDifficulty(storedPrev.getHeader().getDifficultyTargetAsInteger(), storedPrev, nextBlock, algo);
            return;
        }


        // Limit adjustment step
        // Use medians to prevent time-warp attacks
        try {
            long nActualTimespan = blockStore.getMedianTimePast(storedPrev) - blockStore.getMedianTimePast(first);
            nActualTimespan = nAveragingTargetTimespan + (nActualTimespan - nAveragingTargetTimespan)/6;
            //LogPrintf("  nActualTimespan = %d before bounds\n", nActualTimespan);
            if (nActualTimespan < nMinActualTimespan)
                nActualTimespan = nMinActualTimespan;
            if (nActualTimespan > nMaxActualTimespan)
                nActualTimespan = nMaxActualTimespan;


            // Global retarget
            BigInteger newDifficulty;
            newDifficulty = lastBlockSolved.getHeader().getDifficultyTargetAsInteger();
            newDifficulty = newDifficulty.multiply(BigInteger.valueOf(nActualTimespan));
            newDifficulty = newDifficulty.divide(BigInteger.valueOf(nAveragingTargetTimespan));

            // Per-algo retarget
            int nAdjustments = lastBlockSolved.getHeight() - storedPrev.getHeight() + Block.NUM_ALGOS - 1;
            if (nAdjustments > 0)
            {
                for (int i = 0; i < nAdjustments; i++)
                {
                    //bnNew /= 100 + nLocalDifficultyAdjustment;
                    newDifficulty = newDifficulty.divide(BigInteger.valueOf(100 + nLocalDifficultyAdjustment));
                    //bnNew *= 100;
                    newDifficulty = newDifficulty.multiply(BigInteger.valueOf(100));
                }
            }
            if (nAdjustments < 0)
            {
                for (int i = 0; i < -nAdjustments; i++)
                {
                    //bnNew *= 100 + nLocalDifficultyAdjustment;
                    newDifficulty = newDifficulty.multiply(BigInteger.valueOf(100 + nLocalDifficultyAdjustment));
                    //bnNew /= 100;
                    newDifficulty = newDifficulty.divide(BigInteger.valueOf(100));
                }
            }

            verifyDifficulty(newDifficulty, storedPrev, nextBlock, algo);
        }
        catch (BlockStoreException x)
        {
            return; //checkpoints file being used.
        }
    }
    private void verifyDifficulty(BigInteger calcDiff, StoredBlock storedPrev, Block nextBlock, int algo)
    {
        if (calcDiff.compareTo(CoinDefinition.getProofOfWorkLimit(algo)) > 0) {
            log.info("Difficulty hit proof of work limit: {}", calcDiff.toString(16));
            calcDiff = CoinDefinition.getProofOfWorkLimit(algo);
        }
        int accuracyBytes = (int) (nextBlock.getDifficultyTarget() >>> 24) - 3;
        BigInteger receivedDifficulty = nextBlock.getDifficultyTargetAsInteger();

        // The calculated difficulty is to a higher precision than received, so reduce here.
        BigInteger mask = BigInteger.valueOf(0xFFFFFFL).shiftLeft(accuracyBytes * 8);
        calcDiff = calcDiff.and(mask);

        if (calcDiff.compareTo(receivedDifficulty) != 0)
            throw new VerificationException("Network provided difficulty bits do not match what was calculated: " +
                    receivedDifficulty.toString(16) + " vs " + calcDiff.toString(16));
    }
    @Override
    public boolean checkTestnetDifficulty(StoredBlock storedPrev, Block prev, Block next) throws VerificationException, BlockStoreException {
         verifyDifficulty(Utils.decodeCompactBits(0x1d13ffec), storedPrev, next, next.getAlgo());
        return true;
    }
}
