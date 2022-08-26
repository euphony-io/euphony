package co.euphony.util;

import java.util.*

open class EuCodec {
	private val Base40 = StringBuilder(BASE40)

	init {
		Base40.append('\u0000')
		Base40.append(".:-zyxwvutsrqponmlkjihgfedcba9876543210")
		Arrays.fill(base40Index, (-1).toByte())
		for (i in Base40.indices)
			base40Index[Base40[i].code] = i.toByte()
	}

	companion object {
		private const val BASE40 = 40
		protected var base40Index = ByteArray(256)
	}
}
