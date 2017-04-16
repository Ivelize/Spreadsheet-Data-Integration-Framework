import java.io.*;
import org.antlr.runtime.*;
import org.antlr.runtime.debug.DebugEventSocketProxy;

import uk.ac.manchester.dataspaces.service.impl.query.queryparser.*;


public class __Test__ {

    public static void main(String args[]) throws Exception {
        DataspacesSQLLexer lex = new DataspacesSQLLexer(new ANTLRFileStream("/Users/chedeler/Documents/dev/dataspaces/src/main/antlr3/uk/ac/manchester/dataspaces/service/impl/query/queryparser/output/__Test___input.txt", "UTF8"));
        CommonTokenStream tokens = new CommonTokenStream(lex);

        DataspacesSQLParser g = new DataspacesSQLParser(tokens, 49100, null);
        try {
            g.statement();
        } catch (RecognitionException e) {
            e.printStackTrace();
        }
    }
}