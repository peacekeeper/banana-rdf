package org.w3.banana

import org.scalatest._
import org.scalatest.matchers.MustMatchers
import org.joda.time.DateTime
import scalaz._

abstract class CommonBindersTest[Rdf <: RDF]()(implicit diesel: Diesel[Rdf])
    extends WordSpec with MustMatchers {

  import diesel._
  import ops._

  "serializing and deserialiazing Joda DateTime" in {
    import DateTimeBinder._
    val dateTime = DateTime.now()
    fromTypedLiteral(toTypedLiteral(dateTime)).getOrElse(sys.error("problem")).compareTo(dateTime) must be(0)
  }

  "serializing and deserializing a List of simple nodes" in {
    val binder = PointedGraphBinder[Rdf, List[Int]]
    val list = List(1, 2, 3)
    binder.fromPointedGraph(binder.toPointedGraph(list)) must be === (Success(list))
  }

  "serializing and deserializing a List of complex types" in {
    val binder = implicitly[PointedGraphBinder[Rdf, List[List[Int]]]]
    val list = List(List(1, 2), List(3))
    binder.fromPointedGraph(binder.toPointedGraph(list)) must be === (Success(list))
  }

  "serializing and deserializing a Tuple2" in {
    val binder = PointedGraphBinder[Rdf, (Int, String)]
    val tuple = (42, "42")
    binder.fromPointedGraph(binder.toPointedGraph(tuple)) must be === (Success(tuple))
  }

  "serializing and deserializing a Map" in {
    val binder = PointedGraphBinder[Rdf, Map[String, List[Int]]]
    val map = Map("1" -> List(1, 2, 3), "2" -> List(4, 5))
    binder.fromPointedGraph(binder.toPointedGraph(map)) must be === (Success(map))
    binder.fromPointedGraph(binder.toPointedGraph(Map.empty)) must be === (Success(Map.empty))
  }

  "serializing and deserializing an Either" in {
    val binder = PointedGraphBinder[Rdf, Either[String, List[Int]]]
    val StringPGBinder = PointedGraphBinder[Rdf, String]
    val left = Left("foo")
    val right = Right(List(1, 2, 3))
    binder.fromPointedGraph(binder.toPointedGraph(left)) must be === (Success(left))
    binder.fromPointedGraph(binder.toPointedGraph(right)) must be === (Success(right))
    binder.fromPointedGraph(StringPGBinder.toPointedGraph("foo")) must be('failure)
  }

}
