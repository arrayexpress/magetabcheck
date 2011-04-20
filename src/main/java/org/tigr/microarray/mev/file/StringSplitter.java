package org.tigr.microarray.mev.file;

/*******************************************************************************
 * Copyright (c) 1999-2005 The Institute for Genomic Research (TIGR).
 * Copyright (c) 2005-2008, the Dana-Farber Cancer Institute (DFCI), 
 * J. Craig Venter Institute (JCVI) and the University of Washington.
 * All rights reserved.
 *******************************************************************************/
/*
 * $RCSfile: StringSplitter.java,v $
 * $Revision: 1.3 $
 * $Date: 2005-03-10 15:39:39 $
 * $Author: braistedj $
 * $State: Exp $
 */

import java.util.NoSuchElementException;

public class StringSplitter {
    
    private String str;
    private char delimiter;
    
    private int curPosition;
    private int maxPosition;
    
    /**
     * Constructs a <code>StringSplitter</code> with specified delimiter.
     */
    public StringSplitter(char delimiter) {
	init("", delimiter);
    }
    
    /**
     * Initializes this <code>StringSplitter</code> with a new data.
     */
    public void init(String str) {
	init(str, this.delimiter);
    }
    
    /**
     * Initializes this <code>StringSplitter</code> with a new data and delimeter.
     */
    private void init(String str, char delimiter) {
	this.str = str;
	this.delimiter = delimiter;
	curPosition = 0;
	maxPosition = str.length();
    }
    
    /**
     * Skips ahead from startPos and returns the index of the next delimiter
     * character encountered, or maxPosition if no such delimiter is found.
     */
    private int scanToken(int startPos) {
	int position = startPos;
	while (position < maxPosition) {
	    char c = str.charAt(position);
	    if (c == delimiter)
		break;
	    position++;
	}
	return position;
    }
    
    /**
     * Tests if there are more tokens available from this tokenizer's string.
     * If this method returns <tt>true</tt>, then a subsequent call to
     * <tt>nextToken</tt> with no argument will successfully return a token.
     *
     * @return  <code>true</code> if and only if there is at least one token
     *          in the string after the current position; <code>false</code>
     *          otherwise.
     */
    public boolean hasMoreTokens() {
	return curPosition < maxPosition;
    }
    
    /**
     * Returns the next token from this string tokenizer.
     *
     * @return     the next token from this string tokenizer.
     * @exception  NoSuchElementException if there are no more tokens in this
     *             tokenizer's string.
     */
    public String nextToken() {
	if (curPosition >= maxPosition)
	    throw new NoSuchElementException("There are no more tokens!");
	int start = curPosition;
	curPosition = scanToken(curPosition);
	String result = str.substring(start, curPosition);
	curPosition++;
	return result;
    }
    
    /**
     * Returns the number of tokens.
     */
    public int countTokens() {
	int count = 0;
	int pos = 0;
	while (pos < this.maxPosition) {
	    if (str.charAt(pos) == this.delimiter)
		count++;
	    pos++;
	}
	return count;
    }
    
    /**
     *  Passes the given count of tokens.
     */
    public final void passTokens(int count) {
	for (int i = count; --i >= 0;) {
	    curPosition = scanToken(curPosition);
	    curPosition++;
	}
    }
    
    /**
     * Returns the next token value as an integer.
     */
    public final int nextIntToken() {
	return nextIntToken(0);
    }
    
    /**
     * Returns the next token value as an integer with specifed default value.
     */
    public final int nextIntToken(int defValue) {
	String token = nextToken();
	try {
	    return Integer.parseInt(token);
	} catch (NumberFormatException e) {
	    return defValue;
	}
    }
    
    /**
     * Returns the next token value as a long.
     */
    public final long nextLongToken() {
	return nextLongToken(0);
    }
    
    /**
     * Returns the next token value as a double rounded to long.
     */
    public final long nextLongToken(boolean rounded) {
	return Math.round(nextDoubleToken(0));
    }
    
    /**
     * Returns the next token value as a long with specifed default value.
     */
    public final long nextLongToken(long defValue) {
	String token = nextToken();
	try {
	    return Long.parseLong(token);
	} catch (NumberFormatException e) {
	    return defValue;
	}
    }
    
    /**
     * Returns the next token value as a double with specifed default value.
     */
    public final double nextDoubleToken(double defValue) {
	String token = nextToken();
	try {
	    return Double.parseDouble(token);
	} catch (NumberFormatException e) {
	    return defValue;
	}
    }
    
    /**
     * Returns the next token value as a float with specifed default value.
     */
    public final float nextFloatToken(float defValue) {
	String token = nextToken();
	try {
	    return Float.parseFloat(token);
	} catch (NumberFormatException e) {
	    return defValue;
	}
    }
}
