package DSCoinPackage;

import java.lang.*;
import java.util.*;
import HelperClasses.Pair;

public class Members
{
  public String UID;
  public List<Pair<String, TransactionBlock>> mycoins;
  public Transaction[] in_process_trans;

  public void addToCoins(String coinID, TransactionBlock tB)
  {
    String d = coinID;
    int z = Integer.parseInt(d);
    Pair<String, TransactionBlock> coin = new Pair<String, TransactionBlock>(d, tB);
    for(int x=0; x<this.mycoins.size(); x++)
    {
      Pair<String, TransactionBlock> xi = this.mycoins.get(x);
      String xu = xi.get_first();
      int y = Integer.parseInt(xu);
      if(z<y)
      {
        this.mycoins.add(x, coin);
        break;
      }
    }
    if(!this.mycoins.contains(coin)) this.mycoins.add(coin);
  }

  public void initiateCoinsend(String destUID, DSCoin_Honest DSobj)
  {
    Pair<String, TransactionBlock> p = mycoins.get(0);
    String coin = p.get_first();
    TransactionBlock coin_source = p.get_second();
    mycoins.remove(0);
    Transaction tobj = new Transaction();
    tobj.coinID = coin;
    tobj.Source = this;
    tobj.coinsrc_block = coin_source;
    int i=0;
    for(; !DSobj.memberlist[i].UID.equals(destUID); i++);
    tobj.Destination = DSobj.memberlist[i];

    int r = 0;
    for(; in_process_trans[r]!= null; r++);
    in_process_trans[r] = tobj;
    DSobj.pendingTransactions.AddTransactions(tobj);
  }

  public Pair<TransactionBlock, List<Pair<String, String>>> findBlock(Transaction tobj, DSCoin_Honest DSObj) throws MissingTransactionException
  {
    TransactionBlock t = DSObj.bChain.lastBlock;
    int num = DSObj.bChain.tr_count;
    TransactionBlock tB = null;
    List<Pair<String, String>> list_dgst = new ArrayList<Pair<String, String>>();
    while(t.previous != null)
    {
      for(int i = 0; i<num; i++)
      {
        if(t.trarray[i] == tobj)
        {
          tB = t;
          break;
        }
      }
      String a = t.dgst;
      String b = t.previous.dgst + "#" + t.trsummary + "#" + t.nonce;
      Pair<String, String> p = new Pair<String, String>(a, b);
      list_dgst.add(p);
      if(tB == null)
      {
        t = t.previous;
      }
      else
      {
        String m = t.previous.dgst;
        Pair<String, String> q = new Pair<String, String>(m, null);
        list_dgst.add(q);
        break;
      }
    }
    if(t.previous == null)
    {
      for(int i = 0; i<num; i++)
      {
        if(t.trarray[i] == tobj)
        {
          tB = t;
          break;
        }
      }
      String a1 = t.dgst;
      String b1 = DSObj.bChain.start_string + "#" + t.trsummary + "#" + t.nonce;
      Pair<String, String> o = new Pair<String, String>(a1, b1);
      list_dgst.add(o);
      if(tB == null) throw new MissingTransactionException();
      else
      {
        String w = DSObj.bChain.start_string;
        Pair<String, String> h = new Pair<String, String>(w, null);
        list_dgst.add(h);
      }
    }
    Collections.reverse(list_dgst);
    Pair<TransactionBlock, List<Pair<String, String>>> foundBlock = new Pair<TransactionBlock, List<Pair<String, String>>>(tB, list_dgst);
    return foundBlock;
  }

  public Pair<List<Pair<String, String>>, List<Pair<String, String>>> finalizeCoinsend (Transaction tobj, DSCoin_Honest DSObj) throws MissingTransactionException
  {
    Pair<TransactionBlock, List<Pair<String, String>>> p = findBlock(tobj, DSObj);
    TransactionBlock tB = p.get_first();
    List<Pair<String, String>> list_dgst = p.get_second();

    int i=0;
    for(; i<tB.trarray.length; i++)
    {
      if(tB.trarray[i] == tobj) break;
    }
    List<Pair<String, String>> list_path = tB.Tree.SiblingPath(i);
    Pair<List<Pair<String, String>>, List<Pair<String, String>>> result = new Pair<List<Pair<String, String>>, List<Pair<String, String>>>(list_path, list_dgst);

    Members Sender = tobj.Source;
    for(int k=0; k<100; k++)
    {
      if(Sender.in_process_trans[k] == tobj)
      {
        for(int j=k; j<99; j++)
        {
          Sender.in_process_trans[j] = Sender.in_process_trans[j+1];
          if(Sender.in_process_trans[j] == null) break;
        }
        break;
      }
    }

    Members Receiver = tobj.Destination;
    Receiver.addToCoins(tobj.coinID, tB);
    return result;
  }

