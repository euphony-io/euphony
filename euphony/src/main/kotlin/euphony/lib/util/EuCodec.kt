package euphony.lib.util;

import java.util.Arrays;

open class EuCodec {
	private val chars = StringBuilder(BASE40)

	init {
		chars.append('\u0000')
		run {
			var ch = 'a'
			while (ch <= 'z') {
				chars.append(ch)
				ch++
			}
		}
		var ch = '0'
		while (ch <= '9') {
			chars.append(ch)
			ch++
		}
		chars.append("-:.")
		Arrays.fill(base40Index, (-1).toByte())
		for (i in 0 until chars.length)
			base40Index[chars[i] as Int] = i as Byte
	}

	companion object {
		private val BASE40 = 40
		var base40Index = ByteArray(256)
	}
}