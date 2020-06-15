class Information {

  def flatMap[F[_], A, B](fa: F[A])(f: A => F[B]): F[B]

  def map[F[_], A, B](fa: F[A])(f: A => B): F[B]

  List(1, 2, 3).flatMap(x => List(-x))
    .flatMap(y => List(y * 2))
    .flatMap(z => List())
    .flatMap(a => List(a + 42))


  /**
   * Applicative, Semigroupal
   */
  def product[F[_], A, B](fa: F[A], fb: F[B]): F[(A, B)]

  /**
   * //  List[Int] List[String] = List[(It, String)]
   * product(List(1,2,3), List("a", "b", "c")) == List((1,2,3), ("a", "b", "c"))
   * List( (1, "a"), (2, "b"), (3, "c") )
   * //  aka zip!
   * *
   *
   */
  / Separation of description and action
    *// Substitution
    * x = 2
  * y = 3 x +15
  **
  *
  val x = 2
  *
  val y = 3 * x + 15
  *
  val y = 3 * 2 + 15
  *
  val y = 6 + 15
  *
  val y = 21
  **
  *
  def add42(x: Int): Int
  = x + 42
  **
  * add42 (5)
  * x +42 // x = 5
  *
  5 +42
  *
  47
  **
  * Expression: program text
  , can be evaluated to produce a value
  * Value: something in the computer 's memory
  , the result of evaluating an expression
    **
  * Analogy:
    * Expression
  = writing
  * Evaluation = reading
  * Value = understanding of the writing that exists in the reader
  's head
  **
  * Names / bindings / variables / assignment (last two generally used in imperative languages not functional languages)
  *
  val name = expression
  * Give the given name to the value that the expression evaluates to(within the current scope)
  **
  *
  val x = 1 + 1
  * x +4
  **
  *
  val x = 1 + 1
  *
  val x = 2 // bind x to the value 2 in the current scope
  * x +4 // x = 2
  *
  2 +4
  *
  6
  **
  *
  *
  val x = println("Hi!")
  *
  val y = x
  *
  val x = () // bind x to unit in the current scope
  *
  val y = x // x = ()
  *
  val y = ()
  **
  *
  val x = println("Hi!")
  *
  val y = println("Hi!")
  **
  * Side -effect is something that breaks substitution
    **
  * Separate describing what we want to happen
  with actually making it happen
    **
  *
  val x = () => println("Hi!")
  *
  val y = x
  **
  *
  val x = () => println("Hi!")
  *
  val y = () => println("Hi!")
  **
  *
  *// Much later
  , run stuff ! *// We can use substitution up to the point we actually run stuff
  **
  * x()
  * y()
  **
  *-------------
  *
  val x = println("Hi!")
  *
  val y = x
  *-------------
  *
  val x = println("Hi!")
  *
  val y = println("Hi!")
  *-------------
  *
  val x = Image.circle(100).draw()
  *
  val y = x
  *-------------
  *
  val x = Image.circle(100).draw()
  *
  val y = Image.circle(100).draw()
  */

}
