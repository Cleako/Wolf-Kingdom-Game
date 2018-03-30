package com.legacy.server.util.rsc;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.legacy.server.model.Point;
import com.legacy.server.net.Packet;


public final class DataConversions {
	
	/**
     * The asynchronous logger.
     */
    private static final Logger LOGGER = LogManager.getLogger();

	public static StringEncryption encryption = new StringEncryption(StringEncryption.asByte(22, 22, 22, 22, 22, 22, 21,
			22, 22, 20, 22, 22, 22, 21, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 3, 8,
			22, 16, 22, 16, 17, 7, 13, 13, 13, 16, 7, 10, 6, 16, 10, 11, 12, 12, 12, 12, 13, 13, 14, 14, 11, 14, 19, 15,
			17, 8, 11, 9, 10, 10, 10, 10, 11, 10, 9, 7, 12, 11, 10, 10, 9, 10, 10, 12, 10, 9, 8, 12, 12, 9, 14, 8, 12,
			17, 16, 17, 22, 13, 21, 4, 7, 6, 5, 3, 6, 6, 5, 4, 10, 7, 5, 6, 4, 4, 6, 10, 5, 4, 4, 5, 7, 6, 10, 6, 10,
			22, 19, 22, 14, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22,
			22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22,
			22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22,
			22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22,
			22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 21, 22, 21, 22, 22, 22, 21, 22, 22));

	private static final char[] special_characters = "~`!@#$%^&*()_-+={}[]|\'\";:?><,./".toCharArray();

	public static String getDateFromMsec(long diffMSec) {
		int left = 0;
		int ss = 0;
		int mm = 0;
		int hh = 0;
		int dd = 0;
		left = (int) (diffMSec / 1000);
		ss = left % 60;
		left = (int) left / 60;
		if (left > 0) {
			mm = left % 60;
			left = (int) left / 60;
			if (left > 0) {
				hh = left % 24;
				left = (int) left / 24;
				if (left > 0) {
					dd = left;
				}
			}
		}
		String diff = Integer.toString(dd) + " days " + Integer.toString(hh) + " hours " + Integer.toString(mm)
				+ " minutes " + Integer.toString(ss) + " seconds";
		return diff;

	}

	private static char characters[] = { ' ', 'e', 't', 'a', 'o', 'i', 'h', 'n', 's', 'r', 'd', 'l', 'u', 'm', 'w', 'c',
			'y', 'f', 'g', 'p', 'b', 'v', 'k', 'x', 'j', 'q', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			' ', '!', '?', '.', ',', ':', ';', '(', ')', '-', '&', '*', '\\', '\'', '@', '#', '+', '=', '\243', '$',
			'%', '"', '[', ']' };

	private static SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd-MM-yy");
	private static MessageDigest md;
	private static Random rand = new Random();

