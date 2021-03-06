/*
 * Copyright 2013 Google Inc.
 * Copyright 2015 Andreas Schildbach
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bitcoinj.params;

import org.bitcoinj.core.*;
import org.bitcoinj.net.discovery.*;

import java.net.*;

import static com.google.common.base.Preconditions.*;
import java.math.BigInteger;
/**
 * Parameters for the main production network on which people trade goods and services.
 */
public class MainNetParams extends AbstractBitcoinNetParams {
    public static final int MAINNET_MAJORITY_WINDOW = 1000;
    public static final int MAINNET_MAJORITY_REJECT_BLOCK_OUTDATED = 950;
    public static final int MAINNET_MAJORITY_ENFORCE_BLOCK_UPGRADE = 750;
    
    
    public MainNetParams() {
        super();
        interval = INTERVAL;
        targetTimespan = TARGET_TIMESPAN;
        //maxTarget = Utils.decodeCompactBits(0x1e00fffffL);//0x1d00ffffL);
        maxTarget = new BigInteger("000008ffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", 16);
        
        dumpedPrivateKeyHeader = 189;
        addressHeader = 61;// 0;
        p2shHeader = 5; 
        segwitAddressHrp = "lr";
        port = 8826; //or 10333
        packetMagic = 0xb8a8d2c6L; //0xf9beb4d9L;
        bip32HeaderPub = 0x0488B21E; //The 4 byte header that serializes in base58 to "xpub".
        bip32HeaderPriv = 0x0488ADE4; //The 4 byte header that serializes in base58 to "xprv"

        majorityEnforceBlockUpgrade = MAINNET_MAJORITY_ENFORCE_BLOCK_UPGRADE;
        majorityRejectBlockOutdated = MAINNET_MAJORITY_REJECT_BLOCK_OUTDATED;
        majorityWindow = MAINNET_MAJORITY_WINDOW;

        genesisBlock.setDifficultyTarget(0x1e08ffffL);//0x1d00ffffL);
        genesisBlock.setTime(1535307960L);//1231006505L);
        genesisBlock.setNonce(541362806);//2083236893);
        genesisBlock.setMerkleRoot( Sha256Hash.wrap("38f40d090640e76cc1b0b9e39eee22768ab2d5c17e6ed288f8267f5df36934d3"));

        //id = ID_MAINNET;
        id = ID_RIGHTNET;
        subsidyDecreaseBlockCount = 1050000;//210000;
        spendableCoinbaseDepth = 100;
        String genesisHash = genesisBlock.getHashAsString();
        System.out.println("genesishash:"+genesisHash);
        checkState(genesisHash.equals("bd44f5fd4a3598f3f4e56c3688b279ca63170cedd5386741607d6cfbe9f8fe13"),genesisHash);
        


        // This contains (at a minimum) the blocks which are not BIP30 compliant. BIP30 changed how duplicate
        // transactions are handled. Duplicated transactions could occur in the case where a coinbase had the same
        // extraNonce and the same outputs but appeared at different heights, and greatly complicated re-org handling.
        // Having these here simplifies block connection logic considerably.
        //checkpoints.put(91722, Sha256Hash.wrap("00000000000271a2dc26e7667f8419f2e15416dc6955e5a6c6cdf3f2574dd08e"));
        //checkpoints.put(91812, Sha256Hash.wrap("00000000000af0aed4792b1acee3d966af36cf5def14935db8de83d6f9306f2f"));
        //checkpoints.put(91842, Sha256Hash.wrap("00000000000a4d0a398161ffc163c503763b1f4360639393e0e4c8e300e0caec"));
        //checkpoints.put(91880, Sha256Hash.wrap("00000000000743f190a18c5577a3c2d2a1f610ae9601ac046a38084ccb7cd721"));
        //checkpoints.put(200000, Sha256Hash.wrap("000000000000034a7dedef4a161fa058a2d67a173a90155f3a2fe6fc132e0ebf"));

        dnsSeeds = new String[] {               
                //"192.168.1.201",
                //"192.168.1.3",
        		"18.222.204.39",
        		//"35.221.218.88",
        };
        /*
        httpSeeds = new HttpDiscovery.Details[] {
                // Andreas Schildbach
                new HttpDiscovery.Details(
                        ECKey.fromPublicOnly(Utils.HEX.decode("0238746c59d46d5408bf8b1d0af5740fe1a6e1703fcb56b2953f0b965c740d256f")),
                        URI.create("http://httpseed.bitcoin.schildbach.de/peers")
                )
        };
		*/
        addrSeeds = new int[] {

        };
    }

    private static MainNetParams instance;
    public static synchronized MainNetParams get() {
        if (instance == null) {
            instance = new MainNetParams();
        }
        return instance;
    }

    @Override
    public String getPaymentProtocolId() {
        return PAYMENT_PROTOCOL_ID_MAINNET;
    }
}
