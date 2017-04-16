package uk.ac.manchester.dstoolkit.service.impl.query.queryparser;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;

public class ANTLRNoCaseStringStream  extends ANTLRStringStream 
{
    public ANTLRNoCaseStringStream(String string) 
    {
        super(string);
    }

    public int LA(int i) 
    {
        int r = i;
        if ( r==0 ) {
            return 0; // undefined
        }
        if ( r<0 ) {
            r++; // e.g., translate LA(-1) to use offset 0
        }

        if ( (p+r-1) >= n ) {

            return CharStream.EOF;
        }
        return Character.toUpperCase(data[p+i-1]);
    }
}