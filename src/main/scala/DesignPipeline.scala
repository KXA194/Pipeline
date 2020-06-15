class DesignPipeline {

  // Any transformation on an algebraic data type
  // can be implemented using structural recursion
  sealed trait Pipeline[A] {
    import Pipeline._

    def map[B](f: A => B): Pipeline[B] =
      PipelineMap(this, f)

    def flatMap[B](f: A => Pipeline[B]): Pipeline[B] =
      PipelineFlatMap(this, f)

    def product[B](that: Pipeline[B]): Pipeline[(A, B)] =
      PipelineProduct(this, tha)

    def filter(f: A => Boolean): Pipeline[A] = 
      PipelineFilter(this, f)

    def foldLeft[B](zero: B)(f: (B, A) => B): B = {
      def next[C](pipeline: Pipeline[C]): Option[C] =
        pipeline match {
          case PipelineMap(s, f) => next(s) match {
            case Some(value) => Some(f(value))
            case None => None
          }
          case PipelineFlatMap(s, f) =>  next(s).flatMap(a => next(f(a)))
          case PipelineProduct(l, r) => ???
          case PipelinePure(v) => ???
          case PipelineFromIterator(i) => ???
          case PipelineFilter(s, f) => 
            val nextValue = next(s)
            if(f(nextValue)) Some(nextValue) else None
        }
      // Pattern matching and polymorphism
    }
  }

  object Pipeline {
    final case class PipelineMap[A, B](source: Pipeline[A], f: A => B)
      extends Pipeline[B]
    final case class PipelineFlatMap[A, B](source: Pipeline[A], f: A => Pipeline[B])
      extends Pipeline[B]
    final case class PipelineProduct[A, B](left: Pipeline[A], right: Pipeline[B])
      extends Pipeline[(A, B)]
    final case class PipelinePure[A](value: A) extends Pipeline[A]
    final case class PipelineFromIterator[A](iterator: Iterator[A]) extends Pipeline[A]
    final case class PipelineFilter[A](source: Pipeline[A], f: A => Boolean) extends Pipeline[A]

    def apply[A](a: A): Pipeline[A] =
      PipelinePure(a)

    def fromIterator[A](iterator: Iterator[A]): Pipeline[A] =
      PipelineFromIterator(iterator)
  }

  object RetailExample {
    final case class Transaction(customerId: Int, day: Int, quantity: Int, cost: Double)


    // Pretend this data arrives over time
    val transactions = 
      Pipeline.fromIterator(List(
        Transaction(1, 1, 3, 3.50),
        Transaction(2, 1, 4, 42.0),
        Transaction(1, 2, 1, 4.20)
      ))

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
  // Separate description from action (why? Substitution / local reasoning)
  // Reification is one implementation strategy for constructing a description

  // Reification (aka defunctionalization)
  // Another fancy word
  // Meaning: to make the abstract concrete
  // More concrete meaning: to turn method calls into data

  // Algebra
  // Because we like fancy words
  // Algebra == interface
  // Ways of constructing instances (aka introduction forms [term from logic])
  // Ways of combining instances
  // Ways of running them (aka elimination forms [again from logic])

  // Pipeline is very similar to fs2 (fs2.io)
}
