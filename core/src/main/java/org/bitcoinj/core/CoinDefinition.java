package org.bitcoinj.core;

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


    public static final String coinName = "digitalcoin";
    public static final String coinTicker = "DGC";
    public static final String coinURIScheme = "digitalcoin";
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


    public static final String BLOCKEXPLORER_BASE_URL_PROD = "http://dgc.blockr.io/";    //blockr.io
    public static final String BLOCKEXPLORER_ADDRESS_PATH = "address/info/";             //blockr.io path
    public static final String BLOCKEXPLORER_TRANSACTION_PATH = "tx/info/";              //blockr.io path
    public static final String BLOCKEXPLORER_BLOCK_PATH = "block/info/";                 //blockr.io path
    public static final String BLOCKEXPLORER_BASE_URL_TEST = BLOCKEXPLORER_BASE_URL_PROD;

    public static final String DONATION_ADDRESS = "DPdbL3n3Y3ypwVEvY3wABmpbjsd3AVqm5M";  //HashEngineering donation DGC address

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

    public static final int TARGET_TIMESPAN = (int)(108 * 40);  // 72 minutes per difficulty cycle, on average.
    public static final int TARGET_SPACING = (int)(1 * 40);  // 40 seconds per block.
    public static final int INTERVAL = TARGET_TIMESPAN / TARGET_SPACING;  //108 blocks

    private static int nDifficultySwitchHeight = 476280;    //retarget every 108 instead of 1080 blocks; adjust by +100%/-50% instead of +400/-75%
    private static int nInflationFixHeight = 523800;        //increase block time to 40 from 20 seconds; decrease reward from 20 to 15 DGC
    private static int nDifficultySwitchHeightTwo = 625800; //retarget adjust changed
    public static final int V3_FORK = 1028000;
    public static final int MAX_BLOCK_ALGO_COUNT = 3;



    public static final int getInterval(int height, boolean testNet) {
        if(height < nDifficultySwitchHeight)
            return INTERVAL_0;    //1080
        else if(height < nInflationFixHeight)
            return INTERVAL_1;    //108
        else
            return INTERVAL;      //108
    }
    public static final int getIntervalCheckpoints() {
            return INTERVAL_0;    //1080

    }
    public static final int getTargetTimespan(int height, boolean testNet) {
        if(height < nDifficultySwitchHeight)
            return TARGET_TIMESPAN_0;  //3.5 days
        else if(height < nInflationFixHeight)
            return TARGET_TIMESPAN_1;  //36 min
        else
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
    public static final int MAX_MONEY = 200000000;//).multiply(Utils.COIN);                 //main.h:  MAX_MONEY
    //public static final String MAX_MONEY_STRING = "200000000";     //main.h:  MAX_MONEY

    public static final Coin DEFAULT_MIN_TX_FEE = Coin.valueOf(10000000);   // MIN_TX_FEE
    public static final Coin DUST_LIMIT = Coin.valueOf(1000000); //main.h CTransaction::GetMinFee        0.01 coins

    public static final int PROTOCOL_VERSION = 3000000;          //version.h PROTOCOL_VERSION
    public static final int MIN_PROTOCOL_VERSION = 3000000;        //version.h MIN_PROTO_VERSION - eliminate 60001 which are on the wrong fork
    public static final int INIT_PROTO_VERSION = 209;            //version.h

    public static final int BLOCK_CURRENTVERSION = 1;   //CBlock::CURRENT_VERSION
    public static final int MAX_BLOCK_SIZE = 1 * 1000 * 1000;


    public static final boolean supportsBloomFiltering = false; //Requires PROTOCOL_VERSION 70000 in the client
    public static boolean supportsIrcDiscovery() {
        return PROTOCOL_VERSION <= 70000;
    }

    public static final int Port    = 7999;       //protocol.h GetDefaultPort(testnet=false)
    public static final int TestPort = 17999;     //protocol.h GetDefaultPort(testnet=true)

    //
    //  Production
    //
    public static final int AddressHeader = 30;             //base58.h CBitcoinAddress::PUBKEY_ADDRESS
    public static final int p2shHeader = 5;             //base58.h CBitcoinAddress::SCRIPT_ADDRESS
    public static final boolean allowBitcoinPrivateKey = true; //for backward compatibility with previous version of digitalcoin
    public static final long PacketMagic = 0xfbc0b6db;      //0xfb, 0xc0, 0xb6, 0xdb

    //Genesis Block Information from main.cpp: LoadBlockIndex
    static public long genesisBlockDifficultyTarget = (0x1e0ffff0L);         //main.cpp: LoadBlockIndex
    static public long genesisBlockTime = 1367867384L;                       //main.cpp: LoadBlockIndex
    static public long genesisBlockNonce = (672176);                         //main.cpp: LoadBlockIndex
    static public String genesisHash = "5e039e1ca1dbf128973bf6cff98169e40a1b194c3b91463ab74956f413b2f9c8"; //main.cpp: hashGenesisBlock
    static public int genesisBlockValue = 50;                                                              //main.cpp: LoadBlockIndex
    //taken from the raw data of the block explorer
    static public String genesisTxInBytes = "04ffff001d0104294469676974616c636f696e2c20412043757272656e637920666f722061204469676974616c20416765";   //"Digitalcoin, A Currency for a Digital Age"
    static public String genesisTxOutBytes = "04a5814813115273a109cff99907ba4a05d951873dae7acb6c973d0c9e7c88911a3dbc9aa600deac241b91707e7b4ffb30ad91c8e56e695a1ddf318592988afe0a";

    //net.cpp strDNSSeed
    static public String[] dnsSeeds = new String[] {
            "dgc1.seed.nodes.mywl.lt",
            "dgc2.seed.nodes.mywl.lt",
            "dgc3.seed.nodes.mywl.lt",
            "dgc4.seed.nodes.mywl.lt",
            "digitalcoin.co",
            "game.digitalcoin.co",
            "dev.digitalcoin.co",
            "178.62.28.81",
             "178.237.35.34",
            "dgc.kadaplace.com",
            "54.208.77.156",
            "68.14.170.140",
     };

    public static int minBroadcastConnections = 1;   //0 for default; we need more peers.

    //
    // TestNet - digitalcoin - not tested
    //
    public static final boolean supportsTestNet = false;
    public static final int testnetAddressHeader = 111;             //base58.h CBitcoinAddress::PUBKEY_ADDRESS_TEST
    public static final int testnetp2shHeader = 196;             //base58.h CBitcoinAddress::SCRIPT_ADDRESS_TEST
    public static final long testnetPacketMagic = 0xfcc1b7dc;      //0xfc, 0xc1, 0xb7, 0xdc
    public static final String testnetGenesisHash = "5e039e1ca1dbf128973bf6cff98169e40a1b194c3b91463ab74956f413b2f9c8";
    static public long testnetGenesisBlockDifficultyTarget = (0x1e0ffff0L);         //main.cpp: LoadBlockIndex
    static public long testnetGenesisBlockTime = 999999L;                       //main.cpp: LoadBlockIndex
    static public long testnetGenesisBlockNonce = (99999);                         //main.cpp: LoadBlockIndex





    public static final boolean usingNewDifficultyProtocol(int height)
    { return height >= nDifficultySwitchHeight;}

    public static final boolean usingInflationFixProtocol(int height)
    { return height >= nInflationFixHeight;}

    //main.cpp GetBlockValue(height, fee)
    /*public static final BigInteger GetBlockReward(int height)
    {
        int COIN = 1;
        BigInteger nSubsidy = Utils.toNanoCoins(15, 0);

        if(height < 1080)
        {
            nSubsidy = Utils.toNanoCoins(2, 0); //2
        }
        else if(height < 2160)
        {
            nSubsidy   = Utils.toNanoCoins(1, 0); //2
        }
        else if(height < 3240)
        {
            nSubsidy   = Utils.toNanoCoins(2, 0); //2
        }
        else if(height < 4320)
        {
            nSubsidy  = Utils.toNanoCoins(5, 0); //5
        }
        else if(height < 5400)
        {
            nSubsidy  = Utils.toNanoCoins(8, 0); //8
        }
        else if(height < 6480)
        {
            nSubsidy = Utils.toNanoCoins(11, 0); //11
        }
        else if(height < 7560)
        {
            nSubsidy  = Utils.toNanoCoins(14, 0); //14
        }
        else if(height < 8640)
        {
            nSubsidy = Utils.toNanoCoins(17, 0); //17
        }
        else if(height < 523800)
        {
            nSubsidy = Utils.toNanoCoins(20, 0); //20
        }
        else if(height >= V3_FORK)
        {
            nSubsidy = Utils.toNanoCoins(5, 0); //5;
        }
        else
        {
            return nSubsidy.shiftRight(height / subsidyDecreaseBlockCount);
        }
        return nSubsidy;
    } */

    public static int subsidyDecreaseBlockCount = 4730400;     //main.cpp GetBlockValue(height, fee)

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
    public static final String SATOSHI_KEY = "04016c44069c3152982413d3ba3bf262a3a4d3ddad859ba78e0d744f5c67c2205d2aa2122e6c62b6310dad2d1e2f7e39028455ff1dbb26511c60fc96c8b4560c43";
    public static final String TESTNET_SATOSHI_KEY = "";

    /** The string returned by getId() for the main, production network where people trade things. */
    public static final String ID_MAINNET = "org.digitalcoin.production";
    /** The string returned by getId() for the testnet. */
    public static final String ID_TESTNET = "org.digitalcoin.test";
    /** Unit test network. */
    public static final String ID_UNITTESTNET = "com.google.digitalcoin.unittest";

    //checkpoints.cpp Checkpoints::mapCheckpoints
    public static void initCheckpoints(Map<Integer, Sha256Hash> checkpoints)
    {
        checkpoints.put( 0, new Sha256Hash("5e039e1ca1dbf128973bf6cff98169e40a1b194c3b91463ab74956f413b2f9c8"));
        checkpoints.put( 1, new Sha256Hash("45b2559dbe5e5772498e4170f3f1561448179fa90dd349e60e891766878dea2e"));
        checkpoints.put( 20, new Sha256Hash("59436aad777d285d52a3fb61b4176c7ca30a1254b7fc1480b2c7320913953fe3"));
        checkpoints.put( 3500, new Sha256Hash("6e92c6cf634c39149d07f022cf13e87b91713d1e7a5d9abc2b5f3646a4027838"));
        checkpoints.put( 22222, new Sha256Hash("7a58919a24c189f8c286413381e6ed7224c90a4181a7f7cd098825cc75ddec27"));
        checkpoints.put( 480000, new Sha256Hash("a11759fa9ed9c11769dc7ec3c279f523c886ea0ca0b9e1d1a49441c32b701f0d"));
        checkpoints.put( 600308, new Sha256Hash("0cd7f68e0e79a4595abb871fb71fd2db803b34b15da182d23c1568f56611af91"));
        checkpoints.put( 778389,  new Sha256Hash("5d1c20eda68cf4221885f2e9e0ad47817cca44da24f770f1334ca8cc2b07eb49"));
        checkpoints.put( 800000,  new Sha256Hash("609f6259e199a0a60705bf4c02d6aee84dc38d7d741f8044b1eec0c636567ebb"));
        checkpoints.put( 850000,  new Sha256Hash("a52a30db508a3bc3ab71f9bd83573e4ab4289ed8e5b66dc92f862baf6eb80eba"));
        checkpoints.put( 900000,  new Sha256Hash("254c90e2a47d0f28292bc7d6f5cd8f856eb782d8692a1d923abd9c43749935bc"));
        checkpoints.put( 950000,  new Sha256Hash("706c8ce10f2c9ffbf90fc10fe683ccabeeafa4e6cf5c8ec320f50319c9ba40a9"));
        checkpoints.put( 1000000, new Sha256Hash("564e27dc7bf17488c4bf66ba0955cbf075c975f8386ede157be9578da0476356"));
        checkpoints.put( 1010000, new Sha256Hash("bbe9adc561be01cb37acd71abf42a17e566d05b9cdfed95793db931540805bcf"));
        checkpoints.put( 1020000, new Sha256Hash("dc51bcc193a2e84bcbdd0448e6e0f5396b4e57c2f43e239d110d1145b147d4c9"));
        checkpoints.put( 1023000, new Sha256Hash("eaae71b7dae28ab3abfcfa959ae3db50eb4ed93204e36731a54063a4ea8e7218"));
        checkpoints.put( 1023013, new Sha256Hash("c328d2a8f8b976769a6b0488cbf6dc641902b6eb7db0995befd58e69679af4f8"));
        checkpoints.put( 1028000, new Sha256Hash("000000000365245106be8addeefadc53d613bf97ff0c7d7cc754e9a59addd408"));
        checkpoints.put( 1028003, new Sha256Hash("58bbb00e2c982744c833fc0555488b3a57236070f4a74e124bdeb2dd2583eda7"));
    }

    //Unit Test Information
    public static final String UNITTEST_ADDRESS = "DPHYTSm3f96dHRY3VG1vZAFC1QrEPkEQnt";
    public static final String UNITTEST_ADDRESS_PRIVATE_KEY = "QU1rjHbrdJonVUgjT7Mncw7PEyPv3fMPvaGXp9EHDs1uzdJ98hUZ";

}
