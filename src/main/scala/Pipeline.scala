/** The API interface for the pipeline of processing streaming data.
  *
  * We first identified some operations one would normally do on data:
  *    1. selecting & filtering data according to some criteria, e.g. select data for a specified day/month/year.
  *    2. applying some logic to transform the the data, e.g. given a dataset with two information: cost per unit of item and quantity sold, we can multiply them to get total revenue.
  *    3. joining two different stream of data together.
  *
  *  Operation (1) & (2) are sequencing operations and order is important. They can be implemented by using methods like `map`, `flatMap` and `filter`.
  *  Operation (3) is parallel operation in which order is less important and can be implemented by using `product` method.
  *
  * We also introduced some methods to create a Pipeline which can be found in the companion object of Pipeline:
  *    `apply` method to create a Pipeline with a single element,
  *    `fromIterator` method to create a Pipeline with infinite element.
  *
  * To actually run the operations, we then introduced a `foldLeft` method which is the only method that returns a `B` type (All the other methods return `Pipeline`). Why?
  * Because we want to separate describing what will happen from actually making it happen.
  *
  * Lastly, we used reification to implement all the methods i.e we turned all our methods into data or more commonly known as Algebraic Data Type (ADT).
  * (Plain English translation: create a case class for the method and stuff the method's parameter(s) into the case class, at least that's how I see it  ¯\_(ツ)_/¯).
  *
  * @tparam A
  */
sealed trait Pipeline[A] {
  import Pipeline._

  def map[B](f: A => B): Pipeline[B] =
    PipelineMap(this, f)

  def flatMap[B](f: A => Pipeline[B]): Pipeline[B] =
    PipelineFlatMap(this, f)

  def product[B](that: Pipeline[B]): Pipeline[(A, B)] =
    PipelineProduct(this, that)

  def filter(f: A => Boolean): Pipeline[A] =
    PipelineFilter(this, f)

  /** Pattern matching on five of the Pipeline's ADT defined in the companion object to implement `foldLeft`.
    *
    * @param zero
    * @param f
    * @tparam B
    * @return
    */
  def foldLeft[B](zero: B)(f: (B, A) => B): B = {
    def next[C](pipeline: Pipeline[C]): Option[C] =
      pipeline match {

        /**
          * Goal: to return Option[B]
          * Available:
          *    s: Pipeline[A]
          *    f: A => B
          */
        case PipelineMap(s, f) =>
          next(s) match {
            case Some(value) => Some(f(value))
            case None        => None
          }
//        case PipelineMap(s, f) => next(s).map(f)

        /**
          * Goal: to return Option[B]
          * Available:
          *    s: Pipeline[A]
          *    f: A => Pipeline[B]
          */
        case PipelineFlatMap(s, f) =>
          next(s) match {
            case Some(value) => next(f(value))
            case None        => None
          }
//         case PipelineFlatMap(s, f) => next(s).flatMap(a => next(f(a)) )
        //Option[A].flatMap(A => Option[B]) =
        //Option[B]

        /**
          * Goal: to return Option[(A, B)]
          * Available:
          *    l:Pipeline[A]
          *    r:Pipeline[B]
          * next: Pipeline[A] => Option[A]
          */
        case PipelineProduct(l, r) =>
          next(l) match {
            case Some(valueA) =>
              next(r) match {
                case Some(valueB) => Some((valueA, valueB))
                case None         => None
              }
            case None => None
          }
//        case PipelineProduct(l, r) => next(l).flatMap(valueA => next(r).map(valueB => Some((valueA, valueB))))

        /**
          * Goal: to return Option[A]
          * Available:
          *    value: A
          * next: Pipeline[A] => Option[A]
          */
        case PipelinePure(v) => Some(v)

        /**
          * Goal: to return Option[A]
          * Available:
          *    i: Iterator[A]
          * next: Pipeline[A] => Option[A]
          *
          * methods on Iterator[A]
          *   hasNext: Boolean
          *   next: A
          */
        case PipelineFromIterator(i) =>
          i.hasNext match {
            case true  => Some(i.next)
            case false => None
          }

        /**
          * Goal: to return Option[A]
          * Available:
          *    source: Pipeline[A],
          *    f: A => Boolean
          * next: Pipeline[A] => Option[A]
          *
          */
        case PipelineFilter(s, f) =>
          next(s) match {
            case Some(value) => if (f(value)) Some(value) else None
            case None        => None
          }
//        case PipelineFilter(s, f) => next(s).map(value => if (f(value)) Some(value) else None)
      }

    // Pattern matching and polymorphism
    // Goal: B
    // Available:
    //   this: Pipeline[A]
    //   zero: B
    //   f: (B, A) => B
    //   next: Pipeline[C] => Option[C]
    //   foldLeft: (Pipeline[A], B, (B, A) => B) => B
    //              ^----- The 'this' parameter. We do not pass this explicitly
    //              this.foldLeft(zero)(f)

    next(this) match {
      case Some(v) =>
        val theB: B = f(zero, v)
        this.foldLeft(theB)(f)
      //theB
      case None => zero
    }

  }
}

object Pipeline {

  final case class PipelineMap[A, B](source: Pipeline[A], f: A => B)
      extends Pipeline[B]

  final case class PipelineFlatMap[A, B](
      source: Pipeline[A],
      f: A => Pipeline[B]
  ) extends Pipeline[B]

  final case class PipelineProduct[A, B](
      left: Pipeline[A],
      right: Pipeline[B]
  ) extends Pipeline[(A, B)]

  final case class PipelineFilter[A](source: Pipeline[A], f: A => Boolean)
      extends Pipeline[A]

  final case class PipelinePure[A](value: A) extends Pipeline[A]

  final case class PipelineFromIterator[A](iterator: Iterator[A])
      extends Pipeline[A]

  def apply[A](a: A): Pipeline[A] =
    PipelinePure(a)

  def fromIterator[A](iterator: Iterator[A]): Pipeline[A] =
    PipelineFromIterator(iterator)
}

/** Notes: */
// Any transformation on an algebraic data type
// can be implemented using structural recursion
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
