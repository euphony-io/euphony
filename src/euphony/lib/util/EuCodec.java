package euphony.lib.util;

import java.util.Arrays;

public class EuCodec {
	public static final int BASE40 = 40;
	public StringBuilder chars = new StringBuilder(BASE40);
	protected static byte[] base40Index = new byte[256];
	{
		chars.append('\0');
		for(char ch = 'a'; ch <= 'z'; ch++) chars.append(ch);
		for(char ch = '0'; ch <= '9'; ch++) chars.append(ch);
		chars.append("-:.");
		Arrays.fill(base40Index, (byte) -1);
		for(byte i = 0; i < chars.length(); i++)
			base40Index[chars.charAt(i)] = i;
	}
}
