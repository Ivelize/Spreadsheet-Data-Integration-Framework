grammar DSToolkitSQL;

options 
{
	output = AST;
	ASTLabelType = CommonTree;
	backtrack=true;
	language=Java;
}

tokens 
{
	SQL_STATEMENT;
	QUERY;
	SETOP;
	ORDER;
	SELECT_LIST;
	FROM_LIST;
	WHERE;
	IN;
	EXISTS;
	BETWEEN;
	GROUP_BY;
	HAVING;
	SUPER_ABSTRACT;
	SUPER_LEXICAL;
	SUPER_RELATIONSHIP;
	FUNCTION;
	NOT;
	SET;
	SUPER_LEXICAL_OF_SUPER_ABSTRACT;
	RIGHT_OUTER_JOIN;
	LEFT_OUTER_JOIN;
	FULL_OUTER_JOIN;
	JOIN;
	IS_NULL;
	UNION;
	EXCEPT;
	INTERSECT;
	UNION_ALL;
	EXCEPT_ALL;
	INTERSECT_ALL;
	BOUND;
	ASC;
	DESC;
	AND;
	OR;
}

@header
{
package uk.ac.manchester.dstoolkit.service.impl.query.queryparser;
}

@lexer::header
{
package uk.ac.manchester.dstoolkit.service.impl.query.queryparser;
}

@members 
{
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
}

@lexer::members 
{
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
}

@rulecatch {
catch (RecognitionException e)
{
    reportError(e);
    throw e;
}
}

@lexer::rulecatch {
catch (RecognitionException e)
{
    reportError(e);
    throw e;
}
}

statement
	:	query_expression order_by? ';'? -> ^(SQL_STATEMENT query_expression order_by?);
query_expression
	:	query (set_op^ query)*
	;

set_op	:	'UNION' 'ALL' -> ^(UNION_ALL)
	|	'UNION' -> ^(UNION)
	|	'EXCEPT' 'ALL' -> ^(EXCEPT_ALL)
	|	'EXCEPT' -> ^(EXCEPT)
	|	'INTERSECT' 'ALL' -> ^(INTERSECT_ALL)
	|	'INTERSECT' -> ^(INTERSECT)
	;

	
query	
	:	sub_query
	|	'SELECT' set_quantifier? super_lexical_select_list 'FROM' super_abstract_expression ('WHERE' s1=search_condition)? ('GROUP BY' super_lexical_list)? ('HAVING' s2=search_condition)?
		 	-> ^(QUERY ^(SELECT_LIST set_quantifier? super_lexical_select_list) ^(FROM_LIST super_abstract_expression) ^(WHERE $s1)? ^(GROUP_BY super_lexical_list)? ^(HAVING $s2)?)
	;
set_quantifier
	:	'DISTINCT' | 'ALL';
sub_query
	:	'('! query_expression ')'!;

super_lexical_select_list
	:	'*' -> ^(SUPER_LEXICAL '*')
	| 	derived_super_lexical (','! derived_super_lexical)*;
derived_super_lexical
	:	value_expression ('AS'? ID)? -> ^(SUPER_LEXICAL value_expression ID?)
	;
	
order_by
	:	'ORDER' 'BY' sort_specification (',' sort_specification)* -> ^(ORDER sort_specification+);
sort_specification
	:	super_lexical_name | INT | reserved_word_super_lexical_name
	;

reserved_word_super_lexical_name
	:	(super_abstract_id=ID'.')?(s='DATE' | s='TIMESTAMP' | s='TIME' | s='INTERVAL' | s='YEAR' | s='MONTH' | s='DAY' | s='HOUR' | s='MINUTE' | s='SECOND' )	
			-> ^(SUPER_LEXICAL_OF_SUPER_ABSTRACT $super_abstract_id? $s)
	;
	
value_expression
	:	string_value_expression
	|	numeric_value_expression 
	;
numeric_value_expression
	: 	factor (('+'|'-')^ factor)* ;
	
factor	:	numeric_primary (('*'|'/')^ numeric_primary)*;

numeric_primary
	: 	('+'^|'-'^)? value_expression_primary; 
value_expression_primary 
	:	'('! value_expression ')'!
	| 	function
	| 	super_lexical_name
	| 	literal
	| 	sub_query
	;

literal	:	INT | FLOAT | NUMERIC | STRING | datetime | interval | 'NULL' | 'TRUE' | 'FALSE';
datetime
	:	('DATE' | 'TIMESTAMP' | 'TIME')^ STRING
	| 	(tableid=ID'.')?(s='DATE' | s='TIMESTAMP' | s='TIME') -> ^(SUPER_LEXICAL_OF_SUPER_ABSTRACT $tableid? $s)
	;
interval
	:	'INTERVAL'^ STRING ('YEAR' | 'MONTH' | 'DAY' | 'HOUR' | 'MINUTE' | 'SECOND')
	|	(tableid=ID'.')?(s='INTERVAL' | s='YEAR' | s='MONTH' | s='DAY' | s='HOUR' | s='MINUTE' | s='SECOND') -> ^(SUPER_LEXICAL_OF_SUPER_ABSTRACT $tableid? $s)
	;
		
function:	(name=ID) '(' value_expression? (',' value_expression)* ')' 
			-> ^(FUNCTION $name value_expression*)
	| 	(name=ID) '(' '*' ')' -> ^(FUNCTION $name '*')
	;

string_value_expression
	:	(super_lexical_name | STRING) ('||'^ (super_lexical_name | STRING))+
	;
	
