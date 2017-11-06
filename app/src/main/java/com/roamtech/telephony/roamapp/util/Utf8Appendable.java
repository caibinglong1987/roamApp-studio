package com.roamtech.telephony.roamapp.util;

import java.io.IOException;

/**
 * Created by admin03 on 2016/8/11.
 */
public abstract class Utf8Appendable
{
    public static final char REPLACEMENT = '\ufffd';
    private static final int UTF8_ACCEPT = 0;
    private static final int UTF8_REJECT = 12;

    protected final Appendable _appendable;
    protected int _state = UTF8_ACCEPT;

    private static final byte[] BYTE_TABLE =
            {
                    // The first part of the table maps bytes to character classes that
                    // to reduce the size of the transition table and create bitmasks.
                    0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,  0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                    0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,  0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                    0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,  0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                    0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,  0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                    1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,  9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,
                    7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,  7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,
                    8,8,2,2,2,2,2,2,2,2,2,2,2,2,2,2,  2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,
                    10,3,3,3,3,3,3,3,3,3,3,3,3,4,3,3, 11,6,6,6,5,8,8,8,8,8,8,8,8,8,8,8
            };

    private static final byte[] TRANS_TABLE =
            {
                    // The second part is a transition table that maps a combination
                    // of a state of the automaton and a character class to a state.
                    0,12,24,36,60,96,84,12,12,12,48,72, 12,12,12,12,12,12,12,12,12,12,12,12,
                    12, 0,12,12,12,12,12, 0,12, 0,12,12, 12,24,12,12,12,12,12,24,12,24,12,12,
                    12,12,12,12,12,12,12,24,12,12,12,12, 12,24,12,12,12,12,12,12,12,24,12,12,
                    12,12,12,12,12,12,12,36,12,36,12,12, 12,36,12,12,12,12,12,36,12,36,12,12,
                    12,36,12,12,12,12,12,12,12,12,12,12
            };

    private int _codep;

    public Utf8Appendable(Appendable appendable)
    {
        _appendable = appendable;
    }

    public abstract int length();

    protected void reset()
    {
        _state = UTF8_ACCEPT;
    }

    public void append(byte b)
    {
        try
        {
            appendByte(b);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void append(byte[] b, int offset, int length)
    {
        try
        {
            int end = offset + length;
            for (int i = offset; i < end; i++)
                appendByte(b[i]);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public boolean append(byte[] b, int offset, int length, int maxChars)
    {
        try
        {
            int end = offset + length;
            for (int i = offset; i < end; i++)
            {
                if (length() > maxChars)
                    return false;
                appendByte(b[i]);
            }
            return true;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
    public static String toHexString(byte b)
    {
        return toHexString(new byte[]{b}, 0, 1);
    }
    public static String toHexString(byte[] b)
    {
        return toHexString(b, 0, b.length);
    }
    /* ------------------------------------------------------------ */
    public static String toHexString(byte[] b,int offset,int length)
    {
        StringBuilder buf = new StringBuilder();
        for (int i=offset;i<offset+length;i++)
        {
            int bi=0xff&b[i];
            int c='0'+(bi/16)%16;
            if (c>'9')
                c= 'A'+(c-'0'-10);
            buf.append((char)c);
            c='0'+bi%16;
            if (c>'9')
                c= 'a'+(c-'0'-10);
            buf.append((char)c);
        }
        return buf.toString();
    }

    /* ------------------------------------------------------------ */
    public static byte[] fromHexString(String s)
    {
        if (s.length()%2!=0)
            throw new IllegalArgumentException(s);
        byte[] array = new byte[s.length()/2];
        for (int i=0;i<array.length;i++)
        {
            int b = Integer.parseInt(s.substring(i*2,i*2+2),16);
            array[i]=(byte)(0xff&b);
        }
        return array;
    }
    protected void appendByte(byte b) throws IOException
    {

        if (b > 0 && _state == UTF8_ACCEPT)
        {
            _appendable.append((char)(b & 0xFF));
        }
        else
        {
            int i = b & 0xFF;
            int type = BYTE_TABLE[i];
            _codep = _state == UTF8_ACCEPT ? (0xFF >> type) & i : (i & 0x3F) | (_codep << 6);
            int next = TRANS_TABLE[_state + type];

            switch(next)
            {
                case UTF8_ACCEPT:
                    _state=next;
                    if (_codep < Character.MIN_HIGH_SURROGATE)
                    {
                        _appendable.append((char)_codep);
                    }
                    else
                    {
                        for (char c : Character.toChars(_codep))
                            _appendable.append(c);
                    }
                    break;

                case UTF8_REJECT:
                    String reason = "byte "+toHexString(b)+" in state "+(_state/12);
                    _codep=0;
                    _state = UTF8_ACCEPT;
                    _appendable.append(REPLACEMENT);
                    throw new NotUtf8Exception(reason);

                default:
                    _state=next;

            }
        }
    }

    public boolean isUtf8SequenceComplete()
    {
        return _state == UTF8_ACCEPT;
    }

    public static class NotUtf8Exception extends IllegalArgumentException
    {
        public NotUtf8Exception(String reason)
        {
            super("Not valid UTF8! "+reason);
        }
    }

    protected void checkState()
    {
        if (!isUtf8SequenceComplete())
        {
            _codep=0;
            _state = UTF8_ACCEPT;
            try
            {
                _appendable.append(REPLACEMENT);
            }
            catch(IOException e)
            {
                throw new RuntimeException(e);
            }
            throw new NotUtf8Exception("incomplete UTF8 sequence");
        }
    }

    public String toReplacedString()
    {
        if (!isUtf8SequenceComplete())
        {
            _codep=0;
            _state = UTF8_ACCEPT;
            try
            {
                _appendable.append(REPLACEMENT);
            }
            catch(IOException e)
            {
                throw new RuntimeException(e);
            }
            Throwable th= new NotUtf8Exception("incomplete UTF8 sequence");
        }
        return _appendable.toString();
    }
}