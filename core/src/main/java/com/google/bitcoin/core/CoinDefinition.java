package com.google.bitcoin.core;

import java.math.BigInteger;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: HashEngineering
 * Date: 8/13/13
 * Time: 7:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class CoinDefinition {


 
    public static final String coinName = "Smileycoin";
    public static final String coinTicker = "SMLY";
    public static final String coinURIScheme = "smileycoin";
    public static final String cryptsyMarketId = "26";
    public static final String cryptsyMarketCurrency = "BTC";
    public static final String PATTERN_PRIVATE_KEY_START = "6";
    public static final String PATTERN_PRIVATE_KEY_START_COMPRESSED = "[Q]";
    public static final String PATTERN_PRIVATE_KEY_START_TESTNET = "9";
    public static final String PATTERN_PRIVATE_KEY_START_COMPRESSED_TESTNET = "c";

    public static String lowerCaseCoinName() { return coinName.toLowerCase(); }

    public enum CoinPrecision {
        Coins,
        Millicoins,
    }
    public static final CoinPrecision coinPrecision = CoinPrecision.Coins;


    public static final String BLOCKEXPLORER_BASE_URL_PROD = "https://coinplorer.com/SMLY/";    //blockr.io
    public static final String BLOCKEXPLORER_ADDRESS_PATH = "Addresses/";             //blockr.io path
    public static final String BLOCKEXPLORER_TRANSACTION_PATH = "Transactions/";              //blockr.io path
    public static final String BLOCKEXPLORER_BLOCK_PATH = "Blocks/";                 //blockr.io path
    public static final String BLOCKEXPLORER_BASE_URL_TEST = BLOCKEXPLORER_BASE_URL_PROD;

    public static final String DONATION_ADDRESS = "BBYfi7P7dVaeJS4w6XrQDVuFcpYSnguNnF";  //Smileycoin donation address

    public static final String UNSPENT_API_URL = "http://dgc.blockr.io/api/v1/address/unspent/";
    public enum UnspentAPIType {
        BitEasy,
        Blockr,
        Abe
    };
    public static final UnspentAPIType UnspentAPI = UnspentAPIType.Blockr;

    enum CoinHash {
        SHA256,
        scrypt,
    };
    public static final CoinHash coinPOWHash = CoinHash.scrypt;

    public static boolean checkpointFileSupport = true;
    public static int checkpointDaysBack = 21;
    //Original Values
    public static final int TARGET_TIMESPAN_0 = (int)(6 * 60 * 3 * 20);  // 3.5 days per difficulty cycle, on average.
    public static final int TARGET_SPACING_0 = (int)(1 * 20);  // 20 seconds per block.
    public static final int INTERVAL_0 = TARGET_TIMESPAN_0 / TARGET_SPACING_0;  //1080 blocks

    public static final int TARGET_TIMESPAN_1 = (int)(108 * 20);  // 36 minutes per difficulty cycle, on average.
    public static final int TARGET_SPACING_1 = (int)(1 * 20);  // 20 seconds per block.
    public static final int INTERVAL_1 = TARGET_TIMESPAN_1 / TARGET_SPACING_1;  //108 blocks

    public static final int TARGET_TIMESPAN = (int)(5 * 24 * 60 * 60);  // 72 minutes per difficulty cycle, on average.
    public static final int TARGET_SPACING = (int)(3 * 60);  // 40 seconds per block.
    public static final int INTERVAL = TARGET_TIMESPAN / TARGET_SPACING;  //108 blocks

    private static int nDifficultySwitchHeight = 476280;    //retarget every 108 instead of 1080 blocks; adjust by +100%/-50% instead of +400/-75%
    private static int nInflationFixHeight = 523800;        //increase block time to 40 from 20 seconds; decrease reward from 20 to 15 DGC
    private static int nDifficultySwitchHeightTwo = 625800; //retarget adjust changed
    public static final int V3_FORK = 1028000;
    public static final int MAX_BLOCK_ALGO_COUNT = 3;



    public static final int getInterval(int height, boolean testNet) {
            return INTERVAL;      //108
    }
    public static final int getIntervalCheckpoints() {
            return INTERVAL_0;    //1080

    }
    public static final int getTargetTimespan(int height, boolean testNet) {
            return TARGET_TIMESPAN;    //72 min
    }
    public static int getMaxTimeSpan(int value, int height, boolean testNet)
    {
        if(height < nDifficultySwitchHeight)
            return value * 4;
        else if(height < nInflationFixHeight)
            return value * 2;
        else
            return value * 75 / 60;
    }
    public static int getMinTimeSpan(int value, int height, boolean testNet)
    {
        if(height < nDifficultySwitchHeight)
            return value / 4;
        else if(height < nInflationFixHeight)
            return value / 2;
        else
            return value * 55 / 73;
    }
    public static int spendableCoinbaseDepth = 5; //main.h: static const int COINBASE_MATURITY
    public static final BigInteger MAX_MONEY = BigInteger.valueOf(50000000).multiply(Utils.COIN);                 //main.h:  MAX_MONEY
    //public static final String MAX_MONEY_STRING = "200000000";     //main.h:  MAX_MONEY

    public static final BigInteger DEFAULT_MIN_TX_FEE = BigInteger.valueOf(100000);   // MIN_TX_FEE
    public static final BigInteger DUST_LIMIT = BigInteger.valueOf(1000000); //main.h CTransaction::GetMinFee        0.01 coins      0.01 coins

    public static final int PROTOCOL_VERSION = 70002;          //version.h PROTOCOL_VERSION
    public static final int MIN_PROTOCOL_VERSION = 70002;        //version.h MIN_PROTO_VERSION - eliminate 60001 which are on the wrong fork
    public static final int INIT_PROTO_VERSION = 209;            //version.h

    public static final int BLOCK_CURRENTVERSION = 1;   //CBlock::CURRENT_VERSION
    public static final int MAX_BLOCK_SIZE = 1 * 1000 * 1000;


    public static final boolean supportsBloomFiltering = false; //Requires PROTOCOL_VERSION 70000 in the client
    public static boolean supportsIrcDiscovery() {
        return PROTOCOL_VERSION <= 70000;
    }

    public static final int Port    = 11337;       //protocol.h GetDefaultPort(testnet=false)
    public static final int TestPort = 21337;     //protocol.h GetDefaultPort(testnet=true)

    //
    //  Production
    //
    public static final int AddressHeader = 25;             //base58.h CBitcoinAddress::PUBKEY_ADDRESS
    public static final int p2shHeader = 5;             //base58.h CBitcoinAddress::SCRIPT_ADDRESS
    public static final boolean allowBitcoinPrivateKey = true; //for backward compatibility with previous version of digitalcoin
    public static final long PacketMagic = 0xfbc0b6db;      //0xfb, 0xc0, 0xb6, 0xdb

    //Genesis Block Information from main.cpp: LoadBlockIndex
    static public long genesisBlockDifficultyTarget = (0x1e0ffff0L);         //main.cpp: LoadBlockIndex
    static public long genesisBlockTime = 1408974288;                       //main.cpp: LoadBlockIndex
    static public long genesisBlockNonce = (386703170);                         //main.cpp: LoadBlockIndex
    static public String genesisHash = "660f734cf6c6d16111bde201bbd2122873f2f2c078b969779b9d4c99732354fd"; //main.cpp: hashGenesisBlock
    static public int genesisBlockValue = 10000;                                                              //main.cpp: LoadBlockIndex
    //taken from the raw data of the block explorer
    static public String genesisTxInBytes = "04ffff001d0104494e592054696d65732031382f4175672f3230313420426974636f696e27732050726963652046616c6c73203132252c20746f204c6f776573742056616c75652053696e6365204d6179";   //"Digitalcoin, A Currency for a Digital Age"
    static public String genesisTxOutBytes = "040184710fa689ad5023690c80f3a49c8f13f8d45b8c857fbcbc8bc4a8e4d3eb4b10f4d4604fa08dce601aaf0f470216fe1b51850b4acf21b179c45070ac7b03a9";

    //net.cpp strDNSSeed
    static public String[] dnsSeeds = new String[] {
            "dnsseed.smileyco.in",
     };

    public static int minBroadcastConnections = 1;   //0 for default; we need more peers.

    //
    // TestNet - digitalcoin - not tested
    //
    public static final boolean supportsTestNet = false;
    public static final int testnetAddressHeader = 111;             //base58.h CBitcoinAddress::PUBKEY_ADDRESS_TEST
    public static final int testnetp2shHeader = 196;             //base58.h CBitcoinAddress::SCRIPT_ADDRESS_TEST
    public static final long testnetPacketMagic = 0xfcc1b7dc;      //0xfc, 0xc1, 0xb7, 0xdc
    public static final String testnetGenesisHash = "660f734cf6c6d16111bde201bbd2122873f2f2c078b969779b9d4c99732354fd";
    static public long testnetGenesisBlockDifficultyTarget = (0x1e0ffff0L);         //main.cpp: LoadBlockIndex
    static public long testnetGenesisBlockTime = 1408974288;                       //main.cpp: LoadBlockIndex
    static public long testnetGenesisBlockNonce = (386703170);                         //main.cpp: LoadBlockIndex





    public static final boolean usingNewDifficultyProtocol(int height)
    { return height >= nDifficultySwitchHeight;}

    public static final boolean usingInflationFixProtocol(int height)
    { return height >= nInflationFixHeight;}

    //main.cpp GetBlockValue(height, fee)
    public static final BigInteger GetBlockReward(int height)
    {
        int COIN = 1;
        BigInteger nSubsidy = Utils.toNanoCoins(1000, 0);

        if(height < 1000)
        {
            nSubsidy = Utils.toNanoCoins(24000000, 0); //2
        }
        else
        {
            return nSubsidy.shiftRight(height / subsidyDecreaseBlockCount);
        }
        return nSubsidy;
    }

    public static int subsidyDecreaseBlockCount = 1226400;     //main.cpp GetBlockValue(height, fee)

    public static BigInteger proofOfWorkLimit = Utils.decodeCompactBits(0x1e0fffffL);  //main.cpp bnProofOfWorkLimit (~uint256(0) >> 20); // digitalcoin: starting difficulty is 1 / 2^12

    public static BigInteger [] proofOfWorkLimits = new BigInteger[] {
            proofOfWorkLimit,proofOfWorkLimit,proofOfWorkLimit,proofOfWorkLimit,proofOfWorkLimit };

    public static BigInteger getProofOfWorkLimit(int algo)
    {
        return proofOfWorkLimits[algo];
    }

    static public String[] testnetDnsSeeds = new String[] {
          "not supported"
    };
    //from main.h: CAlert::CheckSignature
    public static final String SATOSHI_KEY = "040184710fa689ad5023690c80f3a49c8f13f8d45b8c857fbcbc8bc4a8e4d3eb4b10f4d4604fa08dce601aaf0f470216fe1b51850b4acf21b179c45070ac7b03a9";
    public static final String TESTNET_SATOSHI_KEY = "04302390343f91cc401d56d68b123028bf52e5fca1939df127f63c6467cdf9c8e2c14b61104cf817d0b780da337893ecc4aaff1309e536162dabbdb45200ca2b0a";

    /** The string returned by getId() for the main, production network where people trade things. */
    public static final String ID_MAINNET = "org.smileycoin.production";
    /** The string returned by getId() for the testnet. */
    public static final String ID_TESTNET = "org.smileycoin.test";
    /** Unit test network. */
    public static final String ID_UNITTESTNET = "com.google.smileycoin.unittest";

    //checkpoints.cpp Checkpoints::mapCheckpoints
    public static void initCheckpoints(Map<Integer, Sha256Hash> checkpoints)
    {
         checkpoints.put( 0, new Sha256Hash("660f734cf6c6d16111bde201bbd2122873f2f2c078b969779b9d4c99732354fd"));
        checkpoints.put( 2000, new Sha256Hash("6c75adede20c70a18ad60bd5f2184f9154edcf94db01514ff9d2d57699d7cbc6"));
        checkpoints.put( 4000, new Sha256Hash("2bf98d7022a4af00395b3e6d59c979939c5bb368505fe2c47e4552e7aa586424"));
        checkpoints.put( 6000, new Sha256Hash("e7310e1f05efae70f0265a156fa9d39151349d27f969b6f94f31cfb8a16af95a"));
     }

    //Unit Test Information
    public static final String UNITTEST_ADDRESS = "DPHYTSm3f96dHRY3VG1vZAFC1QrEPkEQnt";
    public static final String UNITTEST_ADDRESS_PRIVATE_KEY = "QU1rjHbrdJonVUgjT7Mncw7PEyPv3fMPvaGXp9EHDs1uzdJ98hUZ";

}