  public void MineCoin(DSCoin_Honest DSObj) throws EmptyQueueException
  {
    int n = DSObj.bChain.tr_count;
    Transaction[] list_trans = new Transaction[n];
    List<String> list_coin = new ArrayList<String>();
    while((list_coin.size())<(n-1)){
      Transaction t = DSObj.pendingTransactions.RemoveTransaction();
      String coin = t.coinID;
      if(!list_coin.contains(coin))
      {
        list_coin.add(coin);
        list_trans[list_coin.size()-1] = t;
      }
    }
    Transaction minerRewardTransaction = new Transaction();
    int z = 1 + Integer.parseInt(DSObj.latestCoinID);
    minerRewardTransaction.coinID = String.valueOf(z);
    minerRewardTransaction.Source = null;
    minerRewardTransaction.Destination = this;
    minerRewardTransaction.coinsrc_block = null;
    minerRewardTransaction.next = null;

    DSObj.latestCoinID = String.valueOf(z);
    list_trans[n-1] = minerRewardTransaction;

    TransactionBlock tB = new TransactionBlock(list_trans);
    DSObj.bChain.InsertBlock_Honest(tB);
    for(int i=0; i<n; i++)
    {
      while(!tB.checkTransaction(tB.trarray[i]))
      {
        for(int g = i; g<n-2; g++){
          tB.trarray[g] = tB.trarray[g+1];
        }
        while(true)
        {
          Transaction t1 = DSObj.pendingTransactions.RemoveTransaction();
          String coin = t1.coinID;
          if(!list_coin.contains(coin))
          {
            list_coin.add(coin);
            tB.trarray[n-2] = t1;
            break;
          }
        }
        tB.trsummary = tB.Tree.Build(tB.trarray);
        String s;
        if(tB.previous == null) s = DSObj.bChain.start_string;
        else s = tB.previous.dgst;
        Pair<String, String> d_n = DSObj.bChain.dgst_nonce(tB, s);
        tB.dgst = d_n.get_first();
        tB.nonce = d_n.get_second();
        DSObj.bChain.lastBlock = tB;
      }
    }
    this.addToCoins(minerRewardTransaction.coinID, tB);
  }

  public void MineCoin(DSCoin_Malicious DSObj) throws EmptyQueueException
  {
    int n = DSObj.bChain.tr_count;
    Transaction[] list_trans = new Transaction[n];
    List<String> list_coin = new ArrayList<String>();
    while((list_coin.size())<(n-1))
    {
      Transaction t = DSObj.pendingTransactions.RemoveTransaction();
      String coin = t.coinID;
      if(!list_coin.contains(coin))
      {
        list_coin.add(coin);
        list_trans[list_coin.size()-1] = t;
      }
    }
    Transaction minerRewardTransaction = new Transaction();
    int z = 1 + Integer.parseInt(DSObj.latestCoinID);
    minerRewardTransaction.coinID = String.valueOf(z);
    minerRewardTransaction.Source = null;
    minerRewardTransaction.Destination = this;
    minerRewardTransaction.coinsrc_block = null;
    minerRewardTransaction.next = null;

    DSObj.latestCoinID = String.valueOf(z);
    list_trans[n-1] = minerRewardTransaction;

    TransactionBlock tB = new TransactionBlock(list_trans);
    DSObj.bChain.InsertBlock_Malicious(tB);
    int indexOfBlock = 0;
    while(DSObj.bChain.lastBlocksList[indexOfBlock]!= tB){
      indexOfBlock++;
      if(indexOfBlock == 99) break;
    }
    for(int i=0; i<n; i++)
    {
      while(!tB.checkTransaction(tB.trarray[i]))
      {
        for(int g = i; g<n-2; g++)
        {
          tB.trarray[g] = tB.trarray[g+1];
        }
        while(true)
        {
          Transaction t1 = DSObj.pendingTransactions.RemoveTransaction();
          String coin = t1.coinID;
          if(!list_coin.contains(coin))
          {
            list_coin.add(coin);
            tB.trarray[n-2] = t1;
            break;
          }
        }
        tB.trsummary = tB.Tree.Build(tB.trarray);
        String s;
        if(tB.previous == null) s = DSObj.bChain.start_string;
        else s = tB.previous.dgst;
        Pair<String, String> d_n = DSObj.bChain.dgst_nonce(tB, s);
        tB.dgst = d_n.get_first();
        tB.nonce = d_n.get_second();
        DSObj.bChain.lastBlocksList[indexOfBlock] = tB;
      }
    }
    this.addToCoins(minerRewardTransaction.coinID, tB);
  }
}
