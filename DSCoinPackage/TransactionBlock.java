package DSCoinPackage;

import HelperClasses.MerkleTree;
import HelperClasses.CRF;

public class TransactionBlock
{
  public Transaction[] trarray;
  public TransactionBlock previous;
  public MerkleTree Tree;
  public String trsummary;
  public String nonce;
  public String dgst;

  TransactionBlock(Transaction[] t)
  {
    trarray = new Transaction[t.length];
    for(int i=0; i<t.length; i++) trarray[i] = t[i];
    previous = null;
    Tree = new MerkleTree();
    trsummary = Tree.Build(trarray);
    dgst = null;
    nonce = null;
  }

  public boolean checkTransaction (Transaction t)
  {
    if(t.coinsrc_block == null) return true;
    int i = 0;
    for(; i<t.coinsrc_block.trarray.length; i++)
    {
      Transaction q = t.coinsrc_block.trarray[i];
      if(q.coinID.equals(t.coinID) && q.Destination.UID.equals(t.Source.UID))break;
    }
    if(i == t.coinsrc_block.trarray.length) return false;
  TransactionBlock tB = this.previous;
    while(tB != t.coinsrc_block)
    {
      for(int j = 0; j<tB.trarray.length; j++)
      {
        Transaction r = tB.trarray[j];
        if(r.coinID.equals(t.coinID)) return false;
      }
      tB = tB.previous;
    }
    return true;
  }
}
