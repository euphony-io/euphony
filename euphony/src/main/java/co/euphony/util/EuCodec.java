package co.euphony.util;

import java.util.Arrays;

public class EuCodec {
	private static final int BASE40 = 40;
	private StringBuilder Base40 = new StringBuilder(BASE40);
	protected static byte[] base40Index = new byte[256];
	{
		Base40.append('\0');
		Base40.append(".:-zyxwvutsrqponmlkjihgfedcba9876543210");
		Arrays.fill(base40Index, (byte) -1);
		for(byte i = 0; i < Base40.length(); i++)
			base40Index[Base40.charAt(i)] = i;
	}
}
