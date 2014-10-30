package mutibo;

import java.io.UnsupportedEncodingException;

/**
 *
 * @author Redacted
 */
public class HashUtils
{
	public static String MD5(String source)
	{
	   try 
	   {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			byte[] digest = md.digest(source.getBytes("UTF-8"));

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < digest.length; ++i) 
			{
				sb.append(Integer.toHexString((digest[i] & 0xFF) | 0x100).substring(1,3));
			}

			return sb.toString();
		} 
		catch (java.security.NoSuchAlgorithmException | UnsupportedEncodingException e)
		{
		}

		return null;
	}
	
}
