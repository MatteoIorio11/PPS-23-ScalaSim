package domain.automaton

import domain.base.Dimensions.Dimension
import domain.base.Dimensions.TwoDimensionalSpace
import domain.base.Position
import domain.automaton.Cell
import domain.automaton.ParametricCell
import domain.automaton.Neighbour
import domain.automaton.ParametricNeighbour
import CellularAutomaton.State

/**
  * A generic rule for specifying the behaviour of a [[CellularAutomaton]].
  * A rule is defined by a generic transformation function and an
  * initial state that this rule must be matched against. The rule
  * logic then is composed by the a matchin state and the logic
  * defined inside the input function.
  * 
  * @param I the input type for the transformation function.
  * @param O the output type for the transformation funtcion.
  * @param P the type of the matcher used to match this rule against an object of type [[P]].
  */
trait Rule[I, O, P]:
   /**
     * Generic transformation function that takes an object of type [[I]]
     * and returns an object of type [[O]].
     *
     * @param in the input object of type [[I]].
     * @return the output object of type [[O]].
     */
   def tFunc(in: I): O
   
   /**
     * Runner for the transformation function specified.
     *
     * @param ca the input object of type [[I]]
     * @return the output object of type [[O]] after applying the function.
     */
   def applyTransformation(ca: I): O = tFunc(ca)
   
   /**
     * The matcher for this rule represented by an object of type [[P]] which
     * compose the predicate to be tested when it comes to rule matching.
     *
     * @return the matcher composed by a state (or more) boxed in an object of type [[B]]
     */
   def matcher: Option[P] = Option.empty

/**
  * [[Rule]] that matches multiple rules
  * 
  * @param I this rule input type.
  * @param O this rule output type after applying the transformation.
  */
trait MultipleStateRule[I, O] extends Rule[I, O, Iterable[State]]

/**
  * [[Rule]] that matches against a [[ParametricRule]] that satisfy
  * a given predicate (defined in [[ParametricRule.matcher]]).
  *
  * @param I this rule input type.
  * @param O this rule output type after applying the transformation.
  */
trait ParametricRule[I, O, T, D <: Dimension] extends Rule[I, O, (ParametricCell[D, T]) => Boolean]

/**
  * A Neighbor rule is a [[Rule]] based on neighbors' states of a given
  * cell. Transformation functions of this rules should map
  * a D dimensional [[Neighbour]] into a D dimensional [[Cell]] with
  * (hopefully) a mutated state.
  *
  * @param D the dimension of the space.
  */
trait NeighbourRule[D <: Dimension] extends Rule[Neighbour[D], Cell[D], State]


/**
  * A [[ParametricRule]] that represents a rule for a [[ParametricNeighbour]].
  * The behavior of this rule is equals to a standard [[NeighbourRule]], the only
  * difference is in [[State]] of the handled cells.
  */
trait ParametricNeighbourRule[D <: Dimension, T] extends ParametricRule[ParametricNeighbour[D, T], ParametricCell[D, T], T, D]

/**
  * Companion object for a generic [[NeighbourRule]].
  */
object NeighbourRule:
   /**
     * Creates a [[D]] dimensional [[NeighbourRule]] that matches the given [[State]] [[s]], and
     * if the state matches, the transformation function [[f]] can be applied.
     *
     * @param state the [[State]] that this rule must match in order to be applied.
     * @param f the transformation funtcion body that (hopefully) mutates [[Neighbour]] center state.
     * @return a new [[NeigbourRule]] with the specified behaviour.
     */
   def apply[D <: Dimension](state: Option[State])(f: Neighbour[D] => Cell[D]): NeighbourRule[D] = new NeighbourRule[D]:
      override def tFunc(n: Neighbour[D]): Cell[D] = f(n)
      override def matcher: Option[State] = state
  
object ParametricNeighbourRule:
  def apply[D <: Dimension, T]
    (matchingCondition: ParametricCell[D, T] => Boolean)
    (f: ParametricNeighbour[D, T] => ParametricCell[D, T]): ParametricNeighbourRule[D, T] =
    new ParametricNeighbourRule[D, T]:
      override def tFunc(n: ParametricNeighbour[D, T]): ParametricCell[D, T] = f(n)
      override def matcher: Option[ParametricCell[D, T] => Boolean] = Some(matchingCondition)

/**
  * Various utility functions for both [[NeighbourRule]]s and [[Neighbour]]s objects.
  * Most of this methods are used when specifying [[NeighbourRule]]s' behaviours.
  */
