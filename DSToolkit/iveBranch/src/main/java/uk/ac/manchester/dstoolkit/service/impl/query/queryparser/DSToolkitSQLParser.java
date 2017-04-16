// $ANTLR 3.2 Sep 23, 2009 14:05:07 uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g 2016-05-04 16:59:44

package uk.ac.manchester.dstoolkit.service.impl.query.queryparser;


import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.BitSet;
import org.antlr.runtime.DFA;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.IntStream;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.MismatchedTokenException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.Parser;
import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.RewriteEarlyExitException;
import org.antlr.runtime.tree.RewriteRuleSubtreeStream;
import org.antlr.runtime.tree.RewriteRuleTokenStream;
import org.antlr.runtime.tree.TreeAdaptor;

public class DSToolkitSQLParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "SQL_STATEMENT", "QUERY", "SETOP", "ORDER", "SELECT_LIST", "FROM_LIST", "WHERE", "IN", "EXISTS", "BETWEEN", "GROUP_BY", "HAVING", "SUPER_ABSTRACT", "SUPER_LEXICAL", "SUPER_RELATIONSHIP", "FUNCTION", "NOT", "SET", "SUPER_LEXICAL_OF_SUPER_ABSTRACT", "RIGHT_OUTER_JOIN", "LEFT_OUTER_JOIN", "FULL_OUTER_JOIN", "JOIN", "IS_NULL", "UNION", "EXCEPT", "INTERSECT", "UNION_ALL", "EXCEPT_ALL", "INTERSECT_ALL", "BOUND", "ASC", "DESC", "AND", "OR", "ID", "INT", "FLOAT", "NUMERIC", "STRING", "WS", "';'", "'UNION'", "'ALL'", "'EXCEPT'", "'INTERSECT'", "'SELECT'", "'FROM'", "'WHERE'", "'GROUP BY'", "'HAVING'", "'DISTINCT'", "'('", "')'", "'*'", "','", "'AS'", "'ORDER'", "'BY'", "'.'", "'DATE'", "'TIMESTAMP'", "'TIME'", "'INTERVAL'", "'YEAR'", "'MONTH'", "'DAY'", "'HOUR'", "'MINUTE'", "'SECOND'", "'+'", "'-'", "'/'", "'NULL'", "'TRUE'", "'FALSE'", "'||'", "'RIGHT'", "'OUTER'", "'JOIN'", "'LEFT'", "'FULL'", "'INNER'", "'ON'", "'OR'", "'AND'", "'NOT'", "'IS'", "'IN'", "'BETWEEN'", "'EXISTS'", "'='", "'<>'", "'!='", "'<'", "'>'", "'>='", "'<='", "'SOME'", "'ANY'", "'LIKE'", "'DEFAULT'", "'@'"
    };
    public static final int FUNCTION=19;
    public static final int EXCEPT_ALL=32;
    public static final int FULL_OUTER_JOIN=25;
    public static final int NOT=20;
    public static final int SQL_STATEMENT=4;
    public static final int EXCEPT=29;
    public static final int EOF=-1;
    public static final int SUPER_LEXICAL=17;
    public static final int T__93=93;
    public static final int T__94=94;
    public static final int T__91=91;
    public static final int T__92=92;
    public static final int T__90=90;
    public static final int IS_NULL=27;
    public static final int T__99=99;
    public static final int T__98=98;
    public static final int BOUND=34;
    public static final int T__97=97;
    public static final int T__96=96;
    public static final int T__95=95;
    public static final int T__80=80;
    public static final int T__81=81;
    public static final int T__82=82;
    public static final int ASC=35;
    public static final int T__83=83;
    public static final int SUPER_LEXICAL_OF_SUPER_ABSTRACT=22;
    public static final int INT=40;
    public static final int NUMERIC=42;
    public static final int T__85=85;
    public static final int T__84=84;
    public static final int T__87=87;
    public static final int T__86=86;
    public static final int T__89=89;
    public static final int T__88=88;
    public static final int INTERSECT=30;
    public static final int WS=44;
    public static final int T__71=71;
    public static final int T__72=72;
    public static final int T__70=70;
    public static final int SELECT_LIST=8;
    public static final int OR=38;
    public static final int QUERY=5;
    public static final int SUPER_RELATIONSHIP=18;
    public static final int SETOP=6;
    public static final int T__76=76;
    public static final int T__75=75;
    public static final int T__74=74;
    public static final int T__73=73;
    public static final int T__79=79;
    public static final int T__78=78;
    public static final int T__77=77;
    public static final int T__68=68;
    public static final int T__69=69;
    public static final int T__66=66;
    public static final int WHERE=10;
    public static final int T__67=67;
    public static final int T__64=64;
    public static final int T__65=65;
    public static final int T__62=62;
    public static final int T__63=63;
    public static final int ORDER=7;
    public static final int SUPER_ABSTRACT=16;
    public static final int LEFT_OUTER_JOIN=24;
    public static final int FLOAT=41;
    public static final int ID=39;
    public static final int AND=37;
    public static final int T__61=61;
    public static final int T__60=60;
    public static final int T__55=55;
    public static final int T__56=56;
    public static final int T__57=57;
    public static final int T__58=58;
    public static final int T__51=51;
    public static final int T__52=52;
    public static final int IN=11;
    public static final int T__53=53;
    public static final int T__54=54;
    public static final int T__59=59;
    public static final int T__103=103;
    public static final int RIGHT_OUTER_JOIN=23;
    public static final int T__104=104;
    public static final int T__105=105;
    public static final int T__106=106;
    public static final int EXISTS=12;
    public static final int T__50=50;
    public static final int T__46=46;
    public static final int T__47=47;
    public static final int T__45=45;
    public static final int T__48=48;
    public static final int T__49=49;
    public static final int SET=21;
    public static final int T__102=102;
    public static final int HAVING=15;
    public static final int T__101=101;
    public static final int T__100=100;
    public static final int INTERSECT_ALL=33;
    public static final int JOIN=26;
    public static final int UNION=28;
    public static final int UNION_ALL=31;
    public static final int FROM_LIST=9;
    public static final int GROUP_BY=14;
    public static final int DESC=36;
    public static final int BETWEEN=13;
    public static final int STRING=43;

    // delegates
    // delegators


        public DSToolkitSQLParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public DSToolkitSQLParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return DSToolkitSQLParser.tokenNames; }
    public String getGrammarFileName() { return "uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g"; }


    protected void mismatch(IntStream input, int ttype, BitSet follow)
        throws RecognitionException
    { 
        throw new MismatchedTokenException(ttype, input);
    }

    public Object recoverFromMismatchedSet(IntStream input, RecognitionException e, BitSet follow)
        throws RecognitionException
    {
        throw e;
    }


    public static class statement_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "statement"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:106:1: statement : query_expression ( order_by )? ( ';' )? -> ^( SQL_STATEMENT query_expression ( order_by )? ) ;
    public final DSToolkitSQLParser.statement_return statement() throws RecognitionException {
        DSToolkitSQLParser.statement_return retval = new DSToolkitSQLParser.statement_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal3=null;
        DSToolkitSQLParser.query_expression_return query_expression1 = null;

        DSToolkitSQLParser.order_by_return order_by2 = null;


        CommonTree char_literal3_tree=null;
        RewriteRuleTokenStream stream_45=new RewriteRuleTokenStream(adaptor,"token 45");
        RewriteRuleSubtreeStream stream_query_expression=new RewriteRuleSubtreeStream(adaptor,"rule query_expression");
        RewriteRuleSubtreeStream stream_order_by=new RewriteRuleSubtreeStream(adaptor,"rule order_by");
        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:107:2: ( query_expression ( order_by )? ( ';' )? -> ^( SQL_STATEMENT query_expression ( order_by )? ) )
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:107:4: query_expression ( order_by )? ( ';' )?
            {
            pushFollow(FOLLOW_query_expression_in_statement236);
            query_expression1=query_expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_query_expression.add(query_expression1.getTree());
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:107:21: ( order_by )?
            int alt1=2;
            switch ( input.LA(1) ) {
                case 61:
                    {
                    alt1=1;
                    }
                    break;
            }

            switch (alt1) {
                case 1 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:0:0: order_by
                    {
                    pushFollow(FOLLOW_order_by_in_statement238);
                    order_by2=order_by();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_order_by.add(order_by2.getTree());

                    }
                    break;

            }

            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:107:31: ( ';' )?
            int alt2=2;
            switch ( input.LA(1) ) {
                case 45:
                    {
                    alt2=1;
                    }
                    break;
            }

            switch (alt2) {
                case 1 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:0:0: ';'
                    {
                    char_literal3=(Token)match(input,45,FOLLOW_45_in_statement241); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_45.add(char_literal3);


                    }
                    break;

            }



            // AST REWRITE
            // elements: order_by, query_expression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 107:36: -> ^( SQL_STATEMENT query_expression ( order_by )? )
            {
                // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:107:39: ^( SQL_STATEMENT query_expression ( order_by )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(SQL_STATEMENT, "SQL_STATEMENT"), root_1);

                adaptor.addChild(root_1, stream_query_expression.nextTree());
                // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:107:72: ( order_by )?
                if ( stream_order_by.hasNext() ) {
                    adaptor.addChild(root_1, stream_order_by.nextTree());

                }
                stream_order_by.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "statement"

    public static class query_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "query_expression"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:108:1: query_expression : query ( set_op query )* ;
    public final DSToolkitSQLParser.query_expression_return query_expression() throws RecognitionException {
        DSToolkitSQLParser.query_expression_return retval = new DSToolkitSQLParser.query_expression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        DSToolkitSQLParser.query_return query4 = null;

        DSToolkitSQLParser.set_op_return set_op5 = null;

        DSToolkitSQLParser.query_return query6 = null;



        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:109:2: ( query ( set_op query )* )
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:109:4: query ( set_op query )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_query_in_query_expression261);
            query4=query();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, query4.getTree());
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:109:10: ( set_op query )*
            loop3:
            do {
                int alt3=2;
                switch ( input.LA(1) ) {
                case 46:
                case 48:
                case 49:
                    {
                    alt3=1;
                    }
                    break;

                }

                switch (alt3) {
            	case 1 :
            	    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:109:11: set_op query
            	    {
            	    pushFollow(FOLLOW_set_op_in_query_expression264);
            	    set_op5=set_op();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) root_0 = (CommonTree)adaptor.becomeRoot(set_op5.getTree(), root_0);
            	    pushFollow(FOLLOW_query_in_query_expression267);
            	    query6=query();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, query6.getTree());

            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "query_expression"

    public static class set_op_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "set_op"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:112:1: set_op : ( 'UNION' 'ALL' -> ^( UNION_ALL ) | 'UNION' -> ^( UNION ) | 'EXCEPT' 'ALL' -> ^( EXCEPT_ALL ) | 'EXCEPT' -> ^( EXCEPT ) | 'INTERSECT' 'ALL' -> ^( INTERSECT_ALL ) | 'INTERSECT' -> ^( INTERSECT ) );
    public final DSToolkitSQLParser.set_op_return set_op() throws RecognitionException {
        DSToolkitSQLParser.set_op_return retval = new DSToolkitSQLParser.set_op_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal7=null;
        Token string_literal8=null;
        Token string_literal9=null;
        Token string_literal10=null;
        Token string_literal11=null;
        Token string_literal12=null;
        Token string_literal13=null;
        Token string_literal14=null;
        Token string_literal15=null;

        CommonTree string_literal7_tree=null;
        CommonTree string_literal8_tree=null;
        CommonTree string_literal9_tree=null;
        CommonTree string_literal10_tree=null;
        CommonTree string_literal11_tree=null;
        CommonTree string_literal12_tree=null;
        CommonTree string_literal13_tree=null;
        CommonTree string_literal14_tree=null;
        CommonTree string_literal15_tree=null;
        RewriteRuleTokenStream stream_49=new RewriteRuleTokenStream(adaptor,"token 49");
        RewriteRuleTokenStream stream_48=new RewriteRuleTokenStream(adaptor,"token 48");
        RewriteRuleTokenStream stream_47=new RewriteRuleTokenStream(adaptor,"token 47");
        RewriteRuleTokenStream stream_46=new RewriteRuleTokenStream(adaptor,"token 46");

        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:112:8: ( 'UNION' 'ALL' -> ^( UNION_ALL ) | 'UNION' -> ^( UNION ) | 'EXCEPT' 'ALL' -> ^( EXCEPT_ALL ) | 'EXCEPT' -> ^( EXCEPT ) | 'INTERSECT' 'ALL' -> ^( INTERSECT_ALL ) | 'INTERSECT' -> ^( INTERSECT ) )
            int alt4=6;
            alt4 = dfa4.predict(input);
            switch (alt4) {
                case 1 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:112:10: 'UNION' 'ALL'
                    {
                    string_literal7=(Token)match(input,46,FOLLOW_46_in_set_op279); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_46.add(string_literal7);

                    string_literal8=(Token)match(input,47,FOLLOW_47_in_set_op281); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_47.add(string_literal8);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 112:24: -> ^( UNION_ALL )
                    {
                        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:112:27: ^( UNION_ALL )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(UNION_ALL, "UNION_ALL"), root_1);

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:113:4: 'UNION'
                    {
                    string_literal9=(Token)match(input,46,FOLLOW_46_in_set_op292); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_46.add(string_literal9);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 113:12: -> ^( UNION )
                    {
                        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:113:15: ^( UNION )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(UNION, "UNION"), root_1);

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:114:4: 'EXCEPT' 'ALL'
                    {
                    string_literal10=(Token)match(input,48,FOLLOW_48_in_set_op303); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_48.add(string_literal10);

                    string_literal11=(Token)match(input,47,FOLLOW_47_in_set_op305); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_47.add(string_literal11);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 114:19: -> ^( EXCEPT_ALL )
                    {
                        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:114:22: ^( EXCEPT_ALL )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(EXCEPT_ALL, "EXCEPT_ALL"), root_1);

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 4 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:115:4: 'EXCEPT'
                    {
                    string_literal12=(Token)match(input,48,FOLLOW_48_in_set_op316); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_48.add(string_literal12);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 115:13: -> ^( EXCEPT )
                    {
                        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:115:16: ^( EXCEPT )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(EXCEPT, "EXCEPT"), root_1);

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 5 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:116:4: 'INTERSECT' 'ALL'
                    {
                    string_literal13=(Token)match(input,49,FOLLOW_49_in_set_op327); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_49.add(string_literal13);

                    string_literal14=(Token)match(input,47,FOLLOW_47_in_set_op329); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_47.add(string_literal14);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 116:22: -> ^( INTERSECT_ALL )
                    {
                        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:116:25: ^( INTERSECT_ALL )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(INTERSECT_ALL, "INTERSECT_ALL"), root_1);

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 6 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:117:4: 'INTERSECT'
                    {
                    string_literal15=(Token)match(input,49,FOLLOW_49_in_set_op340); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_49.add(string_literal15);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 117:16: -> ^( INTERSECT )
                    {
                        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:117:19: ^( INTERSECT )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(INTERSECT, "INTERSECT"), root_1);

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "set_op"

    public static class query_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "query"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:121:1: query : ( sub_query | 'SELECT' ( set_quantifier )? super_lexical_select_list 'FROM' super_abstract_expression ( 'WHERE' s1= search_condition )? ( 'GROUP BY' super_lexical_list )? ( 'HAVING' s2= search_condition )? -> ^( QUERY ^( SELECT_LIST ( set_quantifier )? super_lexical_select_list ) ^( FROM_LIST super_abstract_expression ) ( ^( WHERE $s1) )? ( ^( GROUP_BY super_lexical_list ) )? ( ^( HAVING $s2) )? ) );
    public final DSToolkitSQLParser.query_return query() throws RecognitionException {
        DSToolkitSQLParser.query_return retval = new DSToolkitSQLParser.query_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal17=null;
        Token string_literal20=null;
        Token string_literal22=null;
        Token string_literal23=null;
        Token string_literal25=null;
        DSToolkitSQLParser.search_condition_return s1 = null;

        DSToolkitSQLParser.search_condition_return s2 = null;

        DSToolkitSQLParser.sub_query_return sub_query16 = null;

        DSToolkitSQLParser.set_quantifier_return set_quantifier18 = null;

        DSToolkitSQLParser.super_lexical_select_list_return super_lexical_select_list19 = null;

        DSToolkitSQLParser.super_abstract_expression_return super_abstract_expression21 = null;

        DSToolkitSQLParser.super_lexical_list_return super_lexical_list24 = null;


        CommonTree string_literal17_tree=null;
        CommonTree string_literal20_tree=null;
        CommonTree string_literal22_tree=null;
        CommonTree string_literal23_tree=null;
        CommonTree string_literal25_tree=null;
        RewriteRuleTokenStream stream_51=new RewriteRuleTokenStream(adaptor,"token 51");
        RewriteRuleTokenStream stream_52=new RewriteRuleTokenStream(adaptor,"token 52");
        RewriteRuleTokenStream stream_53=new RewriteRuleTokenStream(adaptor,"token 53");
        RewriteRuleTokenStream stream_54=new RewriteRuleTokenStream(adaptor,"token 54");
        RewriteRuleTokenStream stream_50=new RewriteRuleTokenStream(adaptor,"token 50");
        RewriteRuleSubtreeStream stream_super_abstract_expression=new RewriteRuleSubtreeStream(adaptor,"rule super_abstract_expression");
        RewriteRuleSubtreeStream stream_set_quantifier=new RewriteRuleSubtreeStream(adaptor,"rule set_quantifier");
        RewriteRuleSubtreeStream stream_search_condition=new RewriteRuleSubtreeStream(adaptor,"rule search_condition");
        RewriteRuleSubtreeStream stream_super_lexical_list=new RewriteRuleSubtreeStream(adaptor,"rule super_lexical_list");
        RewriteRuleSubtreeStream stream_super_lexical_select_list=new RewriteRuleSubtreeStream(adaptor,"rule super_lexical_select_list");
        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:122:2: ( sub_query | 'SELECT' ( set_quantifier )? super_lexical_select_list 'FROM' super_abstract_expression ( 'WHERE' s1= search_condition )? ( 'GROUP BY' super_lexical_list )? ( 'HAVING' s2= search_condition )? -> ^( QUERY ^( SELECT_LIST ( set_quantifier )? super_lexical_select_list ) ^( FROM_LIST super_abstract_expression ) ( ^( WHERE $s1) )? ( ^( GROUP_BY super_lexical_list ) )? ( ^( HAVING $s2) )? ) )
            int alt9=2;
            switch ( input.LA(1) ) {
            case 56:
                {
                alt9=1;
                }
                break;
            case 50:
                {
                alt9=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 9, 0, input);

                throw nvae;
            }

            switch (alt9) {
                case 1 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:122:4: sub_query
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_sub_query_in_query360);
                    sub_query16=sub_query();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, sub_query16.getTree());

                    }
                    break;
                case 2 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:123:4: 'SELECT' ( set_quantifier )? super_lexical_select_list 'FROM' super_abstract_expression ( 'WHERE' s1= search_condition )? ( 'GROUP BY' super_lexical_list )? ( 'HAVING' s2= search_condition )?
                    {
                    string_literal17=(Token)match(input,50,FOLLOW_50_in_query365); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_50.add(string_literal17);

                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:123:13: ( set_quantifier )?
                    int alt5=2;
                    switch ( input.LA(1) ) {
                        case 47:
                        case 55:
                            {
                            alt5=1;
                            }
                            break;
                    }

                    switch (alt5) {
                        case 1 :
                            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:0:0: set_quantifier
                            {
                            pushFollow(FOLLOW_set_quantifier_in_query367);
                            set_quantifier18=set_quantifier();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_set_quantifier.add(set_quantifier18.getTree());

                            }
                            break;

                    }

                    pushFollow(FOLLOW_super_lexical_select_list_in_query370);
                    super_lexical_select_list19=super_lexical_select_list();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_super_lexical_select_list.add(super_lexical_select_list19.getTree());
                    string_literal20=(Token)match(input,51,FOLLOW_51_in_query372); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_51.add(string_literal20);

                    pushFollow(FOLLOW_super_abstract_expression_in_query374);
                    super_abstract_expression21=super_abstract_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_super_abstract_expression.add(super_abstract_expression21.getTree());
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:123:88: ( 'WHERE' s1= search_condition )?
                    int alt6=2;
                    switch ( input.LA(1) ) {
                        case 52:
                            {
                            alt6=1;
                            }
                            break;
                    }

                    switch (alt6) {
                        case 1 :
                            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:123:89: 'WHERE' s1= search_condition
                            {
                            string_literal22=(Token)match(input,52,FOLLOW_52_in_query377); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_52.add(string_literal22);

                            pushFollow(FOLLOW_search_condition_in_query381);
                            s1=search_condition();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_search_condition.add(s1.getTree());

                            }
                            break;

                    }

                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:123:119: ( 'GROUP BY' super_lexical_list )?
                    int alt7=2;
                    switch ( input.LA(1) ) {
                        case 53:
                            {
                            alt7=1;
                            }
                            break;
                    }

                    switch (alt7) {
                        case 1 :
                            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:123:120: 'GROUP BY' super_lexical_list
                            {
                            string_literal23=(Token)match(input,53,FOLLOW_53_in_query386); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_53.add(string_literal23);

                            pushFollow(FOLLOW_super_lexical_list_in_query388);
                            super_lexical_list24=super_lexical_list();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_super_lexical_list.add(super_lexical_list24.getTree());

                            }
                            break;

                    }

                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:123:152: ( 'HAVING' s2= search_condition )?
                    int alt8=2;
                    switch ( input.LA(1) ) {
                        case 54:
                            {
                            alt8=1;
                            }
                            break;
                    }

                    switch (alt8) {
                        case 1 :
                            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:123:153: 'HAVING' s2= search_condition
                            {
                            string_literal25=(Token)match(input,54,FOLLOW_54_in_query393); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_54.add(string_literal25);

                            pushFollow(FOLLOW_search_condition_in_query397);
                            s2=search_condition();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_search_condition.add(s2.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: super_lexical_select_list, super_lexical_list, s2, s1, super_abstract_expression, set_quantifier
                    // token labels: 
                    // rule labels: retval, s2, s1
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
                    RewriteRuleSubtreeStream stream_s2=new RewriteRuleSubtreeStream(adaptor,"rule s2",s2!=null?s2.tree:null);
                    RewriteRuleSubtreeStream stream_s1=new RewriteRuleSubtreeStream(adaptor,"rule s1",s1!=null?s1.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 124:5: -> ^( QUERY ^( SELECT_LIST ( set_quantifier )? super_lexical_select_list ) ^( FROM_LIST super_abstract_expression ) ( ^( WHERE $s1) )? ( ^( GROUP_BY super_lexical_list ) )? ( ^( HAVING $s2) )? )
                    {
                        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:124:8: ^( QUERY ^( SELECT_LIST ( set_quantifier )? super_lexical_select_list ) ^( FROM_LIST super_abstract_expression ) ( ^( WHERE $s1) )? ( ^( GROUP_BY super_lexical_list ) )? ( ^( HAVING $s2) )? )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(QUERY, "QUERY"), root_1);

                        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:124:16: ^( SELECT_LIST ( set_quantifier )? super_lexical_select_list )
                        {
                        CommonTree root_2 = (CommonTree)adaptor.nil();
                        root_2 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(SELECT_LIST, "SELECT_LIST"), root_2);

                        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:124:30: ( set_quantifier )?
                        if ( stream_set_quantifier.hasNext() ) {
                            adaptor.addChild(root_2, stream_set_quantifier.nextTree());

                        }
                        stream_set_quantifier.reset();
                        adaptor.addChild(root_2, stream_super_lexical_select_list.nextTree());

                        adaptor.addChild(root_1, root_2);
                        }
                        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:124:73: ^( FROM_LIST super_abstract_expression )
                        {
                        CommonTree root_2 = (CommonTree)adaptor.nil();
                        root_2 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(FROM_LIST, "FROM_LIST"), root_2);

                        adaptor.addChild(root_2, stream_super_abstract_expression.nextTree());

                        adaptor.addChild(root_1, root_2);
                        }
                        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:124:112: ( ^( WHERE $s1) )?
                        if ( stream_s1.hasNext() ) {
                            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:124:112: ^( WHERE $s1)
                            {
                            CommonTree root_2 = (CommonTree)adaptor.nil();
                            root_2 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(WHERE, "WHERE"), root_2);

                            adaptor.addChild(root_2, stream_s1.nextTree());

                            adaptor.addChild(root_1, root_2);
                            }

                        }
                        stream_s1.reset();
                        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:124:126: ( ^( GROUP_BY super_lexical_list ) )?
                        if ( stream_super_lexical_list.hasNext() ) {
                            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:124:126: ^( GROUP_BY super_lexical_list )
                            {
                            CommonTree root_2 = (CommonTree)adaptor.nil();
                            root_2 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(GROUP_BY, "GROUP_BY"), root_2);

                            adaptor.addChild(root_2, stream_super_lexical_list.nextTree());

                            adaptor.addChild(root_1, root_2);
                            }

                        }
                        stream_super_lexical_list.reset();
                        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:124:158: ( ^( HAVING $s2) )?
                        if ( stream_s2.hasNext() ) {
                            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:124:158: ^( HAVING $s2)
                            {
                            CommonTree root_2 = (CommonTree)adaptor.nil();
                            root_2 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(HAVING, "HAVING"), root_2);

                            adaptor.addChild(root_2, stream_s2.nextTree());

                            adaptor.addChild(root_1, root_2);
                            }

                        }
                        stream_s2.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "query"

    public static class set_quantifier_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "set_quantifier"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:126:1: set_quantifier : ( 'DISTINCT' | 'ALL' );
    public final DSToolkitSQLParser.set_quantifier_return set_quantifier() throws RecognitionException {
        DSToolkitSQLParser.set_quantifier_return retval = new DSToolkitSQLParser.set_quantifier_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token set26=null;

        CommonTree set26_tree=null;

        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:127:2: ( 'DISTINCT' | 'ALL' )
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:
            {
            root_0 = (CommonTree)adaptor.nil();

            set26=(Token)input.LT(1);
            if ( input.LA(1)==47||input.LA(1)==55 ) {
                input.consume();
                if ( state.backtracking==0 ) adaptor.addChild(root_0, (CommonTree)adaptor.create(set26));
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "set_quantifier"

    public static class sub_query_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "sub_query"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:128:1: sub_query : '(' query_expression ')' ;
    public final DSToolkitSQLParser.sub_query_return sub_query() throws RecognitionException {
        DSToolkitSQLParser.sub_query_return retval = new DSToolkitSQLParser.sub_query_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal27=null;
        Token char_literal29=null;
        DSToolkitSQLParser.query_expression_return query_expression28 = null;


        CommonTree char_literal27_tree=null;
        CommonTree char_literal29_tree=null;

        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:129:2: ( '(' query_expression ')' )
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:129:4: '(' query_expression ')'
            {
            root_0 = (CommonTree)adaptor.nil();

            char_literal27=(Token)match(input,56,FOLLOW_56_in_sub_query469); if (state.failed) return retval;
            pushFollow(FOLLOW_query_expression_in_sub_query472);
            query_expression28=query_expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, query_expression28.getTree());
            char_literal29=(Token)match(input,57,FOLLOW_57_in_sub_query474); if (state.failed) return retval;

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "sub_query"

    public static class super_lexical_select_list_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "super_lexical_select_list"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:131:1: super_lexical_select_list : ( '*' -> ^( SUPER_LEXICAL '*' ) | derived_super_lexical ( ',' derived_super_lexical )* );
    public final DSToolkitSQLParser.super_lexical_select_list_return super_lexical_select_list() throws RecognitionException {
        DSToolkitSQLParser.super_lexical_select_list_return retval = new DSToolkitSQLParser.super_lexical_select_list_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal30=null;
        Token char_literal32=null;
        DSToolkitSQLParser.derived_super_lexical_return derived_super_lexical31 = null;

        DSToolkitSQLParser.derived_super_lexical_return derived_super_lexical33 = null;


        CommonTree char_literal30_tree=null;
        CommonTree char_literal32_tree=null;
        RewriteRuleTokenStream stream_58=new RewriteRuleTokenStream(adaptor,"token 58");

        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:132:2: ( '*' -> ^( SUPER_LEXICAL '*' ) | derived_super_lexical ( ',' derived_super_lexical )* )
            int alt11=2;
            switch ( input.LA(1) ) {
            case 58:
                {
                alt11=1;
                }
                break;
            case ID:
            case INT:
            case FLOAT:
            case NUMERIC:
            case STRING:
            case 56:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 77:
            case 78:
            case 79:
                {
                alt11=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }

            switch (alt11) {
                case 1 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:132:4: '*'
                    {
                    char_literal30=(Token)match(input,58,FOLLOW_58_in_super_lexical_select_list484); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_58.add(char_literal30);



                    // AST REWRITE
                    // elements: 58
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 132:8: -> ^( SUPER_LEXICAL '*' )
                    {
                        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:132:11: ^( SUPER_LEXICAL '*' )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(SUPER_LEXICAL, "SUPER_LEXICAL"), root_1);

                        adaptor.addChild(root_1, stream_58.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:133:5: derived_super_lexical ( ',' derived_super_lexical )*
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_derived_super_lexical_in_super_lexical_select_list498);
                    derived_super_lexical31=derived_super_lexical();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, derived_super_lexical31.getTree());
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:133:27: ( ',' derived_super_lexical )*
                    loop10:
                    do {
                        int alt10=2;
                        switch ( input.LA(1) ) {
                        case 59:
                            {
                            alt10=1;
                            }
                            break;

                        }

                        switch (alt10) {
                    	case 1 :
                    	    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:133:28: ',' derived_super_lexical
                    	    {
                    	    char_literal32=(Token)match(input,59,FOLLOW_59_in_super_lexical_select_list501); if (state.failed) return retval;
                    	    pushFollow(FOLLOW_derived_super_lexical_in_super_lexical_select_list504);
                    	    derived_super_lexical33=derived_super_lexical();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) adaptor.addChild(root_0, derived_super_lexical33.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop10;
                        }
                    } while (true);


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "super_lexical_select_list"

    public static class derived_super_lexical_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "derived_super_lexical"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:134:1: derived_super_lexical : value_expression ( ( 'AS' )? ID )? -> ^( SUPER_LEXICAL value_expression ( ID )? ) ;
    public final DSToolkitSQLParser.derived_super_lexical_return derived_super_lexical() throws RecognitionException {
        DSToolkitSQLParser.derived_super_lexical_return retval = new DSToolkitSQLParser.derived_super_lexical_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal35=null;
        Token ID36=null;
        DSToolkitSQLParser.value_expression_return value_expression34 = null;


        CommonTree string_literal35_tree=null;
        CommonTree ID36_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_60=new RewriteRuleTokenStream(adaptor,"token 60");
        RewriteRuleSubtreeStream stream_value_expression=new RewriteRuleSubtreeStream(adaptor,"rule value_expression");
        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:135:2: ( value_expression ( ( 'AS' )? ID )? -> ^( SUPER_LEXICAL value_expression ( ID )? ) )
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:135:4: value_expression ( ( 'AS' )? ID )?
            {
            pushFollow(FOLLOW_value_expression_in_derived_super_lexical514);
            value_expression34=value_expression();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_value_expression.add(value_expression34.getTree());
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:135:21: ( ( 'AS' )? ID )?
            int alt13=2;
            switch ( input.LA(1) ) {
                case ID:
                case 60:
                    {
                    alt13=1;
                    }
                    break;
            }

            switch (alt13) {
                case 1 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:135:22: ( 'AS' )? ID
                    {
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:135:22: ( 'AS' )?
                    int alt12=2;
                    switch ( input.LA(1) ) {
                        case 60:
                            {
                            alt12=1;
                            }
                            break;
                    }

                    switch (alt12) {
                        case 1 :
                            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:0:0: 'AS'
                            {
                            string_literal35=(Token)match(input,60,FOLLOW_60_in_derived_super_lexical517); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_60.add(string_literal35);


                            }
                            break;

                    }

                    ID36=(Token)match(input,ID,FOLLOW_ID_in_derived_super_lexical520); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(ID36);


                    }
                    break;

            }



            // AST REWRITE
            // elements: ID, value_expression
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 135:33: -> ^( SUPER_LEXICAL value_expression ( ID )? )
            {
                // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:135:36: ^( SUPER_LEXICAL value_expression ( ID )? )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(SUPER_LEXICAL, "SUPER_LEXICAL"), root_1);

                adaptor.addChild(root_1, stream_value_expression.nextTree());
                // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:135:69: ( ID )?
                if ( stream_ID.hasNext() ) {
                    adaptor.addChild(root_1, stream_ID.nextNode());

                }
                stream_ID.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "derived_super_lexical"

    public static class order_by_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "order_by"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:138:1: order_by : 'ORDER' 'BY' sort_specification ( ',' sort_specification )* -> ^( ORDER ( sort_specification )+ ) ;
    public final DSToolkitSQLParser.order_by_return order_by() throws RecognitionException {
        DSToolkitSQLParser.order_by_return retval = new DSToolkitSQLParser.order_by_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal37=null;
        Token string_literal38=null;
        Token char_literal40=null;
        DSToolkitSQLParser.sort_specification_return sort_specification39 = null;

        DSToolkitSQLParser.sort_specification_return sort_specification41 = null;


        CommonTree string_literal37_tree=null;
        CommonTree string_literal38_tree=null;
        CommonTree char_literal40_tree=null;
        RewriteRuleTokenStream stream_59=new RewriteRuleTokenStream(adaptor,"token 59");
        RewriteRuleTokenStream stream_62=new RewriteRuleTokenStream(adaptor,"token 62");
        RewriteRuleTokenStream stream_61=new RewriteRuleTokenStream(adaptor,"token 61");
        RewriteRuleSubtreeStream stream_sort_specification=new RewriteRuleSubtreeStream(adaptor,"rule sort_specification");
        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:139:2: ( 'ORDER' 'BY' sort_specification ( ',' sort_specification )* -> ^( ORDER ( sort_specification )+ ) )
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:139:4: 'ORDER' 'BY' sort_specification ( ',' sort_specification )*
            {
            string_literal37=(Token)match(input,61,FOLLOW_61_in_order_by545); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_61.add(string_literal37);

            string_literal38=(Token)match(input,62,FOLLOW_62_in_order_by547); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_62.add(string_literal38);

            pushFollow(FOLLOW_sort_specification_in_order_by549);
            sort_specification39=sort_specification();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_sort_specification.add(sort_specification39.getTree());
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:139:36: ( ',' sort_specification )*
            loop14:
            do {
                int alt14=2;
                switch ( input.LA(1) ) {
                case 59:
                    {
                    alt14=1;
                    }
                    break;

                }

                switch (alt14) {
            	case 1 :
            	    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:139:37: ',' sort_specification
            	    {
            	    char_literal40=(Token)match(input,59,FOLLOW_59_in_order_by552); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_59.add(char_literal40);

            	    pushFollow(FOLLOW_sort_specification_in_order_by554);
            	    sort_specification41=sort_specification();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_sort_specification.add(sort_specification41.getTree());

            	    }
            	    break;

            	default :
            	    break loop14;
                }
            } while (true);



            // AST REWRITE
            // elements: sort_specification
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 139:62: -> ^( ORDER ( sort_specification )+ )
            {
                // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:139:65: ^( ORDER ( sort_specification )+ )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(ORDER, "ORDER"), root_1);

                if ( !(stream_sort_specification.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_sort_specification.hasNext() ) {
                    adaptor.addChild(root_1, stream_sort_specification.nextTree());

                }
                stream_sort_specification.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "order_by"

    public static class sort_specification_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "sort_specification"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:140:1: sort_specification : ( super_lexical_name | INT | reserved_word_super_lexical_name );
    public final DSToolkitSQLParser.sort_specification_return sort_specification() throws RecognitionException {
        DSToolkitSQLParser.sort_specification_return retval = new DSToolkitSQLParser.sort_specification_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token INT43=null;
        DSToolkitSQLParser.super_lexical_name_return super_lexical_name42 = null;

        DSToolkitSQLParser.reserved_word_super_lexical_name_return reserved_word_super_lexical_name44 = null;


        CommonTree INT43_tree=null;

        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:141:2: ( super_lexical_name | INT | reserved_word_super_lexical_name )
            int alt15=3;
            switch ( input.LA(1) ) {
            case ID:
                {
                switch ( input.LA(2) ) {
                case 63:
                    {
                    switch ( input.LA(3) ) {
                    case ID:
                        {
                        alt15=1;
                        }
                        break;
                    case 64:
                    case 65:
                    case 66:
                    case 67:
                    case 68:
                    case 69:
                    case 70:
                    case 71:
                    case 72:
                    case 73:
                        {
                        alt15=3;
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 15, 4, input);

                        throw nvae;
                    }

                    }
                    break;
                case EOF:
                case 45:
                case 59:
                    {
                    alt15=1;
                    }
                    break;
                default:
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 15, 1, input);

                    throw nvae;
                }

                }
                break;
            case INT:
                {
                alt15=2;
                }
                break;
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
            case 73:
                {
                alt15=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;
            }

            switch (alt15) {
                case 1 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:141:4: super_lexical_name
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_super_lexical_name_in_sort_specification573);
                    super_lexical_name42=super_lexical_name();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, super_lexical_name42.getTree());

                    }
                    break;
                case 2 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:141:25: INT
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    INT43=(Token)match(input,INT,FOLLOW_INT_in_sort_specification577); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    INT43_tree = (CommonTree)adaptor.create(INT43);
                    adaptor.addChild(root_0, INT43_tree);
                    }

                    }
                    break;
                case 3 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:141:31: reserved_word_super_lexical_name
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_reserved_word_super_lexical_name_in_sort_specification581);
                    reserved_word_super_lexical_name44=reserved_word_super_lexical_name();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, reserved_word_super_lexical_name44.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "sort_specification"

    public static class reserved_word_super_lexical_name_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "reserved_word_super_lexical_name"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:144:1: reserved_word_super_lexical_name : (super_abstract_id= ID '.' )? (s= 'DATE' | s= 'TIMESTAMP' | s= 'TIME' | s= 'INTERVAL' | s= 'YEAR' | s= 'MONTH' | s= 'DAY' | s= 'HOUR' | s= 'MINUTE' | s= 'SECOND' ) -> ^( SUPER_LEXICAL_OF_SUPER_ABSTRACT ( $super_abstract_id)? $s) ;
    public final DSToolkitSQLParser.reserved_word_super_lexical_name_return reserved_word_super_lexical_name() throws RecognitionException {
        DSToolkitSQLParser.reserved_word_super_lexical_name_return retval = new DSToolkitSQLParser.reserved_word_super_lexical_name_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token super_abstract_id=null;
        Token s=null;
        Token char_literal45=null;

        CommonTree super_abstract_id_tree=null;
        CommonTree s_tree=null;
        CommonTree char_literal45_tree=null;
        RewriteRuleTokenStream stream_67=new RewriteRuleTokenStream(adaptor,"token 67");
        RewriteRuleTokenStream stream_66=new RewriteRuleTokenStream(adaptor,"token 66");
        RewriteRuleTokenStream stream_69=new RewriteRuleTokenStream(adaptor,"token 69");
        RewriteRuleTokenStream stream_68=new RewriteRuleTokenStream(adaptor,"token 68");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_64=new RewriteRuleTokenStream(adaptor,"token 64");
        RewriteRuleTokenStream stream_65=new RewriteRuleTokenStream(adaptor,"token 65");
        RewriteRuleTokenStream stream_70=new RewriteRuleTokenStream(adaptor,"token 70");
        RewriteRuleTokenStream stream_71=new RewriteRuleTokenStream(adaptor,"token 71");
        RewriteRuleTokenStream stream_72=new RewriteRuleTokenStream(adaptor,"token 72");
        RewriteRuleTokenStream stream_63=new RewriteRuleTokenStream(adaptor,"token 63");
        RewriteRuleTokenStream stream_73=new RewriteRuleTokenStream(adaptor,"token 73");

        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:145:2: ( (super_abstract_id= ID '.' )? (s= 'DATE' | s= 'TIMESTAMP' | s= 'TIME' | s= 'INTERVAL' | s= 'YEAR' | s= 'MONTH' | s= 'DAY' | s= 'HOUR' | s= 'MINUTE' | s= 'SECOND' ) -> ^( SUPER_LEXICAL_OF_SUPER_ABSTRACT ( $super_abstract_id)? $s) )
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:145:4: (super_abstract_id= ID '.' )? (s= 'DATE' | s= 'TIMESTAMP' | s= 'TIME' | s= 'INTERVAL' | s= 'YEAR' | s= 'MONTH' | s= 'DAY' | s= 'HOUR' | s= 'MINUTE' | s= 'SECOND' )
            {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:145:4: (super_abstract_id= ID '.' )?
            int alt16=2;
            switch ( input.LA(1) ) {
                case ID:
                    {
                    alt16=1;
                    }
                    break;
            }

            switch (alt16) {
                case 1 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:145:5: super_abstract_id= ID '.'
                    {
                    super_abstract_id=(Token)match(input,ID,FOLLOW_ID_in_reserved_word_super_lexical_name595); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(super_abstract_id);

                    char_literal45=(Token)match(input,63,FOLLOW_63_in_reserved_word_super_lexical_name596); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_63.add(char_literal45);


                    }
                    break;

            }

            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:145:30: (s= 'DATE' | s= 'TIMESTAMP' | s= 'TIME' | s= 'INTERVAL' | s= 'YEAR' | s= 'MONTH' | s= 'DAY' | s= 'HOUR' | s= 'MINUTE' | s= 'SECOND' )
            int alt17=10;
            switch ( input.LA(1) ) {
            case 64:
                {
                alt17=1;
                }
                break;
            case 65:
                {
                alt17=2;
                }
                break;
            case 66:
                {
                alt17=3;
                }
                break;
            case 67:
                {
                alt17=4;
                }
                break;
            case 68:
                {
                alt17=5;
                }
                break;
            case 69:
                {
                alt17=6;
                }
                break;
            case 70:
                {
                alt17=7;
                }
                break;
            case 71:
                {
                alt17=8;
                }
                break;
            case 72:
                {
                alt17=9;
                }
                break;
            case 73:
                {
                alt17=10;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 17, 0, input);

                throw nvae;
            }

            switch (alt17) {
                case 1 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:145:31: s= 'DATE'
                    {
                    s=(Token)match(input,64,FOLLOW_64_in_reserved_word_super_lexical_name602); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_64.add(s);


                    }
                    break;
                case 2 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:145:42: s= 'TIMESTAMP'
                    {
                    s=(Token)match(input,65,FOLLOW_65_in_reserved_word_super_lexical_name608); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_65.add(s);


                    }
                    break;
                case 3 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:145:58: s= 'TIME'
                    {
                    s=(Token)match(input,66,FOLLOW_66_in_reserved_word_super_lexical_name614); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_66.add(s);


                    }
                    break;
                case 4 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:145:69: s= 'INTERVAL'
                    {
                    s=(Token)match(input,67,FOLLOW_67_in_reserved_word_super_lexical_name620); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_67.add(s);


                    }
                    break;
                case 5 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:145:84: s= 'YEAR'
                    {
                    s=(Token)match(input,68,FOLLOW_68_in_reserved_word_super_lexical_name626); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_68.add(s);


                    }
                    break;
                case 6 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:145:95: s= 'MONTH'
                    {
                    s=(Token)match(input,69,FOLLOW_69_in_reserved_word_super_lexical_name632); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_69.add(s);


                    }
                    break;
                case 7 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:145:107: s= 'DAY'
                    {
                    s=(Token)match(input,70,FOLLOW_70_in_reserved_word_super_lexical_name638); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_70.add(s);


                    }
                    break;
                case 8 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:145:117: s= 'HOUR'
                    {
                    s=(Token)match(input,71,FOLLOW_71_in_reserved_word_super_lexical_name644); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_71.add(s);


                    }
                    break;
                case 9 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:145:128: s= 'MINUTE'
                    {
                    s=(Token)match(input,72,FOLLOW_72_in_reserved_word_super_lexical_name650); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_72.add(s);


                    }
                    break;
                case 10 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:145:141: s= 'SECOND'
                    {
                    s=(Token)match(input,73,FOLLOW_73_in_reserved_word_super_lexical_name656); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_73.add(s);


                    }
                    break;

            }



            // AST REWRITE
            // elements: s, super_abstract_id
            // token labels: super_abstract_id, s
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_super_abstract_id=new RewriteRuleTokenStream(adaptor,"token super_abstract_id",super_abstract_id);
            RewriteRuleTokenStream stream_s=new RewriteRuleTokenStream(adaptor,"token s",s);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 146:4: -> ^( SUPER_LEXICAL_OF_SUPER_ABSTRACT ( $super_abstract_id)? $s)
            {
                // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:146:7: ^( SUPER_LEXICAL_OF_SUPER_ABSTRACT ( $super_abstract_id)? $s)
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(SUPER_LEXICAL_OF_SUPER_ABSTRACT, "SUPER_LEXICAL_OF_SUPER_ABSTRACT"), root_1);

                // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:146:41: ( $super_abstract_id)?
                if ( stream_super_abstract_id.hasNext() ) {
                    adaptor.addChild(root_1, stream_super_abstract_id.nextNode());

                }
                stream_super_abstract_id.reset();
                adaptor.addChild(root_1, stream_s.nextNode());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "reserved_word_super_lexical_name"

    public static class value_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "value_expression"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:149:1: value_expression : ( string_value_expression | numeric_value_expression );
    public final DSToolkitSQLParser.value_expression_return value_expression() throws RecognitionException {
        DSToolkitSQLParser.value_expression_return retval = new DSToolkitSQLParser.value_expression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        DSToolkitSQLParser.string_value_expression_return string_value_expression46 = null;

        DSToolkitSQLParser.numeric_value_expression_return numeric_value_expression47 = null;



        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:150:2: ( string_value_expression | numeric_value_expression )
            int alt18=2;
            switch ( input.LA(1) ) {
            case ID:
                {
                switch ( input.LA(2) ) {
                case 63:
                    {
                    switch ( input.LA(3) ) {
                    case ID:
                        {
                        switch ( input.LA(4) ) {
                        case 63:
                            {
                            switch ( input.LA(5) ) {
                            case ID:
                                {
                                switch ( input.LA(6) ) {
                                case EOF:
                                case ID:
                                case INT:
                                case FLOAT:
                                case NUMERIC:
                                case STRING:
                                case 45:
                                case 46:
                                case 48:
                                case 49:
                                case 51:
                                case 52:
                                case 53:
                                case 54:
                                case 56:
                                case 57:
                                case 58:
                                case 59:
                                case 60:
                                case 61:
                                case 64:
                                case 65:
                                case 66:
                                case 67:
                                case 68:
                                case 69:
                                case 70:
                                case 71:
                                case 72:
                                case 73:
                                case 74:
                                case 75:
                                case 76:
                                case 77:
                                case 78:
                                case 79:
                                case 81:
                                case 83:
                                case 84:
                                case 85:
                                case 86:
                                case 88:
                                case 89:
                                case 90:
                                case 91:
                                case 92:
                                case 93:
                                case 94:
                                case 95:
                                case 96:
                                case 97:
                                case 98:
                                case 99:
                                case 100:
                                case 101:
                                case 104:
                                case 105:
                                case 106:
                                    {
                                    alt18=2;
                                    }
                                    break;
                                case 80:
                                    {
                                    alt18=1;
                                    }
                                    break;
                                default:
                                    if (state.backtracking>0) {state.failed=true; return retval;}
                                    NoViableAltException nvae =
                                        new NoViableAltException("", 18, 8, input);

                                    throw nvae;
                                }

                                }
                                break;
                            default:
                                if (state.backtracking>0) {state.failed=true; return retval;}
                                NoViableAltException nvae =
                                    new NoViableAltException("", 18, 7, input);

                                throw nvae;
                            }

                            }
                            break;
                        case EOF:
                        case ID:
                        case INT:
                        case FLOAT:
                        case NUMERIC:
                        case STRING:
                        case 45:
                        case 46:
                        case 48:
                        case 49:
                        case 51:
                        case 52:
                        case 53:
                        case 54:
                        case 56:
                        case 57:
                        case 58:
                        case 59:
                        case 60:
                        case 61:
                        case 64:
                        case 65:
                        case 66:
                        case 67:
                        case 68:
                        case 69:
                        case 70:
                        case 71:
                        case 72:
                        case 73:
                        case 74:
                        case 75:
                        case 76:
                        case 77:
                        case 78:
                        case 79:
                        case 81:
                        case 83:
                        case 84:
                        case 85:
                        case 86:
                        case 88:
                        case 89:
                        case 90:
                        case 91:
                        case 92:
                        case 93:
                        case 94:
                        case 95:
                        case 96:
                        case 97:
                        case 98:
                        case 99:
                        case 100:
                        case 101:
                        case 104:
                        case 105:
                        case 106:
                            {
                            alt18=2;
                            }
                            break;
                        case 80:
                            {
                            alt18=1;
                            }
                            break;
                        default:
                            if (state.backtracking>0) {state.failed=true; return retval;}
                            NoViableAltException nvae =
                                new NoViableAltException("", 18, 6, input);

                            throw nvae;
                        }

                        }
                        break;
                    case 64:
                    case 65:
                    case 66:
                    case 67:
                    case 68:
                    case 69:
                    case 70:
                    case 71:
                    case 72:
                    case 73:
                        {
                        alt18=2;
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 18, 4, input);

                        throw nvae;
                    }

                    }
                    break;
                case EOF:
                case ID:
                case INT:
                case FLOAT:
                case NUMERIC:
                case STRING:
                case 45:
                case 46:
                case 48:
                case 49:
                case 51:
                case 52:
                case 53:
                case 54:
                case 56:
                case 57:
                case 58:
                case 59:
                case 60:
                case 61:
                case 64:
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                case 70:
                case 71:
                case 72:
                case 73:
                case 74:
                case 75:
                case 76:
                case 77:
                case 78:
                case 79:
                case 81:
                case 83:
                case 84:
                case 85:
                case 86:
                case 88:
                case 89:
                case 90:
                case 91:
                case 92:
                case 93:
                case 94:
                case 95:
                case 96:
                case 97:
                case 98:
                case 99:
                case 100:
                case 101:
                case 104:
                case 105:
                case 106:
                    {
                    alt18=2;
                    }
                    break;
                case 80:
                    {
                    alt18=1;
                    }
                    break;
                default:
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 18, 1, input);

                    throw nvae;
                }

                }
                break;
            case STRING:
                {
                switch ( input.LA(2) ) {
                case EOF:
                case ID:
                case INT:
                case FLOAT:
                case NUMERIC:
                case STRING:
                case 45:
                case 46:
                case 48:
                case 49:
                case 51:
                case 52:
                case 53:
                case 54:
                case 56:
                case 57:
                case 58:
                case 59:
                case 60:
                case 61:
                case 64:
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                case 70:
                case 71:
                case 72:
                case 73:
                case 74:
                case 75:
                case 76:
                case 77:
                case 78:
                case 79:
                case 81:
                case 83:
                case 84:
                case 85:
                case 86:
                case 88:
                case 89:
                case 90:
                case 91:
                case 92:
                case 93:
                case 94:
                case 95:
                case 96:
                case 97:
                case 98:
                case 99:
                case 100:
                case 101:
                case 104:
                case 105:
                case 106:
                    {
                    alt18=2;
                    }
                    break;
                case 80:
                    {
                    alt18=1;
                    }
                    break;
                default:
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 18, 2, input);

                    throw nvae;
                }

                }
                break;
            case INT:
            case FLOAT:
            case NUMERIC:
            case 56:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 77:
            case 78:
            case 79:
                {
                alt18=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 18, 0, input);

                throw nvae;
            }

            switch (alt18) {
                case 1 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:150:4: string_value_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_string_value_expression_in_value_expression687);
                    string_value_expression46=string_value_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, string_value_expression46.getTree());

                    }
                    break;
                case 2 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:151:4: numeric_value_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_numeric_value_expression_in_value_expression692);
                    numeric_value_expression47=numeric_value_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, numeric_value_expression47.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "value_expression"

    public static class numeric_value_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "numeric_value_expression"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:153:1: numeric_value_expression : factor ( ( '+' | '-' ) factor )* ;
    public final DSToolkitSQLParser.numeric_value_expression_return numeric_value_expression() throws RecognitionException {
        DSToolkitSQLParser.numeric_value_expression_return retval = new DSToolkitSQLParser.numeric_value_expression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token set49=null;
        DSToolkitSQLParser.factor_return factor48 = null;

        DSToolkitSQLParser.factor_return factor50 = null;


        CommonTree set49_tree=null;

        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:154:2: ( factor ( ( '+' | '-' ) factor )* )
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:154:5: factor ( ( '+' | '-' ) factor )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_factor_in_numeric_value_expression704);
            factor48=factor();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, factor48.getTree());
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:154:12: ( ( '+' | '-' ) factor )*
            loop19:
            do {
                int alt19=2;
                alt19 = dfa19.predict(input);
                switch (alt19) {
            	case 1 :
            	    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:154:13: ( '+' | '-' ) factor
            	    {
            	    set49=(Token)input.LT(1);
            	    set49=(Token)input.LT(1);
            	    if ( (input.LA(1)>=74 && input.LA(1)<=75) ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) root_0 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(set49), root_0);
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_factor_in_numeric_value_expression714);
            	    factor50=factor();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, factor50.getTree());

            	    }
            	    break;

            	default :
            	    break loop19;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "numeric_value_expression"

    public static class factor_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "factor"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:156:1: factor : numeric_primary ( ( '*' | '/' ) numeric_primary )* ;
    public final DSToolkitSQLParser.factor_return factor() throws RecognitionException {
        DSToolkitSQLParser.factor_return retval = new DSToolkitSQLParser.factor_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token set52=null;
        DSToolkitSQLParser.numeric_primary_return numeric_primary51 = null;

        DSToolkitSQLParser.numeric_primary_return numeric_primary53 = null;


        CommonTree set52_tree=null;

        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:156:8: ( numeric_primary ( ( '*' | '/' ) numeric_primary )* )
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:156:10: numeric_primary ( ( '*' | '/' ) numeric_primary )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_numeric_primary_in_factor726);
            numeric_primary51=numeric_primary();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, numeric_primary51.getTree());
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:156:26: ( ( '*' | '/' ) numeric_primary )*
            loop20:
            do {
                int alt20=2;
                switch ( input.LA(1) ) {
                case 58:
                case 76:
                    {
                    alt20=1;
                    }
                    break;

                }

                switch (alt20) {
            	case 1 :
            	    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:156:27: ( '*' | '/' ) numeric_primary
            	    {
            	    set52=(Token)input.LT(1);
            	    set52=(Token)input.LT(1);
            	    if ( input.LA(1)==58||input.LA(1)==76 ) {
            	        input.consume();
            	        if ( state.backtracking==0 ) root_0 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(set52), root_0);
            	        state.errorRecovery=false;state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }

            	    pushFollow(FOLLOW_numeric_primary_in_factor736);
            	    numeric_primary53=numeric_primary();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, numeric_primary53.getTree());

            	    }
            	    break;

            	default :
            	    break loop20;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "factor"

    public static class numeric_primary_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "numeric_primary"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:158:1: numeric_primary : ( '+' | '-' )? value_expression_primary ;
    public final DSToolkitSQLParser.numeric_primary_return numeric_primary() throws RecognitionException {
        DSToolkitSQLParser.numeric_primary_return retval = new DSToolkitSQLParser.numeric_primary_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal54=null;
        Token char_literal55=null;
        DSToolkitSQLParser.value_expression_primary_return value_expression_primary56 = null;


        CommonTree char_literal54_tree=null;
        CommonTree char_literal55_tree=null;

        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:159:2: ( ( '+' | '-' )? value_expression_primary )
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:159:5: ( '+' | '-' )? value_expression_primary
            {
            root_0 = (CommonTree)adaptor.nil();

            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:159:5: ( '+' | '-' )?
            int alt21=3;
            switch ( input.LA(1) ) {
                case 74:
                    {
                    alt21=1;
                    }
                    break;
                case 75:
                    {
                    alt21=2;
                    }
                    break;
            }

            switch (alt21) {
                case 1 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:159:6: '+'
                    {
                    char_literal54=(Token)match(input,74,FOLLOW_74_in_numeric_primary749); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal54_tree = (CommonTree)adaptor.create(char_literal54);
                    root_0 = (CommonTree)adaptor.becomeRoot(char_literal54_tree, root_0);
                    }

                    }
                    break;
                case 2 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:159:11: '-'
                    {
                    char_literal55=(Token)match(input,75,FOLLOW_75_in_numeric_primary752); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    char_literal55_tree = (CommonTree)adaptor.create(char_literal55);
                    root_0 = (CommonTree)adaptor.becomeRoot(char_literal55_tree, root_0);
                    }

                    }
                    break;

            }

            pushFollow(FOLLOW_value_expression_primary_in_numeric_primary757);
            value_expression_primary56=value_expression_primary();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, value_expression_primary56.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "numeric_primary"

    public static class value_expression_primary_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "value_expression_primary"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:160:1: value_expression_primary : ( '(' value_expression ')' | function | super_lexical_name | literal | sub_query );
    public final DSToolkitSQLParser.value_expression_primary_return value_expression_primary() throws RecognitionException {
        DSToolkitSQLParser.value_expression_primary_return retval = new DSToolkitSQLParser.value_expression_primary_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal57=null;
        Token char_literal59=null;
        DSToolkitSQLParser.value_expression_return value_expression58 = null;

        DSToolkitSQLParser.function_return function60 = null;

        DSToolkitSQLParser.super_lexical_name_return super_lexical_name61 = null;

        DSToolkitSQLParser.literal_return literal62 = null;

        DSToolkitSQLParser.sub_query_return sub_query63 = null;


        CommonTree char_literal57_tree=null;
        CommonTree char_literal59_tree=null;

        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:161:2: ( '(' value_expression ')' | function | super_lexical_name | literal | sub_query )
            int alt22=5;
            alt22 = dfa22.predict(input);
            switch (alt22) {
                case 1 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:161:4: '(' value_expression ')'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    char_literal57=(Token)match(input,56,FOLLOW_56_in_value_expression_primary767); if (state.failed) return retval;
                    pushFollow(FOLLOW_value_expression_in_value_expression_primary770);
                    value_expression58=value_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, value_expression58.getTree());
                    char_literal59=(Token)match(input,57,FOLLOW_57_in_value_expression_primary772); if (state.failed) return retval;

                    }
                    break;
                case 2 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:162:5: function
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_function_in_value_expression_primary779);
                    function60=function();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, function60.getTree());

                    }
                    break;
                case 3 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:163:5: super_lexical_name
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_super_lexical_name_in_value_expression_primary785);
                    super_lexical_name61=super_lexical_name();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, super_lexical_name61.getTree());

                    }
                    break;
                case 4 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:164:5: literal
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_literal_in_value_expression_primary791);
                    literal62=literal();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, literal62.getTree());

                    }
                    break;
                case 5 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:165:5: sub_query
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_sub_query_in_value_expression_primary797);
                    sub_query63=sub_query();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, sub_query63.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "value_expression_primary"

    public static class literal_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "literal"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:168:1: literal : ( INT | FLOAT | NUMERIC | STRING | datetime | interval | 'NULL' | 'TRUE' | 'FALSE' );
    public final DSToolkitSQLParser.literal_return literal() throws RecognitionException {
        DSToolkitSQLParser.literal_return retval = new DSToolkitSQLParser.literal_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token INT64=null;
        Token FLOAT65=null;
        Token NUMERIC66=null;
        Token STRING67=null;
        Token string_literal70=null;
        Token string_literal71=null;
        Token string_literal72=null;
        DSToolkitSQLParser.datetime_return datetime68 = null;

        DSToolkitSQLParser.interval_return interval69 = null;


        CommonTree INT64_tree=null;
        CommonTree FLOAT65_tree=null;
        CommonTree NUMERIC66_tree=null;
        CommonTree STRING67_tree=null;
        CommonTree string_literal70_tree=null;
        CommonTree string_literal71_tree=null;
        CommonTree string_literal72_tree=null;

        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:168:9: ( INT | FLOAT | NUMERIC | STRING | datetime | interval | 'NULL' | 'TRUE' | 'FALSE' )
            int alt23=9;
            alt23 = dfa23.predict(input);
            switch (alt23) {
                case 1 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:168:11: INT
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    INT64=(Token)match(input,INT,FOLLOW_INT_in_literal807); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    INT64_tree = (CommonTree)adaptor.create(INT64);
                    adaptor.addChild(root_0, INT64_tree);
                    }

                    }
                    break;
                case 2 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:168:17: FLOAT
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    FLOAT65=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_literal811); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    FLOAT65_tree = (CommonTree)adaptor.create(FLOAT65);
                    adaptor.addChild(root_0, FLOAT65_tree);
                    }

                    }
                    break;
                case 3 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:168:25: NUMERIC
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    NUMERIC66=(Token)match(input,NUMERIC,FOLLOW_NUMERIC_in_literal815); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    NUMERIC66_tree = (CommonTree)adaptor.create(NUMERIC66);
                    adaptor.addChild(root_0, NUMERIC66_tree);
                    }

                    }
                    break;
                case 4 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:168:35: STRING
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    STRING67=(Token)match(input,STRING,FOLLOW_STRING_in_literal819); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    STRING67_tree = (CommonTree)adaptor.create(STRING67);
                    adaptor.addChild(root_0, STRING67_tree);
                    }

                    }
                    break;
                case 5 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:168:44: datetime
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_datetime_in_literal823);
                    datetime68=datetime();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, datetime68.getTree());

                    }
                    break;
                case 6 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:168:55: interval
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_interval_in_literal827);
                    interval69=interval();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, interval69.getTree());

                    }
                    break;
                case 7 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:168:66: 'NULL'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    string_literal70=(Token)match(input,77,FOLLOW_77_in_literal831); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal70_tree = (CommonTree)adaptor.create(string_literal70);
                    adaptor.addChild(root_0, string_literal70_tree);
                    }

                    }
                    break;
                case 8 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:168:75: 'TRUE'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    string_literal71=(Token)match(input,78,FOLLOW_78_in_literal835); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal71_tree = (CommonTree)adaptor.create(string_literal71);
                    adaptor.addChild(root_0, string_literal71_tree);
                    }

                    }
                    break;
                case 9 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:168:84: 'FALSE'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    string_literal72=(Token)match(input,79,FOLLOW_79_in_literal839); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal72_tree = (CommonTree)adaptor.create(string_literal72);
                    adaptor.addChild(root_0, string_literal72_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "literal"

    public static class datetime_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "datetime"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:169:1: datetime : ( ( 'DATE' | 'TIMESTAMP' | 'TIME' ) STRING | (tableid= ID '.' )? (s= 'DATE' | s= 'TIMESTAMP' | s= 'TIME' ) -> ^( SUPER_LEXICAL_OF_SUPER_ABSTRACT ( $tableid)? $s) );
    public final DSToolkitSQLParser.datetime_return datetime() throws RecognitionException {
        DSToolkitSQLParser.datetime_return retval = new DSToolkitSQLParser.datetime_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token tableid=null;
        Token s=null;
        Token set73=null;
        Token STRING74=null;
        Token char_literal75=null;

        CommonTree tableid_tree=null;
        CommonTree s_tree=null;
        CommonTree set73_tree=null;
        CommonTree STRING74_tree=null;
        CommonTree char_literal75_tree=null;
        RewriteRuleTokenStream stream_66=new RewriteRuleTokenStream(adaptor,"token 66");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_64=new RewriteRuleTokenStream(adaptor,"token 64");
        RewriteRuleTokenStream stream_65=new RewriteRuleTokenStream(adaptor,"token 65");
        RewriteRuleTokenStream stream_63=new RewriteRuleTokenStream(adaptor,"token 63");

        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:170:2: ( ( 'DATE' | 'TIMESTAMP' | 'TIME' ) STRING | (tableid= ID '.' )? (s= 'DATE' | s= 'TIMESTAMP' | s= 'TIME' ) -> ^( SUPER_LEXICAL_OF_SUPER_ABSTRACT ( $tableid)? $s) )
            int alt26=2;
            switch ( input.LA(1) ) {
            case 64:
                {
                switch ( input.LA(2) ) {
                case STRING:
                    {
                    int LA26_5 = input.LA(3);

                    if ( (synpred53_DSToolkitSQL()) ) {
                        alt26=1;
                    }
                    else if ( (true) ) {
                        alt26=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 26, 5, input);

                        throw nvae;
                    }
                    }
                    break;
                case EOF:
                case ID:
                case INT:
                case FLOAT:
                case NUMERIC:
                case 45:
                case 46:
                case 48:
                case 49:
                case 51:
                case 52:
                case 53:
                case 54:
                case 56:
                case 57:
                case 58:
                case 59:
                case 60:
                case 61:
                case 64:
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                case 70:
                case 71:
                case 72:
                case 73:
                case 74:
                case 75:
                case 76:
                case 77:
                case 78:
                case 79:
                case 81:
                case 83:
                case 84:
                case 85:
                case 86:
                case 88:
                case 89:
                case 90:
                case 91:
                case 92:
                case 93:
                case 94:
                case 95:
                case 96:
                case 97:
                case 98:
                case 99:
                case 100:
                case 101:
                case 104:
                case 105:
                case 106:
                    {
                    alt26=2;
                    }
                    break;
                default:
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 26, 1, input);

                    throw nvae;
                }

                }
                break;
            case ID:
                {
                alt26=2;
                }
                break;
            case 65:
                {
                switch ( input.LA(2) ) {
                case STRING:
                    {
                    int LA26_5 = input.LA(3);

                    if ( (synpred53_DSToolkitSQL()) ) {
                        alt26=1;
                    }
                    else if ( (true) ) {
                        alt26=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 26, 5, input);

                        throw nvae;
                    }
                    }
                    break;
                case EOF:
                case ID:
                case INT:
                case FLOAT:
                case NUMERIC:
                case 45:
                case 46:
                case 48:
                case 49:
                case 51:
                case 52:
                case 53:
                case 54:
                case 56:
                case 57:
                case 58:
                case 59:
                case 60:
                case 61:
                case 64:
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                case 70:
                case 71:
                case 72:
                case 73:
                case 74:
                case 75:
                case 76:
                case 77:
                case 78:
                case 79:
                case 81:
                case 83:
                case 84:
                case 85:
                case 86:
                case 88:
                case 89:
                case 90:
                case 91:
                case 92:
                case 93:
                case 94:
                case 95:
                case 96:
                case 97:
                case 98:
                case 99:
                case 100:
                case 101:
                case 104:
                case 105:
                case 106:
                    {
                    alt26=2;
                    }
                    break;
                default:
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 26, 3, input);

                    throw nvae;
                }

                }
                break;
            case 66:
                {
                switch ( input.LA(2) ) {
                case STRING:
                    {
                    int LA26_5 = input.LA(3);

                    if ( (synpred53_DSToolkitSQL()) ) {
                        alt26=1;
                    }
                    else if ( (true) ) {
                        alt26=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 26, 5, input);

                        throw nvae;
                    }
                    }
                    break;
                case EOF:
                case ID:
                case INT:
                case FLOAT:
                case NUMERIC:
                case 45:
                case 46:
                case 48:
                case 49:
                case 51:
                case 52:
                case 53:
                case 54:
                case 56:
                case 57:
                case 58:
                case 59:
                case 60:
                case 61:
                case 64:
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                case 70:
                case 71:
                case 72:
                case 73:
                case 74:
                case 75:
                case 76:
                case 77:
                case 78:
                case 79:
                case 81:
                case 83:
                case 84:
                case 85:
                case 86:
                case 88:
                case 89:
                case 90:
                case 91:
                case 92:
                case 93:
                case 94:
                case 95:
                case 96:
                case 97:
                case 98:
                case 99:
                case 100:
                case 101:
                case 104:
                case 105:
                case 106:
                    {
                    alt26=2;
                    }
                    break;
                default:
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 26, 4, input);

                    throw nvae;
                }

                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 26, 0, input);

                throw nvae;
            }

            switch (alt26) {
                case 1 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:170:4: ( 'DATE' | 'TIMESTAMP' | 'TIME' ) STRING
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    set73=(Token)input.LT(1);
                    set73=(Token)input.LT(1);
                    if ( (input.LA(1)>=64 && input.LA(1)<=66) ) {
                        input.consume();
                        if ( state.backtracking==0 ) root_0 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(set73), root_0);
                        state.errorRecovery=false;state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }

                    STRING74=(Token)match(input,STRING,FOLLOW_STRING_in_datetime860); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    STRING74_tree = (CommonTree)adaptor.create(STRING74);
                    adaptor.addChild(root_0, STRING74_tree);
                    }

                    }
                    break;
                case 2 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:171:5: (tableid= ID '.' )? (s= 'DATE' | s= 'TIMESTAMP' | s= 'TIME' )
                    {
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:171:5: (tableid= ID '.' )?
                    int alt24=2;
                    switch ( input.LA(1) ) {
                        case ID:
                            {
                            alt24=1;
                            }
                            break;
                    }

                    switch (alt24) {
                        case 1 :
                            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:171:6: tableid= ID '.'
                            {
                            tableid=(Token)match(input,ID,FOLLOW_ID_in_datetime869); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_ID.add(tableid);

                            char_literal75=(Token)match(input,63,FOLLOW_63_in_datetime870); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_63.add(char_literal75);


                            }
                            break;

                    }

                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:171:21: (s= 'DATE' | s= 'TIMESTAMP' | s= 'TIME' )
                    int alt25=3;
                    switch ( input.LA(1) ) {
                    case 64:
                        {
                        alt25=1;
                        }
                        break;
                    case 65:
                        {
                        alt25=2;
                        }
                        break;
                    case 66:
                        {
                        alt25=3;
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 25, 0, input);

                        throw nvae;
                    }

                    switch (alt25) {
                        case 1 :
                            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:171:22: s= 'DATE'
                            {
                            s=(Token)match(input,64,FOLLOW_64_in_datetime876); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_64.add(s);


                            }
                            break;
                        case 2 :
                            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:171:33: s= 'TIMESTAMP'
                            {
                            s=(Token)match(input,65,FOLLOW_65_in_datetime882); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_65.add(s);


                            }
                            break;
                        case 3 :
                            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:171:49: s= 'TIME'
                            {
                            s=(Token)match(input,66,FOLLOW_66_in_datetime888); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_66.add(s);


                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: tableid, s
                    // token labels: tableid, s
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_tableid=new RewriteRuleTokenStream(adaptor,"token tableid",tableid);
                    RewriteRuleTokenStream stream_s=new RewriteRuleTokenStream(adaptor,"token s",s);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 171:59: -> ^( SUPER_LEXICAL_OF_SUPER_ABSTRACT ( $tableid)? $s)
                    {
                        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:171:62: ^( SUPER_LEXICAL_OF_SUPER_ABSTRACT ( $tableid)? $s)
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(SUPER_LEXICAL_OF_SUPER_ABSTRACT, "SUPER_LEXICAL_OF_SUPER_ABSTRACT"), root_1);

                        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:171:96: ( $tableid)?
                        if ( stream_tableid.hasNext() ) {
                            adaptor.addChild(root_1, stream_tableid.nextNode());

                        }
                        stream_tableid.reset();
                        adaptor.addChild(root_1, stream_s.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "datetime"

    public static class interval_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "interval"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:173:1: interval : ( 'INTERVAL' STRING ( 'YEAR' | 'MONTH' | 'DAY' | 'HOUR' | 'MINUTE' | 'SECOND' ) | (tableid= ID '.' )? (s= 'INTERVAL' | s= 'YEAR' | s= 'MONTH' | s= 'DAY' | s= 'HOUR' | s= 'MINUTE' | s= 'SECOND' ) -> ^( SUPER_LEXICAL_OF_SUPER_ABSTRACT ( $tableid)? $s) );
    public final DSToolkitSQLParser.interval_return interval() throws RecognitionException {
        DSToolkitSQLParser.interval_return retval = new DSToolkitSQLParser.interval_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token tableid=null;
        Token s=null;
        Token string_literal76=null;
        Token STRING77=null;
        Token set78=null;
        Token char_literal79=null;

        CommonTree tableid_tree=null;
        CommonTree s_tree=null;
        CommonTree string_literal76_tree=null;
        CommonTree STRING77_tree=null;
        CommonTree set78_tree=null;
        CommonTree char_literal79_tree=null;
        RewriteRuleTokenStream stream_67=new RewriteRuleTokenStream(adaptor,"token 67");
        RewriteRuleTokenStream stream_69=new RewriteRuleTokenStream(adaptor,"token 69");
        RewriteRuleTokenStream stream_68=new RewriteRuleTokenStream(adaptor,"token 68");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_70=new RewriteRuleTokenStream(adaptor,"token 70");
        RewriteRuleTokenStream stream_71=new RewriteRuleTokenStream(adaptor,"token 71");
        RewriteRuleTokenStream stream_72=new RewriteRuleTokenStream(adaptor,"token 72");
        RewriteRuleTokenStream stream_63=new RewriteRuleTokenStream(adaptor,"token 63");
        RewriteRuleTokenStream stream_73=new RewriteRuleTokenStream(adaptor,"token 73");

        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:174:2: ( 'INTERVAL' STRING ( 'YEAR' | 'MONTH' | 'DAY' | 'HOUR' | 'MINUTE' | 'SECOND' ) | (tableid= ID '.' )? (s= 'INTERVAL' | s= 'YEAR' | s= 'MONTH' | s= 'DAY' | s= 'HOUR' | s= 'MINUTE' | s= 'SECOND' ) -> ^( SUPER_LEXICAL_OF_SUPER_ABSTRACT ( $tableid)? $s) )
            int alt29=2;
            alt29 = dfa29.predict(input);
            switch (alt29) {
                case 1 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:174:4: 'INTERVAL' STRING ( 'YEAR' | 'MONTH' | 'DAY' | 'HOUR' | 'MINUTE' | 'SECOND' )
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    string_literal76=(Token)match(input,67,FOLLOW_67_in_interval912); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal76_tree = (CommonTree)adaptor.create(string_literal76);
                    root_0 = (CommonTree)adaptor.becomeRoot(string_literal76_tree, root_0);
                    }
                    STRING77=(Token)match(input,STRING,FOLLOW_STRING_in_interval915); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    STRING77_tree = (CommonTree)adaptor.create(STRING77);
                    adaptor.addChild(root_0, STRING77_tree);
                    }
                    set78=(Token)input.LT(1);
                    if ( (input.LA(1)>=68 && input.LA(1)<=73) ) {
                        input.consume();
                        if ( state.backtracking==0 ) adaptor.addChild(root_0, (CommonTree)adaptor.create(set78));
                        state.errorRecovery=false;state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    }
                    break;
                case 2 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:175:4: (tableid= ID '.' )? (s= 'INTERVAL' | s= 'YEAR' | s= 'MONTH' | s= 'DAY' | s= 'HOUR' | s= 'MINUTE' | s= 'SECOND' )
                    {
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:175:4: (tableid= ID '.' )?
                    int alt27=2;
                    switch ( input.LA(1) ) {
                        case ID:
                            {
                            alt27=1;
                            }
                            break;
                    }

                    switch (alt27) {
                        case 1 :
                            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:175:5: tableid= ID '.'
                            {
                            tableid=(Token)match(input,ID,FOLLOW_ID_in_interval947); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_ID.add(tableid);

                            char_literal79=(Token)match(input,63,FOLLOW_63_in_interval948); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_63.add(char_literal79);


                            }
                            break;

                    }

                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:175:20: (s= 'INTERVAL' | s= 'YEAR' | s= 'MONTH' | s= 'DAY' | s= 'HOUR' | s= 'MINUTE' | s= 'SECOND' )
                    int alt28=7;
                    switch ( input.LA(1) ) {
                    case 67:
                        {
                        alt28=1;
                        }
                        break;
                    case 68:
                        {
                        alt28=2;
                        }
                        break;
                    case 69:
                        {
                        alt28=3;
                        }
                        break;
                    case 70:
                        {
                        alt28=4;
                        }
                        break;
                    case 71:
                        {
                        alt28=5;
                        }
                        break;
                    case 72:
                        {
                        alt28=6;
                        }
                        break;
                    case 73:
                        {
                        alt28=7;
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 28, 0, input);

                        throw nvae;
                    }

                    switch (alt28) {
                        case 1 :
                            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:175:21: s= 'INTERVAL'
                            {
                            s=(Token)match(input,67,FOLLOW_67_in_interval954); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_67.add(s);


                            }
                            break;
                        case 2 :
                            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:175:36: s= 'YEAR'
                            {
                            s=(Token)match(input,68,FOLLOW_68_in_interval960); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_68.add(s);


                            }
                            break;
                        case 3 :
                            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:175:47: s= 'MONTH'
                            {
                            s=(Token)match(input,69,FOLLOW_69_in_interval966); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_69.add(s);


                            }
                            break;
                        case 4 :
                            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:175:59: s= 'DAY'
                            {
                            s=(Token)match(input,70,FOLLOW_70_in_interval972); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_70.add(s);


                            }
                            break;
                        case 5 :
                            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:175:69: s= 'HOUR'
                            {
                            s=(Token)match(input,71,FOLLOW_71_in_interval978); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_71.add(s);


                            }
                            break;
                        case 6 :
                            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:175:80: s= 'MINUTE'
                            {
                            s=(Token)match(input,72,FOLLOW_72_in_interval984); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_72.add(s);


                            }
                            break;
                        case 7 :
                            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:175:93: s= 'SECOND'
                            {
                            s=(Token)match(input,73,FOLLOW_73_in_interval990); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_73.add(s);


                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: tableid, s
                    // token labels: tableid, s
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_tableid=new RewriteRuleTokenStream(adaptor,"token tableid",tableid);
                    RewriteRuleTokenStream stream_s=new RewriteRuleTokenStream(adaptor,"token s",s);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 175:105: -> ^( SUPER_LEXICAL_OF_SUPER_ABSTRACT ( $tableid)? $s)
                    {
                        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:175:108: ^( SUPER_LEXICAL_OF_SUPER_ABSTRACT ( $tableid)? $s)
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(SUPER_LEXICAL_OF_SUPER_ABSTRACT, "SUPER_LEXICAL_OF_SUPER_ABSTRACT"), root_1);

                        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:175:142: ( $tableid)?
                        if ( stream_tableid.hasNext() ) {
                            adaptor.addChild(root_1, stream_tableid.nextNode());

                        }
                        stream_tableid.reset();
                        adaptor.addChild(root_1, stream_s.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "interval"

    public static class function_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "function"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:178:1: function : ( (name= ID ) '(' ( value_expression )? ( ',' value_expression )* ')' -> ^( FUNCTION $name ( value_expression )* ) | (name= ID ) '(' '*' ')' -> ^( FUNCTION $name '*' ) );
    public final DSToolkitSQLParser.function_return function() throws RecognitionException {
        DSToolkitSQLParser.function_return retval = new DSToolkitSQLParser.function_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token name=null;
        Token char_literal80=null;
        Token char_literal82=null;
        Token char_literal84=null;
        Token char_literal85=null;
        Token char_literal86=null;
        Token char_literal87=null;
        DSToolkitSQLParser.value_expression_return value_expression81 = null;

        DSToolkitSQLParser.value_expression_return value_expression83 = null;


        CommonTree name_tree=null;
        CommonTree char_literal80_tree=null;
        CommonTree char_literal82_tree=null;
        CommonTree char_literal84_tree=null;
        CommonTree char_literal85_tree=null;
        CommonTree char_literal86_tree=null;
        CommonTree char_literal87_tree=null;
        RewriteRuleTokenStream stream_59=new RewriteRuleTokenStream(adaptor,"token 59");
        RewriteRuleTokenStream stream_58=new RewriteRuleTokenStream(adaptor,"token 58");
        RewriteRuleTokenStream stream_57=new RewriteRuleTokenStream(adaptor,"token 57");
        RewriteRuleTokenStream stream_56=new RewriteRuleTokenStream(adaptor,"token 56");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_value_expression=new RewriteRuleSubtreeStream(adaptor,"rule value_expression");
        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:178:9: ( (name= ID ) '(' ( value_expression )? ( ',' value_expression )* ')' -> ^( FUNCTION $name ( value_expression )* ) | (name= ID ) '(' '*' ')' -> ^( FUNCTION $name '*' ) )
            int alt32=2;
            switch ( input.LA(1) ) {
            case ID:
                {
                switch ( input.LA(2) ) {
                case 56:
                    {
                    switch ( input.LA(3) ) {
                    case 58:
                        {
                        alt32=2;
                        }
                        break;
                    case ID:
                    case INT:
                    case FLOAT:
                    case NUMERIC:
                    case STRING:
                    case 56:
                    case 57:
                    case 59:
                    case 64:
                    case 65:
                    case 66:
                    case 67:
                    case 68:
                    case 69:
                    case 70:
                    case 71:
                    case 72:
                    case 73:
                    case 74:
                    case 75:
                    case 77:
                    case 78:
                    case 79:
                        {
                        alt32=1;
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 32, 2, input);

                        throw nvae;
                    }

                    }
                    break;
                default:
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 32, 1, input);

                    throw nvae;
                }

                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 32, 0, input);

                throw nvae;
            }

            switch (alt32) {
                case 1 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:178:11: (name= ID ) '(' ( value_expression )? ( ',' value_expression )* ')'
                    {
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:178:11: (name= ID )
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:178:12: name= ID
                    {
                    name=(Token)match(input,ID,FOLLOW_ID_in_function1018); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(name);


                    }

                    char_literal80=(Token)match(input,56,FOLLOW_56_in_function1021); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_56.add(char_literal80);

                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:178:25: ( value_expression )?
                    int alt30=2;
                    switch ( input.LA(1) ) {
                        case ID:
                        case INT:
                        case FLOAT:
                        case NUMERIC:
                        case STRING:
                        case 56:
                        case 64:
                        case 65:
                        case 66:
                        case 67:
                        case 68:
                        case 69:
                        case 70:
                        case 71:
                        case 72:
                        case 73:
                        case 74:
                        case 75:
                        case 77:
                        case 78:
                        case 79:
                            {
                            alt30=1;
                            }
                            break;
                    }

                    switch (alt30) {
                        case 1 :
                            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:0:0: value_expression
                            {
                            pushFollow(FOLLOW_value_expression_in_function1023);
                            value_expression81=value_expression();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_value_expression.add(value_expression81.getTree());

                            }
                            break;

                    }

                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:178:43: ( ',' value_expression )*
                    loop31:
                    do {
                        int alt31=2;
                        switch ( input.LA(1) ) {
                        case 59:
                            {
                            alt31=1;
                            }
                            break;

                        }

                        switch (alt31) {
                    	case 1 :
                    	    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:178:44: ',' value_expression
                    	    {
                    	    char_literal82=(Token)match(input,59,FOLLOW_59_in_function1027); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_59.add(char_literal82);

                    	    pushFollow(FOLLOW_value_expression_in_function1029);
                    	    value_expression83=value_expression();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_value_expression.add(value_expression83.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop31;
                        }
                    } while (true);

                    char_literal84=(Token)match(input,57,FOLLOW_57_in_function1033); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_57.add(char_literal84);



                    // AST REWRITE
                    // elements: name, value_expression
                    // token labels: name
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_name=new RewriteRuleTokenStream(adaptor,"token name",name);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 179:4: -> ^( FUNCTION $name ( value_expression )* )
                    {
                        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:179:7: ^( FUNCTION $name ( value_expression )* )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(FUNCTION, "FUNCTION"), root_1);

                        adaptor.addChild(root_1, stream_name.nextNode());
                        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:179:24: ( value_expression )*
                        while ( stream_value_expression.hasNext() ) {
                            adaptor.addChild(root_1, stream_value_expression.nextTree());

                        }
                        stream_value_expression.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:180:5: (name= ID ) '(' '*' ')'
                    {
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:180:5: (name= ID )
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:180:6: name= ID
                    {
                    name=(Token)match(input,ID,FOLLOW_ID_in_function1058); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(name);


                    }

                    char_literal85=(Token)match(input,56,FOLLOW_56_in_function1061); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_56.add(char_literal85);

                    char_literal86=(Token)match(input,58,FOLLOW_58_in_function1063); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_58.add(char_literal86);

                    char_literal87=(Token)match(input,57,FOLLOW_57_in_function1065); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_57.add(char_literal87);



                    // AST REWRITE
                    // elements: name, 58
                    // token labels: name
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_name=new RewriteRuleTokenStream(adaptor,"token name",name);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 180:27: -> ^( FUNCTION $name '*' )
                    {
                        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:180:30: ^( FUNCTION $name '*' )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(FUNCTION, "FUNCTION"), root_1);

                        adaptor.addChild(root_1, stream_name.nextNode());
                        adaptor.addChild(root_1, stream_58.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "function"

    public static class string_value_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "string_value_expression"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:183:1: string_value_expression : ( super_lexical_name | STRING ) ( '||' ( super_lexical_name | STRING ) )+ ;
    public final DSToolkitSQLParser.string_value_expression_return string_value_expression() throws RecognitionException {
        DSToolkitSQLParser.string_value_expression_return retval = new DSToolkitSQLParser.string_value_expression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token STRING89=null;
        Token string_literal90=null;
        Token STRING92=null;
        DSToolkitSQLParser.super_lexical_name_return super_lexical_name88 = null;

        DSToolkitSQLParser.super_lexical_name_return super_lexical_name91 = null;


        CommonTree STRING89_tree=null;
        CommonTree string_literal90_tree=null;
        CommonTree STRING92_tree=null;

        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:184:2: ( ( super_lexical_name | STRING ) ( '||' ( super_lexical_name | STRING ) )+ )
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:184:4: ( super_lexical_name | STRING ) ( '||' ( super_lexical_name | STRING ) )+
            {
            root_0 = (CommonTree)adaptor.nil();

            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:184:4: ( super_lexical_name | STRING )
            int alt33=2;
            switch ( input.LA(1) ) {
            case ID:
                {
                alt33=1;
                }
                break;
            case STRING:
                {
                alt33=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 33, 0, input);

                throw nvae;
            }

            switch (alt33) {
                case 1 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:184:5: super_lexical_name
                    {
                    pushFollow(FOLLOW_super_lexical_name_in_string_value_expression1088);
                    super_lexical_name88=super_lexical_name();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, super_lexical_name88.getTree());

                    }
                    break;
                case 2 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:184:26: STRING
                    {
                    STRING89=(Token)match(input,STRING,FOLLOW_STRING_in_string_value_expression1092); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    STRING89_tree = (CommonTree)adaptor.create(STRING89);
                    adaptor.addChild(root_0, STRING89_tree);
                    }

                    }
                    break;

            }

            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:184:34: ( '||' ( super_lexical_name | STRING ) )+
            int cnt35=0;
            loop35:
            do {
                int alt35=2;
                switch ( input.LA(1) ) {
                case 80:
                    {
                    alt35=1;
                    }
                    break;

                }

                switch (alt35) {
            	case 1 :
            	    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:184:35: '||' ( super_lexical_name | STRING )
            	    {
            	    string_literal90=(Token)match(input,80,FOLLOW_80_in_string_value_expression1096); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal90_tree = (CommonTree)adaptor.create(string_literal90);
            	    root_0 = (CommonTree)adaptor.becomeRoot(string_literal90_tree, root_0);
            	    }
            	    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:184:41: ( super_lexical_name | STRING )
            	    int alt34=2;
            	    switch ( input.LA(1) ) {
            	    case ID:
            	        {
            	        alt34=1;
            	        }
            	        break;
            	    case STRING:
            	        {
            	        alt34=2;
            	        }
            	        break;
            	    default:
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 34, 0, input);

            	        throw nvae;
            	    }

            	    switch (alt34) {
            	        case 1 :
            	            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:184:42: super_lexical_name
            	            {
            	            pushFollow(FOLLOW_super_lexical_name_in_string_value_expression1100);
            	            super_lexical_name91=super_lexical_name();

            	            state._fsp--;
            	            if (state.failed) return retval;
            	            if ( state.backtracking==0 ) adaptor.addChild(root_0, super_lexical_name91.getTree());

            	            }
            	            break;
            	        case 2 :
            	            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:184:63: STRING
            	            {
            	            STRING92=(Token)match(input,STRING,FOLLOW_STRING_in_string_value_expression1104); if (state.failed) return retval;
            	            if ( state.backtracking==0 ) {
            	            STRING92_tree = (CommonTree)adaptor.create(STRING92);
            	            adaptor.addChild(root_0, STRING92_tree);
            	            }

            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt35 >= 1 ) break loop35;
            	    if (state.backtracking>0) {state.failed=true; return retval;}
                        EarlyExitException eee =
                            new EarlyExitException(35, input);
                        throw eee;
                }
                cnt35++;
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "string_value_expression"

    public static class super_abstract_expression_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "super_abstract_expression"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:187:1: super_abstract_expression : super_abstract_reference ;
    public final DSToolkitSQLParser.super_abstract_expression_return super_abstract_expression() throws RecognitionException {
        DSToolkitSQLParser.super_abstract_expression_return retval = new DSToolkitSQLParser.super_abstract_expression_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        DSToolkitSQLParser.super_abstract_reference_return super_abstract_reference93 = null;



        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:188:2: ( super_abstract_reference )
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:188:4: super_abstract_reference
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_super_abstract_reference_in_super_abstract_expression1119);
            super_abstract_reference93=super_abstract_reference();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, super_abstract_reference93.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "super_abstract_expression"

    public static class super_abstract_reference_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "super_abstract_reference"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:190:1: super_abstract_reference : super_abstract ( ',' super_abstract_reference )* ;
    public final DSToolkitSQLParser.super_abstract_reference_return super_abstract_reference() throws RecognitionException {
        DSToolkitSQLParser.super_abstract_reference_return retval = new DSToolkitSQLParser.super_abstract_reference_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal95=null;
        DSToolkitSQLParser.super_abstract_return super_abstract94 = null;

        DSToolkitSQLParser.super_abstract_reference_return super_abstract_reference96 = null;


        CommonTree char_literal95_tree=null;

        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:191:2: ( super_abstract ( ',' super_abstract_reference )* )
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:191:4: super_abstract ( ',' super_abstract_reference )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_super_abstract_in_super_abstract_reference1129);
            super_abstract94=super_abstract();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, super_abstract94.getTree());
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:191:19: ( ',' super_abstract_reference )*
            loop36:
            do {
                int alt36=2;
                switch ( input.LA(1) ) {
                case 59:
                    {
                    int LA36_2 = input.LA(2);

                    if ( (synpred76_DSToolkitSQL()) ) {
                        alt36=1;
                    }


                    }
                    break;

                }

                switch (alt36) {
            	case 1 :
            	    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:191:20: ',' super_abstract_reference
            	    {
            	    char_literal95=(Token)match(input,59,FOLLOW_59_in_super_abstract_reference1132); if (state.failed) return retval;
            	    pushFollow(FOLLOW_super_abstract_reference_in_super_abstract_reference1135);
            	    super_abstract_reference96=super_abstract_reference();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, super_abstract_reference96.getTree());

            	    }
            	    break;

            	default :
            	    break loop36;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "super_abstract_reference"

    public static class join_type_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "join_type"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:194:1: join_type : ( 'RIGHT' ( 'OUTER' )? 'JOIN' -> RIGHT_OUTER_JOIN | 'LEFT' ( 'OUTER' )? 'JOIN' -> LEFT_OUTER_JOIN | 'FULL' ( 'OUTER' )? 'JOIN' -> FULL_OUTER_JOIN | ( 'INNER' )? 'JOIN' -> JOIN );
    public final DSToolkitSQLParser.join_type_return join_type() throws RecognitionException {
        DSToolkitSQLParser.join_type_return retval = new DSToolkitSQLParser.join_type_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal97=null;
        Token string_literal98=null;
        Token string_literal99=null;
        Token string_literal100=null;
        Token string_literal101=null;
        Token string_literal102=null;
        Token string_literal103=null;
        Token string_literal104=null;
        Token string_literal105=null;
        Token string_literal106=null;
        Token string_literal107=null;

        CommonTree string_literal97_tree=null;
        CommonTree string_literal98_tree=null;
        CommonTree string_literal99_tree=null;
        CommonTree string_literal100_tree=null;
        CommonTree string_literal101_tree=null;
        CommonTree string_literal102_tree=null;
        CommonTree string_literal103_tree=null;
        CommonTree string_literal104_tree=null;
        CommonTree string_literal105_tree=null;
        CommonTree string_literal106_tree=null;
        CommonTree string_literal107_tree=null;
        RewriteRuleTokenStream stream_82=new RewriteRuleTokenStream(adaptor,"token 82");
        RewriteRuleTokenStream stream_83=new RewriteRuleTokenStream(adaptor,"token 83");
        RewriteRuleTokenStream stream_81=new RewriteRuleTokenStream(adaptor,"token 81");
        RewriteRuleTokenStream stream_86=new RewriteRuleTokenStream(adaptor,"token 86");
        RewriteRuleTokenStream stream_84=new RewriteRuleTokenStream(adaptor,"token 84");
        RewriteRuleTokenStream stream_85=new RewriteRuleTokenStream(adaptor,"token 85");

        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:195:2: ( 'RIGHT' ( 'OUTER' )? 'JOIN' -> RIGHT_OUTER_JOIN | 'LEFT' ( 'OUTER' )? 'JOIN' -> LEFT_OUTER_JOIN | 'FULL' ( 'OUTER' )? 'JOIN' -> FULL_OUTER_JOIN | ( 'INNER' )? 'JOIN' -> JOIN )
            int alt41=4;
            switch ( input.LA(1) ) {
            case 81:
                {
                alt41=1;
                }
                break;
            case 84:
                {
                alt41=2;
                }
                break;
            case 85:
                {
                alt41=3;
                }
                break;
            case 83:
            case 86:
                {
                alt41=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 41, 0, input);

                throw nvae;
            }

            switch (alt41) {
                case 1 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:195:4: 'RIGHT' ( 'OUTER' )? 'JOIN'
                    {
                    string_literal97=(Token)match(input,81,FOLLOW_81_in_join_type1148); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_81.add(string_literal97);

                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:195:12: ( 'OUTER' )?
                    int alt37=2;
                    switch ( input.LA(1) ) {
                        case 82:
                            {
                            alt37=1;
                            }
                            break;
                    }

                    switch (alt37) {
                        case 1 :
                            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:0:0: 'OUTER'
                            {
                            string_literal98=(Token)match(input,82,FOLLOW_82_in_join_type1150); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_82.add(string_literal98);


                            }
                            break;

                    }

                    string_literal99=(Token)match(input,83,FOLLOW_83_in_join_type1153); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_83.add(string_literal99);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 195:28: -> RIGHT_OUTER_JOIN
                    {
                        adaptor.addChild(root_0, (CommonTree)adaptor.create(RIGHT_OUTER_JOIN, "RIGHT_OUTER_JOIN"));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:196:4: 'LEFT' ( 'OUTER' )? 'JOIN'
                    {
                    string_literal100=(Token)match(input,84,FOLLOW_84_in_join_type1163); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_84.add(string_literal100);

                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:196:11: ( 'OUTER' )?
                    int alt38=2;
                    switch ( input.LA(1) ) {
                        case 82:
                            {
                            alt38=1;
                            }
                            break;
                    }

                    switch (alt38) {
                        case 1 :
                            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:0:0: 'OUTER'
                            {
                            string_literal101=(Token)match(input,82,FOLLOW_82_in_join_type1165); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_82.add(string_literal101);


                            }
                            break;

                    }

                    string_literal102=(Token)match(input,83,FOLLOW_83_in_join_type1168); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_83.add(string_literal102);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 196:27: -> LEFT_OUTER_JOIN
                    {
                        adaptor.addChild(root_0, (CommonTree)adaptor.create(LEFT_OUTER_JOIN, "LEFT_OUTER_JOIN"));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:197:4: 'FULL' ( 'OUTER' )? 'JOIN'
                    {
                    string_literal103=(Token)match(input,85,FOLLOW_85_in_join_type1177); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_85.add(string_literal103);

                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:197:11: ( 'OUTER' )?
                    int alt39=2;
                    switch ( input.LA(1) ) {
                        case 82:
                            {
                            alt39=1;
                            }
                            break;
                    }

                    switch (alt39) {
                        case 1 :
                            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:0:0: 'OUTER'
                            {
                            string_literal104=(Token)match(input,82,FOLLOW_82_in_join_type1179); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_82.add(string_literal104);


                            }
                            break;

                    }

                    string_literal105=(Token)match(input,83,FOLLOW_83_in_join_type1182); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_83.add(string_literal105);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 197:27: -> FULL_OUTER_JOIN
                    {
                        adaptor.addChild(root_0, (CommonTree)adaptor.create(FULL_OUTER_JOIN, "FULL_OUTER_JOIN"));

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 4 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:198:5: ( 'INNER' )? 'JOIN'
                    {
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:198:5: ( 'INNER' )?
                    int alt40=2;
                    switch ( input.LA(1) ) {
                        case 86:
                            {
                            alt40=1;
                            }
                            break;
                    }

                    switch (alt40) {
                        case 1 :
                            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:0:0: 'INNER'
                            {
                            string_literal106=(Token)match(input,86,FOLLOW_86_in_join_type1192); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_86.add(string_literal106);


                            }
                            break;

                    }

                    string_literal107=(Token)match(input,83,FOLLOW_83_in_join_type1195); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_83.add(string_literal107);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 198:21: -> JOIN
                    {
                        adaptor.addChild(root_0, (CommonTree)adaptor.create(JOIN, "JOIN"));

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "join_type"

    public static class super_abstract_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "super_abstract"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:200:1: super_abstract : non_join_super_abstract ( join_type non_join_super_abstract 'ON' search_condition )* ;
    public final DSToolkitSQLParser.super_abstract_return super_abstract() throws RecognitionException {
        DSToolkitSQLParser.super_abstract_return retval = new DSToolkitSQLParser.super_abstract_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal111=null;
        DSToolkitSQLParser.non_join_super_abstract_return non_join_super_abstract108 = null;

        DSToolkitSQLParser.join_type_return join_type109 = null;

        DSToolkitSQLParser.non_join_super_abstract_return non_join_super_abstract110 = null;

        DSToolkitSQLParser.search_condition_return search_condition112 = null;


        CommonTree string_literal111_tree=null;

        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:200:17: ( non_join_super_abstract ( join_type non_join_super_abstract 'ON' search_condition )* )
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:200:19: non_join_super_abstract ( join_type non_join_super_abstract 'ON' search_condition )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_non_join_super_abstract_in_super_abstract1209);
            non_join_super_abstract108=non_join_super_abstract();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, non_join_super_abstract108.getTree());
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:200:43: ( join_type non_join_super_abstract 'ON' search_condition )*
            loop42:
            do {
                int alt42=2;
                switch ( input.LA(1) ) {
                case 81:
                case 83:
                case 84:
                case 85:
                case 86:
                    {
                    alt42=1;
                    }
                    break;

                }

                switch (alt42) {
            	case 1 :
            	    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:200:44: join_type non_join_super_abstract 'ON' search_condition
            	    {
            	    pushFollow(FOLLOW_join_type_in_super_abstract1212);
            	    join_type109=join_type();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) root_0 = (CommonTree)adaptor.becomeRoot(join_type109.getTree(), root_0);
            	    pushFollow(FOLLOW_non_join_super_abstract_in_super_abstract1215);
            	    non_join_super_abstract110=non_join_super_abstract();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, non_join_super_abstract110.getTree());
            	    string_literal111=(Token)match(input,87,FOLLOW_87_in_super_abstract1217); if (state.failed) return retval;
            	    pushFollow(FOLLOW_search_condition_in_super_abstract1220);
            	    search_condition112=search_condition();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, search_condition112.getTree());

            	    }
            	    break;

            	default :
            	    break loop42;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "super_abstract"

    public static class non_join_super_abstract_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "non_join_super_abstract"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:204:1: non_join_super_abstract : ( (schema_id= ID '.' )? (super_abstract_id= ID ) ( correlation_specification )? -> ^( SUPER_ABSTRACT ( $schema_id)? $super_abstract_id ( correlation_specification )? ) | super_abstract_function correlation_specification -> ^( SUPER_ABSTRACT super_abstract_function correlation_specification ) | sub_query correlation_specification -> ^( SUPER_ABSTRACT sub_query correlation_specification ) );
    public final DSToolkitSQLParser.non_join_super_abstract_return non_join_super_abstract() throws RecognitionException {
        DSToolkitSQLParser.non_join_super_abstract_return retval = new DSToolkitSQLParser.non_join_super_abstract_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token schema_id=null;
        Token super_abstract_id=null;
        Token char_literal113=null;
        DSToolkitSQLParser.correlation_specification_return correlation_specification114 = null;

        DSToolkitSQLParser.super_abstract_function_return super_abstract_function115 = null;

        DSToolkitSQLParser.correlation_specification_return correlation_specification116 = null;

        DSToolkitSQLParser.sub_query_return sub_query117 = null;

        DSToolkitSQLParser.correlation_specification_return correlation_specification118 = null;


        CommonTree schema_id_tree=null;
        CommonTree super_abstract_id_tree=null;
        CommonTree char_literal113_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_63=new RewriteRuleTokenStream(adaptor,"token 63");
        RewriteRuleSubtreeStream stream_super_abstract_function=new RewriteRuleSubtreeStream(adaptor,"rule super_abstract_function");
        RewriteRuleSubtreeStream stream_sub_query=new RewriteRuleSubtreeStream(adaptor,"rule sub_query");
        RewriteRuleSubtreeStream stream_correlation_specification=new RewriteRuleSubtreeStream(adaptor,"rule correlation_specification");
        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:205:2: ( (schema_id= ID '.' )? (super_abstract_id= ID ) ( correlation_specification )? -> ^( SUPER_ABSTRACT ( $schema_id)? $super_abstract_id ( correlation_specification )? ) | super_abstract_function correlation_specification -> ^( SUPER_ABSTRACT super_abstract_function correlation_specification ) | sub_query correlation_specification -> ^( SUPER_ABSTRACT sub_query correlation_specification ) )
            int alt45=3;
            switch ( input.LA(1) ) {
            case ID:
                {
                switch ( input.LA(2) ) {
                case EOF:
                case ID:
                case 45:
                case 46:
                case 48:
                case 49:
                case 52:
                case 53:
                case 54:
                case 57:
                case 59:
                case 60:
                case 61:
                case 63:
                case 81:
                case 83:
                case 84:
                case 85:
                case 86:
                case 87:
                    {
                    alt45=1;
                    }
                    break;
                case 56:
                    {
                    alt45=2;
                    }
                    break;
                default:
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 45, 1, input);

                    throw nvae;
                }

                }
                break;
            case 56:
                {
                alt45=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 45, 0, input);

                throw nvae;
            }

            switch (alt45) {
                case 1 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:205:4: (schema_id= ID '.' )? (super_abstract_id= ID ) ( correlation_specification )?
                    {
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:205:4: (schema_id= ID '.' )?
                    int alt43=2;
                    switch ( input.LA(1) ) {
                        case ID:
                            {
                            switch ( input.LA(2) ) {
                                case 63:
                                    {
                                    alt43=1;
                                    }
                                    break;
                            }

                            }
                            break;
                    }

                    switch (alt43) {
                        case 1 :
                            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:205:5: schema_id= ID '.'
                            {
                            schema_id=(Token)match(input,ID,FOLLOW_ID_in_non_join_super_abstract1237); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_ID.add(schema_id);

                            char_literal113=(Token)match(input,63,FOLLOW_63_in_non_join_super_abstract1238); if (state.failed) return retval; 
                            if ( state.backtracking==0 ) stream_63.add(char_literal113);


                            }
                            break;

                    }

                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:205:22: (super_abstract_id= ID )
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:205:23: super_abstract_id= ID
                    {
                    super_abstract_id=(Token)match(input,ID,FOLLOW_ID_in_non_join_super_abstract1244); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(super_abstract_id);


                    }

                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:205:45: ( correlation_specification )?
                    int alt44=2;
                    switch ( input.LA(1) ) {
                        case ID:
                        case 60:
                            {
                            alt44=1;
                            }
                            break;
                    }

                    switch (alt44) {
                        case 1 :
                            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:0:0: correlation_specification
                            {
                            pushFollow(FOLLOW_correlation_specification_in_non_join_super_abstract1247);
                            correlation_specification114=correlation_specification();

                            state._fsp--;
                            if (state.failed) return retval;
                            if ( state.backtracking==0 ) stream_correlation_specification.add(correlation_specification114.getTree());

                            }
                            break;

                    }



                    // AST REWRITE
                    // elements: correlation_specification, schema_id, super_abstract_id
                    // token labels: super_abstract_id, schema_id
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_super_abstract_id=new RewriteRuleTokenStream(adaptor,"token super_abstract_id",super_abstract_id);
                    RewriteRuleTokenStream stream_schema_id=new RewriteRuleTokenStream(adaptor,"token schema_id",schema_id);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 205:72: -> ^( SUPER_ABSTRACT ( $schema_id)? $super_abstract_id ( correlation_specification )? )
                    {
                        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:205:75: ^( SUPER_ABSTRACT ( $schema_id)? $super_abstract_id ( correlation_specification )? )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(SUPER_ABSTRACT, "SUPER_ABSTRACT"), root_1);

                        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:205:92: ( $schema_id)?
                        if ( stream_schema_id.hasNext() ) {
                            adaptor.addChild(root_1, stream_schema_id.nextNode());

                        }
                        stream_schema_id.reset();
                        adaptor.addChild(root_1, stream_super_abstract_id.nextNode());
                        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:205:123: ( correlation_specification )?
                        if ( stream_correlation_specification.hasNext() ) {
                            adaptor.addChild(root_1, stream_correlation_specification.nextTree());

                        }
                        stream_correlation_specification.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:206:5: super_abstract_function correlation_specification
                    {
                    pushFollow(FOLLOW_super_abstract_function_in_non_join_super_abstract1270);
                    super_abstract_function115=super_abstract_function();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_super_abstract_function.add(super_abstract_function115.getTree());
                    pushFollow(FOLLOW_correlation_specification_in_non_join_super_abstract1272);
                    correlation_specification116=correlation_specification();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_correlation_specification.add(correlation_specification116.getTree());


                    // AST REWRITE
                    // elements: correlation_specification, super_abstract_function
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 206:55: -> ^( SUPER_ABSTRACT super_abstract_function correlation_specification )
                    {
                        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:206:58: ^( SUPER_ABSTRACT super_abstract_function correlation_specification )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(SUPER_ABSTRACT, "SUPER_ABSTRACT"), root_1);

                        adaptor.addChild(root_1, stream_super_abstract_function.nextTree());
                        adaptor.addChild(root_1, stream_correlation_specification.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 3 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:207:5: sub_query correlation_specification
                    {
                    pushFollow(FOLLOW_sub_query_in_non_join_super_abstract1288);
                    sub_query117=sub_query();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_sub_query.add(sub_query117.getTree());
                    pushFollow(FOLLOW_correlation_specification_in_non_join_super_abstract1290);
                    correlation_specification118=correlation_specification();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_correlation_specification.add(correlation_specification118.getTree());


                    // AST REWRITE
                    // elements: sub_query, correlation_specification
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 207:41: -> ^( SUPER_ABSTRACT sub_query correlation_specification )
                    {
                        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:207:44: ^( SUPER_ABSTRACT sub_query correlation_specification )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(SUPER_ABSTRACT, "SUPER_ABSTRACT"), root_1);

                        adaptor.addChild(root_1, stream_sub_query.nextTree());
                        adaptor.addChild(root_1, stream_correlation_specification.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "non_join_super_abstract"

    public static class super_abstract_function_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "super_abstract_function"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:210:1: super_abstract_function : name= ID '(' ( super_abstract_function_subquery )? ( ',' super_abstract_function_subquery )* ( ( ',' )? super_abstract_function_param )* ')' -> ^( FUNCTION $name ( super_abstract_function_subquery )* ( super_abstract_function_param )* ) ;
    public final DSToolkitSQLParser.super_abstract_function_return super_abstract_function() throws RecognitionException {
        DSToolkitSQLParser.super_abstract_function_return retval = new DSToolkitSQLParser.super_abstract_function_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token name=null;
        Token char_literal119=null;
        Token char_literal121=null;
        Token char_literal123=null;
        Token char_literal125=null;
        DSToolkitSQLParser.super_abstract_function_subquery_return super_abstract_function_subquery120 = null;

        DSToolkitSQLParser.super_abstract_function_subquery_return super_abstract_function_subquery122 = null;

        DSToolkitSQLParser.super_abstract_function_param_return super_abstract_function_param124 = null;


        CommonTree name_tree=null;
        CommonTree char_literal119_tree=null;
        CommonTree char_literal121_tree=null;
        CommonTree char_literal123_tree=null;
        CommonTree char_literal125_tree=null;
        RewriteRuleTokenStream stream_59=new RewriteRuleTokenStream(adaptor,"token 59");
        RewriteRuleTokenStream stream_57=new RewriteRuleTokenStream(adaptor,"token 57");
        RewriteRuleTokenStream stream_56=new RewriteRuleTokenStream(adaptor,"token 56");
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleSubtreeStream stream_super_abstract_function_subquery=new RewriteRuleSubtreeStream(adaptor,"rule super_abstract_function_subquery");
        RewriteRuleSubtreeStream stream_super_abstract_function_param=new RewriteRuleSubtreeStream(adaptor,"rule super_abstract_function_param");
        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:211:2: (name= ID '(' ( super_abstract_function_subquery )? ( ',' super_abstract_function_subquery )* ( ( ',' )? super_abstract_function_param )* ')' -> ^( FUNCTION $name ( super_abstract_function_subquery )* ( super_abstract_function_param )* ) )
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:211:4: name= ID '(' ( super_abstract_function_subquery )? ( ',' super_abstract_function_subquery )* ( ( ',' )? super_abstract_function_param )* ')'
            {
            name=(Token)match(input,ID,FOLLOW_ID_in_super_abstract_function1314); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(name);

            char_literal119=(Token)match(input,56,FOLLOW_56_in_super_abstract_function1316); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_56.add(char_literal119);

            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:211:16: ( super_abstract_function_subquery )?
            int alt46=2;
            alt46 = dfa46.predict(input);
            switch (alt46) {
                case 1 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:0:0: super_abstract_function_subquery
                    {
                    pushFollow(FOLLOW_super_abstract_function_subquery_in_super_abstract_function1318);
                    super_abstract_function_subquery120=super_abstract_function_subquery();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_super_abstract_function_subquery.add(super_abstract_function_subquery120.getTree());

                    }
                    break;

            }

            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:211:50: ( ',' super_abstract_function_subquery )*
            loop47:
            do {
                int alt47=2;
                alt47 = dfa47.predict(input);
                switch (alt47) {
            	case 1 :
            	    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:211:51: ',' super_abstract_function_subquery
            	    {
            	    char_literal121=(Token)match(input,59,FOLLOW_59_in_super_abstract_function1322); if (state.failed) return retval; 
            	    if ( state.backtracking==0 ) stream_59.add(char_literal121);

            	    pushFollow(FOLLOW_super_abstract_function_subquery_in_super_abstract_function1324);
            	    super_abstract_function_subquery122=super_abstract_function_subquery();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_super_abstract_function_subquery.add(super_abstract_function_subquery122.getTree());

            	    }
            	    break;

            	default :
            	    break loop47;
                }
            } while (true);

            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:211:90: ( ( ',' )? super_abstract_function_param )*
            loop49:
            do {
                int alt49=2;
                switch ( input.LA(1) ) {
                case ID:
                case INT:
                case FLOAT:
                case NUMERIC:
                case STRING:
                case 56:
                case 59:
                case 64:
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                case 70:
                case 71:
                case 72:
                case 73:
                case 74:
                case 75:
                case 77:
                case 78:
                case 79:
                case 90:
                case 94:
                case 105:
                case 106:
                    {
                    alt49=1;
                    }
                    break;

                }

                switch (alt49) {
            	case 1 :
            	    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:211:91: ( ',' )? super_abstract_function_param
            	    {
            	    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:211:91: ( ',' )?
            	    int alt48=2;
            	    switch ( input.LA(1) ) {
            	        case 59:
            	            {
            	            alt48=1;
            	            }
            	            break;
            	    }

            	    switch (alt48) {
            	        case 1 :
            	            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:0:0: ','
            	            {
            	            char_literal123=(Token)match(input,59,FOLLOW_59_in_super_abstract_function1329); if (state.failed) return retval; 
            	            if ( state.backtracking==0 ) stream_59.add(char_literal123);


            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_super_abstract_function_param_in_super_abstract_function1332);
            	    super_abstract_function_param124=super_abstract_function_param();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) stream_super_abstract_function_param.add(super_abstract_function_param124.getTree());

            	    }
            	    break;

            	default :
            	    break loop49;
                }
            } while (true);

            char_literal125=(Token)match(input,57,FOLLOW_57_in_super_abstract_function1336); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_57.add(char_literal125);



            // AST REWRITE
            // elements: name, super_abstract_function_param, super_abstract_function_subquery
            // token labels: name
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_name=new RewriteRuleTokenStream(adaptor,"token name",name);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 212:5: -> ^( FUNCTION $name ( super_abstract_function_subquery )* ( super_abstract_function_param )* )
            {
                // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:212:8: ^( FUNCTION $name ( super_abstract_function_subquery )* ( super_abstract_function_param )* )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(FUNCTION, "FUNCTION"), root_1);

                adaptor.addChild(root_1, stream_name.nextNode());
                // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:212:25: ( super_abstract_function_subquery )*
                while ( stream_super_abstract_function_subquery.hasNext() ) {
                    adaptor.addChild(root_1, stream_super_abstract_function_subquery.nextTree());

                }
                stream_super_abstract_function_subquery.reset();
                // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:212:61: ( super_abstract_function_param )*
                while ( stream_super_abstract_function_param.hasNext() ) {
                    adaptor.addChild(root_1, stream_super_abstract_function_param.nextTree());

                }
                stream_super_abstract_function_param.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "super_abstract_function"

    public static class super_abstract_function_subquery_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "super_abstract_function_subquery"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:215:1: super_abstract_function_subquery : sub_query correlation_specification -> ^( SUPER_ABSTRACT sub_query correlation_specification ) ;
    public final DSToolkitSQLParser.super_abstract_function_subquery_return super_abstract_function_subquery() throws RecognitionException {
        DSToolkitSQLParser.super_abstract_function_subquery_return retval = new DSToolkitSQLParser.super_abstract_function_subquery_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        DSToolkitSQLParser.sub_query_return sub_query126 = null;

        DSToolkitSQLParser.correlation_specification_return correlation_specification127 = null;


        RewriteRuleSubtreeStream stream_sub_query=new RewriteRuleSubtreeStream(adaptor,"rule sub_query");
        RewriteRuleSubtreeStream stream_correlation_specification=new RewriteRuleSubtreeStream(adaptor,"rule correlation_specification");
        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:216:2: ( sub_query correlation_specification -> ^( SUPER_ABSTRACT sub_query correlation_specification ) )
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:216:4: sub_query correlation_specification
            {
            pushFollow(FOLLOW_sub_query_in_super_abstract_function_subquery1368);
            sub_query126=sub_query();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_sub_query.add(sub_query126.getTree());
            pushFollow(FOLLOW_correlation_specification_in_super_abstract_function_subquery1370);
            correlation_specification127=correlation_specification();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_correlation_specification.add(correlation_specification127.getTree());


            // AST REWRITE
            // elements: sub_query, correlation_specification
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 216:40: -> ^( SUPER_ABSTRACT sub_query correlation_specification )
            {
                // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:216:43: ^( SUPER_ABSTRACT sub_query correlation_specification )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(SUPER_ABSTRACT, "SUPER_ABSTRACT"), root_1);

                adaptor.addChild(root_1, stream_sub_query.nextTree());
                adaptor.addChild(root_1, stream_correlation_specification.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "super_abstract_function_subquery"

    public static class super_abstract_function_param_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "super_abstract_function_param"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:219:1: super_abstract_function_param : ( search_condition | value_expression );
    public final DSToolkitSQLParser.super_abstract_function_param_return super_abstract_function_param() throws RecognitionException {
        DSToolkitSQLParser.super_abstract_function_param_return retval = new DSToolkitSQLParser.super_abstract_function_param_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        DSToolkitSQLParser.search_condition_return search_condition128 = null;

        DSToolkitSQLParser.value_expression_return value_expression129 = null;



        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:220:2: ( search_condition | value_expression )
            int alt50=2;
            alt50 = dfa50.predict(input);
            switch (alt50) {
                case 1 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:220:4: search_condition
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_search_condition_in_super_abstract_function_param1391);
                    search_condition128=search_condition();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, search_condition128.getTree());

                    }
                    break;
                case 2 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:221:4: value_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_value_expression_in_super_abstract_function_param1396);
                    value_expression129=value_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, value_expression129.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "super_abstract_function_param"

    public static class search_condition_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "search_condition"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:224:1: search_condition : boolean_factor ( 'OR' boolean_factor )* ;
    public final DSToolkitSQLParser.search_condition_return search_condition() throws RecognitionException {
        DSToolkitSQLParser.search_condition_return retval = new DSToolkitSQLParser.search_condition_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal131=null;
        DSToolkitSQLParser.boolean_factor_return boolean_factor130 = null;

        DSToolkitSQLParser.boolean_factor_return boolean_factor132 = null;


        CommonTree string_literal131_tree=null;

        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:225:2: ( boolean_factor ( 'OR' boolean_factor )* )
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:225:4: boolean_factor ( 'OR' boolean_factor )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_boolean_factor_in_search_condition1409);
            boolean_factor130=boolean_factor();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, boolean_factor130.getTree());
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:225:19: ( 'OR' boolean_factor )*
            loop51:
            do {
                int alt51=2;
                switch ( input.LA(1) ) {
                case 88:
                    {
                    alt51=1;
                    }
                    break;

                }

                switch (alt51) {
            	case 1 :
            	    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:225:20: 'OR' boolean_factor
            	    {
            	    string_literal131=(Token)match(input,88,FOLLOW_88_in_search_condition1412); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal131_tree = (CommonTree)adaptor.create(string_literal131);
            	    root_0 = (CommonTree)adaptor.becomeRoot(string_literal131_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_boolean_factor_in_search_condition1415);
            	    boolean_factor132=boolean_factor();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, boolean_factor132.getTree());

            	    }
            	    break;

            	default :
            	    break loop51;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "search_condition"

    public static class boolean_factor_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "boolean_factor"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:227:1: boolean_factor : boolean_term ( 'AND' boolean_term )* ;
    public final DSToolkitSQLParser.boolean_factor_return boolean_factor() throws RecognitionException {
        DSToolkitSQLParser.boolean_factor_return retval = new DSToolkitSQLParser.boolean_factor_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal134=null;
        DSToolkitSQLParser.boolean_term_return boolean_term133 = null;

        DSToolkitSQLParser.boolean_term_return boolean_term135 = null;


        CommonTree string_literal134_tree=null;

        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:228:2: ( boolean_term ( 'AND' boolean_term )* )
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:228:4: boolean_term ( 'AND' boolean_term )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_boolean_term_in_boolean_factor1427);
            boolean_term133=boolean_term();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, boolean_term133.getTree());
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:228:17: ( 'AND' boolean_term )*
            loop52:
            do {
                int alt52=2;
                switch ( input.LA(1) ) {
                case 89:
                    {
                    alt52=1;
                    }
                    break;

                }

                switch (alt52) {
            	case 1 :
            	    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:228:18: 'AND' boolean_term
            	    {
            	    string_literal134=(Token)match(input,89,FOLLOW_89_in_boolean_factor1430); if (state.failed) return retval;
            	    if ( state.backtracking==0 ) {
            	    string_literal134_tree = (CommonTree)adaptor.create(string_literal134);
            	    root_0 = (CommonTree)adaptor.becomeRoot(string_literal134_tree, root_0);
            	    }
            	    pushFollow(FOLLOW_boolean_term_in_boolean_factor1433);
            	    boolean_term135=boolean_term();

            	    state._fsp--;
            	    if (state.failed) return retval;
            	    if ( state.backtracking==0 ) adaptor.addChild(root_0, boolean_term135.getTree());

            	    }
            	    break;

            	default :
            	    break loop52;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "boolean_factor"

    public static class boolean_term_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "boolean_term"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:230:1: boolean_term : ( boolean_test | 'NOT' boolean_term -> ^( NOT boolean_term ) );
    public final DSToolkitSQLParser.boolean_term_return boolean_term() throws RecognitionException {
        DSToolkitSQLParser.boolean_term_return retval = new DSToolkitSQLParser.boolean_term_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal137=null;
        DSToolkitSQLParser.boolean_test_return boolean_test136 = null;

        DSToolkitSQLParser.boolean_term_return boolean_term138 = null;


        CommonTree string_literal137_tree=null;
        RewriteRuleTokenStream stream_90=new RewriteRuleTokenStream(adaptor,"token 90");
        RewriteRuleSubtreeStream stream_boolean_term=new RewriteRuleSubtreeStream(adaptor,"rule boolean_term");
        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:231:2: ( boolean_test | 'NOT' boolean_term -> ^( NOT boolean_term ) )
            int alt53=2;
            switch ( input.LA(1) ) {
            case ID:
            case INT:
            case FLOAT:
            case NUMERIC:
            case STRING:
            case 56:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 77:
            case 78:
            case 79:
            case 94:
            case 105:
            case 106:
                {
                alt53=1;
                }
                break;
            case 90:
                {
                alt53=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 53, 0, input);

                throw nvae;
            }

            switch (alt53) {
                case 1 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:231:4: boolean_test
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_boolean_test_in_boolean_term1446);
                    boolean_test136=boolean_test();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, boolean_test136.getTree());

                    }
                    break;
                case 2 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:232:4: 'NOT' boolean_term
                    {
                    string_literal137=(Token)match(input,90,FOLLOW_90_in_boolean_term1451); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_90.add(string_literal137);

                    pushFollow(FOLLOW_boolean_term_in_boolean_term1453);
                    boolean_term138=boolean_term();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_boolean_term.add(boolean_term138.getTree());


                    // AST REWRITE
                    // elements: boolean_term
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 232:23: -> ^( NOT boolean_term )
                    {
                        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:232:26: ^( NOT boolean_term )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(NOT, "NOT"), root_1);

                        adaptor.addChild(root_1, stream_boolean_term.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "boolean_term"

    public static class boolean_test_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "boolean_test"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:233:1: boolean_test : boolean_primary ;
    public final DSToolkitSQLParser.boolean_test_return boolean_test() throws RecognitionException {
        DSToolkitSQLParser.boolean_test_return retval = new DSToolkitSQLParser.boolean_test_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        DSToolkitSQLParser.boolean_primary_return boolean_primary139 = null;



        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:234:2: ( boolean_primary )
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:234:4: boolean_primary
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_boolean_primary_in_boolean_test1469);
            boolean_primary139=boolean_primary();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, boolean_primary139.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "boolean_test"

    public static class boolean_primary_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "boolean_primary"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:235:1: boolean_primary : ( predicate | '(' search_condition ')' );
    public final DSToolkitSQLParser.boolean_primary_return boolean_primary() throws RecognitionException {
        DSToolkitSQLParser.boolean_primary_return retval = new DSToolkitSQLParser.boolean_primary_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal141=null;
        Token char_literal143=null;
        DSToolkitSQLParser.predicate_return predicate140 = null;

        DSToolkitSQLParser.search_condition_return search_condition142 = null;


        CommonTree char_literal141_tree=null;
        CommonTree char_literal143_tree=null;

        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:236:2: ( predicate | '(' search_condition ')' )
            int alt54=2;
            alt54 = dfa54.predict(input);
            switch (alt54) {
                case 1 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:236:4: predicate
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_predicate_in_boolean_primary1477);
                    predicate140=predicate();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, predicate140.getTree());

                    }
                    break;
                case 2 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:236:16: '(' search_condition ')'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    char_literal141=(Token)match(input,56,FOLLOW_56_in_boolean_primary1481); if (state.failed) return retval;
                    pushFollow(FOLLOW_search_condition_in_boolean_primary1484);
                    search_condition142=search_condition();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, search_condition142.getTree());
                    char_literal143=(Token)match(input,57,FOLLOW_57_in_boolean_primary1486); if (state.failed) return retval;

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "boolean_primary"

    public static class predicate_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "predicate"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:237:1: predicate : ( comparison_predicate | like_predicate | in_predicate | null_predicate | exists_predicate | between_predicate );
    public final DSToolkitSQLParser.predicate_return predicate() throws RecognitionException {
        DSToolkitSQLParser.predicate_return retval = new DSToolkitSQLParser.predicate_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        DSToolkitSQLParser.comparison_predicate_return comparison_predicate144 = null;

        DSToolkitSQLParser.like_predicate_return like_predicate145 = null;

        DSToolkitSQLParser.in_predicate_return in_predicate146 = null;

        DSToolkitSQLParser.null_predicate_return null_predicate147 = null;

        DSToolkitSQLParser.exists_predicate_return exists_predicate148 = null;

        DSToolkitSQLParser.between_predicate_return between_predicate149 = null;



        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:238:2: ( comparison_predicate | like_predicate | in_predicate | null_predicate | exists_predicate | between_predicate )
            int alt55=6;
            alt55 = dfa55.predict(input);
            switch (alt55) {
                case 1 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:238:4: comparison_predicate
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_comparison_predicate_in_predicate1496);
                    comparison_predicate144=comparison_predicate();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, comparison_predicate144.getTree());

                    }
                    break;
                case 2 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:238:27: like_predicate
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_like_predicate_in_predicate1500);
                    like_predicate145=like_predicate();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, like_predicate145.getTree());

                    }
                    break;
                case 3 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:238:44: in_predicate
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_in_predicate_in_predicate1504);
                    in_predicate146=in_predicate();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, in_predicate146.getTree());

                    }
                    break;
                case 4 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:238:59: null_predicate
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_null_predicate_in_predicate1508);
                    null_predicate147=null_predicate();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, null_predicate147.getTree());

                    }
                    break;
                case 5 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:238:76: exists_predicate
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_exists_predicate_in_predicate1512);
                    exists_predicate148=exists_predicate();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, exists_predicate148.getTree());

                    }
                    break;
                case 6 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:238:95: between_predicate
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_between_predicate_in_predicate1516);
                    between_predicate149=between_predicate();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, between_predicate149.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "predicate"

    public static class null_predicate_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "null_predicate"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:239:1: null_predicate : ( super_lexical_value 'IS' 'NULL' -> ^( IS_NULL super_lexical_value ) | super_lexical_value 'IS' 'NOT' 'NULL' -> ^( NOT ^( IS_NULL super_lexical_value ) ) );
    public final DSToolkitSQLParser.null_predicate_return null_predicate() throws RecognitionException {
        DSToolkitSQLParser.null_predicate_return retval = new DSToolkitSQLParser.null_predicate_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal151=null;
        Token string_literal152=null;
        Token string_literal154=null;
        Token string_literal155=null;
        Token string_literal156=null;
        DSToolkitSQLParser.super_lexical_value_return super_lexical_value150 = null;

        DSToolkitSQLParser.super_lexical_value_return super_lexical_value153 = null;


        CommonTree string_literal151_tree=null;
        CommonTree string_literal152_tree=null;
        CommonTree string_literal154_tree=null;
        CommonTree string_literal155_tree=null;
        CommonTree string_literal156_tree=null;
        RewriteRuleTokenStream stream_77=new RewriteRuleTokenStream(adaptor,"token 77");
        RewriteRuleTokenStream stream_91=new RewriteRuleTokenStream(adaptor,"token 91");
        RewriteRuleTokenStream stream_90=new RewriteRuleTokenStream(adaptor,"token 90");
        RewriteRuleSubtreeStream stream_super_lexical_value=new RewriteRuleSubtreeStream(adaptor,"rule super_lexical_value");
        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:240:2: ( super_lexical_value 'IS' 'NULL' -> ^( IS_NULL super_lexical_value ) | super_lexical_value 'IS' 'NOT' 'NULL' -> ^( NOT ^( IS_NULL super_lexical_value ) ) )
            int alt56=2;
            alt56 = dfa56.predict(input);
            switch (alt56) {
                case 1 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:240:4: super_lexical_value 'IS' 'NULL'
                    {
                    pushFollow(FOLLOW_super_lexical_value_in_null_predicate1524);
                    super_lexical_value150=super_lexical_value();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_super_lexical_value.add(super_lexical_value150.getTree());
                    string_literal151=(Token)match(input,91,FOLLOW_91_in_null_predicate1526); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_91.add(string_literal151);

                    string_literal152=(Token)match(input,77,FOLLOW_77_in_null_predicate1528); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_77.add(string_literal152);



                    // AST REWRITE
                    // elements: super_lexical_value
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 240:36: -> ^( IS_NULL super_lexical_value )
                    {
                        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:240:39: ^( IS_NULL super_lexical_value )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(IS_NULL, "IS_NULL"), root_1);

                        adaptor.addChild(root_1, stream_super_lexical_value.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:241:4: super_lexical_value 'IS' 'NOT' 'NULL'
                    {
                    pushFollow(FOLLOW_super_lexical_value_in_null_predicate1541);
                    super_lexical_value153=super_lexical_value();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_super_lexical_value.add(super_lexical_value153.getTree());
                    string_literal154=(Token)match(input,91,FOLLOW_91_in_null_predicate1543); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_91.add(string_literal154);

                    string_literal155=(Token)match(input,90,FOLLOW_90_in_null_predicate1545); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_90.add(string_literal155);

                    string_literal156=(Token)match(input,77,FOLLOW_77_in_null_predicate1547); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_77.add(string_literal156);



                    // AST REWRITE
                    // elements: super_lexical_value
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 241:42: -> ^( NOT ^( IS_NULL super_lexical_value ) )
                    {
                        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:241:45: ^( NOT ^( IS_NULL super_lexical_value ) )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(NOT, "NOT"), root_1);

                        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:241:51: ^( IS_NULL super_lexical_value )
                        {
                        CommonTree root_2 = (CommonTree)adaptor.nil();
                        root_2 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(IS_NULL, "IS_NULL"), root_2);

                        adaptor.addChild(root_2, stream_super_lexical_value.nextTree());

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "null_predicate"

    public static class in_predicate_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "in_predicate"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:243:1: in_predicate : ( super_lexical_value 'NOT' 'IN' in_predicate_tail -> ^( NOT ^( IN super_lexical_value in_predicate_tail ) ) | super_lexical_value 'IN' in_predicate_tail -> ^( IN super_lexical_value in_predicate_tail ) );
    public final DSToolkitSQLParser.in_predicate_return in_predicate() throws RecognitionException {
        DSToolkitSQLParser.in_predicate_return retval = new DSToolkitSQLParser.in_predicate_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal158=null;
        Token string_literal159=null;
        Token string_literal162=null;
        DSToolkitSQLParser.super_lexical_value_return super_lexical_value157 = null;

        DSToolkitSQLParser.in_predicate_tail_return in_predicate_tail160 = null;

        DSToolkitSQLParser.super_lexical_value_return super_lexical_value161 = null;

        DSToolkitSQLParser.in_predicate_tail_return in_predicate_tail163 = null;


        CommonTree string_literal158_tree=null;
        CommonTree string_literal159_tree=null;
        CommonTree string_literal162_tree=null;
        RewriteRuleTokenStream stream_92=new RewriteRuleTokenStream(adaptor,"token 92");
        RewriteRuleTokenStream stream_90=new RewriteRuleTokenStream(adaptor,"token 90");
        RewriteRuleSubtreeStream stream_super_lexical_value=new RewriteRuleSubtreeStream(adaptor,"rule super_lexical_value");
        RewriteRuleSubtreeStream stream_in_predicate_tail=new RewriteRuleSubtreeStream(adaptor,"rule in_predicate_tail");
        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:244:2: ( super_lexical_value 'NOT' 'IN' in_predicate_tail -> ^( NOT ^( IN super_lexical_value in_predicate_tail ) ) | super_lexical_value 'IN' in_predicate_tail -> ^( IN super_lexical_value in_predicate_tail ) )
            int alt57=2;
            alt57 = dfa57.predict(input);
            switch (alt57) {
                case 1 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:244:4: super_lexical_value 'NOT' 'IN' in_predicate_tail
                    {
                    pushFollow(FOLLOW_super_lexical_value_in_in_predicate1569);
                    super_lexical_value157=super_lexical_value();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_super_lexical_value.add(super_lexical_value157.getTree());
                    string_literal158=(Token)match(input,90,FOLLOW_90_in_in_predicate1571); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_90.add(string_literal158);

                    string_literal159=(Token)match(input,92,FOLLOW_92_in_in_predicate1573); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_92.add(string_literal159);

                    pushFollow(FOLLOW_in_predicate_tail_in_in_predicate1575);
                    in_predicate_tail160=in_predicate_tail();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_in_predicate_tail.add(in_predicate_tail160.getTree());


                    // AST REWRITE
                    // elements: super_lexical_value, in_predicate_tail
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 245:4: -> ^( NOT ^( IN super_lexical_value in_predicate_tail ) )
                    {
                        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:245:7: ^( NOT ^( IN super_lexical_value in_predicate_tail ) )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(NOT, "NOT"), root_1);

                        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:245:13: ^( IN super_lexical_value in_predicate_tail )
                        {
                        CommonTree root_2 = (CommonTree)adaptor.nil();
                        root_2 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(IN, "IN"), root_2);

                        adaptor.addChild(root_2, stream_super_lexical_value.nextTree());
                        adaptor.addChild(root_2, stream_in_predicate_tail.nextTree());

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:246:4: super_lexical_value 'IN' in_predicate_tail
                    {
                    pushFollow(FOLLOW_super_lexical_value_in_in_predicate1597);
                    super_lexical_value161=super_lexical_value();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_super_lexical_value.add(super_lexical_value161.getTree());
                    string_literal162=(Token)match(input,92,FOLLOW_92_in_in_predicate1599); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_92.add(string_literal162);

                    pushFollow(FOLLOW_in_predicate_tail_in_in_predicate1601);
                    in_predicate_tail163=in_predicate_tail();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_in_predicate_tail.add(in_predicate_tail163.getTree());


                    // AST REWRITE
                    // elements: in_predicate_tail, super_lexical_value
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 247:4: -> ^( IN super_lexical_value in_predicate_tail )
                    {
                        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:247:7: ^( IN super_lexical_value in_predicate_tail )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(IN, "IN"), root_1);

                        adaptor.addChild(root_1, stream_super_lexical_value.nextTree());
                        adaptor.addChild(root_1, stream_in_predicate_tail.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "in_predicate"

    public static class in_predicate_tail_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "in_predicate_tail"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:249:1: in_predicate_tail : ( sub_query | '(' ( value_expression ( ',' value_expression )* ) ')' -> ^( SET ( value_expression )* ) );
    public final DSToolkitSQLParser.in_predicate_tail_return in_predicate_tail() throws RecognitionException {
        DSToolkitSQLParser.in_predicate_tail_return retval = new DSToolkitSQLParser.in_predicate_tail_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal165=null;
        Token char_literal167=null;
        Token char_literal169=null;
        DSToolkitSQLParser.sub_query_return sub_query164 = null;

        DSToolkitSQLParser.value_expression_return value_expression166 = null;

        DSToolkitSQLParser.value_expression_return value_expression168 = null;


        CommonTree char_literal165_tree=null;
        CommonTree char_literal167_tree=null;
        CommonTree char_literal169_tree=null;
        RewriteRuleTokenStream stream_59=new RewriteRuleTokenStream(adaptor,"token 59");
        RewriteRuleTokenStream stream_57=new RewriteRuleTokenStream(adaptor,"token 57");
        RewriteRuleTokenStream stream_56=new RewriteRuleTokenStream(adaptor,"token 56");
        RewriteRuleSubtreeStream stream_value_expression=new RewriteRuleSubtreeStream(adaptor,"rule value_expression");
        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:250:2: ( sub_query | '(' ( value_expression ( ',' value_expression )* ) ')' -> ^( SET ( value_expression )* ) )
            int alt59=2;
            switch ( input.LA(1) ) {
            case 56:
                {
                int LA59_1 = input.LA(2);

                if ( (synpred105_DSToolkitSQL()) ) {
                    alt59=1;
                }
                else if ( (true) ) {
                    alt59=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 59, 1, input);

                    throw nvae;
                }
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 59, 0, input);

                throw nvae;
            }

            switch (alt59) {
                case 1 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:250:4: sub_query
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_sub_query_in_in_predicate_tail1624);
                    sub_query164=sub_query();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, sub_query164.getTree());

                    }
                    break;
                case 2 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:251:5: '(' ( value_expression ( ',' value_expression )* ) ')'
                    {
                    char_literal165=(Token)match(input,56,FOLLOW_56_in_in_predicate_tail1631); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_56.add(char_literal165);

                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:251:9: ( value_expression ( ',' value_expression )* )
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:251:10: value_expression ( ',' value_expression )*
                    {
                    pushFollow(FOLLOW_value_expression_in_in_predicate_tail1634);
                    value_expression166=value_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_value_expression.add(value_expression166.getTree());
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:251:27: ( ',' value_expression )*
                    loop58:
                    do {
                        int alt58=2;
                        switch ( input.LA(1) ) {
                        case 59:
                            {
                            alt58=1;
                            }
                            break;

                        }

                        switch (alt58) {
                    	case 1 :
                    	    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:251:28: ',' value_expression
                    	    {
                    	    char_literal167=(Token)match(input,59,FOLLOW_59_in_in_predicate_tail1637); if (state.failed) return retval; 
                    	    if ( state.backtracking==0 ) stream_59.add(char_literal167);

                    	    pushFollow(FOLLOW_value_expression_in_in_predicate_tail1639);
                    	    value_expression168=value_expression();

                    	    state._fsp--;
                    	    if (state.failed) return retval;
                    	    if ( state.backtracking==0 ) stream_value_expression.add(value_expression168.getTree());

                    	    }
                    	    break;

                    	default :
                    	    break loop58;
                        }
                    } while (true);


                    }

                    char_literal169=(Token)match(input,57,FOLLOW_57_in_in_predicate_tail1644); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_57.add(char_literal169);



                    // AST REWRITE
                    // elements: value_expression
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 251:56: -> ^( SET ( value_expression )* )
                    {
                        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:251:59: ^( SET ( value_expression )* )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(SET, "SET"), root_1);

                        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:251:65: ( value_expression )*
                        while ( stream_value_expression.hasNext() ) {
                            adaptor.addChild(root_1, stream_value_expression.nextTree());

                        }
                        stream_value_expression.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "in_predicate_tail"

    public static class between_predicate_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "between_predicate"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:252:1: between_predicate : (value= super_lexical_value 'BETWEEN' btw1= super_lexical_value 'AND' btw2= super_lexical_value -> ^( BETWEEN $value $btw1 $btw2) | value= super_lexical_value 'NOT' 'BETWEEN' btw1= super_lexical_value 'AND' btw2= super_lexical_value -> ^( NOT ^( BETWEEN $value $btw1 $btw2) ) );
    public final DSToolkitSQLParser.between_predicate_return between_predicate() throws RecognitionException {
        DSToolkitSQLParser.between_predicate_return retval = new DSToolkitSQLParser.between_predicate_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal170=null;
        Token string_literal171=null;
        Token string_literal172=null;
        Token string_literal173=null;
        Token string_literal174=null;
        DSToolkitSQLParser.super_lexical_value_return value = null;

        DSToolkitSQLParser.super_lexical_value_return btw1 = null;

        DSToolkitSQLParser.super_lexical_value_return btw2 = null;


        CommonTree string_literal170_tree=null;
        CommonTree string_literal171_tree=null;
        CommonTree string_literal172_tree=null;
        CommonTree string_literal173_tree=null;
        CommonTree string_literal174_tree=null;
        RewriteRuleTokenStream stream_93=new RewriteRuleTokenStream(adaptor,"token 93");
        RewriteRuleTokenStream stream_90=new RewriteRuleTokenStream(adaptor,"token 90");
        RewriteRuleTokenStream stream_89=new RewriteRuleTokenStream(adaptor,"token 89");
        RewriteRuleSubtreeStream stream_super_lexical_value=new RewriteRuleSubtreeStream(adaptor,"rule super_lexical_value");
        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:253:2: (value= super_lexical_value 'BETWEEN' btw1= super_lexical_value 'AND' btw2= super_lexical_value -> ^( BETWEEN $value $btw1 $btw2) | value= super_lexical_value 'NOT' 'BETWEEN' btw1= super_lexical_value 'AND' btw2= super_lexical_value -> ^( NOT ^( BETWEEN $value $btw1 $btw2) ) )
            int alt60=2;
            alt60 = dfa60.predict(input);
            switch (alt60) {
                case 1 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:253:4: value= super_lexical_value 'BETWEEN' btw1= super_lexical_value 'AND' btw2= super_lexical_value
                    {
                    pushFollow(FOLLOW_super_lexical_value_in_between_predicate1664);
                    value=super_lexical_value();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_super_lexical_value.add(value.getTree());
                    string_literal170=(Token)match(input,93,FOLLOW_93_in_between_predicate1666); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_93.add(string_literal170);

                    pushFollow(FOLLOW_super_lexical_value_in_between_predicate1670);
                    btw1=super_lexical_value();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_super_lexical_value.add(btw1.getTree());
                    string_literal171=(Token)match(input,89,FOLLOW_89_in_between_predicate1672); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_89.add(string_literal171);

                    pushFollow(FOLLOW_super_lexical_value_in_between_predicate1676);
                    btw2=super_lexical_value();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_super_lexical_value.add(btw2.getTree());


                    // AST REWRITE
                    // elements: btw2, btw1, value
                    // token labels: 
                    // rule labels: retval, value, btw2, btw1
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
                    RewriteRuleSubtreeStream stream_value=new RewriteRuleSubtreeStream(adaptor,"rule value",value!=null?value.tree:null);
                    RewriteRuleSubtreeStream stream_btw2=new RewriteRuleSubtreeStream(adaptor,"rule btw2",btw2!=null?btw2.tree:null);
                    RewriteRuleSubtreeStream stream_btw1=new RewriteRuleSubtreeStream(adaptor,"rule btw1",btw1!=null?btw1.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 254:4: -> ^( BETWEEN $value $btw1 $btw2)
                    {
                        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:254:7: ^( BETWEEN $value $btw1 $btw2)
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(BETWEEN, "BETWEEN"), root_1);

                        adaptor.addChild(root_1, stream_value.nextTree());
                        adaptor.addChild(root_1, stream_btw1.nextTree());
                        adaptor.addChild(root_1, stream_btw2.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;
                case 2 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:255:4: value= super_lexical_value 'NOT' 'BETWEEN' btw1= super_lexical_value 'AND' btw2= super_lexical_value
                    {
                    pushFollow(FOLLOW_super_lexical_value_in_between_predicate1702);
                    value=super_lexical_value();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_super_lexical_value.add(value.getTree());
                    string_literal172=(Token)match(input,90,FOLLOW_90_in_between_predicate1704); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_90.add(string_literal172);

                    string_literal173=(Token)match(input,93,FOLLOW_93_in_between_predicate1706); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_93.add(string_literal173);

                    pushFollow(FOLLOW_super_lexical_value_in_between_predicate1710);
                    btw1=super_lexical_value();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_super_lexical_value.add(btw1.getTree());
                    string_literal174=(Token)match(input,89,FOLLOW_89_in_between_predicate1712); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_89.add(string_literal174);

                    pushFollow(FOLLOW_super_lexical_value_in_between_predicate1716);
                    btw2=super_lexical_value();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_super_lexical_value.add(btw2.getTree());


                    // AST REWRITE
                    // elements: value, btw1, btw2
                    // token labels: 
                    // rule labels: retval, value, btw2, btw1
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
                    RewriteRuleSubtreeStream stream_value=new RewriteRuleSubtreeStream(adaptor,"rule value",value!=null?value.tree:null);
                    RewriteRuleSubtreeStream stream_btw2=new RewriteRuleSubtreeStream(adaptor,"rule btw2",btw2!=null?btw2.tree:null);
                    RewriteRuleSubtreeStream stream_btw1=new RewriteRuleSubtreeStream(adaptor,"rule btw1",btw1!=null?btw1.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 256:4: -> ^( NOT ^( BETWEEN $value $btw1 $btw2) )
                    {
                        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:256:7: ^( NOT ^( BETWEEN $value $btw1 $btw2) )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(NOT, "NOT"), root_1);

                        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:256:13: ^( BETWEEN $value $btw1 $btw2)
                        {
                        CommonTree root_2 = (CommonTree)adaptor.nil();
                        root_2 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(BETWEEN, "BETWEEN"), root_2);

                        adaptor.addChild(root_2, stream_value.nextTree());
                        adaptor.addChild(root_2, stream_btw1.nextTree());
                        adaptor.addChild(root_2, stream_btw2.nextTree());

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "between_predicate"

    public static class exists_predicate_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "exists_predicate"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:257:1: exists_predicate : 'EXISTS' sub_query -> ^( EXISTS sub_query ) ;
    public final DSToolkitSQLParser.exists_predicate_return exists_predicate() throws RecognitionException {
        DSToolkitSQLParser.exists_predicate_return retval = new DSToolkitSQLParser.exists_predicate_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal175=null;
        DSToolkitSQLParser.sub_query_return sub_query176 = null;


        CommonTree string_literal175_tree=null;
        RewriteRuleTokenStream stream_94=new RewriteRuleTokenStream(adaptor,"token 94");
        RewriteRuleSubtreeStream stream_sub_query=new RewriteRuleSubtreeStream(adaptor,"rule sub_query");
        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:258:2: ( 'EXISTS' sub_query -> ^( EXISTS sub_query ) )
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:258:4: 'EXISTS' sub_query
            {
            string_literal175=(Token)match(input,94,FOLLOW_94_in_exists_predicate1746); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_94.add(string_literal175);

            pushFollow(FOLLOW_sub_query_in_exists_predicate1748);
            sub_query176=sub_query();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) stream_sub_query.add(sub_query176.getTree());


            // AST REWRITE
            // elements: sub_query
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 258:23: -> ^( EXISTS sub_query )
            {
                // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:258:26: ^( EXISTS sub_query )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(EXISTS, "EXISTS"), root_1);

                adaptor.addChild(root_1, stream_sub_query.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "exists_predicate"

    public static class comparison_predicate_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "comparison_predicate"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:259:1: comparison_predicate : super_lexical_value ( '=' | '<>' | '!=' | '<' | '>' | '>=' | '<=' ) ( 'ALL' | 'SOME' | 'ANY' )? super_lexical_value ;
    public final DSToolkitSQLParser.comparison_predicate_return comparison_predicate() throws RecognitionException {
        DSToolkitSQLParser.comparison_predicate_return retval = new DSToolkitSQLParser.comparison_predicate_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token set178=null;
        Token set179=null;
        DSToolkitSQLParser.super_lexical_value_return super_lexical_value177 = null;

        DSToolkitSQLParser.super_lexical_value_return super_lexical_value180 = null;


        CommonTree set178_tree=null;
        CommonTree set179_tree=null;

        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:260:2: ( super_lexical_value ( '=' | '<>' | '!=' | '<' | '>' | '>=' | '<=' ) ( 'ALL' | 'SOME' | 'ANY' )? super_lexical_value )
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:260:4: super_lexical_value ( '=' | '<>' | '!=' | '<' | '>' | '>=' | '<=' ) ( 'ALL' | 'SOME' | 'ANY' )? super_lexical_value
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_super_lexical_value_in_comparison_predicate1764);
            super_lexical_value177=super_lexical_value();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, super_lexical_value177.getTree());
            set178=(Token)input.LT(1);
            set178=(Token)input.LT(1);
            if ( (input.LA(1)>=95 && input.LA(1)<=101) ) {
                input.consume();
                if ( state.backtracking==0 ) root_0 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(set178), root_0);
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return retval;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }

            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:260:71: ( 'ALL' | 'SOME' | 'ANY' )?
            int alt61=2;
            switch ( input.LA(1) ) {
                case 47:
                case 102:
                case 103:
                    {
                    alt61=1;
                    }
                    break;
            }

            switch (alt61) {
                case 1 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:
                    {
                    set179=(Token)input.LT(1);
                    if ( input.LA(1)==47||(input.LA(1)>=102 && input.LA(1)<=103) ) {
                        input.consume();
                        if ( state.backtracking==0 ) adaptor.addChild(root_0, (CommonTree)adaptor.create(set179));
                        state.errorRecovery=false;state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    }
                    break;

            }

            pushFollow(FOLLOW_super_lexical_value_in_comparison_predicate1804);
            super_lexical_value180=super_lexical_value();

            state._fsp--;
            if (state.failed) return retval;
            if ( state.backtracking==0 ) adaptor.addChild(root_0, super_lexical_value180.getTree());

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "comparison_predicate"

    public static class like_predicate_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "like_predicate"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:261:1: like_predicate : ( super_lexical_value 'LIKE' super_lexical_value | v1= super_lexical_value 'NOT' 'LIKE' v2= super_lexical_value -> ^( NOT ^( 'LIKE' $v1 $v2) ) );
    public final DSToolkitSQLParser.like_predicate_return like_predicate() throws RecognitionException {
        DSToolkitSQLParser.like_predicate_return retval = new DSToolkitSQLParser.like_predicate_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal182=null;
        Token string_literal184=null;
        Token string_literal185=null;
        DSToolkitSQLParser.super_lexical_value_return v1 = null;

        DSToolkitSQLParser.super_lexical_value_return v2 = null;

        DSToolkitSQLParser.super_lexical_value_return super_lexical_value181 = null;

        DSToolkitSQLParser.super_lexical_value_return super_lexical_value183 = null;


        CommonTree string_literal182_tree=null;
        CommonTree string_literal184_tree=null;
        CommonTree string_literal185_tree=null;
        RewriteRuleTokenStream stream_90=new RewriteRuleTokenStream(adaptor,"token 90");
        RewriteRuleTokenStream stream_104=new RewriteRuleTokenStream(adaptor,"token 104");
        RewriteRuleSubtreeStream stream_super_lexical_value=new RewriteRuleSubtreeStream(adaptor,"rule super_lexical_value");
        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:262:2: ( super_lexical_value 'LIKE' super_lexical_value | v1= super_lexical_value 'NOT' 'LIKE' v2= super_lexical_value -> ^( NOT ^( 'LIKE' $v1 $v2) ) )
            int alt62=2;
            alt62 = dfa62.predict(input);
            switch (alt62) {
                case 1 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:262:4: super_lexical_value 'LIKE' super_lexical_value
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_super_lexical_value_in_like_predicate1812);
                    super_lexical_value181=super_lexical_value();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, super_lexical_value181.getTree());
                    string_literal182=(Token)match(input,104,FOLLOW_104_in_like_predicate1814); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal182_tree = (CommonTree)adaptor.create(string_literal182);
                    root_0 = (CommonTree)adaptor.becomeRoot(string_literal182_tree, root_0);
                    }
                    pushFollow(FOLLOW_super_lexical_value_in_like_predicate1817);
                    super_lexical_value183=super_lexical_value();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, super_lexical_value183.getTree());

                    }
                    break;
                case 2 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:263:4: v1= super_lexical_value 'NOT' 'LIKE' v2= super_lexical_value
                    {
                    pushFollow(FOLLOW_super_lexical_value_in_like_predicate1824);
                    v1=super_lexical_value();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_super_lexical_value.add(v1.getTree());
                    string_literal184=(Token)match(input,90,FOLLOW_90_in_like_predicate1826); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_90.add(string_literal184);

                    string_literal185=(Token)match(input,104,FOLLOW_104_in_like_predicate1828); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_104.add(string_literal185);

                    pushFollow(FOLLOW_super_lexical_value_in_like_predicate1832);
                    v2=super_lexical_value();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) stream_super_lexical_value.add(v2.getTree());


                    // AST REWRITE
                    // elements: v2, 104, v1
                    // token labels: 
                    // rule labels: v1, retval, v2
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    if ( state.backtracking==0 ) {
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_v1=new RewriteRuleSubtreeStream(adaptor,"rule v1",v1!=null?v1.tree:null);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
                    RewriteRuleSubtreeStream stream_v2=new RewriteRuleSubtreeStream(adaptor,"rule v2",v2!=null?v2.tree:null);

                    root_0 = (CommonTree)adaptor.nil();
                    // 263:63: -> ^( NOT ^( 'LIKE' $v1 $v2) )
                    {
                        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:263:66: ^( NOT ^( 'LIKE' $v1 $v2) )
                        {
                        CommonTree root_1 = (CommonTree)adaptor.nil();
                        root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(NOT, "NOT"), root_1);

                        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:263:72: ^( 'LIKE' $v1 $v2)
                        {
                        CommonTree root_2 = (CommonTree)adaptor.nil();
                        root_2 = (CommonTree)adaptor.becomeRoot(stream_104.nextNode(), root_2);

                        adaptor.addChild(root_2, stream_v1.nextTree());
                        adaptor.addChild(root_2, stream_v2.nextTree());

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;}
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "like_predicate"

    public static class super_lexical_value_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "super_lexical_value"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:265:1: super_lexical_value : ( value_expression | bind_super_abstract | 'NULL' | 'DEFAULT' );
    public final DSToolkitSQLParser.super_lexical_value_return super_lexical_value() throws RecognitionException {
        DSToolkitSQLParser.super_lexical_value_return retval = new DSToolkitSQLParser.super_lexical_value_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal188=null;
        Token string_literal189=null;
        DSToolkitSQLParser.value_expression_return value_expression186 = null;

        DSToolkitSQLParser.bind_super_abstract_return bind_super_abstract187 = null;


        CommonTree string_literal188_tree=null;
        CommonTree string_literal189_tree=null;

        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:266:2: ( value_expression | bind_super_abstract | 'NULL' | 'DEFAULT' )
            int alt63=4;
            switch ( input.LA(1) ) {
            case ID:
            case INT:
            case FLOAT:
            case NUMERIC:
            case STRING:
            case 56:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 78:
            case 79:
                {
                alt63=1;
                }
                break;
            case 77:
                {
                int LA63_2 = input.LA(2);

                if ( (synpred118_DSToolkitSQL()) ) {
                    alt63=1;
                }
                else if ( (synpred120_DSToolkitSQL()) ) {
                    alt63=3;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 63, 2, input);

                    throw nvae;
                }
                }
                break;
            case 106:
                {
                alt63=2;
                }
                break;
            case 105:
                {
                alt63=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 63, 0, input);

                throw nvae;
            }

            switch (alt63) {
                case 1 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:266:4: value_expression
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_value_expression_in_super_lexical_value1857);
                    value_expression186=value_expression();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, value_expression186.getTree());

                    }
                    break;
                case 2 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:266:23: bind_super_abstract
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_bind_super_abstract_in_super_lexical_value1861);
                    bind_super_abstract187=bind_super_abstract();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, bind_super_abstract187.getTree());

                    }
                    break;
                case 3 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:266:44: 'NULL'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    string_literal188=(Token)match(input,77,FOLLOW_77_in_super_lexical_value1864); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal188_tree = (CommonTree)adaptor.create(string_literal188);
                    adaptor.addChild(root_0, string_literal188_tree);
                    }

                    }
                    break;
                case 4 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:266:53: 'DEFAULT'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    string_literal189=(Token)match(input,105,FOLLOW_105_in_super_lexical_value1868); if (state.failed) return retval;
                    if ( state.backtracking==0 ) {
                    string_literal189_tree = (CommonTree)adaptor.create(string_literal189);
                    adaptor.addChild(root_0, string_literal189_tree);
                    }

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "super_lexical_value"

    public static class bind_super_abstract_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "bind_super_abstract"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:268:1: bind_super_abstract : '@' super_abstract_id= ID '.' super_lexical_id= ID -> ^( BOUND ^( SUPER_LEXICAL_OF_SUPER_ABSTRACT $super_abstract_id $super_lexical_id) ) ;
    public final DSToolkitSQLParser.bind_super_abstract_return bind_super_abstract() throws RecognitionException {
        DSToolkitSQLParser.bind_super_abstract_return retval = new DSToolkitSQLParser.bind_super_abstract_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token super_abstract_id=null;
        Token super_lexical_id=null;
        Token char_literal190=null;
        Token char_literal191=null;

        CommonTree super_abstract_id_tree=null;
        CommonTree super_lexical_id_tree=null;
        CommonTree char_literal190_tree=null;
        CommonTree char_literal191_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_106=new RewriteRuleTokenStream(adaptor,"token 106");
        RewriteRuleTokenStream stream_63=new RewriteRuleTokenStream(adaptor,"token 63");

        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:269:2: ( '@' super_abstract_id= ID '.' super_lexical_id= ID -> ^( BOUND ^( SUPER_LEXICAL_OF_SUPER_ABSTRACT $super_abstract_id $super_lexical_id) ) )
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:269:4: '@' super_abstract_id= ID '.' super_lexical_id= ID
            {
            char_literal190=(Token)match(input,106,FOLLOW_106_in_bind_super_abstract1878); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_106.add(char_literal190);

            super_abstract_id=(Token)match(input,ID,FOLLOW_ID_in_bind_super_abstract1881); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(super_abstract_id);

            char_literal191=(Token)match(input,63,FOLLOW_63_in_bind_super_abstract1882); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_63.add(char_literal191);

            super_lexical_id=(Token)match(input,ID,FOLLOW_ID_in_bind_super_abstract1885); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(super_lexical_id);



            // AST REWRITE
            // elements: super_abstract_id, super_lexical_id
            // token labels: super_abstract_id, super_lexical_id
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_super_abstract_id=new RewriteRuleTokenStream(adaptor,"token super_abstract_id",super_abstract_id);
            RewriteRuleTokenStream stream_super_lexical_id=new RewriteRuleTokenStream(adaptor,"token super_lexical_id",super_lexical_id);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 269:50: -> ^( BOUND ^( SUPER_LEXICAL_OF_SUPER_ABSTRACT $super_abstract_id $super_lexical_id) )
            {
                // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:269:53: ^( BOUND ^( SUPER_LEXICAL_OF_SUPER_ABSTRACT $super_abstract_id $super_lexical_id) )
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(BOUND, "BOUND"), root_1);

                // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:269:61: ^( SUPER_LEXICAL_OF_SUPER_ABSTRACT $super_abstract_id $super_lexical_id)
                {
                CommonTree root_2 = (CommonTree)adaptor.nil();
                root_2 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(SUPER_LEXICAL_OF_SUPER_ABSTRACT, "SUPER_LEXICAL_OF_SUPER_ABSTRACT"), root_2);

                adaptor.addChild(root_2, stream_super_abstract_id.nextNode());
                adaptor.addChild(root_2, stream_super_lexical_id.nextNode());

                adaptor.addChild(root_1, root_2);
                }

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "bind_super_abstract"

    public static class correlation_specification_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "correlation_specification"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:272:1: correlation_specification : ( 'AS' )? ID ;
    public final DSToolkitSQLParser.correlation_specification_return correlation_specification() throws RecognitionException {
        DSToolkitSQLParser.correlation_specification_return retval = new DSToolkitSQLParser.correlation_specification_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token string_literal192=null;
        Token ID193=null;

        CommonTree string_literal192_tree=null;
        CommonTree ID193_tree=null;

        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:273:2: ( ( 'AS' )? ID )
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:273:4: ( 'AS' )? ID
            {
            root_0 = (CommonTree)adaptor.nil();

            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:273:4: ( 'AS' )?
            int alt64=2;
            switch ( input.LA(1) ) {
                case 60:
                    {
                    alt64=1;
                    }
                    break;
            }

            switch (alt64) {
                case 1 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:273:5: 'AS'
                    {
                    string_literal192=(Token)match(input,60,FOLLOW_60_in_correlation_specification1913); if (state.failed) return retval;

                    }
                    break;

            }

            ID193=(Token)match(input,ID,FOLLOW_ID_in_correlation_specification1918); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID193_tree = (CommonTree)adaptor.create(ID193);
            adaptor.addChild(root_0, ID193_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "correlation_specification"

    public static class super_abstract_name_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "super_abstract_name"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:274:1: super_abstract_name : ID ;
    public final DSToolkitSQLParser.super_abstract_name_return super_abstract_name() throws RecognitionException {
        DSToolkitSQLParser.super_abstract_name_return retval = new DSToolkitSQLParser.super_abstract_name_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token ID194=null;

        CommonTree ID194_tree=null;

        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:275:2: ( ID )
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:275:4: ID
            {
            root_0 = (CommonTree)adaptor.nil();

            ID194=(Token)match(input,ID,FOLLOW_ID_in_super_abstract_name1927); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID194_tree = (CommonTree)adaptor.create(ID194);
            adaptor.addChild(root_0, ID194_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "super_abstract_name"

    public static class schema_name_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "schema_name"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:276:1: schema_name : ID ;
    public final DSToolkitSQLParser.schema_name_return schema_name() throws RecognitionException {
        DSToolkitSQLParser.schema_name_return retval = new DSToolkitSQLParser.schema_name_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token ID195=null;

        CommonTree ID195_tree=null;

        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:277:2: ( ID )
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:277:4: ID
            {
            root_0 = (CommonTree)adaptor.nil();

            ID195=(Token)match(input,ID,FOLLOW_ID_in_schema_name1935); if (state.failed) return retval;
            if ( state.backtracking==0 ) {
            ID195_tree = (CommonTree)adaptor.create(ID195);
            adaptor.addChild(root_0, ID195_tree);
            }

            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "schema_name"

    public static class super_lexical_list_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "super_lexical_list"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:278:1: super_lexical_list : ( super_lexical_name | reserved_word_super_lexical_name ) ( ',' ( super_lexical_name | reserved_word_super_lexical_name ) )* ;
    public final DSToolkitSQLParser.super_lexical_list_return super_lexical_list() throws RecognitionException {
        DSToolkitSQLParser.super_lexical_list_return retval = new DSToolkitSQLParser.super_lexical_list_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal198=null;
        DSToolkitSQLParser.super_lexical_name_return super_lexical_name196 = null;

        DSToolkitSQLParser.reserved_word_super_lexical_name_return reserved_word_super_lexical_name197 = null;

        DSToolkitSQLParser.super_lexical_name_return super_lexical_name199 = null;

        DSToolkitSQLParser.reserved_word_super_lexical_name_return reserved_word_super_lexical_name200 = null;


        CommonTree char_literal198_tree=null;

        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:279:2: ( ( super_lexical_name | reserved_word_super_lexical_name ) ( ',' ( super_lexical_name | reserved_word_super_lexical_name ) )* )
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:279:4: ( super_lexical_name | reserved_word_super_lexical_name ) ( ',' ( super_lexical_name | reserved_word_super_lexical_name ) )*
            {
            root_0 = (CommonTree)adaptor.nil();

            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:279:4: ( super_lexical_name | reserved_word_super_lexical_name )
            int alt65=2;
            switch ( input.LA(1) ) {
            case ID:
                {
                switch ( input.LA(2) ) {
                case 63:
                    {
                    switch ( input.LA(3) ) {
                    case ID:
                        {
                        alt65=1;
                        }
                        break;
                    case 64:
                    case 65:
                    case 66:
                    case 67:
                    case 68:
                    case 69:
                    case 70:
                    case 71:
                    case 72:
                    case 73:
                        {
                        alt65=2;
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return retval;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 65, 3, input);

                        throw nvae;
                    }

                    }
                    break;
                case EOF:
                case 45:
                case 46:
                case 48:
                case 49:
                case 54:
                case 57:
                case 59:
                case 61:
                    {
                    alt65=1;
                    }
                    break;
                default:
                    if (state.backtracking>0) {state.failed=true; return retval;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 65, 1, input);

                    throw nvae;
                }

                }
                break;
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
            case 73:
                {
                alt65=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return retval;}
                NoViableAltException nvae =
                    new NoViableAltException("", 65, 0, input);

                throw nvae;
            }

            switch (alt65) {
                case 1 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:279:5: super_lexical_name
                    {
                    pushFollow(FOLLOW_super_lexical_name_in_super_lexical_list1944);
                    super_lexical_name196=super_lexical_name();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, super_lexical_name196.getTree());

                    }
                    break;
                case 2 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:279:26: reserved_word_super_lexical_name
                    {
                    pushFollow(FOLLOW_reserved_word_super_lexical_name_in_super_lexical_list1948);
                    reserved_word_super_lexical_name197=reserved_word_super_lexical_name();

                    state._fsp--;
                    if (state.failed) return retval;
                    if ( state.backtracking==0 ) adaptor.addChild(root_0, reserved_word_super_lexical_name197.getTree());

                    }
                    break;

            }

            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:279:60: ( ',' ( super_lexical_name | reserved_word_super_lexical_name ) )*
            loop67:
            do {
                int alt67=2;
                switch ( input.LA(1) ) {
                case 59:
                    {
                    alt67=1;
                    }
                    break;

                }

                switch (alt67) {
            	case 1 :
            	    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:279:61: ',' ( super_lexical_name | reserved_word_super_lexical_name )
            	    {
            	    char_literal198=(Token)match(input,59,FOLLOW_59_in_super_lexical_list1952); if (state.failed) return retval;
            	    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:279:66: ( super_lexical_name | reserved_word_super_lexical_name )
            	    int alt66=2;
            	    switch ( input.LA(1) ) {
            	    case ID:
            	        {
            	        switch ( input.LA(2) ) {
            	        case 63:
            	            {
            	            switch ( input.LA(3) ) {
            	            case ID:
            	                {
            	                alt66=1;
            	                }
            	                break;
            	            case 64:
            	            case 65:
            	            case 66:
            	            case 67:
            	            case 68:
            	            case 69:
            	            case 70:
            	            case 71:
            	            case 72:
            	            case 73:
            	                {
            	                alt66=2;
            	                }
            	                break;
            	            default:
            	                if (state.backtracking>0) {state.failed=true; return retval;}
            	                NoViableAltException nvae =
            	                    new NoViableAltException("", 66, 3, input);

            	                throw nvae;
            	            }

            	            }
            	            break;
            	        case EOF:
            	        case 45:
            	        case 46:
            	        case 48:
            	        case 49:
            	        case 54:
            	        case 57:
            	        case 59:
            	        case 61:
            	            {
            	            alt66=1;
            	            }
            	            break;
            	        default:
            	            if (state.backtracking>0) {state.failed=true; return retval;}
            	            NoViableAltException nvae =
            	                new NoViableAltException("", 66, 1, input);

            	            throw nvae;
            	        }

            	        }
            	        break;
            	    case 64:
            	    case 65:
            	    case 66:
            	    case 67:
            	    case 68:
            	    case 69:
            	    case 70:
            	    case 71:
            	    case 72:
            	    case 73:
            	        {
            	        alt66=2;
            	        }
            	        break;
            	    default:
            	        if (state.backtracking>0) {state.failed=true; return retval;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 66, 0, input);

            	        throw nvae;
            	    }

            	    switch (alt66) {
            	        case 1 :
            	            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:279:67: super_lexical_name
            	            {
            	            pushFollow(FOLLOW_super_lexical_name_in_super_lexical_list1956);
            	            super_lexical_name199=super_lexical_name();

            	            state._fsp--;
            	            if (state.failed) return retval;
            	            if ( state.backtracking==0 ) adaptor.addChild(root_0, super_lexical_name199.getTree());

            	            }
            	            break;
            	        case 2 :
            	            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:279:88: reserved_word_super_lexical_name
            	            {
            	            pushFollow(FOLLOW_reserved_word_super_lexical_name_in_super_lexical_list1960);
            	            reserved_word_super_lexical_name200=reserved_word_super_lexical_name();

            	            state._fsp--;
            	            if (state.failed) return retval;
            	            if ( state.backtracking==0 ) adaptor.addChild(root_0, reserved_word_super_lexical_name200.getTree());

            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    break loop67;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "super_lexical_list"

    public static class super_lexical_name_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "super_lexical_name"
    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:280:1: super_lexical_name : (schema_id= ID '.' )? (super_abstract_id= ID '.' )? super_lexical_id= ID -> ^( SUPER_LEXICAL_OF_SUPER_ABSTRACT ( $schema_id)? ( $super_abstract_id)? $super_lexical_id) ;
    public final DSToolkitSQLParser.super_lexical_name_return super_lexical_name() throws RecognitionException {
        DSToolkitSQLParser.super_lexical_name_return retval = new DSToolkitSQLParser.super_lexical_name_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token schema_id=null;
        Token super_abstract_id=null;
        Token super_lexical_id=null;
        Token char_literal201=null;
        Token char_literal202=null;

        CommonTree schema_id_tree=null;
        CommonTree super_abstract_id_tree=null;
        CommonTree super_lexical_id_tree=null;
        CommonTree char_literal201_tree=null;
        CommonTree char_literal202_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_63=new RewriteRuleTokenStream(adaptor,"token 63");

        try {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:281:2: ( (schema_id= ID '.' )? (super_abstract_id= ID '.' )? super_lexical_id= ID -> ^( SUPER_LEXICAL_OF_SUPER_ABSTRACT ( $schema_id)? ( $super_abstract_id)? $super_lexical_id) )
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:281:4: (schema_id= ID '.' )? (super_abstract_id= ID '.' )? super_lexical_id= ID
            {
            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:281:4: (schema_id= ID '.' )?
            int alt68=2;
            switch ( input.LA(1) ) {
                case ID:
                    {
                    switch ( input.LA(2) ) {
                        case 63:
                            {
                            int LA68_2 = input.LA(3);

                            if ( (synpred125_DSToolkitSQL()) ) {
                                alt68=1;
                            }
                            }
                            break;
                    }

                    }
                    break;
            }

            switch (alt68) {
                case 1 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:281:5: schema_id= ID '.'
                    {
                    schema_id=(Token)match(input,ID,FOLLOW_ID_in_super_lexical_name1974); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(schema_id);

                    char_literal201=(Token)match(input,63,FOLLOW_63_in_super_lexical_name1975); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_63.add(char_literal201);


                    }
                    break;

            }

            // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:281:22: (super_abstract_id= ID '.' )?
            int alt69=2;
            switch ( input.LA(1) ) {
                case ID:
                    {
                    switch ( input.LA(2) ) {
                        case 63:
                            {
                            alt69=1;
                            }
                            break;
                    }

                    }
                    break;
            }

            switch (alt69) {
                case 1 :
                    // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:281:23: super_abstract_id= ID '.'
                    {
                    super_abstract_id=(Token)match(input,ID,FOLLOW_ID_in_super_lexical_name1981); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_ID.add(super_abstract_id);

                    char_literal202=(Token)match(input,63,FOLLOW_63_in_super_lexical_name1982); if (state.failed) return retval; 
                    if ( state.backtracking==0 ) stream_63.add(char_literal202);


                    }
                    break;

            }

            super_lexical_id=(Token)match(input,ID,FOLLOW_ID_in_super_lexical_name1987); if (state.failed) return retval; 
            if ( state.backtracking==0 ) stream_ID.add(super_lexical_id);



            // AST REWRITE
            // elements: schema_id, super_abstract_id, super_lexical_id
            // token labels: super_abstract_id, schema_id, super_lexical_id
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            if ( state.backtracking==0 ) {
            retval.tree = root_0;
            RewriteRuleTokenStream stream_super_abstract_id=new RewriteRuleTokenStream(adaptor,"token super_abstract_id",super_abstract_id);
            RewriteRuleTokenStream stream_schema_id=new RewriteRuleTokenStream(adaptor,"token schema_id",schema_id);
            RewriteRuleTokenStream stream_super_lexical_id=new RewriteRuleTokenStream(adaptor,"token super_lexical_id",super_lexical_id);
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (CommonTree)adaptor.nil();
            // 281:68: -> ^( SUPER_LEXICAL_OF_SUPER_ABSTRACT ( $schema_id)? ( $super_abstract_id)? $super_lexical_id)
            {
                // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:281:71: ^( SUPER_LEXICAL_OF_SUPER_ABSTRACT ( $schema_id)? ( $super_abstract_id)? $super_lexical_id)
                {
                CommonTree root_1 = (CommonTree)adaptor.nil();
                root_1 = (CommonTree)adaptor.becomeRoot((CommonTree)adaptor.create(SUPER_LEXICAL_OF_SUPER_ABSTRACT, "SUPER_LEXICAL_OF_SUPER_ABSTRACT"), root_1);

                // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:281:105: ( $schema_id)?
                if ( stream_schema_id.hasNext() ) {
                    adaptor.addChild(root_1, stream_schema_id.nextNode());

                }
                stream_schema_id.reset();
                // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:281:117: ( $super_abstract_id)?
                if ( stream_super_abstract_id.hasNext() ) {
                    adaptor.addChild(root_1, stream_super_abstract_id.nextNode());

                }
                stream_super_abstract_id.reset();
                adaptor.addChild(root_1, stream_super_lexical_id.nextNode());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;}
            }

            retval.stop = input.LT(-1);

            if ( state.backtracking==0 ) {

            retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
            }
        }

        catch (RecognitionException e)
        {
            reportError(e);
            throw e;
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "super_lexical_name"

    // $ANTLR start synpred34_DSToolkitSQL
    public final void synpred34_DSToolkitSQL_fragment() throws RecognitionException {   
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:154:13: ( ( '+' | '-' ) factor )
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:154:13: ( '+' | '-' ) factor
        {
        if ( (input.LA(1)>=74 && input.LA(1)<=75) ) {
            input.consume();
            state.errorRecovery=false;state.failed=false;
        }
        else {
            if (state.backtracking>0) {state.failed=true; return ;}
            MismatchedSetException mse = new MismatchedSetException(null,input);
            throw mse;
        }

        pushFollow(FOLLOW_factor_in_synpred34_DSToolkitSQL714);
        factor();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred34_DSToolkitSQL

    // $ANTLR start synpred39_DSToolkitSQL
    public final void synpred39_DSToolkitSQL_fragment() throws RecognitionException {   
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:161:4: ( '(' value_expression ')' )
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:161:4: '(' value_expression ')'
        {
        match(input,56,FOLLOW_56_in_synpred39_DSToolkitSQL767); if (state.failed) return ;
        pushFollow(FOLLOW_value_expression_in_synpred39_DSToolkitSQL770);
        value_expression();

        state._fsp--;
        if (state.failed) return ;
        match(input,57,FOLLOW_57_in_synpred39_DSToolkitSQL772); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred39_DSToolkitSQL

    // $ANTLR start synpred40_DSToolkitSQL
    public final void synpred40_DSToolkitSQL_fragment() throws RecognitionException {   
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:162:5: ( function )
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:162:5: function
        {
        pushFollow(FOLLOW_function_in_synpred40_DSToolkitSQL779);
        function();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred40_DSToolkitSQL

    // $ANTLR start synpred41_DSToolkitSQL
    public final void synpred41_DSToolkitSQL_fragment() throws RecognitionException {   
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:163:5: ( super_lexical_name )
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:163:5: super_lexical_name
        {
        pushFollow(FOLLOW_super_lexical_name_in_synpred41_DSToolkitSQL785);
        super_lexical_name();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred41_DSToolkitSQL

    // $ANTLR start synpred42_DSToolkitSQL
    public final void synpred42_DSToolkitSQL_fragment() throws RecognitionException {   
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:164:5: ( literal )
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:164:5: literal
        {
        pushFollow(FOLLOW_literal_in_synpred42_DSToolkitSQL791);
        literal();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred42_DSToolkitSQL

    // $ANTLR start synpred53_DSToolkitSQL
    public final void synpred53_DSToolkitSQL_fragment() throws RecognitionException {   
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:170:4: ( ( 'DATE' | 'TIMESTAMP' | 'TIME' ) STRING )
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:170:4: ( 'DATE' | 'TIMESTAMP' | 'TIME' ) STRING
        {
        if ( (input.LA(1)>=64 && input.LA(1)<=66) ) {
            input.consume();
            state.errorRecovery=false;state.failed=false;
        }
        else {
            if (state.backtracking>0) {state.failed=true; return ;}
            MismatchedSetException mse = new MismatchedSetException(null,input);
            throw mse;
        }

        match(input,STRING,FOLLOW_STRING_in_synpred53_DSToolkitSQL860); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred53_DSToolkitSQL

    // $ANTLR start synpred62_DSToolkitSQL
    public final void synpred62_DSToolkitSQL_fragment() throws RecognitionException {   
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:174:4: ( 'INTERVAL' STRING ( 'YEAR' | 'MONTH' | 'DAY' | 'HOUR' | 'MINUTE' | 'SECOND' ) )
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:174:4: 'INTERVAL' STRING ( 'YEAR' | 'MONTH' | 'DAY' | 'HOUR' | 'MINUTE' | 'SECOND' )
        {
        match(input,67,FOLLOW_67_in_synpred62_DSToolkitSQL912); if (state.failed) return ;
        match(input,STRING,FOLLOW_STRING_in_synpred62_DSToolkitSQL915); if (state.failed) return ;
        if ( (input.LA(1)>=68 && input.LA(1)<=73) ) {
            input.consume();
            state.errorRecovery=false;state.failed=false;
        }
        else {
            if (state.backtracking>0) {state.failed=true; return ;}
            MismatchedSetException mse = new MismatchedSetException(null,input);
            throw mse;
        }


        }
    }
    // $ANTLR end synpred62_DSToolkitSQL

    // $ANTLR start synpred76_DSToolkitSQL
    public final void synpred76_DSToolkitSQL_fragment() throws RecognitionException {   
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:191:20: ( ',' super_abstract_reference )
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:191:20: ',' super_abstract_reference
        {
        match(input,59,FOLLOW_59_in_synpred76_DSToolkitSQL1132); if (state.failed) return ;
        pushFollow(FOLLOW_super_abstract_reference_in_synpred76_DSToolkitSQL1135);
        super_abstract_reference();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred76_DSToolkitSQL

    // $ANTLR start synpred89_DSToolkitSQL
    public final void synpred89_DSToolkitSQL_fragment() throws RecognitionException {   
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:211:16: ( super_abstract_function_subquery )
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:211:16: super_abstract_function_subquery
        {
        pushFollow(FOLLOW_super_abstract_function_subquery_in_synpred89_DSToolkitSQL1318);
        super_abstract_function_subquery();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred89_DSToolkitSQL

    // $ANTLR start synpred90_DSToolkitSQL
    public final void synpred90_DSToolkitSQL_fragment() throws RecognitionException {   
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:211:51: ( ',' super_abstract_function_subquery )
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:211:51: ',' super_abstract_function_subquery
        {
        match(input,59,FOLLOW_59_in_synpred90_DSToolkitSQL1322); if (state.failed) return ;
        pushFollow(FOLLOW_super_abstract_function_subquery_in_synpred90_DSToolkitSQL1324);
        super_abstract_function_subquery();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred90_DSToolkitSQL

    // $ANTLR start synpred93_DSToolkitSQL
    public final void synpred93_DSToolkitSQL_fragment() throws RecognitionException {   
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:220:4: ( search_condition )
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:220:4: search_condition
        {
        pushFollow(FOLLOW_search_condition_in_synpred93_DSToolkitSQL1391);
        search_condition();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred93_DSToolkitSQL

    // $ANTLR start synpred97_DSToolkitSQL
    public final void synpred97_DSToolkitSQL_fragment() throws RecognitionException {   
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:236:4: ( predicate )
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:236:4: predicate
        {
        pushFollow(FOLLOW_predicate_in_synpred97_DSToolkitSQL1477);
        predicate();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred97_DSToolkitSQL

    // $ANTLR start synpred98_DSToolkitSQL
    public final void synpred98_DSToolkitSQL_fragment() throws RecognitionException {   
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:238:4: ( comparison_predicate )
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:238:4: comparison_predicate
        {
        pushFollow(FOLLOW_comparison_predicate_in_synpred98_DSToolkitSQL1496);
        comparison_predicate();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred98_DSToolkitSQL

    // $ANTLR start synpred99_DSToolkitSQL
    public final void synpred99_DSToolkitSQL_fragment() throws RecognitionException {   
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:238:27: ( like_predicate )
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:238:27: like_predicate
        {
        pushFollow(FOLLOW_like_predicate_in_synpred99_DSToolkitSQL1500);
        like_predicate();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred99_DSToolkitSQL

    // $ANTLR start synpred100_DSToolkitSQL
    public final void synpred100_DSToolkitSQL_fragment() throws RecognitionException {   
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:238:44: ( in_predicate )
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:238:44: in_predicate
        {
        pushFollow(FOLLOW_in_predicate_in_synpred100_DSToolkitSQL1504);
        in_predicate();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred100_DSToolkitSQL

    // $ANTLR start synpred101_DSToolkitSQL
    public final void synpred101_DSToolkitSQL_fragment() throws RecognitionException {   
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:238:59: ( null_predicate )
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:238:59: null_predicate
        {
        pushFollow(FOLLOW_null_predicate_in_synpred101_DSToolkitSQL1508);
        null_predicate();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred101_DSToolkitSQL

    // $ANTLR start synpred103_DSToolkitSQL
    public final void synpred103_DSToolkitSQL_fragment() throws RecognitionException {   
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:240:4: ( super_lexical_value 'IS' 'NULL' )
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:240:4: super_lexical_value 'IS' 'NULL'
        {
        pushFollow(FOLLOW_super_lexical_value_in_synpred103_DSToolkitSQL1524);
        super_lexical_value();

        state._fsp--;
        if (state.failed) return ;
        match(input,91,FOLLOW_91_in_synpred103_DSToolkitSQL1526); if (state.failed) return ;
        match(input,77,FOLLOW_77_in_synpred103_DSToolkitSQL1528); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred103_DSToolkitSQL

    // $ANTLR start synpred104_DSToolkitSQL
    public final void synpred104_DSToolkitSQL_fragment() throws RecognitionException {   
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:244:4: ( super_lexical_value 'NOT' 'IN' in_predicate_tail )
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:244:4: super_lexical_value 'NOT' 'IN' in_predicate_tail
        {
        pushFollow(FOLLOW_super_lexical_value_in_synpred104_DSToolkitSQL1569);
        super_lexical_value();

        state._fsp--;
        if (state.failed) return ;
        match(input,90,FOLLOW_90_in_synpred104_DSToolkitSQL1571); if (state.failed) return ;
        match(input,92,FOLLOW_92_in_synpred104_DSToolkitSQL1573); if (state.failed) return ;
        pushFollow(FOLLOW_in_predicate_tail_in_synpred104_DSToolkitSQL1575);
        in_predicate_tail();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred104_DSToolkitSQL

    // $ANTLR start synpred105_DSToolkitSQL
    public final void synpred105_DSToolkitSQL_fragment() throws RecognitionException {   
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:250:4: ( sub_query )
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:250:4: sub_query
        {
        pushFollow(FOLLOW_sub_query_in_synpred105_DSToolkitSQL1624);
        sub_query();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred105_DSToolkitSQL

    // $ANTLR start synpred107_DSToolkitSQL
    public final void synpred107_DSToolkitSQL_fragment() throws RecognitionException {   
        DSToolkitSQLParser.super_lexical_value_return value = null;

        DSToolkitSQLParser.super_lexical_value_return btw1 = null;

        DSToolkitSQLParser.super_lexical_value_return btw2 = null;


        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:253:4: (value= super_lexical_value 'BETWEEN' btw1= super_lexical_value 'AND' btw2= super_lexical_value )
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:253:4: value= super_lexical_value 'BETWEEN' btw1= super_lexical_value 'AND' btw2= super_lexical_value
        {
        pushFollow(FOLLOW_super_lexical_value_in_synpred107_DSToolkitSQL1664);
        value=super_lexical_value();

        state._fsp--;
        if (state.failed) return ;
        match(input,93,FOLLOW_93_in_synpred107_DSToolkitSQL1666); if (state.failed) return ;
        pushFollow(FOLLOW_super_lexical_value_in_synpred107_DSToolkitSQL1670);
        btw1=super_lexical_value();

        state._fsp--;
        if (state.failed) return ;
        match(input,89,FOLLOW_89_in_synpred107_DSToolkitSQL1672); if (state.failed) return ;
        pushFollow(FOLLOW_super_lexical_value_in_synpred107_DSToolkitSQL1676);
        btw2=super_lexical_value();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred107_DSToolkitSQL

    // $ANTLR start synpred117_DSToolkitSQL
    public final void synpred117_DSToolkitSQL_fragment() throws RecognitionException {   
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:262:4: ( super_lexical_value 'LIKE' super_lexical_value )
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:262:4: super_lexical_value 'LIKE' super_lexical_value
        {
        pushFollow(FOLLOW_super_lexical_value_in_synpred117_DSToolkitSQL1812);
        super_lexical_value();

        state._fsp--;
        if (state.failed) return ;
        match(input,104,FOLLOW_104_in_synpred117_DSToolkitSQL1814); if (state.failed) return ;
        pushFollow(FOLLOW_super_lexical_value_in_synpred117_DSToolkitSQL1817);
        super_lexical_value();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred117_DSToolkitSQL

    // $ANTLR start synpred118_DSToolkitSQL
    public final void synpred118_DSToolkitSQL_fragment() throws RecognitionException {   
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:266:4: ( value_expression )
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:266:4: value_expression
        {
        pushFollow(FOLLOW_value_expression_in_synpred118_DSToolkitSQL1857);
        value_expression();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred118_DSToolkitSQL

    // $ANTLR start synpred120_DSToolkitSQL
    public final void synpred120_DSToolkitSQL_fragment() throws RecognitionException {   
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:266:44: ( 'NULL' )
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:266:44: 'NULL'
        {
        match(input,77,FOLLOW_77_in_synpred120_DSToolkitSQL1864); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred120_DSToolkitSQL

    // $ANTLR start synpred125_DSToolkitSQL
    public final void synpred125_DSToolkitSQL_fragment() throws RecognitionException {   
        Token schema_id=null;

        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:281:5: (schema_id= ID '.' )
        // uk\\ac\\manchester\\dstoolkit\\service\\impl\\query\\queryparser\\DSToolkitSQL.g:281:5: schema_id= ID '.'
        {
        schema_id=(Token)match(input,ID,FOLLOW_ID_in_synpred125_DSToolkitSQL1974); if (state.failed) return ;
        match(input,63,FOLLOW_63_in_synpred125_DSToolkitSQL1975); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred125_DSToolkitSQL

    // Delegated rules

    public final boolean synpred76_DSToolkitSQL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred76_DSToolkitSQL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred34_DSToolkitSQL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred34_DSToolkitSQL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred42_DSToolkitSQL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred42_DSToolkitSQL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred99_DSToolkitSQL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred99_DSToolkitSQL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred107_DSToolkitSQL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred107_DSToolkitSQL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred104_DSToolkitSQL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred104_DSToolkitSQL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred101_DSToolkitSQL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred101_DSToolkitSQL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred105_DSToolkitSQL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred105_DSToolkitSQL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred40_DSToolkitSQL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred40_DSToolkitSQL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred100_DSToolkitSQL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred100_DSToolkitSQL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred117_DSToolkitSQL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred117_DSToolkitSQL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred93_DSToolkitSQL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred93_DSToolkitSQL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred120_DSToolkitSQL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred120_DSToolkitSQL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred39_DSToolkitSQL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred39_DSToolkitSQL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred62_DSToolkitSQL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred62_DSToolkitSQL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred118_DSToolkitSQL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred118_DSToolkitSQL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred90_DSToolkitSQL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred90_DSToolkitSQL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred89_DSToolkitSQL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred89_DSToolkitSQL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred41_DSToolkitSQL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred41_DSToolkitSQL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred125_DSToolkitSQL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred125_DSToolkitSQL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred103_DSToolkitSQL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred103_DSToolkitSQL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred98_DSToolkitSQL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred98_DSToolkitSQL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred53_DSToolkitSQL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred53_DSToolkitSQL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred97_DSToolkitSQL() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred97_DSToolkitSQL_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA4 dfa4 = new DFA4(this);
    protected DFA19 dfa19 = new DFA19(this);
    protected DFA22 dfa22 = new DFA22(this);
    protected DFA23 dfa23 = new DFA23(this);
    protected DFA29 dfa29 = new DFA29(this);
    protected DFA46 dfa46 = new DFA46(this);
    protected DFA47 dfa47 = new DFA47(this);
    protected DFA50 dfa50 = new DFA50(this);
    protected DFA54 dfa54 = new DFA54(this);
    protected DFA55 dfa55 = new DFA55(this);
    protected DFA56 dfa56 = new DFA56(this);
    protected DFA57 dfa57 = new DFA57(this);
    protected DFA60 dfa60 = new DFA60(this);
    protected DFA62 dfa62 = new DFA62(this);
    static final String DFA4_eotS =
        "\12\uffff";
    static final String DFA4_eofS =
        "\12\uffff";
    static final String DFA4_minS =
        "\1\56\3\57\6\uffff";
    static final String DFA4_maxS =
        "\1\61\3\70\6\uffff";
    static final String DFA4_acceptS =
        "\4\uffff\1\1\1\2\1\3\1\4\1\5\1\6";
    static final String DFA4_specialS =
        "\12\uffff}>";
    static final String[] DFA4_transitionS = {
            "\1\1\1\uffff\1\2\1\3",
            "\1\4\2\uffff\1\5\5\uffff\1\5",
            "\1\6\2\uffff\1\7\5\uffff\1\7",
            "\1\10\2\uffff\1\11\5\uffff\1\11",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA4_eot = DFA.unpackEncodedString(DFA4_eotS);
    static final short[] DFA4_eof = DFA.unpackEncodedString(DFA4_eofS);
    static final char[] DFA4_min = DFA.unpackEncodedStringToUnsignedChars(DFA4_minS);
    static final char[] DFA4_max = DFA.unpackEncodedStringToUnsignedChars(DFA4_maxS);
    static final short[] DFA4_accept = DFA.unpackEncodedString(DFA4_acceptS);
    static final short[] DFA4_special = DFA.unpackEncodedString(DFA4_specialS);
    static final short[][] DFA4_transition;

    static {
        int numStates = DFA4_transitionS.length;
        DFA4_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA4_transition[i] = DFA.unpackEncodedString(DFA4_transitionS[i]);
        }
    }

    class DFA4 extends DFA {

        public DFA4(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 4;
            this.eot = DFA4_eot;
            this.eof = DFA4_eof;
            this.min = DFA4_min;
            this.max = DFA4_max;
            this.accept = DFA4_accept;
            this.special = DFA4_special;
            this.transition = DFA4_transition;
        }
        public String getDescription() {
            return "112:1: set_op : ( 'UNION' 'ALL' -> ^( UNION_ALL ) | 'UNION' -> ^( UNION ) | 'EXCEPT' 'ALL' -> ^( EXCEPT_ALL ) | 'EXCEPT' -> ^( EXCEPT ) | 'INTERSECT' 'ALL' -> ^( INTERSECT_ALL ) | 'INTERSECT' -> ^( INTERSECT ) );";
        }
    }
    static final String DFA19_eotS =
        "\64\uffff";
    static final String DFA19_eofS =
        "\1\1\63\uffff";
    static final String DFA19_minS =
        "\1\47\7\uffff\2\0\52\uffff";
    static final String DFA19_maxS =
        "\1\152\7\uffff\2\0\52\uffff";
    static final String DFA19_acceptS =
        "\1\uffff\1\2\61\uffff\1\1";
    static final String DFA19_specialS =
        "\10\uffff\1\0\1\1\52\uffff}>";
    static final String[] DFA19_transitionS = {
            "\5\1\1\uffff\2\1\1\uffff\2\1\1\uffff\4\1\1\uffff\2\1\1\uffff"+
            "\3\1\2\uffff\12\1\1\10\1\11\1\uffff\3\1\1\uffff\1\1\1\uffff"+
            "\4\1\1\uffff\16\1\2\uffff\3\1",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA19_eot = DFA.unpackEncodedString(DFA19_eotS);
    static final short[] DFA19_eof = DFA.unpackEncodedString(DFA19_eofS);
    static final char[] DFA19_min = DFA.unpackEncodedStringToUnsignedChars(DFA19_minS);
    static final char[] DFA19_max = DFA.unpackEncodedStringToUnsignedChars(DFA19_maxS);
    static final short[] DFA19_accept = DFA.unpackEncodedString(DFA19_acceptS);
    static final short[] DFA19_special = DFA.unpackEncodedString(DFA19_specialS);
    static final short[][] DFA19_transition;

    static {
        int numStates = DFA19_transitionS.length;
        DFA19_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA19_transition[i] = DFA.unpackEncodedString(DFA19_transitionS[i]);
        }
    }

    class DFA19 extends DFA {

        public DFA19(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 19;
            this.eot = DFA19_eot;
            this.eof = DFA19_eof;
            this.min = DFA19_min;
            this.max = DFA19_max;
            this.accept = DFA19_accept;
            this.special = DFA19_special;
            this.transition = DFA19_transition;
        }
        public String getDescription() {
            return "()* loopback of 154:12: ( ( '+' | '-' ) factor )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA19_8 = input.LA(1);

                         
                        int index19_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred34_DSToolkitSQL()) ) {s = 51;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index19_8);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA19_9 = input.LA(1);

                         
                        int index19_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred34_DSToolkitSQL()) ) {s = 51;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index19_9);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 19, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA22_eotS =
        "\30\uffff";
    static final String DFA22_eofS =
        "\30\uffff";
    static final String DFA22_minS =
        "\1\47\2\0\25\uffff";
    static final String DFA22_maxS =
        "\1\117\2\0\25\uffff";
    static final String DFA22_acceptS =
        "\3\uffff\1\4\20\uffff\1\1\1\5\1\2\1\3";
    static final String DFA22_specialS =
        "\1\uffff\1\0\1\1\25\uffff}>";
    static final String[] DFA22_transitionS = {
            "\1\2\4\3\14\uffff\1\1\7\uffff\12\3\3\uffff\3\3",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA22_eot = DFA.unpackEncodedString(DFA22_eotS);
    static final short[] DFA22_eof = DFA.unpackEncodedString(DFA22_eofS);
    static final char[] DFA22_min = DFA.unpackEncodedStringToUnsignedChars(DFA22_minS);
    static final char[] DFA22_max = DFA.unpackEncodedStringToUnsignedChars(DFA22_maxS);
    static final short[] DFA22_accept = DFA.unpackEncodedString(DFA22_acceptS);
    static final short[] DFA22_special = DFA.unpackEncodedString(DFA22_specialS);
    static final short[][] DFA22_transition;

    static {
        int numStates = DFA22_transitionS.length;
        DFA22_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA22_transition[i] = DFA.unpackEncodedString(DFA22_transitionS[i]);
        }
    }

    class DFA22 extends DFA {

        public DFA22(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 22;
            this.eot = DFA22_eot;
            this.eof = DFA22_eof;
            this.min = DFA22_min;
            this.max = DFA22_max;
            this.accept = DFA22_accept;
            this.special = DFA22_special;
            this.transition = DFA22_transition;
        }
        public String getDescription() {
            return "160:1: value_expression_primary : ( '(' value_expression ')' | function | super_lexical_name | literal | sub_query );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA22_1 = input.LA(1);

                         
                        int index22_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred39_DSToolkitSQL()) ) {s = 20;}

                        else if ( (true) ) {s = 21;}

                         
                        input.seek(index22_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA22_2 = input.LA(1);

                         
                        int index22_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred40_DSToolkitSQL()) ) {s = 22;}

                        else if ( (synpred41_DSToolkitSQL()) ) {s = 23;}

                        else if ( (synpred42_DSToolkitSQL()) ) {s = 3;}

                         
                        input.seek(index22_2);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 22, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA23_eotS =
        "\14\uffff";
    static final String DFA23_eofS =
        "\14\uffff";
    static final String DFA23_minS =
        "\1\47\5\uffff\1\77\4\uffff\1\100";
    static final String DFA23_maxS =
        "\1\117\5\uffff\1\77\4\uffff\1\111";
    static final String DFA23_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\uffff\1\6\1\7\1\10\1\11\1\uffff";
    static final String DFA23_specialS =
        "\14\uffff}>";
    static final String[] DFA23_transitionS = {
            "\1\6\1\1\1\2\1\3\1\4\24\uffff\3\5\7\7\3\uffff\1\10\1\11\1\12",
            "",
            "",
            "",
            "",
            "",
            "\1\13",
            "",
            "",
            "",
            "",
            "\3\5\7\7"
    };

    static final short[] DFA23_eot = DFA.unpackEncodedString(DFA23_eotS);
    static final short[] DFA23_eof = DFA.unpackEncodedString(DFA23_eofS);
    static final char[] DFA23_min = DFA.unpackEncodedStringToUnsignedChars(DFA23_minS);
    static final char[] DFA23_max = DFA.unpackEncodedStringToUnsignedChars(DFA23_maxS);
    static final short[] DFA23_accept = DFA.unpackEncodedString(DFA23_acceptS);
    static final short[] DFA23_special = DFA.unpackEncodedString(DFA23_specialS);
    static final short[][] DFA23_transition;

    static {
        int numStates = DFA23_transitionS.length;
        DFA23_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA23_transition[i] = DFA.unpackEncodedString(DFA23_transitionS[i]);
        }
    }

    class DFA23 extends DFA {

        public DFA23(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 23;
            this.eot = DFA23_eot;
            this.eof = DFA23_eof;
            this.min = DFA23_min;
            this.max = DFA23_max;
            this.accept = DFA23_accept;
            this.special = DFA23_special;
            this.transition = DFA23_transition;
        }
        public String getDescription() {
            return "168:1: literal : ( INT | FLOAT | NUMERIC | STRING | datetime | interval | 'NULL' | 'TRUE' | 'FALSE' );";
        }
    }
    static final String DFA29_eotS =
        "\13\uffff";
    static final String DFA29_eofS =
        "\1\uffff\1\2\11\uffff";
    static final String DFA29_minS =
        "\2\47\1\uffff\1\47\6\0\1\uffff";
    static final String DFA29_maxS =
        "\1\111\1\152\1\uffff\1\152\6\0\1\uffff";
    static final String DFA29_acceptS =
        "\2\uffff\1\2\7\uffff\1\1";
    static final String DFA29_specialS =
        "\4\uffff\1\1\1\4\1\5\1\2\1\3\1\0\1\uffff}>";
    static final String[] DFA29_transitionS = {
            "\1\2\33\uffff\1\1\6\2",
            "\4\2\1\3\1\uffff\2\2\1\uffff\2\2\1\uffff\4\2\1\uffff\6\2\2"+
            "\uffff\20\2\1\uffff\1\2\1\uffff\4\2\1\uffff\16\2\2\uffff\3\2",
            "",
            "\5\2\14\uffff\4\2\4\uffff\4\2\1\4\1\5\1\6\1\7\1\10\1\11\7"+
            "\2\11\uffff\14\2\2\uffff\3\2",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            ""
    };

    static final short[] DFA29_eot = DFA.unpackEncodedString(DFA29_eotS);
    static final short[] DFA29_eof = DFA.unpackEncodedString(DFA29_eofS);
    static final char[] DFA29_min = DFA.unpackEncodedStringToUnsignedChars(DFA29_minS);
    static final char[] DFA29_max = DFA.unpackEncodedStringToUnsignedChars(DFA29_maxS);
    static final short[] DFA29_accept = DFA.unpackEncodedString(DFA29_acceptS);
    static final short[] DFA29_special = DFA.unpackEncodedString(DFA29_specialS);
    static final short[][] DFA29_transition;

    static {
        int numStates = DFA29_transitionS.length;
        DFA29_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA29_transition[i] = DFA.unpackEncodedString(DFA29_transitionS[i]);
        }
    }

    class DFA29 extends DFA {

        public DFA29(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 29;
            this.eot = DFA29_eot;
            this.eof = DFA29_eof;
            this.min = DFA29_min;
            this.max = DFA29_max;
            this.accept = DFA29_accept;
            this.special = DFA29_special;
            this.transition = DFA29_transition;
        }
        public String getDescription() {
            return "173:1: interval : ( 'INTERVAL' STRING ( 'YEAR' | 'MONTH' | 'DAY' | 'HOUR' | 'MINUTE' | 'SECOND' ) | (tableid= ID '.' )? (s= 'INTERVAL' | s= 'YEAR' | s= 'MONTH' | s= 'DAY' | s= 'HOUR' | s= 'MINUTE' | s= 'SECOND' ) -> ^( SUPER_LEXICAL_OF_SUPER_ABSTRACT ( $tableid)? $s) );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA29_9 = input.LA(1);

                         
                        int index29_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred62_DSToolkitSQL()) ) {s = 10;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index29_9);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA29_4 = input.LA(1);

                         
                        int index29_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred62_DSToolkitSQL()) ) {s = 10;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index29_4);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA29_7 = input.LA(1);

                         
                        int index29_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred62_DSToolkitSQL()) ) {s = 10;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index29_7);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA29_8 = input.LA(1);

                         
                        int index29_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred62_DSToolkitSQL()) ) {s = 10;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index29_8);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA29_5 = input.LA(1);

                         
                        int index29_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred62_DSToolkitSQL()) ) {s = 10;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index29_5);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA29_6 = input.LA(1);

                         
                        int index29_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred62_DSToolkitSQL()) ) {s = 10;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index29_6);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 29, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA46_eotS =
        "\35\uffff";
    static final String DFA46_eofS =
        "\35\uffff";
    static final String DFA46_minS =
        "\1\47\1\0\33\uffff";
    static final String DFA46_maxS =
        "\1\152\1\0\33\uffff";
    static final String DFA46_acceptS =
        "\2\uffff\1\2\31\uffff\1\1";
    static final String DFA46_specialS =
        "\1\uffff\1\0\33\uffff}>";
    static final String[] DFA46_transitionS = {
            "\5\2\14\uffff\1\1\1\2\1\uffff\1\2\4\uffff\14\2\1\uffff\3\2"+
            "\12\uffff\1\2\3\uffff\1\2\12\uffff\2\2",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA46_eot = DFA.unpackEncodedString(DFA46_eotS);
    static final short[] DFA46_eof = DFA.unpackEncodedString(DFA46_eofS);
    static final char[] DFA46_min = DFA.unpackEncodedStringToUnsignedChars(DFA46_minS);
    static final char[] DFA46_max = DFA.unpackEncodedStringToUnsignedChars(DFA46_maxS);
    static final short[] DFA46_accept = DFA.unpackEncodedString(DFA46_acceptS);
    static final short[] DFA46_special = DFA.unpackEncodedString(DFA46_specialS);
    static final short[][] DFA46_transition;

    static {
        int numStates = DFA46_transitionS.length;
        DFA46_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA46_transition[i] = DFA.unpackEncodedString(DFA46_transitionS[i]);
        }
    }

    class DFA46 extends DFA {

        public DFA46(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 46;
            this.eot = DFA46_eot;
            this.eof = DFA46_eof;
            this.min = DFA46_min;
            this.max = DFA46_max;
            this.accept = DFA46_accept;
            this.special = DFA46_special;
            this.transition = DFA46_transition;
        }
        public String getDescription() {
            return "211:16: ( super_abstract_function_subquery )?";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA46_1 = input.LA(1);

                         
                        int index46_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred89_DSToolkitSQL()) ) {s = 28;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index46_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 46, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA47_eotS =
        "\35\uffff";
    static final String DFA47_eofS =
        "\35\uffff";
    static final String DFA47_minS =
        "\1\47\1\0\33\uffff";
    static final String DFA47_maxS =
        "\1\152\1\0\33\uffff";
    static final String DFA47_acceptS =
        "\2\uffff\1\2\31\uffff\1\1";
    static final String DFA47_specialS =
        "\1\uffff\1\0\33\uffff}>";
    static final String[] DFA47_transitionS = {
            "\5\2\14\uffff\2\2\1\uffff\1\1\4\uffff\14\2\1\uffff\3\2\12\uffff"+
            "\1\2\3\uffff\1\2\12\uffff\2\2",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA47_eot = DFA.unpackEncodedString(DFA47_eotS);
    static final short[] DFA47_eof = DFA.unpackEncodedString(DFA47_eofS);
    static final char[] DFA47_min = DFA.unpackEncodedStringToUnsignedChars(DFA47_minS);
    static final char[] DFA47_max = DFA.unpackEncodedStringToUnsignedChars(DFA47_maxS);
    static final short[] DFA47_accept = DFA.unpackEncodedString(DFA47_acceptS);
    static final short[] DFA47_special = DFA.unpackEncodedString(DFA47_specialS);
    static final short[][] DFA47_transition;

    static {
        int numStates = DFA47_transitionS.length;
        DFA47_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA47_transition[i] = DFA.unpackEncodedString(DFA47_transitionS[i]);
        }
    }

    class DFA47 extends DFA {

        public DFA47(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 47;
            this.eot = DFA47_eot;
            this.eof = DFA47_eof;
            this.min = DFA47_min;
            this.max = DFA47_max;
            this.accept = DFA47_accept;
            this.special = DFA47_special;
            this.transition = DFA47_transition;
        }
        public String getDescription() {
            return "()* loopback of 211:50: ( ',' super_abstract_function_subquery )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA47_1 = input.LA(1);

                         
                        int index47_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred90_DSToolkitSQL()) ) {s = 28;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index47_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 47, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA50_eotS =
        "\33\uffff";
    static final String DFA50_eofS =
        "\33\uffff";
    static final String DFA50_minS =
        "\1\47\25\0\5\uffff";
    static final String DFA50_maxS =
        "\1\152\25\0\5\uffff";
    static final String DFA50_acceptS =
        "\26\uffff\1\1\3\uffff\1\2";
    static final String DFA50_specialS =
        "\1\uffff\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1"+
        "\14\1\15\1\16\1\17\1\20\1\21\1\22\1\23\1\24\5\uffff}>";
    static final String[] DFA50_transitionS = {
            "\1\1\1\6\1\7\1\10\1\2\14\uffff\1\5\7\uffff\1\11\1\12\1\13\1"+
            "\14\1\15\1\16\1\17\1\20\1\21\1\22\1\3\1\4\1\uffff\1\23\1\24"+
            "\1\25\12\uffff\1\26\3\uffff\1\26\12\uffff\2\26",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA50_eot = DFA.unpackEncodedString(DFA50_eotS);
    static final short[] DFA50_eof = DFA.unpackEncodedString(DFA50_eofS);
    static final char[] DFA50_min = DFA.unpackEncodedStringToUnsignedChars(DFA50_minS);
    static final char[] DFA50_max = DFA.unpackEncodedStringToUnsignedChars(DFA50_maxS);
    static final short[] DFA50_accept = DFA.unpackEncodedString(DFA50_acceptS);
    static final short[] DFA50_special = DFA.unpackEncodedString(DFA50_specialS);
    static final short[][] DFA50_transition;

    static {
        int numStates = DFA50_transitionS.length;
        DFA50_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA50_transition[i] = DFA.unpackEncodedString(DFA50_transitionS[i]);
        }
    }

    class DFA50 extends DFA {

        public DFA50(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 50;
            this.eot = DFA50_eot;
            this.eof = DFA50_eof;
            this.min = DFA50_min;
            this.max = DFA50_max;
            this.accept = DFA50_accept;
            this.special = DFA50_special;
            this.transition = DFA50_transition;
        }
        public String getDescription() {
            return "219:1: super_abstract_function_param : ( search_condition | value_expression );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA50_1 = input.LA(1);

                         
                        int index50_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred93_DSToolkitSQL()) ) {s = 22;}

                        else if ( (true) ) {s = 26;}

                         
                        input.seek(index50_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA50_2 = input.LA(1);

                         
                        int index50_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred93_DSToolkitSQL()) ) {s = 22;}

                        else if ( (true) ) {s = 26;}

                         
                        input.seek(index50_2);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA50_3 = input.LA(1);

                         
                        int index50_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred93_DSToolkitSQL()) ) {s = 22;}

                        else if ( (true) ) {s = 26;}

                         
                        input.seek(index50_3);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA50_4 = input.LA(1);

                         
                        int index50_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred93_DSToolkitSQL()) ) {s = 22;}

                        else if ( (true) ) {s = 26;}

                         
                        input.seek(index50_4);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA50_5 = input.LA(1);

                         
                        int index50_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred93_DSToolkitSQL()) ) {s = 22;}

                        else if ( (true) ) {s = 26;}

                         
                        input.seek(index50_5);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA50_6 = input.LA(1);

                         
                        int index50_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred93_DSToolkitSQL()) ) {s = 22;}

                        else if ( (true) ) {s = 26;}

                         
                        input.seek(index50_6);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA50_7 = input.LA(1);

                         
                        int index50_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred93_DSToolkitSQL()) ) {s = 22;}

                        else if ( (true) ) {s = 26;}

                         
                        input.seek(index50_7);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA50_8 = input.LA(1);

                         
                        int index50_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred93_DSToolkitSQL()) ) {s = 22;}

                        else if ( (true) ) {s = 26;}

                         
                        input.seek(index50_8);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA50_9 = input.LA(1);

                         
                        int index50_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred93_DSToolkitSQL()) ) {s = 22;}

                        else if ( (true) ) {s = 26;}

                         
                        input.seek(index50_9);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA50_10 = input.LA(1);

                         
                        int index50_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred93_DSToolkitSQL()) ) {s = 22;}

                        else if ( (true) ) {s = 26;}

                         
                        input.seek(index50_10);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA50_11 = input.LA(1);

                         
                        int index50_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred93_DSToolkitSQL()) ) {s = 22;}

                        else if ( (true) ) {s = 26;}

                         
                        input.seek(index50_11);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA50_12 = input.LA(1);

                         
                        int index50_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred93_DSToolkitSQL()) ) {s = 22;}

                        else if ( (true) ) {s = 26;}

                         
                        input.seek(index50_12);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA50_13 = input.LA(1);

                         
                        int index50_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred93_DSToolkitSQL()) ) {s = 22;}

                        else if ( (true) ) {s = 26;}

                         
                        input.seek(index50_13);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA50_14 = input.LA(1);

                         
                        int index50_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred93_DSToolkitSQL()) ) {s = 22;}

                        else if ( (true) ) {s = 26;}

                         
                        input.seek(index50_14);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA50_15 = input.LA(1);

                         
                        int index50_15 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred93_DSToolkitSQL()) ) {s = 22;}

                        else if ( (true) ) {s = 26;}

                         
                        input.seek(index50_15);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA50_16 = input.LA(1);

                         
                        int index50_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred93_DSToolkitSQL()) ) {s = 22;}

                        else if ( (true) ) {s = 26;}

                         
                        input.seek(index50_16);
                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA50_17 = input.LA(1);

                         
                        int index50_17 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred93_DSToolkitSQL()) ) {s = 22;}

                        else if ( (true) ) {s = 26;}

                         
                        input.seek(index50_17);
                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA50_18 = input.LA(1);

                         
                        int index50_18 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred93_DSToolkitSQL()) ) {s = 22;}

                        else if ( (true) ) {s = 26;}

                         
                        input.seek(index50_18);
                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA50_19 = input.LA(1);

                         
                        int index50_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred93_DSToolkitSQL()) ) {s = 22;}

                        else if ( (true) ) {s = 26;}

                         
                        input.seek(index50_19);
                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA50_20 = input.LA(1);

                         
                        int index50_20 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred93_DSToolkitSQL()) ) {s = 22;}

                        else if ( (true) ) {s = 26;}

                         
                        input.seek(index50_20);
                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA50_21 = input.LA(1);

                         
                        int index50_21 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred93_DSToolkitSQL()) ) {s = 22;}

                        else if ( (true) ) {s = 26;}

                         
                        input.seek(index50_21);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 50, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA54_eotS =
        "\32\uffff";
    static final String DFA54_eofS =
        "\32\uffff";
    static final String DFA54_minS =
        "\1\47\4\uffff\1\0\24\uffff";
    static final String DFA54_maxS =
        "\1\152\4\uffff\1\0\24\uffff";
    static final String DFA54_acceptS =
        "\1\uffff\1\1\27\uffff\1\2";
    static final String DFA54_specialS =
        "\5\uffff\1\0\24\uffff}>";
    static final String[] DFA54_transitionS = {
            "\5\1\14\uffff\1\5\7\uffff\14\1\1\uffff\3\1\16\uffff\1\1\12"+
            "\uffff\2\1",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA54_eot = DFA.unpackEncodedString(DFA54_eotS);
    static final short[] DFA54_eof = DFA.unpackEncodedString(DFA54_eofS);
    static final char[] DFA54_min = DFA.unpackEncodedStringToUnsignedChars(DFA54_minS);
    static final char[] DFA54_max = DFA.unpackEncodedStringToUnsignedChars(DFA54_maxS);
    static final short[] DFA54_accept = DFA.unpackEncodedString(DFA54_acceptS);
    static final short[] DFA54_special = DFA.unpackEncodedString(DFA54_specialS);
    static final short[][] DFA54_transition;

    static {
        int numStates = DFA54_transitionS.length;
        DFA54_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA54_transition[i] = DFA.unpackEncodedString(DFA54_transitionS[i]);
        }
    }

    class DFA54 extends DFA {

        public DFA54(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 54;
            this.eot = DFA54_eot;
            this.eof = DFA54_eof;
            this.min = DFA54_min;
            this.max = DFA54_max;
            this.accept = DFA54_accept;
            this.special = DFA54_special;
            this.transition = DFA54_transition;
        }
        public String getDescription() {
            return "235:1: boolean_primary : ( predicate | '(' search_condition ')' );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA54_5 = input.LA(1);

                         
                        int index54_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred97_DSToolkitSQL()) ) {s = 1;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index54_5);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 54, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA55_eotS =
        "\36\uffff";
    static final String DFA55_eofS =
        "\36\uffff";
    static final String DFA55_minS =
        "\1\47\27\0\6\uffff";
    static final String DFA55_maxS =
        "\1\152\27\0\6\uffff";
    static final String DFA55_acceptS =
        "\30\uffff\1\5\1\1\1\2\1\3\1\4\1\6";
    static final String DFA55_specialS =
        "\1\uffff\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1"+
        "\14\1\15\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\25\1\26\6\uffff}>";
    static final String[] DFA55_transitionS = {
            "\1\1\1\6\1\7\1\10\1\2\14\uffff\1\5\7\uffff\1\11\1\12\1\13\1"+
            "\14\1\15\1\16\1\17\1\20\1\21\1\22\1\3\1\4\1\uffff\1\23\1\24"+
            "\1\25\16\uffff\1\30\12\uffff\1\27\1\26",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA55_eot = DFA.unpackEncodedString(DFA55_eotS);
    static final short[] DFA55_eof = DFA.unpackEncodedString(DFA55_eofS);
    static final char[] DFA55_min = DFA.unpackEncodedStringToUnsignedChars(DFA55_minS);
    static final char[] DFA55_max = DFA.unpackEncodedStringToUnsignedChars(DFA55_maxS);
    static final short[] DFA55_accept = DFA.unpackEncodedString(DFA55_acceptS);
    static final short[] DFA55_special = DFA.unpackEncodedString(DFA55_specialS);
    static final short[][] DFA55_transition;

    static {
        int numStates = DFA55_transitionS.length;
        DFA55_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA55_transition[i] = DFA.unpackEncodedString(DFA55_transitionS[i]);
        }
    }

    class DFA55 extends DFA {

        public DFA55(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 55;
            this.eot = DFA55_eot;
            this.eof = DFA55_eof;
            this.min = DFA55_min;
            this.max = DFA55_max;
            this.accept = DFA55_accept;
            this.special = DFA55_special;
            this.transition = DFA55_transition;
        }
        public String getDescription() {
            return "237:1: predicate : ( comparison_predicate | like_predicate | in_predicate | null_predicate | exists_predicate | between_predicate );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA55_1 = input.LA(1);

                         
                        int index55_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred98_DSToolkitSQL()) ) {s = 25;}

                        else if ( (synpred99_DSToolkitSQL()) ) {s = 26;}

                        else if ( (synpred100_DSToolkitSQL()) ) {s = 27;}

                        else if ( (synpred101_DSToolkitSQL()) ) {s = 28;}

                        else if ( (true) ) {s = 29;}

                         
                        input.seek(index55_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA55_2 = input.LA(1);

                         
                        int index55_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred98_DSToolkitSQL()) ) {s = 25;}

                        else if ( (synpred99_DSToolkitSQL()) ) {s = 26;}

                        else if ( (synpred100_DSToolkitSQL()) ) {s = 27;}

                        else if ( (synpred101_DSToolkitSQL()) ) {s = 28;}

                        else if ( (true) ) {s = 29;}

                         
                        input.seek(index55_2);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA55_3 = input.LA(1);

                         
                        int index55_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred98_DSToolkitSQL()) ) {s = 25;}

                        else if ( (synpred99_DSToolkitSQL()) ) {s = 26;}

                        else if ( (synpred100_DSToolkitSQL()) ) {s = 27;}

                        else if ( (synpred101_DSToolkitSQL()) ) {s = 28;}

                        else if ( (true) ) {s = 29;}

                         
                        input.seek(index55_3);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA55_4 = input.LA(1);

                         
                        int index55_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred98_DSToolkitSQL()) ) {s = 25;}

                        else if ( (synpred99_DSToolkitSQL()) ) {s = 26;}

                        else if ( (synpred100_DSToolkitSQL()) ) {s = 27;}

                        else if ( (synpred101_DSToolkitSQL()) ) {s = 28;}

                        else if ( (true) ) {s = 29;}

                         
                        input.seek(index55_4);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA55_5 = input.LA(1);

                         
                        int index55_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred98_DSToolkitSQL()) ) {s = 25;}

                        else if ( (synpred99_DSToolkitSQL()) ) {s = 26;}

                        else if ( (synpred100_DSToolkitSQL()) ) {s = 27;}

                        else if ( (synpred101_DSToolkitSQL()) ) {s = 28;}

                        else if ( (true) ) {s = 29;}

                         
                        input.seek(index55_5);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA55_6 = input.LA(1);

                         
                        int index55_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred98_DSToolkitSQL()) ) {s = 25;}

                        else if ( (synpred99_DSToolkitSQL()) ) {s = 26;}

                        else if ( (synpred100_DSToolkitSQL()) ) {s = 27;}

                        else if ( (synpred101_DSToolkitSQL()) ) {s = 28;}

                        else if ( (true) ) {s = 29;}

                         
                        input.seek(index55_6);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA55_7 = input.LA(1);

                         
                        int index55_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred98_DSToolkitSQL()) ) {s = 25;}

                        else if ( (synpred99_DSToolkitSQL()) ) {s = 26;}

                        else if ( (synpred100_DSToolkitSQL()) ) {s = 27;}

                        else if ( (synpred101_DSToolkitSQL()) ) {s = 28;}

                        else if ( (true) ) {s = 29;}

                         
                        input.seek(index55_7);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA55_8 = input.LA(1);

                         
                        int index55_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred98_DSToolkitSQL()) ) {s = 25;}

                        else if ( (synpred99_DSToolkitSQL()) ) {s = 26;}

                        else if ( (synpred100_DSToolkitSQL()) ) {s = 27;}

                        else if ( (synpred101_DSToolkitSQL()) ) {s = 28;}

                        else if ( (true) ) {s = 29;}

                         
                        input.seek(index55_8);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA55_9 = input.LA(1);

                         
                        int index55_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred98_DSToolkitSQL()) ) {s = 25;}

                        else if ( (synpred99_DSToolkitSQL()) ) {s = 26;}

                        else if ( (synpred100_DSToolkitSQL()) ) {s = 27;}

                        else if ( (synpred101_DSToolkitSQL()) ) {s = 28;}

                        else if ( (true) ) {s = 29;}

                         
                        input.seek(index55_9);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA55_10 = input.LA(1);

                         
                        int index55_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred98_DSToolkitSQL()) ) {s = 25;}

                        else if ( (synpred99_DSToolkitSQL()) ) {s = 26;}

                        else if ( (synpred100_DSToolkitSQL()) ) {s = 27;}

                        else if ( (synpred101_DSToolkitSQL()) ) {s = 28;}

                        else if ( (true) ) {s = 29;}

                         
                        input.seek(index55_10);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA55_11 = input.LA(1);

                         
                        int index55_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred98_DSToolkitSQL()) ) {s = 25;}

                        else if ( (synpred99_DSToolkitSQL()) ) {s = 26;}

                        else if ( (synpred100_DSToolkitSQL()) ) {s = 27;}

                        else if ( (synpred101_DSToolkitSQL()) ) {s = 28;}

                        else if ( (true) ) {s = 29;}

                         
                        input.seek(index55_11);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA55_12 = input.LA(1);

                         
                        int index55_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred98_DSToolkitSQL()) ) {s = 25;}

                        else if ( (synpred99_DSToolkitSQL()) ) {s = 26;}

                        else if ( (synpred100_DSToolkitSQL()) ) {s = 27;}

                        else if ( (synpred101_DSToolkitSQL()) ) {s = 28;}

                        else if ( (true) ) {s = 29;}

                         
                        input.seek(index55_12);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA55_13 = input.LA(1);

                         
                        int index55_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred98_DSToolkitSQL()) ) {s = 25;}

                        else if ( (synpred99_DSToolkitSQL()) ) {s = 26;}

                        else if ( (synpred100_DSToolkitSQL()) ) {s = 27;}

                        else if ( (synpred101_DSToolkitSQL()) ) {s = 28;}

                        else if ( (true) ) {s = 29;}

                         
                        input.seek(index55_13);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA55_14 = input.LA(1);

                         
                        int index55_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred98_DSToolkitSQL()) ) {s = 25;}

                        else if ( (synpred99_DSToolkitSQL()) ) {s = 26;}

                        else if ( (synpred100_DSToolkitSQL()) ) {s = 27;}

                        else if ( (synpred101_DSToolkitSQL()) ) {s = 28;}

                        else if ( (true) ) {s = 29;}

                         
                        input.seek(index55_14);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA55_15 = input.LA(1);

                         
                        int index55_15 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred98_DSToolkitSQL()) ) {s = 25;}

                        else if ( (synpred99_DSToolkitSQL()) ) {s = 26;}

                        else if ( (synpred100_DSToolkitSQL()) ) {s = 27;}

                        else if ( (synpred101_DSToolkitSQL()) ) {s = 28;}

                        else if ( (true) ) {s = 29;}

                         
                        input.seek(index55_15);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA55_16 = input.LA(1);

                         
                        int index55_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred98_DSToolkitSQL()) ) {s = 25;}

                        else if ( (synpred99_DSToolkitSQL()) ) {s = 26;}

                        else if ( (synpred100_DSToolkitSQL()) ) {s = 27;}

                        else if ( (synpred101_DSToolkitSQL()) ) {s = 28;}

                        else if ( (true) ) {s = 29;}

                         
                        input.seek(index55_16);
                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA55_17 = input.LA(1);

                         
                        int index55_17 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred98_DSToolkitSQL()) ) {s = 25;}

                        else if ( (synpred99_DSToolkitSQL()) ) {s = 26;}

                        else if ( (synpred100_DSToolkitSQL()) ) {s = 27;}

                        else if ( (synpred101_DSToolkitSQL()) ) {s = 28;}

                        else if ( (true) ) {s = 29;}

                         
                        input.seek(index55_17);
                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA55_18 = input.LA(1);

                         
                        int index55_18 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred98_DSToolkitSQL()) ) {s = 25;}

                        else if ( (synpred99_DSToolkitSQL()) ) {s = 26;}

                        else if ( (synpred100_DSToolkitSQL()) ) {s = 27;}

                        else if ( (synpred101_DSToolkitSQL()) ) {s = 28;}

                        else if ( (true) ) {s = 29;}

                         
                        input.seek(index55_18);
                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA55_19 = input.LA(1);

                         
                        int index55_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred98_DSToolkitSQL()) ) {s = 25;}

                        else if ( (synpred99_DSToolkitSQL()) ) {s = 26;}

                        else if ( (synpred100_DSToolkitSQL()) ) {s = 27;}

                        else if ( (synpred101_DSToolkitSQL()) ) {s = 28;}

                        else if ( (true) ) {s = 29;}

                         
                        input.seek(index55_19);
                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA55_20 = input.LA(1);

                         
                        int index55_20 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred98_DSToolkitSQL()) ) {s = 25;}

                        else if ( (synpred99_DSToolkitSQL()) ) {s = 26;}

                        else if ( (synpred100_DSToolkitSQL()) ) {s = 27;}

                        else if ( (synpred101_DSToolkitSQL()) ) {s = 28;}

                        else if ( (true) ) {s = 29;}

                         
                        input.seek(index55_20);
                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA55_21 = input.LA(1);

                         
                        int index55_21 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred98_DSToolkitSQL()) ) {s = 25;}

                        else if ( (synpred99_DSToolkitSQL()) ) {s = 26;}

                        else if ( (synpred100_DSToolkitSQL()) ) {s = 27;}

                        else if ( (synpred101_DSToolkitSQL()) ) {s = 28;}

                        else if ( (true) ) {s = 29;}

                         
                        input.seek(index55_21);
                        if ( s>=0 ) return s;
                        break;
                    case 21 : 
                        int LA55_22 = input.LA(1);

                         
                        int index55_22 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred98_DSToolkitSQL()) ) {s = 25;}

                        else if ( (synpred99_DSToolkitSQL()) ) {s = 26;}

                        else if ( (synpred100_DSToolkitSQL()) ) {s = 27;}

                        else if ( (synpred101_DSToolkitSQL()) ) {s = 28;}

                        else if ( (true) ) {s = 29;}

                         
                        input.seek(index55_22);
                        if ( s>=0 ) return s;
                        break;
                    case 22 : 
                        int LA55_23 = input.LA(1);

                         
                        int index55_23 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred98_DSToolkitSQL()) ) {s = 25;}

                        else if ( (synpred99_DSToolkitSQL()) ) {s = 26;}

                        else if ( (synpred100_DSToolkitSQL()) ) {s = 27;}

                        else if ( (synpred101_DSToolkitSQL()) ) {s = 28;}

                        else if ( (true) ) {s = 29;}

                         
                        input.seek(index55_23);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 55, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA56_eotS =
        "\32\uffff";
    static final String DFA56_eofS =
        "\32\uffff";
    static final String DFA56_minS =
        "\1\47\27\0\2\uffff";
    static final String DFA56_maxS =
        "\1\152\27\0\2\uffff";
    static final String DFA56_acceptS =
        "\30\uffff\1\1\1\2";
    static final String DFA56_specialS =
        "\1\uffff\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1"+
        "\14\1\15\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\25\1\26\2\uffff}>";
    static final String[] DFA56_transitionS = {
            "\1\1\1\6\1\7\1\10\1\2\14\uffff\1\5\7\uffff\1\11\1\12\1\13\1"+
            "\14\1\15\1\16\1\17\1\20\1\21\1\22\1\3\1\4\1\uffff\1\23\1\24"+
            "\1\25\31\uffff\1\27\1\26",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            ""
    };

    static final short[] DFA56_eot = DFA.unpackEncodedString(DFA56_eotS);
    static final short[] DFA56_eof = DFA.unpackEncodedString(DFA56_eofS);
    static final char[] DFA56_min = DFA.unpackEncodedStringToUnsignedChars(DFA56_minS);
    static final char[] DFA56_max = DFA.unpackEncodedStringToUnsignedChars(DFA56_maxS);
    static final short[] DFA56_accept = DFA.unpackEncodedString(DFA56_acceptS);
    static final short[] DFA56_special = DFA.unpackEncodedString(DFA56_specialS);
    static final short[][] DFA56_transition;

    static {
        int numStates = DFA56_transitionS.length;
        DFA56_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA56_transition[i] = DFA.unpackEncodedString(DFA56_transitionS[i]);
        }
    }

    class DFA56 extends DFA {

        public DFA56(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 56;
            this.eot = DFA56_eot;
            this.eof = DFA56_eof;
            this.min = DFA56_min;
            this.max = DFA56_max;
            this.accept = DFA56_accept;
            this.special = DFA56_special;
            this.transition = DFA56_transition;
        }
        public String getDescription() {
            return "239:1: null_predicate : ( super_lexical_value 'IS' 'NULL' -> ^( IS_NULL super_lexical_value ) | super_lexical_value 'IS' 'NOT' 'NULL' -> ^( NOT ^( IS_NULL super_lexical_value ) ) );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA56_1 = input.LA(1);

                         
                        int index56_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred103_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index56_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA56_2 = input.LA(1);

                         
                        int index56_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred103_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index56_2);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA56_3 = input.LA(1);

                         
                        int index56_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred103_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index56_3);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA56_4 = input.LA(1);

                         
                        int index56_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred103_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index56_4);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA56_5 = input.LA(1);

                         
                        int index56_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred103_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index56_5);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA56_6 = input.LA(1);

                         
                        int index56_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred103_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index56_6);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA56_7 = input.LA(1);

                         
                        int index56_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred103_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index56_7);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA56_8 = input.LA(1);

                         
                        int index56_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred103_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index56_8);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA56_9 = input.LA(1);

                         
                        int index56_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred103_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index56_9);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA56_10 = input.LA(1);

                         
                        int index56_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred103_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index56_10);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA56_11 = input.LA(1);

                         
                        int index56_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred103_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index56_11);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA56_12 = input.LA(1);

                         
                        int index56_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred103_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index56_12);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA56_13 = input.LA(1);

                         
                        int index56_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred103_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index56_13);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA56_14 = input.LA(1);

                         
                        int index56_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred103_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index56_14);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA56_15 = input.LA(1);

                         
                        int index56_15 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred103_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index56_15);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA56_16 = input.LA(1);

                         
                        int index56_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred103_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index56_16);
                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA56_17 = input.LA(1);

                         
                        int index56_17 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred103_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index56_17);
                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA56_18 = input.LA(1);

                         
                        int index56_18 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred103_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index56_18);
                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA56_19 = input.LA(1);

                         
                        int index56_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred103_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index56_19);
                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA56_20 = input.LA(1);

                         
                        int index56_20 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred103_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index56_20);
                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA56_21 = input.LA(1);

                         
                        int index56_21 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred103_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index56_21);
                        if ( s>=0 ) return s;
                        break;
                    case 21 : 
                        int LA56_22 = input.LA(1);

                         
                        int index56_22 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred103_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index56_22);
                        if ( s>=0 ) return s;
                        break;
                    case 22 : 
                        int LA56_23 = input.LA(1);

                         
                        int index56_23 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred103_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index56_23);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 56, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA57_eotS =
        "\32\uffff";
    static final String DFA57_eofS =
        "\32\uffff";
    static final String DFA57_minS =
        "\1\47\27\0\2\uffff";
    static final String DFA57_maxS =
        "\1\152\27\0\2\uffff";
    static final String DFA57_acceptS =
        "\30\uffff\1\1\1\2";
    static final String DFA57_specialS =
        "\1\uffff\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1"+
        "\14\1\15\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\25\1\26\2\uffff}>";
    static final String[] DFA57_transitionS = {
            "\1\1\1\6\1\7\1\10\1\2\14\uffff\1\5\7\uffff\1\11\1\12\1\13\1"+
            "\14\1\15\1\16\1\17\1\20\1\21\1\22\1\3\1\4\1\uffff\1\23\1\24"+
            "\1\25\31\uffff\1\27\1\26",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            ""
    };

    static final short[] DFA57_eot = DFA.unpackEncodedString(DFA57_eotS);
    static final short[] DFA57_eof = DFA.unpackEncodedString(DFA57_eofS);
    static final char[] DFA57_min = DFA.unpackEncodedStringToUnsignedChars(DFA57_minS);
    static final char[] DFA57_max = DFA.unpackEncodedStringToUnsignedChars(DFA57_maxS);
    static final short[] DFA57_accept = DFA.unpackEncodedString(DFA57_acceptS);
    static final short[] DFA57_special = DFA.unpackEncodedString(DFA57_specialS);
    static final short[][] DFA57_transition;

    static {
        int numStates = DFA57_transitionS.length;
        DFA57_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA57_transition[i] = DFA.unpackEncodedString(DFA57_transitionS[i]);
        }
    }

    class DFA57 extends DFA {

        public DFA57(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 57;
            this.eot = DFA57_eot;
            this.eof = DFA57_eof;
            this.min = DFA57_min;
            this.max = DFA57_max;
            this.accept = DFA57_accept;
            this.special = DFA57_special;
            this.transition = DFA57_transition;
        }
        public String getDescription() {
            return "243:1: in_predicate : ( super_lexical_value 'NOT' 'IN' in_predicate_tail -> ^( NOT ^( IN super_lexical_value in_predicate_tail ) ) | super_lexical_value 'IN' in_predicate_tail -> ^( IN super_lexical_value in_predicate_tail ) );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA57_1 = input.LA(1);

                         
                        int index57_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred104_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index57_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA57_2 = input.LA(1);

                         
                        int index57_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred104_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index57_2);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA57_3 = input.LA(1);

                         
                        int index57_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred104_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index57_3);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA57_4 = input.LA(1);

                         
                        int index57_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred104_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index57_4);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA57_5 = input.LA(1);

                         
                        int index57_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred104_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index57_5);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA57_6 = input.LA(1);

                         
                        int index57_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred104_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index57_6);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA57_7 = input.LA(1);

                         
                        int index57_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred104_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index57_7);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA57_8 = input.LA(1);

                         
                        int index57_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred104_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index57_8);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA57_9 = input.LA(1);

                         
                        int index57_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred104_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index57_9);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA57_10 = input.LA(1);

                         
                        int index57_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred104_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index57_10);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA57_11 = input.LA(1);

                         
                        int index57_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred104_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index57_11);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA57_12 = input.LA(1);

                         
                        int index57_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred104_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index57_12);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA57_13 = input.LA(1);

                         
                        int index57_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred104_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index57_13);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA57_14 = input.LA(1);

                         
                        int index57_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred104_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index57_14);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA57_15 = input.LA(1);

                         
                        int index57_15 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred104_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index57_15);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA57_16 = input.LA(1);

                         
                        int index57_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred104_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index57_16);
                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA57_17 = input.LA(1);

                         
                        int index57_17 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred104_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index57_17);
                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA57_18 = input.LA(1);

                         
                        int index57_18 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred104_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index57_18);
                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA57_19 = input.LA(1);

                         
                        int index57_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred104_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index57_19);
                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA57_20 = input.LA(1);

                         
                        int index57_20 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred104_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index57_20);
                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA57_21 = input.LA(1);

                         
                        int index57_21 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred104_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index57_21);
                        if ( s>=0 ) return s;
                        break;
                    case 21 : 
                        int LA57_22 = input.LA(1);

                         
                        int index57_22 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred104_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index57_22);
                        if ( s>=0 ) return s;
                        break;
                    case 22 : 
                        int LA57_23 = input.LA(1);

                         
                        int index57_23 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred104_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index57_23);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 57, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA60_eotS =
        "\32\uffff";
    static final String DFA60_eofS =
        "\32\uffff";
    static final String DFA60_minS =
        "\1\47\27\0\2\uffff";
    static final String DFA60_maxS =
        "\1\152\27\0\2\uffff";
    static final String DFA60_acceptS =
        "\30\uffff\1\1\1\2";
    static final String DFA60_specialS =
        "\1\uffff\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1"+
        "\14\1\15\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\25\1\26\2\uffff}>";
    static final String[] DFA60_transitionS = {
            "\1\1\1\6\1\7\1\10\1\2\14\uffff\1\5\7\uffff\1\11\1\12\1\13\1"+
            "\14\1\15\1\16\1\17\1\20\1\21\1\22\1\3\1\4\1\uffff\1\23\1\24"+
            "\1\25\31\uffff\1\27\1\26",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            ""
    };

    static final short[] DFA60_eot = DFA.unpackEncodedString(DFA60_eotS);
    static final short[] DFA60_eof = DFA.unpackEncodedString(DFA60_eofS);
    static final char[] DFA60_min = DFA.unpackEncodedStringToUnsignedChars(DFA60_minS);
    static final char[] DFA60_max = DFA.unpackEncodedStringToUnsignedChars(DFA60_maxS);
    static final short[] DFA60_accept = DFA.unpackEncodedString(DFA60_acceptS);
    static final short[] DFA60_special = DFA.unpackEncodedString(DFA60_specialS);
    static final short[][] DFA60_transition;

    static {
        int numStates = DFA60_transitionS.length;
        DFA60_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA60_transition[i] = DFA.unpackEncodedString(DFA60_transitionS[i]);
        }
    }

    class DFA60 extends DFA {

        public DFA60(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 60;
            this.eot = DFA60_eot;
            this.eof = DFA60_eof;
            this.min = DFA60_min;
            this.max = DFA60_max;
            this.accept = DFA60_accept;
            this.special = DFA60_special;
            this.transition = DFA60_transition;
        }
        public String getDescription() {
            return "252:1: between_predicate : (value= super_lexical_value 'BETWEEN' btw1= super_lexical_value 'AND' btw2= super_lexical_value -> ^( BETWEEN $value $btw1 $btw2) | value= super_lexical_value 'NOT' 'BETWEEN' btw1= super_lexical_value 'AND' btw2= super_lexical_value -> ^( NOT ^( BETWEEN $value $btw1 $btw2) ) );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA60_1 = input.LA(1);

                         
                        int index60_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred107_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index60_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA60_2 = input.LA(1);

                         
                        int index60_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred107_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index60_2);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA60_3 = input.LA(1);

                         
                        int index60_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred107_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index60_3);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA60_4 = input.LA(1);

                         
                        int index60_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred107_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index60_4);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA60_5 = input.LA(1);

                         
                        int index60_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred107_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index60_5);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA60_6 = input.LA(1);

                         
                        int index60_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred107_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index60_6);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA60_7 = input.LA(1);

                         
                        int index60_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred107_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index60_7);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA60_8 = input.LA(1);

                         
                        int index60_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred107_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index60_8);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA60_9 = input.LA(1);

                         
                        int index60_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred107_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index60_9);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA60_10 = input.LA(1);

                         
                        int index60_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred107_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index60_10);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA60_11 = input.LA(1);

                         
                        int index60_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred107_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index60_11);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA60_12 = input.LA(1);

                         
                        int index60_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred107_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index60_12);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA60_13 = input.LA(1);

                         
                        int index60_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred107_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index60_13);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA60_14 = input.LA(1);

                         
                        int index60_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred107_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index60_14);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA60_15 = input.LA(1);

                         
                        int index60_15 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred107_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index60_15);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA60_16 = input.LA(1);

                         
                        int index60_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred107_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index60_16);
                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA60_17 = input.LA(1);

                         
                        int index60_17 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred107_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index60_17);
                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA60_18 = input.LA(1);

                         
                        int index60_18 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred107_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index60_18);
                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA60_19 = input.LA(1);

                         
                        int index60_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred107_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index60_19);
                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA60_20 = input.LA(1);

                         
                        int index60_20 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred107_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index60_20);
                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA60_21 = input.LA(1);

                         
                        int index60_21 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred107_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index60_21);
                        if ( s>=0 ) return s;
                        break;
                    case 21 : 
                        int LA60_22 = input.LA(1);

                         
                        int index60_22 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred107_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index60_22);
                        if ( s>=0 ) return s;
                        break;
                    case 22 : 
                        int LA60_23 = input.LA(1);

                         
                        int index60_23 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred107_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index60_23);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 60, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA62_eotS =
        "\32\uffff";
    static final String DFA62_eofS =
        "\32\uffff";
    static final String DFA62_minS =
        "\1\47\27\0\2\uffff";
    static final String DFA62_maxS =
        "\1\152\27\0\2\uffff";
    static final String DFA62_acceptS =
        "\30\uffff\1\1\1\2";
    static final String DFA62_specialS =
        "\1\uffff\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1"+
        "\14\1\15\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\25\1\26\2\uffff}>";
    static final String[] DFA62_transitionS = {
            "\1\1\1\6\1\7\1\10\1\2\14\uffff\1\5\7\uffff\1\11\1\12\1\13\1"+
            "\14\1\15\1\16\1\17\1\20\1\21\1\22\1\3\1\4\1\uffff\1\23\1\24"+
            "\1\25\31\uffff\1\27\1\26",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            ""
    };

    static final short[] DFA62_eot = DFA.unpackEncodedString(DFA62_eotS);
    static final short[] DFA62_eof = DFA.unpackEncodedString(DFA62_eofS);
    static final char[] DFA62_min = DFA.unpackEncodedStringToUnsignedChars(DFA62_minS);
    static final char[] DFA62_max = DFA.unpackEncodedStringToUnsignedChars(DFA62_maxS);
    static final short[] DFA62_accept = DFA.unpackEncodedString(DFA62_acceptS);
    static final short[] DFA62_special = DFA.unpackEncodedString(DFA62_specialS);
    static final short[][] DFA62_transition;

    static {
        int numStates = DFA62_transitionS.length;
        DFA62_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA62_transition[i] = DFA.unpackEncodedString(DFA62_transitionS[i]);
        }
    }

    class DFA62 extends DFA {

        public DFA62(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 62;
            this.eot = DFA62_eot;
            this.eof = DFA62_eof;
            this.min = DFA62_min;
            this.max = DFA62_max;
            this.accept = DFA62_accept;
            this.special = DFA62_special;
            this.transition = DFA62_transition;
        }
        public String getDescription() {
            return "261:1: like_predicate : ( super_lexical_value 'LIKE' super_lexical_value | v1= super_lexical_value 'NOT' 'LIKE' v2= super_lexical_value -> ^( NOT ^( 'LIKE' $v1 $v2) ) );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA62_1 = input.LA(1);

                         
                        int index62_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred117_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index62_1);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA62_2 = input.LA(1);

                         
                        int index62_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred117_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index62_2);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA62_3 = input.LA(1);

                         
                        int index62_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred117_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index62_3);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA62_4 = input.LA(1);

                         
                        int index62_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred117_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index62_4);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA62_5 = input.LA(1);

                         
                        int index62_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred117_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index62_5);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA62_6 = input.LA(1);

                         
                        int index62_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred117_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index62_6);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA62_7 = input.LA(1);

                         
                        int index62_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred117_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index62_7);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA62_8 = input.LA(1);

                         
                        int index62_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred117_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index62_8);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA62_9 = input.LA(1);

                         
                        int index62_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred117_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index62_9);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA62_10 = input.LA(1);

                         
                        int index62_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred117_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index62_10);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA62_11 = input.LA(1);

                         
                        int index62_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred117_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index62_11);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA62_12 = input.LA(1);

                         
                        int index62_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred117_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index62_12);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA62_13 = input.LA(1);

                         
                        int index62_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred117_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index62_13);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA62_14 = input.LA(1);

                         
                        int index62_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred117_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index62_14);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA62_15 = input.LA(1);

                         
                        int index62_15 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred117_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index62_15);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA62_16 = input.LA(1);

                         
                        int index62_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred117_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index62_16);
                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA62_17 = input.LA(1);

                         
                        int index62_17 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred117_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index62_17);
                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA62_18 = input.LA(1);

                         
                        int index62_18 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred117_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index62_18);
                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA62_19 = input.LA(1);

                         
                        int index62_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred117_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index62_19);
                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA62_20 = input.LA(1);

                         
                        int index62_20 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred117_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index62_20);
                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA62_21 = input.LA(1);

                         
                        int index62_21 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred117_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index62_21);
                        if ( s>=0 ) return s;
                        break;
                    case 21 : 
                        int LA62_22 = input.LA(1);

                         
                        int index62_22 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred117_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index62_22);
                        if ( s>=0 ) return s;
                        break;
                    case 22 : 
                        int LA62_23 = input.LA(1);

                         
                        int index62_23 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred117_DSToolkitSQL()) ) {s = 24;}

                        else if ( (true) ) {s = 25;}

                         
                        input.seek(index62_23);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 62, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

    public static final BitSet FOLLOW_query_expression_in_statement236 = new BitSet(new long[]{0x2000200000000002L});
    public static final BitSet FOLLOW_order_by_in_statement238 = new BitSet(new long[]{0x0000200000000002L});
    public static final BitSet FOLLOW_45_in_statement241 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_query_in_query_expression261 = new BitSet(new long[]{0x0003400000000002L});
    public static final BitSet FOLLOW_set_op_in_query_expression264 = new BitSet(new long[]{0x0104000000000000L});
    public static final BitSet FOLLOW_query_in_query_expression267 = new BitSet(new long[]{0x0003400000000002L});
    public static final BitSet FOLLOW_46_in_set_op279 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_47_in_set_op281 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_set_op292 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_set_op303 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_47_in_set_op305 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_set_op316 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_49_in_set_op327 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_47_in_set_op329 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_49_in_set_op340 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_sub_query_in_query360 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_50_in_query365 = new BitSet(new long[]{0x05808F8000000000L,0x000000000000EFFFL});
    public static final BitSet FOLLOW_set_quantifier_in_query367 = new BitSet(new long[]{0x05808F8000000000L,0x000000000000EFFFL});
    public static final BitSet FOLLOW_super_lexical_select_list_in_query370 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_query372 = new BitSet(new long[]{0x0100008000000000L});
    public static final BitSet FOLLOW_super_abstract_expression_in_query374 = new BitSet(new long[]{0x0070000000000002L});
    public static final BitSet FOLLOW_52_in_query377 = new BitSet(new long[]{0x05808F8000000000L,0x000006004400EFFFL});
    public static final BitSet FOLLOW_search_condition_in_query381 = new BitSet(new long[]{0x0060000000000002L});
    public static final BitSet FOLLOW_53_in_query386 = new BitSet(new long[]{0x0000008000000000L,0x00000000000003FFL});
    public static final BitSet FOLLOW_super_lexical_list_in_query388 = new BitSet(new long[]{0x0040000000000002L});
    public static final BitSet FOLLOW_54_in_query393 = new BitSet(new long[]{0x05808F8000000000L,0x000006004400EFFFL});
    public static final BitSet FOLLOW_search_condition_in_query397 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_set_quantifier0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_56_in_sub_query469 = new BitSet(new long[]{0x0104000000000000L});
    public static final BitSet FOLLOW_query_expression_in_sub_query472 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_57_in_sub_query474 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_58_in_super_lexical_select_list484 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_derived_super_lexical_in_super_lexical_select_list498 = new BitSet(new long[]{0x0800000000000002L});
    public static final BitSet FOLLOW_59_in_super_lexical_select_list501 = new BitSet(new long[]{0x05808F8000000000L,0x000000000000EFFFL});
    public static final BitSet FOLLOW_derived_super_lexical_in_super_lexical_select_list504 = new BitSet(new long[]{0x0800000000000002L});
    public static final BitSet FOLLOW_value_expression_in_derived_super_lexical514 = new BitSet(new long[]{0x1000008000000002L});
    public static final BitSet FOLLOW_60_in_derived_super_lexical517 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_ID_in_derived_super_lexical520 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_61_in_order_by545 = new BitSet(new long[]{0x4000000000000000L});
    public static final BitSet FOLLOW_62_in_order_by547 = new BitSet(new long[]{0x0000018000000000L,0x00000000000003FFL});
    public static final BitSet FOLLOW_sort_specification_in_order_by549 = new BitSet(new long[]{0x0800000000000002L});
    public static final BitSet FOLLOW_59_in_order_by552 = new BitSet(new long[]{0x0000018000000000L,0x00000000000003FFL});
    public static final BitSet FOLLOW_sort_specification_in_order_by554 = new BitSet(new long[]{0x0800000000000002L});
    public static final BitSet FOLLOW_super_lexical_name_in_sort_specification573 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_sort_specification577 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_reserved_word_super_lexical_name_in_sort_specification581 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_reserved_word_super_lexical_name595 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_63_in_reserved_word_super_lexical_name596 = new BitSet(new long[]{0x0000000000000000L,0x00000000000003FFL});
    public static final BitSet FOLLOW_64_in_reserved_word_super_lexical_name602 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_reserved_word_super_lexical_name608 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_reserved_word_super_lexical_name614 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_67_in_reserved_word_super_lexical_name620 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_68_in_reserved_word_super_lexical_name626 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_69_in_reserved_word_super_lexical_name632 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_70_in_reserved_word_super_lexical_name638 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_71_in_reserved_word_super_lexical_name644 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_72_in_reserved_word_super_lexical_name650 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_73_in_reserved_word_super_lexical_name656 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_string_value_expression_in_value_expression687 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_numeric_value_expression_in_value_expression692 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_factor_in_numeric_value_expression704 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000C00L});
    public static final BitSet FOLLOW_set_in_numeric_value_expression707 = new BitSet(new long[]{0x05808F8000000000L,0x000000000000EFFFL});
    public static final BitSet FOLLOW_factor_in_numeric_value_expression714 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000C00L});
    public static final BitSet FOLLOW_numeric_primary_in_factor726 = new BitSet(new long[]{0x0400000000000002L,0x0000000000001000L});
    public static final BitSet FOLLOW_set_in_factor729 = new BitSet(new long[]{0x05808F8000000000L,0x000000000000EFFFL});
    public static final BitSet FOLLOW_numeric_primary_in_factor736 = new BitSet(new long[]{0x0400000000000002L,0x0000000000001000L});
    public static final BitSet FOLLOW_74_in_numeric_primary749 = new BitSet(new long[]{0x05808F8000000000L,0x000000000000EFFFL});
    public static final BitSet FOLLOW_75_in_numeric_primary752 = new BitSet(new long[]{0x05808F8000000000L,0x000000000000EFFFL});
    public static final BitSet FOLLOW_value_expression_primary_in_numeric_primary757 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_56_in_value_expression_primary767 = new BitSet(new long[]{0x05808F8000000000L,0x000000000000EFFFL});
    public static final BitSet FOLLOW_value_expression_in_value_expression_primary770 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_57_in_value_expression_primary772 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_in_value_expression_primary779 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_lexical_name_in_value_expression_primary785 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_value_expression_primary791 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_sub_query_in_value_expression_primary797 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal807 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal811 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NUMERIC_in_literal815 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal819 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_datetime_in_literal823 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_interval_in_literal827 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_77_in_literal831 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_78_in_literal835 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_79_in_literal839 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_datetime847 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_STRING_in_datetime860 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_datetime869 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_63_in_datetime870 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000007L});
    public static final BitSet FOLLOW_64_in_datetime876 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_datetime882 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_datetime888 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_67_in_interval912 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_STRING_in_interval915 = new BitSet(new long[]{0x0000000000000000L,0x00000000000003F0L});
    public static final BitSet FOLLOW_set_in_interval917 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_interval947 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_63_in_interval948 = new BitSet(new long[]{0x0000000000000000L,0x00000000000003F8L});
    public static final BitSet FOLLOW_67_in_interval954 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_68_in_interval960 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_69_in_interval966 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_70_in_interval972 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_71_in_interval978 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_72_in_interval984 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_73_in_interval990 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_function1018 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_56_in_function1021 = new BitSet(new long[]{0x0F808F8000000000L,0x000000000000EFFFL});
    public static final BitSet FOLLOW_value_expression_in_function1023 = new BitSet(new long[]{0x0A00000000000000L});
    public static final BitSet FOLLOW_59_in_function1027 = new BitSet(new long[]{0x05808F8000000000L,0x000000000000EFFFL});
    public static final BitSet FOLLOW_value_expression_in_function1029 = new BitSet(new long[]{0x0A00000000000000L});
    public static final BitSet FOLLOW_57_in_function1033 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_function1058 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_56_in_function1061 = new BitSet(new long[]{0x0400000000000000L});
    public static final BitSet FOLLOW_58_in_function1063 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_57_in_function1065 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_lexical_name_in_string_value_expression1088 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_STRING_in_string_value_expression1092 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_80_in_string_value_expression1096 = new BitSet(new long[]{0x0000088000000000L});
    public static final BitSet FOLLOW_super_lexical_name_in_string_value_expression1100 = new BitSet(new long[]{0x0000000000000002L,0x0000000000010000L});
    public static final BitSet FOLLOW_STRING_in_string_value_expression1104 = new BitSet(new long[]{0x0000000000000002L,0x0000000000010000L});
    public static final BitSet FOLLOW_super_abstract_reference_in_super_abstract_expression1119 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_abstract_in_super_abstract_reference1129 = new BitSet(new long[]{0x0800000000000002L});
    public static final BitSet FOLLOW_59_in_super_abstract_reference1132 = new BitSet(new long[]{0x0100008000000000L});
    public static final BitSet FOLLOW_super_abstract_reference_in_super_abstract_reference1135 = new BitSet(new long[]{0x0800000000000002L});
    public static final BitSet FOLLOW_81_in_join_type1148 = new BitSet(new long[]{0x0000000000000000L,0x00000000000C0000L});
    public static final BitSet FOLLOW_82_in_join_type1150 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_83_in_join_type1153 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_84_in_join_type1163 = new BitSet(new long[]{0x0000000000000000L,0x00000000000C0000L});
    public static final BitSet FOLLOW_82_in_join_type1165 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_83_in_join_type1168 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_85_in_join_type1177 = new BitSet(new long[]{0x0000000000000000L,0x00000000000C0000L});
    public static final BitSet FOLLOW_82_in_join_type1179 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_83_in_join_type1182 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_86_in_join_type1192 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_83_in_join_type1195 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_non_join_super_abstract_in_super_abstract1209 = new BitSet(new long[]{0x0000000000000002L,0x00000000007A0000L});
    public static final BitSet FOLLOW_join_type_in_super_abstract1212 = new BitSet(new long[]{0x0100008000000000L});
    public static final BitSet FOLLOW_non_join_super_abstract_in_super_abstract1215 = new BitSet(new long[]{0x0000000000000000L,0x0000000000800000L});
    public static final BitSet FOLLOW_87_in_super_abstract1217 = new BitSet(new long[]{0x05808F8000000000L,0x000006004400EFFFL});
    public static final BitSet FOLLOW_search_condition_in_super_abstract1220 = new BitSet(new long[]{0x0000000000000002L,0x00000000007A0000L});
    public static final BitSet FOLLOW_ID_in_non_join_super_abstract1237 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_63_in_non_join_super_abstract1238 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_ID_in_non_join_super_abstract1244 = new BitSet(new long[]{0x1000008000000002L});
    public static final BitSet FOLLOW_correlation_specification_in_non_join_super_abstract1247 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_abstract_function_in_non_join_super_abstract1270 = new BitSet(new long[]{0x1000008000000000L});
    public static final BitSet FOLLOW_correlation_specification_in_non_join_super_abstract1272 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_sub_query_in_non_join_super_abstract1288 = new BitSet(new long[]{0x1000008000000000L});
    public static final BitSet FOLLOW_correlation_specification_in_non_join_super_abstract1290 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_super_abstract_function1314 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_56_in_super_abstract_function1316 = new BitSet(new long[]{0x0F808F8000000000L,0x000006004400EFFFL});
    public static final BitSet FOLLOW_super_abstract_function_subquery_in_super_abstract_function1318 = new BitSet(new long[]{0x0F808F8000000000L,0x000006004400EFFFL});
    public static final BitSet FOLLOW_59_in_super_abstract_function1322 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_super_abstract_function_subquery_in_super_abstract_function1324 = new BitSet(new long[]{0x0F808F8000000000L,0x000006004400EFFFL});
    public static final BitSet FOLLOW_59_in_super_abstract_function1329 = new BitSet(new long[]{0x0D808F8000000000L,0x000006004400EFFFL});
    public static final BitSet FOLLOW_super_abstract_function_param_in_super_abstract_function1332 = new BitSet(new long[]{0x0F808F8000000000L,0x000006004400EFFFL});
    public static final BitSet FOLLOW_57_in_super_abstract_function1336 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_sub_query_in_super_abstract_function_subquery1368 = new BitSet(new long[]{0x1000008000000000L});
    public static final BitSet FOLLOW_correlation_specification_in_super_abstract_function_subquery1370 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_search_condition_in_super_abstract_function_param1391 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_value_expression_in_super_abstract_function_param1396 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_boolean_factor_in_search_condition1409 = new BitSet(new long[]{0x0000000000000002L,0x0000000001000000L});
    public static final BitSet FOLLOW_88_in_search_condition1412 = new BitSet(new long[]{0x05808F8000000000L,0x000006004400EFFFL});
    public static final BitSet FOLLOW_boolean_factor_in_search_condition1415 = new BitSet(new long[]{0x0000000000000002L,0x0000000001000000L});
    public static final BitSet FOLLOW_boolean_term_in_boolean_factor1427 = new BitSet(new long[]{0x0000000000000002L,0x0000000002000000L});
    public static final BitSet FOLLOW_89_in_boolean_factor1430 = new BitSet(new long[]{0x05808F8000000000L,0x000006004400EFFFL});
    public static final BitSet FOLLOW_boolean_term_in_boolean_factor1433 = new BitSet(new long[]{0x0000000000000002L,0x0000000002000000L});
    public static final BitSet FOLLOW_boolean_test_in_boolean_term1446 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_90_in_boolean_term1451 = new BitSet(new long[]{0x05808F8000000000L,0x000006004400EFFFL});
    public static final BitSet FOLLOW_boolean_term_in_boolean_term1453 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_boolean_primary_in_boolean_test1469 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_predicate_in_boolean_primary1477 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_56_in_boolean_primary1481 = new BitSet(new long[]{0x05808F8000000000L,0x000006004400EFFFL});
    public static final BitSet FOLLOW_search_condition_in_boolean_primary1484 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_57_in_boolean_primary1486 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_comparison_predicate_in_predicate1496 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_like_predicate_in_predicate1500 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_in_predicate_in_predicate1504 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_null_predicate_in_predicate1508 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exists_predicate_in_predicate1512 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_between_predicate_in_predicate1516 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_lexical_value_in_null_predicate1524 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_91_in_null_predicate1526 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_77_in_null_predicate1528 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_lexical_value_in_null_predicate1541 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_91_in_null_predicate1543 = new BitSet(new long[]{0x0000000000000000L,0x0000000004000000L});
    public static final BitSet FOLLOW_90_in_null_predicate1545 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_77_in_null_predicate1547 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_lexical_value_in_in_predicate1569 = new BitSet(new long[]{0x0000000000000000L,0x0000000004000000L});
    public static final BitSet FOLLOW_90_in_in_predicate1571 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_92_in_in_predicate1573 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_in_predicate_tail_in_in_predicate1575 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_lexical_value_in_in_predicate1597 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_92_in_in_predicate1599 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_in_predicate_tail_in_in_predicate1601 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_sub_query_in_in_predicate_tail1624 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_56_in_in_predicate_tail1631 = new BitSet(new long[]{0x05808F8000000000L,0x000000000000EFFFL});
    public static final BitSet FOLLOW_value_expression_in_in_predicate_tail1634 = new BitSet(new long[]{0x0A00000000000000L});
    public static final BitSet FOLLOW_59_in_in_predicate_tail1637 = new BitSet(new long[]{0x05808F8000000000L,0x000000000000EFFFL});
    public static final BitSet FOLLOW_value_expression_in_in_predicate_tail1639 = new BitSet(new long[]{0x0A00000000000000L});
    public static final BitSet FOLLOW_57_in_in_predicate_tail1644 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_lexical_value_in_between_predicate1664 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_93_in_between_predicate1666 = new BitSet(new long[]{0x05808F8000000000L,0x000006000000EFFFL});
    public static final BitSet FOLLOW_super_lexical_value_in_between_predicate1670 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_89_in_between_predicate1672 = new BitSet(new long[]{0x05808F8000000000L,0x000006000000EFFFL});
    public static final BitSet FOLLOW_super_lexical_value_in_between_predicate1676 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_lexical_value_in_between_predicate1702 = new BitSet(new long[]{0x0000000000000000L,0x0000000004000000L});
    public static final BitSet FOLLOW_90_in_between_predicate1704 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_93_in_between_predicate1706 = new BitSet(new long[]{0x05808F8000000000L,0x000006000000EFFFL});
    public static final BitSet FOLLOW_super_lexical_value_in_between_predicate1710 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_89_in_between_predicate1712 = new BitSet(new long[]{0x05808F8000000000L,0x000006000000EFFFL});
    public static final BitSet FOLLOW_super_lexical_value_in_between_predicate1716 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_94_in_exists_predicate1746 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_sub_query_in_exists_predicate1748 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_lexical_value_in_comparison_predicate1764 = new BitSet(new long[]{0x0000000000000000L,0x0000003F80000000L});
    public static final BitSet FOLLOW_set_in_comparison_predicate1766 = new BitSet(new long[]{0x05808F8000000000L,0x000006C00000EFFFL});
    public static final BitSet FOLLOW_set_in_comparison_predicate1795 = new BitSet(new long[]{0x05808F8000000000L,0x000006000000EFFFL});
    public static final BitSet FOLLOW_super_lexical_value_in_comparison_predicate1804 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_lexical_value_in_like_predicate1812 = new BitSet(new long[]{0x0000000000000000L,0x0000010000000000L});
    public static final BitSet FOLLOW_104_in_like_predicate1814 = new BitSet(new long[]{0x05808F8000000000L,0x000006000000EFFFL});
    public static final BitSet FOLLOW_super_lexical_value_in_like_predicate1817 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_lexical_value_in_like_predicate1824 = new BitSet(new long[]{0x0000000000000000L,0x0000000004000000L});
    public static final BitSet FOLLOW_90_in_like_predicate1826 = new BitSet(new long[]{0x0000000000000000L,0x0000010000000000L});
    public static final BitSet FOLLOW_104_in_like_predicate1828 = new BitSet(new long[]{0x05808F8000000000L,0x000006000000EFFFL});
    public static final BitSet FOLLOW_super_lexical_value_in_like_predicate1832 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_value_expression_in_super_lexical_value1857 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_bind_super_abstract_in_super_lexical_value1861 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_77_in_super_lexical_value1864 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_105_in_super_lexical_value1868 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_106_in_bind_super_abstract1878 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_ID_in_bind_super_abstract1881 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_63_in_bind_super_abstract1882 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_ID_in_bind_super_abstract1885 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_60_in_correlation_specification1913 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_ID_in_correlation_specification1918 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_super_abstract_name1927 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_schema_name1935 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_lexical_name_in_super_lexical_list1944 = new BitSet(new long[]{0x0800000000000002L});
    public static final BitSet FOLLOW_reserved_word_super_lexical_name_in_super_lexical_list1948 = new BitSet(new long[]{0x0800000000000002L});
    public static final BitSet FOLLOW_59_in_super_lexical_list1952 = new BitSet(new long[]{0x0000008000000000L,0x00000000000003FFL});
    public static final BitSet FOLLOW_super_lexical_name_in_super_lexical_list1956 = new BitSet(new long[]{0x0800000000000002L});
    public static final BitSet FOLLOW_reserved_word_super_lexical_name_in_super_lexical_list1960 = new BitSet(new long[]{0x0800000000000002L});
    public static final BitSet FOLLOW_ID_in_super_lexical_name1974 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_63_in_super_lexical_name1975 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_ID_in_super_lexical_name1981 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_63_in_super_lexical_name1982 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_ID_in_super_lexical_name1987 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_synpred34_DSToolkitSQL707 = new BitSet(new long[]{0x05808F8000000000L,0x000000000000EFFFL});
    public static final BitSet FOLLOW_factor_in_synpred34_DSToolkitSQL714 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_56_in_synpred39_DSToolkitSQL767 = new BitSet(new long[]{0x05808F8000000000L,0x000000000000EFFFL});
    public static final BitSet FOLLOW_value_expression_in_synpred39_DSToolkitSQL770 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_57_in_synpred39_DSToolkitSQL772 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_in_synpred40_DSToolkitSQL779 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_lexical_name_in_synpred41_DSToolkitSQL785 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_synpred42_DSToolkitSQL791 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_synpred53_DSToolkitSQL847 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_STRING_in_synpred53_DSToolkitSQL860 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_67_in_synpred62_DSToolkitSQL912 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_STRING_in_synpred62_DSToolkitSQL915 = new BitSet(new long[]{0x0000000000000000L,0x00000000000003F0L});
    public static final BitSet FOLLOW_set_in_synpred62_DSToolkitSQL917 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_59_in_synpred76_DSToolkitSQL1132 = new BitSet(new long[]{0x0100008000000000L});
    public static final BitSet FOLLOW_super_abstract_reference_in_synpred76_DSToolkitSQL1135 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_abstract_function_subquery_in_synpred89_DSToolkitSQL1318 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_59_in_synpred90_DSToolkitSQL1322 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_super_abstract_function_subquery_in_synpred90_DSToolkitSQL1324 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_search_condition_in_synpred93_DSToolkitSQL1391 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_predicate_in_synpred97_DSToolkitSQL1477 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_comparison_predicate_in_synpred98_DSToolkitSQL1496 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_like_predicate_in_synpred99_DSToolkitSQL1500 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_in_predicate_in_synpred100_DSToolkitSQL1504 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_null_predicate_in_synpred101_DSToolkitSQL1508 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_lexical_value_in_synpred103_DSToolkitSQL1524 = new BitSet(new long[]{0x0000000000000000L,0x0000000008000000L});
    public static final BitSet FOLLOW_91_in_synpred103_DSToolkitSQL1526 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_77_in_synpred103_DSToolkitSQL1528 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_lexical_value_in_synpred104_DSToolkitSQL1569 = new BitSet(new long[]{0x0000000000000000L,0x0000000004000000L});
    public static final BitSet FOLLOW_90_in_synpred104_DSToolkitSQL1571 = new BitSet(new long[]{0x0000000000000000L,0x0000000010000000L});
    public static final BitSet FOLLOW_92_in_synpred104_DSToolkitSQL1573 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_in_predicate_tail_in_synpred104_DSToolkitSQL1575 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_sub_query_in_synpred105_DSToolkitSQL1624 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_lexical_value_in_synpred107_DSToolkitSQL1664 = new BitSet(new long[]{0x0000000000000000L,0x0000000020000000L});
    public static final BitSet FOLLOW_93_in_synpred107_DSToolkitSQL1666 = new BitSet(new long[]{0x05808F8000000000L,0x000006000000EFFFL});
    public static final BitSet FOLLOW_super_lexical_value_in_synpred107_DSToolkitSQL1670 = new BitSet(new long[]{0x0000000000000000L,0x0000000002000000L});
    public static final BitSet FOLLOW_89_in_synpred107_DSToolkitSQL1672 = new BitSet(new long[]{0x05808F8000000000L,0x000006000000EFFFL});
    public static final BitSet FOLLOW_super_lexical_value_in_synpred107_DSToolkitSQL1676 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_super_lexical_value_in_synpred117_DSToolkitSQL1812 = new BitSet(new long[]{0x0000000000000000L,0x0000010000000000L});
    public static final BitSet FOLLOW_104_in_synpred117_DSToolkitSQL1814 = new BitSet(new long[]{0x05808F8000000000L,0x000006000000EFFFL});
    public static final BitSet FOLLOW_super_lexical_value_in_synpred117_DSToolkitSQL1817 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_value_expression_in_synpred118_DSToolkitSQL1857 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_77_in_synpred120_DSToolkitSQL1864 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_synpred125_DSToolkitSQL1974 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_63_in_synpred125_DSToolkitSQL1975 = new BitSet(new long[]{0x0000000000000002L});

}