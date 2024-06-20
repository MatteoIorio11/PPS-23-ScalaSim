package domain.engine

import domain.Environment.Environment
import domain.base.Dimensions.Dimension
import domain.Environment.*

trait Engine[D <: Dimension, I, O]:
    def environment: Environment[D, I, O]
    def nextIteration: Matrix