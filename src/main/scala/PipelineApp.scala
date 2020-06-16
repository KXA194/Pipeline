object PipelineApp {

  /** A randomly made-up function so I can use `flatMap` */
  def explosiveFunction(x: Int): Pipeline[Int] =
    Pipeline.fromIterator(Iterator(x * 2, x * 3, x * 4))

  // First, create a Pipeline with 4 Int elements.
  val p1: Pipeline[Int] = Pipeline.fromIterator(Iterator(1, 2, 3, 4))
  // Do two sequencing operations on the Pipeline.
  val p2: Pipeline[Int] = p1.map(_ * 10).flatMap(x => explosiveFunction(x))
  // Actually run the pipeline to obtain final result.
  val r1: Int = p2.foldLeft(0)(_ + _)

  // Split the sequencing operations into two separate values (just to see what does each value return).
  val s1: Pipeline[Int] = p1.map(_ * 10)
  val s2: Pipeline[Int] = s1.flatMap(x => explosiveFunction(x))

  def main(args: Array[String]): Unit = {
    println(p1)
    println(p2.toString)
    println(r1)
    println(s1)
    println(s2)
  }
}