object NeighborRuleUtility:
   import PositionArithmeticOperations.*

   enum RelativePositions(x: Int, y: Int):
      case TopLeft      extends RelativePositions(-1, -1)
      case TopCenter    extends RelativePositions(-1, 0)
      case TopRight     extends RelativePositions(-1, 1)
      case CenterLeft   extends RelativePositions(0, -1)
      case Center       extends RelativePositions(0, 0)
      case CenterRight  extends RelativePositions(0, 1)
      case BottomLeft   extends RelativePositions(1, -1)
      case BottomCenter extends RelativePositions(1, 0)
      case BottomRight  extends RelativePositions(1, 1)

      def coordinates: List[Int] = List(x, y)

      def toPosition = Position(coordinates.toArray*)

   /**
     * Utility for configuring a certain neighbourhood placement inside a [[D]] dimensional space.
     */
   trait NeighbourhoodLocator[D <: Dimension]:
      /**
        * 
        * @return the neighbourhood placement in terms of relative positions with respect to the neighbourhood center.
        */
      def relativeNeighboursLocations: Iterable[Position[D]]

      /**
        * Returns a set of absolute positions (also negative positions) computed on top of the neighbourhood
        * placement configured with this object, with respect to the provied center.
        * 
        * @param center the central [[Position]] of this neighbourhood expressed in absolute position.
        * @return the neigbours [[Position]]s computed with the provided center position.
        */
      def absoluteNeighboursLocations(center: Position[D]): Iterable[Position[D]] = relativeNeighboursLocations.map(c => center + c)

   /**
     * Collection of arithmetic operations with D dimensional [[Position]]s.
     */
   object PositionArithmeticOperations:
      extension[D <: Dimension] (p: Position[D])
         private def elementWiseFunc(other: Position[D])(func: (Int, Int) => Int): Position[D] =
            Position(((p.coordinates zip other.coordinates) map { case (a, b) => func(a, b)}).toArray*)

         /**
           * Elementwise plus operator among two positions inside with a number [[D]] coorindates.
           *
           * @param other the other position to apply elementiwise sum.
           * @return a new position representing the application of elementwise sum.
           */
         def +(other: Position[D]): Position[D] = elementWiseFunc(other)(_ + _)

         /**
           * Elementwise minus operator among two positions inside with a number [[D]] coorindates.
           *
           * @param other the other position to apply elementiwise minus.
           * @return a new position representing the application of elementwise minus.
           */
         def -(other: Position[D]): Position[D] = elementWiseFunc(other)(_ - _)

         /**
           * Elementwise plus operator among a this position and an integer [[n]].
           *
           * @param n the integer used for applying an elementwise sum with this position.
           * @return a new position representing the application of elementwise sum with an integer.
           */
         def +(n: Int): Position[D] = Position(p.coordinates.map(_ + n).toArray*)

         /**
           * Elementwise minus operator among a this position and an integer [[n]].
           *
           * @param n the integer used for applying an elementwise minus with this position.
           * @return a new position representing the application of elementwise minus with an integer.
           */
         def -(n: Int): Position[D] = Position(p.coordinates.map(_ - n).toArray*)

   /**
     * [[NeighbourhoodLocator]] in a two dimensional space representing a full circle of radius one
     * starting from a central position.
     */
   given circleNeighbourhoodLocator: NeighbourhoodLocator[TwoDimensionalSpace] = new NeighbourhoodLocator[TwoDimensionalSpace]:
      override def relativeNeighboursLocations: Iterable[Position[TwoDimensionalSpace]] =
         import RelativePositions.*
         List(
            TopLeft,
            TopCenter,
            TopRight,
            CenterLeft,
            CenterRight,
            BottomLeft,
            BottomCenter,
            BottomRight,
            ).map(p => Position(p.coordinates.toArray*))

   /**
     * Returns a [[NeighbourhoodLocator]] in a two dimensional space representing configured
     * for handling a circular neighbourhood of the provided radius starting from a central
     * position.
     *
     * @param radius of the neighbourhood.
     * @return a [[NeighbourhoodLocator]] for handling circular neighbourhoods with a given [[radius]].
     */
   def getCircularNeighbourhoodPositions(radius: Int = 1): NeighbourhoodLocator[TwoDimensionalSpace] =
      val center = Position(radius, radius)
      var neighbours: List[Position[TwoDimensionalSpace]] = List.empty
      for (i <- 0 until (2 * radius + 1))
         for (j <- 0 until (2 * radius + 1))
            neighbours = neighbours :+ Position(i, j)
      new NeighbourhoodLocator[TwoDimensionalSpace]:
         override def relativeNeighboursLocations: Iterable[Position[TwoDimensionalSpace]] =
            neighbours.filter(_ != center) map (_ - radius)

   /**
     * Returns a list of [[D]] dimensional [[Cell]]s inside a [[Neighbour]] with the provided [[State]].
     *
     * @param state to be used to match neighbours state.
     * @param neighbours neighbourhood to look for matching cells with the provided [[State]].
     * @return the neighbours of this neighbourhood that have the given [[State]].
     */
   def getNeighboursWithState[D <: Dimension](state: State, neighbours: Neighbour[D]): List[Cell[D]] =
      neighbours.neighbourhood.filter(_.state == state)
