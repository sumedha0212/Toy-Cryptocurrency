package DSCoinPackage;

import HelperClasses.CRF;
import HelperClasses.Pair;

public class BlockChain_Malicious
{

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock[] lastBlocksList;

  public static boolean checkTransactionBlock (TransactionBlock tB)
  {
    CRF c = new CRF(64);
    //checking dgst
    if(!tB.dgst.substring(0, 4).equals("0000")) return false;
    String s;
    if(tB.previous == null) s = start_string;
    else s = tB.previous.dgst;
    String z = c.Fn(s + "#" + tB.trsummary + "#" + tB.nonce);
    if(!tB.dgst.equals(z)) return false;
    //checking trsummary
    int zz = tB.trarray.length;
    String[] checkTree = new String[2*zz-1];
    for(int h = zz; h>= 1; h = h/2)
    {
      if(h == zz)
      {
        for(int i=h-1; i<(2*h-1); i++)
        {
          String a = tB.Tree.get_str(tB.trarray[i-h+1]);
          checkTree[i] = a;
        }
      }
      else
      {
        for(int i=h-1; i<(2*h-1); i++)
        {
          String a1 = checkTree[2*i+1];
          String a2 = checkTree[2*i+2];
          String a = c.Fn(a1 + "#" + a2);
          checkTree[i] = a;
        }
      }
    }
    if(!tB.trsummary.equals(checkTree[0])) return false;
    //checking transactions
    for(int i=0; i<zz; i++)
    {
      if(!tB.checkTransaction(tB.trarray[i])) return false;
    }
    return true;
  }

  public TransactionBlock FindLongestValidChain()
  {
    if(lastBlocksList[0] == null) return null;
    int p = 0;
    for(; lastBlocksList[p] != null; p++);

    TransactionBlock[] validArray = new TransactionBlock[p];
    int[] countArray = new int[p];

    for(int i=0; i<p; i++)
    {
      TransactionBlock t = lastBlocksList[i];
      validArray[i] = t;
      countArray[i] = 1;
      for(; t.previous!= null; t=t.previous)
      {
        if(!checkTransactionBlock(t))
        {
          validArray[i] = t.previous;
          countArray[i] = 1;
        }
        else{
          countArray[i]++;
        }
      }
    }
    int j = 0;
    for(int i = 0; i<p; i++)
    {
      if(countArray[i]>=countArray[j]) j = i;
    }

    return validArray[j];
  }

  public Pair<String, String> dgst_nonce(TransactionBlock newBlock, String s)
  {
    long inn = 1000000001L;
    CRF c = new CRF(64);
    String z = c.Fn(s + "#" + newBlock.trsummary + "#" + String.valueOf(inn));
    String y = z.substring(0, 4);
    while(!y.equals("0000") && inn<10000000000L)
    {
      inn++;
      z = c.Fn(s + "#" + newBlock.trsummary + "#" + String.valueOf(inn));
      y = z.substring(0, 4);
    }
    Pair<String, String> w = new Pair<String, String>(z, String.valueOf(inn));
    return w;
  }

  public void InsertBlock_Malicious (TransactionBlock newBlock)
  {
    TransactionBlock lastBlock = FindLongestValidChain();
    if(lastBlock == null)
    {
      newBlock.previous = null;
      Pair<String, String> p = dgst_nonce(newBlock, start_string);
      newBlock.dgst = p.get_first();
      newBlock.nonce = p.get_second();
    }
    else
    {
      newBlock.previous = lastBlock;
      Pair<String, String> q = dgst_nonce(newBlock, lastBlock.dgst);
      newBlock.dgst = q.get_first();
      newBlock.nonce = q.get_second();
    }
    int o = 0;
    while(lastBlocksList[o] != null)
    {
      o++;
    }
    int i=0;
    while(i<o)
    {
      if(lastBlocksList[i] == lastBlock) break;
      i++;
    }
    lastBlocksList[i] = newBlock;
  }
}
