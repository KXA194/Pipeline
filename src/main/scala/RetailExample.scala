
object RetailExample {
  final case class Transaction(
      customerId: Int,
      day: Int,
      quantity: Int,
      cost: Double
  )

  // Pretend this data arrives over time
  val transactions: Pipeline[Transaction] =
    Pipeline.fromIterator(
      Iterator(
        Transaction(1, 1, 3, 3.50),
        Transaction(2, 1, 4, 42.0),
        Transaction(1, 2, 1, 4.20)
      )
    )

  def totalSalesForDay(day: Int): Double =
    transactions
      .filter(tx => tx.day == day)
      .map(tx => tx.cost)
      .foldLeft(0.0)((accum, cost) => accum + cost)

  def totalSalesForCustomer(customerId: Int): Double =
    transactions
      .filter(tx => tx.customerId == customerId)
      .map(tx => tx.cost)
      .foldLeft(0.0)((accum, cost) => accum + cost)
}
