package DSCoinPackage;

public class TransactionQueue
{
  public Transaction firstTransaction;
  public Transaction lastTransaction;
  public int numTransactions;

  public void AddTransactions (Transaction transaction)
  {
    transaction.next = null;
    if(firstTransaction == null)
    {
      firstTransaction = transaction;
      lastTransaction = firstTransaction;
      numTransactions = 0;
    }
    else if(firstTransaction.next == null)
    {
      firstTransaction.next = transaction;
      lastTransaction.next = transaction;
      lastTransaction = transaction;
    }
    else
    {
      lastTransaction.next = transaction;
      lastTransaction = transaction;
    }
    numTransactions++;
  }

  public Transaction RemoveTransaction () throws EmptyQueueException
  {
    if(firstTransaction == null)
    {
      throw new EmptyQueueException();
    }
    Transaction t = this.firstTransaction;
    numTransactions--;
    if(firstTransaction.next != null) firstTransaction = firstTransaction.next;
    else firstTransaction = null;
    return t;
  }

  public int size()
  {
    return numTransactions;
  }
}
