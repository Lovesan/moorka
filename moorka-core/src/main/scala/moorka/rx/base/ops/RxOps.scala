package moorka.rx.base.ops

import moorka.rx.base._

/**
 * @author Aleksey Fomkin <aleksey.fomkin@gmail.com>
 */
final class RxOps[A](val self: Rx[A]) extends AnyVal{

  def until(f: A ⇒ Boolean): Rx[Unit] = {
    self >>= { x ⇒
      if (!f(x)) Killer
      else Dummy
    }
  }

  def map[B](f: A ⇒ B): Rx[B] = {
    self >>= { x ⇒
      Val(f(x))
    }    
  }

  def zip[B](wth: Rx[B]): Rx[(A, B)] = {
    self >>= { a ⇒
      wth >>= { b ⇒
        Val((a, b))
      }
    }
  }

  def drop(num: Int): Rx[A] = {
    var drops = 0
    self >>= { x ⇒
      if (drops < num) {
        drops += 1
        Dummy
      }
      else {
        Val(x)
      }
    }
  }

  def take(num: Int): Rx[Seq[A]] = {
    val channel = Channel[Seq[A]]()
    val seq = collection.mutable.Buffer[A]()
    self foreach { value ⇒
      seq += value
      if (seq.length == num) {
        channel.update(Seq(seq:_*))
        seq.remove(0, seq.length)
      }
    }
    channel
  }

  // TODO this code doesn't work
  def fold[B](z: B)(op: (B, A) => B): Rx[B] = {
    val rx = Var(z)
    rx pull {
      rx >>= { b =>
        self >>= { a =>
          Val(op(b, a))
        }
      }
    }
    rx
  }

  def or[B](b: Rx[B]): Rx[Either[A, B]] = {
    val rx = Channel[Either[A, B]]()
    val left: Rx[Either[A, B]] = self >>= (x ⇒ Val(Left(x)))
    val right: Rx[Either[A, B]] = b >>= (x ⇒ Val(Right(x)))
    rx.pull(left)
    rx.pull(right)
    rx
  }

//  @deprecated("Use foreach() instead subscribe()", "0.4.0")
//  def subscribe[U](f: A ⇒ U): Rx[Unit] = self.foreach(f)
//
//  @deprecated("Use foreach() instead observe()", "0.4.0")
//  def observe[U](f: ⇒ U): Rx[Unit] = self.foreach(_ ⇒ f)
}
