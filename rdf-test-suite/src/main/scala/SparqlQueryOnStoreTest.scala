package org.w3.rdf

import org.w3.rdf.diesel._
import org.scalatest._
import org.scalatest.matchers._

abstract class SparqlQueryOnStoreTest[Rdf <: RDF, Sparql <: SPARQL](
  ops: RDFOperations[Rdf],
  dsl: Diesel[Rdf],
  rdfStore: RDFStore[Rdf],
  iso: GraphIsomorphism[Rdf],
  queryBuilder: SPARQLQueryBuilder[Rdf, Sparql],
  queryExecution: RDFStoreQuery[Rdf, Sparql]
) extends WordSpec with MustMatchers with BeforeAndAfterAll {

  import rdfStore._
  import iso._
  import ops._
  import dsl._
  import queryBuilder._
  import queryExecution._

  val store: Rdf#Store

  val foaf = FOAFPrefix(ops)

  val graph: Rdf#Graph = (
    bnode("betehess")
      -- foaf.name ->- "Alexandre".lang("fr")
      -- foaf.title ->- "Mr"
  ).graph

  val graph2: Rdf#Graph = (
    bnode("betehess")
      -- foaf.name ->- "Alexandre".lang("fr")
      -- foaf.knows ->- (
        uri("http://bblfish.net/#hjs")
          -- foaf.name ->- "Henry Story"
          -- foaf.currentProject ->- uri("http://webid.info/")
      )
  ).graph

  override def beforeAll(): Unit = {
    addNamedGraph(store, IRI("http://example.com/graph"), graph)
    addNamedGraph(store, IRI("http://example.com/graph2"), graph2)
  }

  "betehess must know henry" in {

    val query = AskQuery("""
prefix foaf: <http://xmlns.com/foaf/0.1/>

ASK {
  GRAPH <http://example.com/graph2> {
    [] foaf:knows <http://bblfish.net/#hjs>
  }
}
""")

    val alexKnowsHenry = executeAskQuery(store, query)

    alexKnowsHenry must be (true)

  }

}
