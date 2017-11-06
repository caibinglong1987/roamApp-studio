package com.roamtech.telephony.roamapp.util;

/**
 * Created by admin03 on 2016/8/11.
 */
public class Utf8StringBuilder extends Utf8Appendable
{
    final StringBuilder _buffer;

    public Utf8StringBuilder()
    {
        super(new StringBuilder());
        _buffer=(StringBuilder)_appendable;
    }

    public Utf8StringBuilder(int capacity)
    {
        super(new StringBuilder(capacity));
        _buffer=(StringBuilder)_appendable;
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

    public StringBuilder getStringBuilder()
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
