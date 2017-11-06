package com.roamtech.telephony.roamapp.util;

/**
 * Created by admin03 on 2016/8/11.
 */
public class Utf8StringBuffer extends Utf8Appendable
{
    final StringBuffer _buffer;

    public Utf8StringBuffer()
    {
        super(new StringBuffer());
        _buffer = (StringBuffer)_appendable;
    }

    public Utf8StringBuffer(int capacity)
    {
        super(new StringBuffer(capacity));
        _buffer = (StringBuffer)_appendable;
    }

    @Override
    public int length()
    {
        return _buffer.length();
    }

    @Override
    public void reset()
    {
        super.reset();
        _buffer.setLength(0);
    }

    public StringBuffer getStringBuffer()
    {
        checkState();
        return _buffer;
    }

    @Override
    public String toString()
    {
        checkState();
        return _buffer.toString();
    }
}