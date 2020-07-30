package utils

import chisel3._
import chisel3.util._
import scala.math.min

object WordShift {
  def apply(data: UInt, wordIndex: UInt, step: Int) = (data << (wordIndex * step.U))
}

object MaskExpand {
 def apply(m: UInt) = Cat(m.asBools.map(Fill(8, _)).reverse)
}

object MaskData {
 def apply(oldData: UInt, newData: UInt, fullmask: UInt) = {
   (newData & fullmask) | (oldData & ~fullmask)
 }
}

object SignExt {
  def apply(a: UInt, len: Int) = {
    val aLen = a.getWidth
    val signBit = a(aLen-1)
    if (aLen == len) a else Cat(Fill(len - aLen, signBit), a)
  }
}

object ZeroExt {
  def apply(a: UInt, len: Int) = {
    val aLen = a.getWidth
    if (aLen == len) a else Cat(0.U((len - aLen).W), a)
  }
}

object Or {
  // Fill 1s from low bits to high bits
  def leftOR(x: UInt): UInt = leftOR(x, x.getWidth, x.getWidth)
  def leftOR(x: UInt, width: Integer, cap: Integer = 999999): UInt = {
    val stop = min(width, cap)
    def helper(s: Int, x: UInt): UInt =
      if (s >= stop) x else helper(s+s, x | (x << s)(width-1,0))
    helper(1, x)(width-1, 0)
  }

  // Fill 1s form high bits to low bits
  def rightOR(x: UInt): UInt = rightOR(x, x.getWidth, x.getWidth)
  def rightOR(x: UInt, width: Integer, cap: Integer = 999999): UInt = {
    val stop = min(width, cap)
    def helper(s: Int, x: UInt): UInt =
      if (s >= stop) x else helper(s+s, x | (x >> s))
    helper(1, x)(width-1, 0)
  }
}

object OneHot {
  def OH1ToOH(x: UInt): UInt = (x << 1 | 1.U) & ~Cat(0.U(1.W), x)
  def OH1ToUInt(x: UInt): UInt = OHToUInt(OH1ToOH(x))
  def UIntToOH1(x: UInt, width: Int): UInt = ~((-1).S(width.W).asUInt << x)(width-1, 0)
  def UIntToOH1(x: UInt): UInt = UIntToOH1(x, (1 << x.getWidth) - 1)
}
