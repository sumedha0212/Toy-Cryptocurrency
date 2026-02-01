package DSCoinPackage;

import HelperClasses.*;

public class BlockChain_Honest
{
  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock lastBlock;

  public Pair<String, String> dgst_nonce(TransactionBlock newBlock, String s)
  {
    long ikk = 1000000001L;
    CRF c = new CRF(64);
    String z = c.Fn(s + "#" + newBlock.trsummary + "#" + String.valueOf(ikk));
    String y = z.substring(0, 4);
    while(!y.equals("0000") && ikk<10000000000L)
    {
      ikk++;
      z = c.Fn(s + "#" + newBlock.trsummary + "#" + String.valueOf(ikk));
      y = z.substring(0, 4);
    }
    Pair<String, String> w = new Pair<String, String>(z, String.valueOf(ikk));
    return w;
  }

  public void InsertBlock_Honest (TransactionBlock newBlock)
  {
    if(lastBlock == null)
    {
      newBlock.previous = null;
      Pair<String, String> p = dgst_nonce(newBlock, start_string);
      newBlock.dgst = p.get_first();
      newBlock.nonce = p.get_second();
      lastBlock = newBlock;
    }
    else
    {
      newBlock.previous = lastBlock;
      Pair<String, String> q = dgst_nonce(newBlock, lastBlock.dgst);
      newBlock.dgst = q.get_first();
      newBlock.nonce = q.get_second();
      lastBlock = newBlock;
    }
  }
}
