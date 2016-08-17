package model


object Transfer {

  trait Operation
  case class Deposit(acc: String, amount: BigDecimal) extends Operation
  case class Withdraw(acc: String, amount: BigDecimal) extends Operation
  case class GetBalance(acc: String) extends Operation
  case class Transfer2Acc(acc1: String, acc2: String, amount: BigDecimal) extends Operation


  trait Result
  trait Err extends Result
  case class InsufficientFunds(message: String) extends Err
  case class AnyErr(message: String) extends Err
  case class Balance(acc: String, amount: BigDecimal) extends Result

}