	/**
	 * Creates an instance of the message digest used for creating md5 hashes
	 */
	static {
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			LOGGER.catching(e);
		}
	}

	/**
	 * returns the md5 hash of a string
	 */
	public static String md5(String s) {
		synchronized (md) {
			md.reset();
			md.update(s.getBytes());
			return toHex(md.digest());
		}
	}

	public static String toHex(byte[] bytes) {
		// change below to lower or uppercase X to control case of output
		return String.format("%0" + (bytes.length << 1) + "x", new BigInteger(1, bytes));
	}

	public static String formatString(String str, int length) {
		String s = "";
		for (int i = 0; i < length; i++)
			if (i >= str.length()) {
				s += " ";
			} else {
				char c = str.charAt(i);
				if (c >= 'a' && c <= 'z') {
					s += c;
				} else {
					if (c >= 'A' && c <= 'Z') {
						s += c;
					} else if (Arrays.binarySearch(special_characters, c) != -1) {
						s += c;
					} else {
						if (c >= '0' && c <= '9') {
							s += c;
						} else {
							s += '_';
						}
					}
				}
			}
		return s;
	}

	/**
	 * Calculates the average of all values in the array
	 */
	public static int average(int[] values) {
		int total = 0;
		for (int value : values) {
			total += value;
		}
		return (int) (total / values.length);
	}

	/**
	 * Decodes a byte array back into a string
	 */
	public static String byteToString(byte[] data, int offset, int length) {
		char[] buffer = new char[100];
		try {
			int k = 0;
			int l = -1;
			for (int i1 = 0; i1 < length; i1++) {
				int j1 = data[offset++] & 0xff;
				int k1 = j1 >> 4 & 0xf;
				if (l == -1) {
					if (k1 < 13) {
						buffer[k++] = characters[k1];
					} else {
						l = k1;
					}
				} else {
					buffer[k++] = characters[((l << 4) + k1) - 195];
					l = -1;
				}
				k1 = j1 & 0xf;
				if (l == -1) {
					if (k1 < 13) {
						buffer[k++] = characters[k1];
					} else {
						l = k1;
					}
				} else {
					buffer[k++] = characters[((l << 4) + k1) - 195];
					l = -1;
				}
			}
			boolean flag = true;
			for (int l1 = 0; l1 < k; l1++) {
				char c = buffer[l1];
				if (l1 > 4 && c == '@') {
					buffer[l1] = ' ';
				}
				if (c == '%') {
					buffer[l1] = ' ';
				}
				if (flag && c >= 'a' && c <= 'z') {
					buffer[l1] += '\uFFE0';
					flag = false;
				}
				if (c == '.' || c == '!' || c == ':') {
					flag = true;
				}
			}
			return new String(buffer, 0, k);
		} catch (Exception e) {
			return ".";
		}
	}

	public static final String getEncryptedString(Packet src, int limit) {

		try {
			int count = src.getSmart08_16();// correct.
			if (count > limit) {
				count = limit;
			}

			byte[] srct = src.readRemainingData();
			byte[] dest = new byte[count];
			encryption.decryptString(srct, dest, 0, 0, -1, count);

			String str = getStringFromBytes(dest, 0, count);
			return str;
		} catch (Exception var6) {
			return "Cabbage";
		}
	}

	public static final String getStringFromBytes(byte[] src, int offset, int count) {
		char[] dest = new char[count];
		int dh = 0;

		for (int i = 0; i < count; ++i) {
			int codepoint = 255 & src[offset + i];
			if (codepoint != 0) {
				if (codepoint >= 128 && codepoint < 160) {
					char c = StringUtil.specialCharLookup[codepoint - 128];
					if (c == 0) {
						c = '?';
					}
					codepoint = c;
				}
				dest[dh++] = (char) codepoint;
			}
		}
		return new String(dest, 0, dh);
	}

	public static String formatToRSCString(String s) {
		char[] charArray = s.toCharArray();
		boolean flag = true;
		for (int l1 = 0; l1 < charArray.length; l1++) {
			char c = charArray[l1];
			if (l1 > 4 && c == '@') {
				charArray[l1] = ' ';
			}
			if (c == '%') {
				charArray[l1] = ' ';
			}
			if (flag && c >= 'a' && c <= 'z') {
				charArray[l1] += '\uFFE0';
				flag = false;
			}
			if (c == '.' || c == '!' || c == ':') {
				flag = true;
			}
		}
		return new String(charArray);
	}

	/**
	 * returns the code used to represent the given character in our byte array
	 * encoding methods
	 */
	private static int getCharCode(char c) {
		for (int x = 0; x < characters.length; x++) {
			if (c == characters[x]) {
				return x;
			}
		}
		return 0;
	}

	private static byte getMobCoordOffset(int coord1, int coord2) {
		byte offset = (byte) (coord1 - coord2);
		if (offset < 0) {
			offset += 32;
		}
		return offset;
	}

	public static byte[] getMobPositionOffsets(Point p1, Point p2) {
		byte[] rv = new byte[2];
		rv[0] = getMobCoordOffset(p1.getX(), p2.getX());
		rv[1] = getMobCoordOffset(p1.getY(), p2.getY());
		return rv;
	}

	private static byte getObjectCoordOffset(int coord1, int coord2) {
		return (byte) (coord1 - coord2);
	}

	public static byte[] getObjectPositionOffsets(Point p1, Point p2) {
		byte[] rv = new byte[2];
		rv[0] = getObjectCoordOffset(p1.getX(), p2.getX());
		rv[1] = getObjectCoordOffset(p1.getY(), p2.getY());
		return rv;
	}

	/**
	 * Returns the random number generator
	 */
	public static Random getRandom() {
		return rand;
	}
	
	public static int random(int range) {
		int number = (int) (Math.random() * (range + 1));
		return number < 0 ? 0 : number;
	}

	/**
	 * Converts a usernames hash back to the username
	 */
	public static String hashToUsername(long l) {
		if (l < 0L)
			return "invalid_name";
		String s = "";
		while (l != 0L) {
			int i = (int) (l % 37L);
			l /= 37L;
			if (i == 0)
				s = " " + s;
			else if (i < 27) {
				if (l % 37L == 0L)
					s = (char) ((i + 65) - 1) + s;
				else
					s = (char) ((i + 97) - 1) + s;
			} else {
				s = (char) ((i + 48) - 27) + s;
			}
		}
		return s;
	}

	/**
	 * Checks if the given int is in the array
	 */
	public static boolean inArray(int[] haystack, int needle) {
		for (int option : haystack) {
			if (needle == option) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if the given point is in the array
	 */
	public static boolean inPointArray(Point[] haystack, Point needle) {
		for (Point option : haystack) {
			if (needle.getX() == option.getX() && needle.getY() == option.getY()) {
				return true;
			}
		}
		return false;
	}

	public static long IPToLong(String ip) {
		String[] octets = ip.split("\\.");
		long result = 0L;
		for (int x = 0; x < 4; x++) {
			result += Integer.parseInt(octets[x]) * Math.pow(256, 3 - x);
		}
		return result;
	}

	public static String IPToString(long ip) {
		String result = "0.0.0.0";
		for (int x = 0; x < 4; x++) {
			int octet = (int) (ip / Math.pow(256, 3 - x));
			ip -= octet * Math.pow(256, 3 - x);
			if (x == 0) {
				result = String.valueOf(octet);
			} else {
				result += ("." + octet);
			}
		}
		return result;
	}

	/**
	 * returns the max of the 2 values
	 */
	public static int max(int i1, int i2) {
		return i1 > i2 ? i1 : i2;
	}

	/**
	 * Returns true percent% of the time
	 */
	public static boolean percentChance(int percent) {
		return random(1, 100) <= percent;
	}

	/**
	 * returns a random number within the given bounds
	 */
	public static double random(double low, double high) {
		return high - (rand.nextDouble() * low);
	}

	/**
	 * returns a random number within the given bounds
	 */
	public static int random(int low, int high) {
		return low + rand.nextInt(high - low + 1);
	}

	/**
	 * returns a random number within the given bounds, but allows for certain
	 * values to be weighted
	 */
	public static int randomWeighted(int low, int dip, int peak, int max) {
		int total = 0;
		int probability = 100;
		int[] probArray = new int[max + 1];
		for (int x = 0; x < probArray.length; x++) {
			total += probArray[x] = probability;
			if (x < dip || x > peak) {
				probability -= 3;
			} else {
				probability += 3;
			}
		}
		int hit = random(0, total);
		total = 0;
		for (int x = 0; x < probArray.length; x++) {
			if (hit >= total && hit < (total + probArray[x])) {
				return x;
			}
			total += probArray[x];
		}
		return 0;
	}

	public static double round(double value, int decimalPlace) {
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return (bd.doubleValue());
	}

	public static int roundUp(double val) {
		return (int) Math.round(val + 0.5D);
	}

	/**
	 * Returns a ByteBuffer containing everything available from the given
	 * InputStream
	 */
	public static final ByteBuffer streamToBuffer(BufferedInputStream in) throws IOException {
		byte[] buffer = new byte[in.available()];
		in.read(buffer, 0, buffer.length);
		return ByteBuffer.wrap(buffer);
	}

	/**
	 * Encodes a string into a byte array
	 */
	public static byte[] stringToByteArray(String message) {
		byte[] buffer = new byte[100];
		if (message.length() > 80) {
			message = message.substring(0, 80);
		}
		message = message.toLowerCase();
		int length = 0; // what does the p.message use?
		int j = -1;
		for (int k = 0; k < message.length(); k++) {
			int code = getCharCode(message.charAt(k));
			if (code > 12) {
				code += 195;
			}
			if (j == -1) {
				if (code < 13)
					j = code;
				else
					buffer[length++] = (byte) code;
			} else if (code < 13) {
				buffer[length++] = (byte) ((j << 4) + code);
				j = -1;
			} else {
				buffer[length++] = (byte) ((j << 4) + (code >> 4));
				j = code & 0xf;
			}
		}
		if (j != -1) {
			buffer[length++] = (byte) (j << 4);
		}
		byte[] string = new byte[length];
		System.arraycopy(buffer, 0, string, 0, length);
		return string;
	}

	public static String timeFormat(long l) {
		return formatter.format(l);
	}

	public static String timeSince(long time) {
		int seconds = (int) ((System.currentTimeMillis() - time) / 1000);
		int minutes = (int) (seconds / 60);
		int hours = (int) (minutes / 60);
		int days = (int) (hours / 24);
		return days + " days " + (hours % 24) + " hours " + (minutes % 60) + " mins";
	}

	/**
	 * Converts a username to a unique hash
	 */
	public static long usernameToHash(String s) {
		s = s.toLowerCase();
		String s1 = "";
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c >= 'a' && c <= 'z')
				s1 = s1 + c;
			else if (c >= '0' && c <= '9')
				s1 = s1 + c;
			else
				s1 = s1 + ' ';
		}

		s1 = s1.trim();
		if (s1.length() > 12)
			s1 = s1.substring(0, 12);
		long l = 0L;
		for (int j = 0; j < s1.length(); j++) {
			char c1 = s1.charAt(j);
			l *= 37L;
			if (c1 >= 'a' && c1 <= 'z')
				l += (1 + c1) - 97;
			else if (c1 >= '0' && c1 <= '9')
				l += (27 + c1) - 48;
		}
		return l;
	}

	public static final byte[] stringToBytes(CharSequence str) {
		int len = str.length();
		byte[] out = new byte[len];

		for (int i = 0; i < len; ++i) {
			char c = str.charAt(i);
			if ((c <= 0 || c >= 128) && (c < 160 || c > 255)) {
				if (c != 8364) {
					if (c == 8218) {
						out[i] = -126;
					} else if (c != 402) {
						if (c != 8222) {
							if (c == 8230) {
								out[i] = -123;
							} else if (c != 8224) {
								if (c != 8225) {
									if (c != 710) {
										if (c == 8240) {
											out[i] = -119;
										} else if (c != 352) {
											if (c == 8249) {
												out[i] = -117;
											} else if (c == 338) {
												out[i] = -116;
											} else if (c != 381) {
												if (c != 8216) {
													if (c != 8217) {
														if (c == 8220) {
															out[i] = -109;
														} else if (c == 8221) {
															out[i] = -108;
														} else if (c == 8226) {
															out[i] = -107;
														} else if (c == 8211) {
															out[i] = -106;
														} else if (c == 8212) {
															out[i] = -105;
														} else if (c != 732) {
															if (c != 8482) {
																if (c != 353) {
																	if (c != 8250) {
																		if (c != 339) {
																			if (c != 382) {
																				if (c == 376) {
																					out[i] = -97;
																				} else {
																					out[i] = 63;
																				}
																			} else {
																				out[i] = -98;
																			}
																		} else {
																			out[i] = -100;
																		}
																	} else {
																		out[i] = -101;
																	}
																} else {
																	out[i] = -102;
																}
															} else {
																out[i] = -103;
															}
														} else {
															out[i] = -104;
														}
													} else {
														out[i] = -110;
													}
												} else {
													out[i] = -111;
												}
											} else {
												out[i] = -114;
											}
										} else {
											out[i] = -118;
										}
									} else {
										out[i] = -120;
									}
								} else {
									out[i] = -121;
								}
							} else {
								out[i] = -122;
							}
						} else {
							out[i] = -124;
						}
					} else {
						out[i] = -125;
					}
				} else {
					out[i] = -128;
				}
			} else {
				out[i] = (byte) c;
			}
		}

		return out;
	}
	
	public static String hmac(String hashType, String value, String key) {
		try {
			byte[] keyBytes = key.getBytes();
			SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "Hmac" + hashType);
			
			Mac mac = Mac.getInstance("Hmac" + hashType);
			mac.init(signingKey);
			
			byte[] rawHmac = mac.doFinal(value.getBytes());
			return new BigInteger(1, rawHmac).toString(16);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	public static String numberFormat(int i) {
		NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
	    String number = numberFormat.format(i);
		return number;
	}
}
