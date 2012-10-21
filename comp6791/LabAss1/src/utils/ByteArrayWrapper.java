package utils;

import java.util.Arrays;

/*
 This class is copied from http://stackoverflow.com/questions/1058149/using-a-byte-array-as-hashmap-key-java
 It is used by the HashMap for inverted index dictionary
 A new function of compareTo is added to this class.

 */
public class ByteArrayWrapper implements Comparable<ByteArrayWrapper> {
	public byte[] data; // Changed from private to public

	public ByteArrayWrapper(byte[] data) {
		if (data == null) {
			//throw new NullPointerException();
		}
		this.data = data;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ByteArrayWrapper)) {
			return false;
		}
		return Arrays.equals(data, ((ByteArrayWrapper) other).data);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(data);
	}

	@Override
	public int compareTo(ByteArrayWrapper o) {
		for (int i = 0, j = 0; i < data.length && j < o.data.length; i++, j++) {
			int a = (data[i] & 0xff);
			int b = (o.data[j] & 0xff);
			if (a != b) {
				return a - b;
			}
		}
		return data.length - o.data.length;
	}
}
