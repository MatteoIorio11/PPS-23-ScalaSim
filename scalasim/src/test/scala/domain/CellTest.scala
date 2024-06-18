package domain

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*
import domain.Dimensions.*
import domain.Cell.*

class CellTest extends AnyFunSuite:

    test("Creation of a new Cell with null elements should return an error"):
        val error = intercept[RuntimeException](Cell2D(null, null))
        error shouldBe a[RuntimeException]

  