super_abstract_expression
	:	super_abstract_reference
	;
super_abstract_reference
	:	super_abstract (','! super_abstract_reference)*
	;

join_type
	:	'RIGHT' 'OUTER'? 'JOIN' -> RIGHT_OUTER_JOIN 
	|	'LEFT' 'OUTER'? 'JOIN' -> LEFT_OUTER_JOIN
	|	'FULL' 'OUTER'? 'JOIN' -> FULL_OUTER_JOIN
	| 	'INNER'? 'JOIN' -> JOIN
	;
super_abstract 	:	non_join_super_abstract (join_type^ non_join_super_abstract 'ON'! search_condition)*
	;


non_join_super_abstract
	:	(schema_id=ID'.')?(super_abstract_id=ID) correlation_specification? -> ^(SUPER_ABSTRACT $schema_id? $super_abstract_id correlation_specification?)
	| 	super_abstract_function correlation_specification -> ^(SUPER_ABSTRACT super_abstract_function correlation_specification)
	| 	sub_query correlation_specification -> ^(SUPER_ABSTRACT sub_query correlation_specification)
	;
	
super_abstract_function
	:	name=ID '(' super_abstract_function_subquery? (',' super_abstract_function_subquery)* (','? super_abstract_function_param)* ')'
			 -> ^(FUNCTION $name (super_abstract_function_subquery)* super_abstract_function_param*)
	;

super_abstract_function_subquery
	:	sub_query correlation_specification -> ^(SUPER_ABSTRACT sub_query correlation_specification)
	;

super_abstract_function_param
	:	search_condition
	|	value_expression
	;	
	
search_condition
	:	boolean_factor ('OR'^ boolean_factor)*
	;
boolean_factor
	:	boolean_term ('AND'^ boolean_term)* 
	;
boolean_term
	:	boolean_test
	|	'NOT' boolean_term -> ^(NOT boolean_term);
boolean_test
	:	boolean_primary;
boolean_primary
	:	predicate | '('! search_condition ')'!;	
predicate
	:	comparison_predicate | like_predicate | in_predicate | null_predicate | exists_predicate | between_predicate;
null_predicate
	:	super_lexical_value 'IS' 'NULL' -> ^(IS_NULL super_lexical_value)
	|	super_lexical_value 'IS' 'NOT' 'NULL' -> ^(NOT ^(IS_NULL super_lexical_value))
	;
in_predicate
	:	super_lexical_value 'NOT' 'IN' in_predicate_tail
			-> ^(NOT ^(IN super_lexical_value in_predicate_tail))
	|	super_lexical_value 'IN' in_predicate_tail
			-> ^(IN super_lexical_value in_predicate_tail)
	;
in_predicate_tail
	:	sub_query 
	| 	'(' (value_expression (',' value_expression)*) ')' -> ^(SET value_expression*) ;
between_predicate
	:	value=super_lexical_value 'BETWEEN' btw1=super_lexical_value 'AND' btw2=super_lexical_value 
			-> ^(BETWEEN $value $btw1 $btw2)
	|	value=super_lexical_value 'NOT' 'BETWEEN' btw1=super_lexical_value 'AND' btw2=super_lexical_value
			-> ^(NOT ^(BETWEEN $value $btw1 $btw2));
exists_predicate
	:	'EXISTS' sub_query -> ^(EXISTS sub_query);
comparison_predicate
	:	super_lexical_value ('=' | '<>' | '!=' | '<' | '>' | '>=' | '<=')^ ('ALL'|'SOME'|'ANY')? super_lexical_value;
like_predicate
	:	super_lexical_value 'LIKE'^ super_lexical_value
	|	v1=super_lexical_value 'NOT' 'LIKE' v2=super_lexical_value -> ^(NOT ^('LIKE' $v1 $v2));

super_lexical_value
	:	value_expression | bind_super_abstract |'NULL' | 'DEFAULT' ;

bind_super_abstract
	:	'@'super_abstract_id=ID'.'super_lexical_id=ID -> ^(BOUND ^(SUPER_LEXICAL_OF_SUPER_ABSTRACT $super_abstract_id $super_lexical_id))
	;

correlation_specification
	:	('AS'!)? ID;	
super_abstract_name
	:	ID;
schema_name
	:	ID;
super_lexical_list
	:	(super_lexical_name | reserved_word_super_lexical_name) (','! (super_lexical_name | reserved_word_super_lexical_name))*;
super_lexical_name
	:	(schema_id=ID'.')?(super_abstract_id=ID'.')?super_lexical_id=ID -> ^(SUPER_LEXICAL_OF_SUPER_ABSTRACT $schema_id? $super_abstract_id? $super_lexical_id)
	;

ID	:	( ('a'..'z' | 'A'..'Z') | '_' ) ( ('a'..'z' | 'A'..'Z') | ('0'..'9') | '_' )* 
	|	'`' (~('\''|'\n'|'\r'|'`')) ( (~('\''|'\n'|'\r'|'`')) )* '`' ;
FLOAT	:	('0'..'9')+ '.' ('0'..'9')+ ;
INT	:	('0'..'9')+ ;
NUMERIC	:	(INT | FLOAT) 'E' ('+' | '-')? INT;
STRING	:	'"' (~('"'|'\n'|'\r'))*  '"'
	|	'\'' (~('\''|'\n'|'\r'))*  '\'';
WS	:	(' ' | '\t' | '\r' | '\n' ) {skip();} ;
