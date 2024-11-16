package cn.edu.hitsz.compiler.parser;

import cn.edu.hitsz.compiler.ir.IRValue;
import cn.edu.hitsz.compiler.lexer.Token;
import cn.edu.hitsz.compiler.parser.table.NonTerminal;
import cn.edu.hitsz.compiler.symtab.SourceCodeType;

public class bak_Symbol{
    Token token;
    NonTerminal nonTerminal;
    SourceCodeType type = null;
    IRValue value = null;

    private bak_Symbol(Token token, NonTerminal nonTerminal){
        this.token = token;
        this.nonTerminal = nonTerminal;
    }

    public bak_Symbol(Token token){
        this(token, null);
    }
    public bak_Symbol(NonTerminal nonTerminal){
        this(null, nonTerminal);
    }
    public boolean isToken(){
        return this.token != null;
    }
    public boolean isNonterminal(){
        return this.nonTerminal != null;
    }
}