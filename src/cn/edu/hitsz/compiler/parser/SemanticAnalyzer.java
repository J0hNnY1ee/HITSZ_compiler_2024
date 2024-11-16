package cn.edu.hitsz.compiler.parser;

import cn.edu.hitsz.compiler.lexer.Token;
import cn.edu.hitsz.compiler.parser.table.Production;
import cn.edu.hitsz.compiler.parser.table.Status;
import cn.edu.hitsz.compiler.symtab.SourceCodeType;
import cn.edu.hitsz.compiler.symtab.SymbolTable;

import java.util.ArrayList;

// 实验三: 实现语义分析
// 该类负责根据语法分析的结果进行语义分析，包括符号表的更新以及类型推断
public class SemanticAnalyzer implements ActionObserver {

    // 符号表，用于存储变量及其类型信息
    private SymbolTable symbolTable = null;

    // tokenStack 用于存储分析过程中遇到的 Token
    private final ArrayList<Token> tokenStack = new ArrayList<>();

    // dataStack 用于存储分析过程中推断出的类型信息
    private final ArrayList<SourceCodeType> dataStack = new ArrayList<>();

    /**
     * 接受状态时的操作（语法分析成功完成）
     * @param currentStatus 当前的状态
     */
    @Override
    public void whenAccept(Status currentStatus) {
        // 打印接受状态的提示信息
        System.out.println("Accept " + currentStatus.toString());
    }

    /**
     * 规约时的操作，根据产生式的不同对符号表和类型信息进行更新
     * @param currentStatus 当前的状态
     * @param production 当前使用的产生式
     */
    @Override
    public void whenReduce(Status currentStatus, Production production) {
        switch (production.index()) {
            // 产生式 4: D -> int id
            case 4 -> {
                // 设置符号表中变量的类型为 int
                // 获取最后一个 token（即 id），并将其类型设为 int
                String identifier = tokenStack.get(tokenStack.size() - 1).getText();
                SourceCodeType type = dataStack.get(dataStack.size() - 2);
                symbolTable.get(identifier).setType(type);
            }

            // 产生式 5: D -> int
            case 5 -> {
                // 移除栈顶元素，因为 "int" 已经处理过了
                tokenStack.remove(tokenStack.size() - 1);
                dataStack.remove(dataStack.size() - 1);

                // 将占位符 null 压入 tokenStack，同时将类型设为 int
                tokenStack.add(null);
                dataStack.add(SourceCodeType.Int);
            }

            // 处理其他产生式
            default -> {
                // 规约过程中，将产生式右部的符号从栈中移除
                for (int i = 0; i < production.body().size(); i++) {
                    tokenStack.remove(tokenStack.size() - 1);
                    dataStack.remove(dataStack.size() - 1);
                }

                // 压入占位符和空类型，表示规约完成
                tokenStack.add(null);
                dataStack.add(null);
            }
        }
    }

    /**
     * 移进操作时的操作，将当前 Token 和其类型压入栈
     * @param currentStatus 当前的状态
     * @param currentToken 当前的 Token
     */
    @Override
    public void whenShift(Status currentStatus, Token currentToken) {
        // 将当前 Token 压入 tokenStack
        tokenStack.add(currentToken);

        // 根据 Token 类型判断其对应的数据类型
        if (currentToken.getKind().getTermName().equals("int")) {
            // 如果是 "int" 类型，将类型信息压入 dataStack
            dataStack.add(SourceCodeType.Int);
        } else {
            // 否则压入 null，表示当前 token 未知类型
            dataStack.add(null);
        }
    }

    /**
     * 设置符号表
     * @param table 符号表，用于存储变量及其类型信息
     */
    @Override
    public void setSymbolTable(SymbolTable table) {
        // 将传入的符号表存储在成员变量中，以便后续使用
        this.symbolTable = table;
    }
}
