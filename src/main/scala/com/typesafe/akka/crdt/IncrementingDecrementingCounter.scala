/**
 * Copyright (C) 2009-2013 Typesafe Inc. <http://www.typesafe.com>
 */

package com.typesafe.akka.crdt

/**
 * Implements a CRDT 'Increment/Decrement Counter' also called a 'PN-Counter'.
 *
 * PN-Counters allow the counter to be decreased by tracking the
 * increments (P) separate from the decrements (N), both represented as
 * internal G-Counters.  Merge is handled by merging the internal P and N
 * counters. The value of the counter is the value of the P counter minus
 * the value of the N counter.
 */
case class IncrementingDecrementingCounter(
  private[crdt] val increments: IncrementingCounter = IncrementingCounter(),
  private[crdt] val decrements: IncrementingCounter = IncrementingCounter()) extends CRDT {

  def value: Int = increments.value - decrements.value

  def +(node: String, delta: Int = 1): IncrementingDecrementingCounter = {
    if (delta < 0) this - (node, delta)
    else IncrementingDecrementingCounter(increments + (node, delta), decrements)
  }

  def -(node: String, delta: Int = 1): IncrementingDecrementingCounter =
    IncrementingDecrementingCounter(increments, decrements + (node, Math.abs(delta)))

  def merge(that: IncrementingDecrementingCounter): IncrementingDecrementingCounter =
    IncrementingDecrementingCounter(that.increments.merge(this.increments), that.decrements.merge(this.decrements))
}
