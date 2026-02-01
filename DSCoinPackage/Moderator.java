package DSCoinPackage;

import java.util.*;

public class Moderator
 {
  public void initializeDSCoin(DSCoin_Honest DSObj, int coinCount)
  {
    Members ModMember = new Members();
    ModMember.UID = "Moderator";

    List<Transaction> listOfTrans = new ArrayList<Transaction>();
    int m = DSObj.memberlist.length;
    int d = coinCount/m;
    for(int j=0; j<d; j++)
    {
      for(int i=0; i<m; i++)
      {
        int v = 100000 + listOfTrans.size();
        Transaction t = new Transaction();
        t.coinID = String.valueOf(v);
        t.Source = ModMember;
        t.Destination = DSObj.memberlist[i];
        t.coinsrc_block = null;
        listOfTrans.add(t);
        DSObj.latestCoinID = t.coinID;
      }
    }
    int n = DSObj.bChain.tr_count;
    int w = coinCount/n;
    for(int j=0; j<w; j++)
    {
      Transaction[] t_arr = new Transaction[n];
      for(int i=0; i<n; i++)
      {
        t_arr[i] = listOfTrans.get(0);
        listOfTrans.remove(0);
      }
      TransactionBlock tB = new TransactionBlock(t_arr);
      DSObj.bChain.InsertBlock_Honest(tB);
    }

    TransactionBlock Tb = DSObj.bChain.lastBlock;
    while(Tb.previous!=null)
    {
      for(int i=0; i<n; i++)
      {
        Transaction h = Tb.trarray[i];
          Members gog = h.Destination;
          int f = 0;
          for(; f<m; f++){
            if(gog == DSObj.memberlist[f]) break;
          }
          gog.addToCoins(h.coinID, Tb);
          DSObj.memberlist[f] = gog;
      }
      Tb = Tb.previous;
    }
    if(Tb.previous == null)
    {
      for(int i=0; i<n; i++)
      {
        Transaction h = Tb.trarray[i];
          Members gog = h.Destination;
          int f = 0;
          for(; f<m; f++){
            if(gog == DSObj.memberlist[f]) break;
          }
          gog.addToCoins(h.coinID, Tb);
          DSObj.memberlist[f] = gog;
      }
    }
  }

  public void initializeDSCoin(DSCoin_Malicious DSObj, int coinCount)
  {
    Members ModMember = new Members();
    ModMember.UID = "Moderator";
    List<Transaction> listOfTrans = new ArrayList<Transaction>();
    int m = DSObj.memberlist.length;
    int d = coinCount/m;
    for(int j=0; j<d; j++)
    {
      for(int i=0; i<m; i++)
      {
        int v = 100000 + listOfTrans.size();
        Transaction t = new Transaction();
        t.coinID = String.valueOf(v);
        t.Source = ModMember;
        t.Destination = DSObj.memberlist[i];
        t.coinsrc_block = null;
        listOfTrans.add(t);
        DSObj.latestCoinID = t.coinID;
      }
    }

    int n = DSObj.bChain.tr_count;
    int w = coinCount/n;
    for(int j=0; j<w; j++)
    {
      Transaction[] t_arr = new Transaction[n];
      for(int i=0; i<n; i++)
      {
        t_arr[i] = listOfTrans.get(0);
        listOfTrans.remove(0);
      }
      TransactionBlock tB = new TransactionBlock(t_arr);
      DSObj.bChain.InsertBlock_Malicious(tB);
    }

    TransactionBlock Tb = DSObj.bChain.lastBlocksList[0];
    while(Tb.previous!=null)
    {
      for(int i=0; i<n; i++)
      {
        Transaction h = Tb.trarray[i];
          Members gog = h.Destination;
          int f = 0;
          for(; f<m; f++){
            if(gog == DSObj.memberlist[f]) break;
          }
          gog.addToCoins(h.coinID, Tb);
          DSObj.memberlist[f] = gog;
      }
      Tb = Tb.previous;
    }
    if(Tb.previous == null)
    {
      for(int i=0; i<n; i++)
      {
        Transaction h = Tb.trarray[i];
          Members gog = h.Destination;
          int f = 0;
          for(; f<m; f++){
            if(gog == DSObj.memberlist[f]) break;
          }
          gog.addToCoins(h.coinID, Tb);
          DSObj.memberlist[f] = gog;
      }
    }
  }
}
