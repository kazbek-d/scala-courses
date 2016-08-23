package com

import common.BinomialHeap
import model.{Buy, MatchingOrders, Order, Sale}

trait OrdersBook extends BinomialHeap {

  override type A = Order
  override def ord = Ordering.by(_.price)

  var buy: H = empty
  var sale: H = empty

  private def canStartBuyAndSale = if (isEmpty(buy) || isEmpty(sale))
    None
  else {
    val b = findMin(buy)
    val s = findMin(sale)
    if (b.price <= -s.price)
      Some(MatchingOrders(b, s))
    else
      None
  }

  private def adjustHeap(buyAndSale: MatchingOrders) = {
    buy = deleteMin(buy)
    sale = deleteMin(sale)

    if (buyAndSale.buy.quantity < buyAndSale.sale.quantity)
      sale = insert(buyAndSale.sale.copy(quantity = buyAndSale.sale.quantity - buyAndSale.buy.quantity), sale)
    else if (buyAndSale.buy.quantity > buyAndSale.sale.quantity)
      buy = insert(buyAndSale.buy.copy(quantity = buyAndSale.buy.quantity - buyAndSale.sale.quantity), buy)
  }

  private def adjustResult(buyAndSale: MatchingOrders) = {
    val bs = if (buyAndSale.buy.quantity == buyAndSale.sale.quantity)
      buyAndSale
    else if (buyAndSale.buy.quantity < buyAndSale.sale.quantity)
      buyAndSale.copy(sale = buyAndSale.sale.copy(quantity = buyAndSale.buy.quantity))
    else
      buyAndSale.copy(buy = buyAndSale.buy.copy(quantity = buyAndSale.sale.quantity))
    bs.copy(sale = bs.sale.copy(price = bs.buy.price))
  }

  private def matching = {
    @annotation.tailrec
    def loop(m: Option[MatchingOrders], acc: List[MatchingOrders]): List[MatchingOrders] = m match {
      case Some(buyAndSale) => {
        adjustHeap(buyAndSale)
        loop(canStartBuyAndSale, adjustResult(buyAndSale) :: acc)
      }
      case _ => acc
    }
    loop(canStartBuyAndSale, Nil).reverse
  }

  def insertOrder(order: Order): List[MatchingOrders] = {
    order.operation match {
      case Buy => buy = insert(order, buy)
      case Sale => sale = insert(order.copy(price = -order.price), sale)
    }
    matching
  }
}

