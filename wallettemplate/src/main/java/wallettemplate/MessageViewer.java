/*
 * Copyright by the original author or authors.
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

package wallettemplate;

import javafx.scene.layout.HBox;
import javafx.scene.control.TextArea;
import org.bitcoinj.core.*;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.utils.Threading;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.spongycastle.crypto.params.KeyParameter;
import wallettemplate.controls.BitcoinAddressValidator;
import wallettemplate.utils.TextFieldValidator;
import wallettemplate.utils.WTUtils;


import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import static wallettemplate.Main.bitcoin;
import static wallettemplate.utils.GuiUtils.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.HashMap;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Nullable;
import javax.swing.text.html.ListView;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.bitcoinj.script.*;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MessageViewer {
    public Button refreshBtn;
    public Button rebuildBtn;
    public Button cancelBtn;
    public Label titleLabel;
    public Label btcLabel;
    public TextArea message;
    public ListView hastTag;
    public Main.OverlayUI overlayUI;
    public javafx.scene.control.ListView hashTagsLst;
    private Set<String> stringSet;
    
    //private ObservableList observableList = FXCollections.observableArrayList();

    
    private Wallet.SendResult sendResult;
    private KeyParameter aesKey;    
    private PeerGroup peerGroup;
    private Peer peer;
    private DB db;
    
    private static final Logger log = LoggerFactory.getLogger(MessageViewer.class);
    
    // Called by FXMLLoader
    public void initialize() {
        //Coin balance = Main.bitcoin.wallet().getBalance();
        //checkState(!balance.isZero());
        //new BitcoinAddressValidator(Main.params, address, sendBtn);
        //new TextFieldValidator(amountEdit, text ->
         //       !WTUtils.didThrow(() -> checkState(Coin.parseCoin(text).compareTo(balance) <= 0)));
        //amountEdit.setText(balance.toPlainString());
        //amountEdit.setText("0.01");
        //address.setText("xxx"); 
    	peerGroup = bitcoin.peerGroup();
    	peer = peerGroup.getDownloadPeer();
    	db = bitcoin.getDB();

    	String alltx=peer.getTxAddress();
    	refreshData();
        hashTagsLst.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                // Your action here
            	if (hashTagsLst.getItems().size()>0) {
	            	String hashtag = hashTagsLst.getSelectionModel().getSelectedItem().toString();
	            	log.info("Selected item: " + hashtag);
	            	String msgs="";
	                BTreeMap map = db.treeMap("#"+hashtag).createOrOpen();
	                for (Object key : map.keySet()) { 
	                	
	                	LocalDateTime date =
	                		    LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(key.toString())*-1000), ZoneId.systemDefault());
	                	
	                	Object hash=map.get(key);
	                	HTreeMap opReturnMap = db.hashMap(peer.APP_OP_RETURN_MAP).createOrOpen();
	                	HTreeMap opReturnTxMap = db.hashMap(peer.APP_OP_RETURN_TX_MAP).createOrOpen();                	
	                	HTreeMap opReturnAddressMap = db.hashMap(peer.APP_OP_RETURN_ADDRESS_MAP).createOrOpen();

	                	msgs=msgs+opReturnTxMap.get(hash).toString()+"\n";
	                	msgs=msgs+date +"\n";
	                	msgs=msgs+opReturnMap.get(hash).toString()+"\n\n";
	                	
	                	log.info(msgs);
	                }
	                message.setText(msgs);
                
            	}
            }
        });
        
    }
    public void refreshData() {
    	System.out.println("Refreshdata start");
    	ObservableList<Object> elements = FXCollections.observableArrayList();    	    	
        NavigableSet hashtagset = db.treeSet(peer.APP_HASHTAG_SET).createOrOpen();
        for (Object hashtag: hashtagset) {
        	elements.add(hashtag.toString().substring(1));
        }
    	System.out.println("Refreshdata after for loop");
        if (elements!=null) {
        	System.out.println("Refreshdata elements not null");
        	System.out.println(elements.size());
        
        	hashTagsLst.getItems().clear();
        	hashTagsLst.setItems(elements);    	
        }
        System.out.println("Refreshdata end");
    }

    public void cancel(ActionEvent event) {
        overlayUI.done();
    }

    public void refresh(ActionEvent event) {    	
        String alltx=peer.getTxAddress();
        refreshData();
    }
    
    public void rebuild(ActionEvent event) {        	
        String alltx=peer.getTxAddress();        
    }
    
    private void setText(String alltx) {
    	message.setText(alltx);
    }

    
    private void updateTitleForBroadcast() {
        final int peers = sendResult.tx.getConfidence().numBroadcastPeers();
        titleLabel.setText(String.format("Broadcasting ... seen by %d peers", peers));
    }
}
