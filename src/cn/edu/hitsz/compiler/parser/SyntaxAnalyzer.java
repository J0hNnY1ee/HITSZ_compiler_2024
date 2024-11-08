package cn.edu.hitsz.compiler.parser;

import cn.edu.hitsz.compiler.lexer.Token;
import cn.edu.hitsz.compiler.parser.table.Action;
import cn.edu.hitsz.compiler.parser.table.Action.ActionKind;
import cn.edu.hitsz.compiler.parser.table.LRTable;
import cn.edu.hitsz.compiler.parser.table.NonTerminal;
import cn.edu.hitsz.compiler.parser.table.Production;
import cn.edu.hitsz.compiler.parser.table.Status;
import cn.edu.hitsz.compiler.symtab.SymbolTable;

import java.util.ArrayList;
import java.util.List;


/**
 * LR 语法分析驱动程序
 * <br>
 * 该程序接受词法单元串与 LR 分析表 (action 和 goto 表), 按表对词法单元流进行分析, 执行对应动作, 并在执行动作时通知各注册的观察者.
 * <br>
 * 你应当按照被挖空的方法的文档实现对应方法, 你可以随意为该类添加你需要的私有成员对象, 但不应该再为此类添加公有接口, 也不应该改动未被挖空的方法,
 * 除非你已经同助教充分沟通, 并能证明你的修改的合理性, 且令助教确定可能被改动的评测方法. 随意修改该类的其它部分有可能导致自动评测出错而被扣分.
 */
public class SyntaxAnalyzer {
    private final SymbolTable symbolTable;
    private final List<ActionObserver> observers = new ArrayList<>();
    private List<Token> tokenList; // 存储词法单元的列表
    private int currentTokenIndex; // 当前正在处理的 token 索引
    private LRTable lrTable; // 用于存储传入的 LR 分析表
    private Status currentStatus; // 当前状态
    private List<Status> statusStack = new ArrayList<>(); // 状态栈
    private List<Object> symbolStack = new ArrayList<>(); // 符号栈 (终结符/非终结符)
    public SyntaxAnalyzer(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        this.tokenList = new ArrayList<>(); // 初始化词法单元列表
        this.currentTokenIndex = 0; // 初始化 token 索引
    }

    /**
     * 注册新的观察者
     *
     * @param observer 观察者
     */
    public void registerObserver(ActionObserver observer) {
        observers.add(observer);
        observer.setSymbolTable(symbolTable);
    }

    /**
     * 在执行 shift 动作时通知各个观察者
     *
     * @param currentStatus 当前状态
     * @param currentToken  当前词法单元
     */
    public void callWhenInShift(Status currentStatus, Token currentToken) {
        for (final var listener : observers) {
            listener.whenShift(currentStatus, currentToken);
        }
    }

    /**
     * 在执行 reduce 动作时通知各个观察者
     *
     * @param currentStatus 当前状态
     * @param production    待规约的产生式
     */
    public void callWhenInReduce(Status currentStatus, Production production) {
        for (final var listener : observers) {
            listener.whenReduce(currentStatus, production);
        }
    }

    /**
     * 在执行 accept 动作时通知各个观察者
     *
     * @param currentStatus 当前状态
     */
    public void callWhenInAccept(Status currentStatus) {
        for (final var listener : observers) {
            listener.whenAccept(currentStatus);
        }
    }

    public void loadTokens(Iterable<Token> tokens) {
        // 你可以自行选择要如何存储词法单元, 譬如使用迭代器, 或是栈, 或是干脆使用一个 list 全存起来
        // 需要注意的是, 在实现驱动程序的过程中, 你会需要面对只读取一个 token 而不能消耗它的情况,
        // 在自行设计的时候请加以考虑此种情况
        for (Token token : tokens) {
            tokenList.add(token);
        }
    }

    public void loadLRTable(LRTable table) {
        // 将传入的 LRTable 存储起来
        this.lrTable = table;
        // 初始化状态为 LR 表中的初始状态
        this.currentStatus = lrTable.getInit();
    }

  public void run() {
        // 初始化状态栈，推入初始状态
        statusStack.add(lrTable.getInit());

        // 遍历输入的 token 列表
        while (currentTokenIndex < tokenList.size()) {
            Token currentToken = tokenList.get(currentTokenIndex);
            currentStatus = statusStack.get(statusStack.size() - 1); // 获取状态栈顶的当前状态

            // 从 LR 表中获取当前状态和当前 token 对应的动作
            Action action = lrTable.getAction(currentStatus, currentToken);
            ActionKind action_kind = action.getKind(); 
            if (action_kind == Action.ActionKind.Shift) {
                // 1. Shift 操作
                Status newStatus = action.getStatus();
                statusStack.add(newStatus); // 将新状态压入状态栈
                symbolStack.add(currentToken); // 将当前 token 压入符号栈
                currentTokenIndex++; // 读取下一个 token
                callWhenInShift(currentStatus, currentToken); // 通知观察者 Shift 动作
            } else if (action_kind == Action.ActionKind.Reduce) {
                // 2. Reduce 操作
                Production production = action.getProduction();
                // 从符号栈和状态栈中弹出产生式右部的符号数
                for (int i = 0; i < production.body().size(); i++) {
                    statusStack.removeLast();
                    symbolStack.removeLast();
                }
                // 获取规约后的非终结符，并压入符号栈
                NonTerminal nonTerminal = production.head();
                symbolStack.add(nonTerminal);

                // 根据状态栈顶和非终结符，获取新的状态并压入状态栈
                Status gotoStatus = lrTable.getGoto(statusStack.get(statusStack.size() - 1), nonTerminal);
                statusStack.add(gotoStatus);

                callWhenInReduce(currentStatus, production); // 通知观察者 Reduce 动作
            } else if (action_kind == Action.ActionKind.Accept) {
                // 3. Accept 操作
                callWhenInAccept(currentStatus); // 通知观察者 Accept 动作
                break; // 分析完成，跳出循环
            } else {
                // 4. 如果没有匹配到合法的动作，则抛出异常或处理错误
                throw new RuntimeException("Syntax Error: Unrecognized token or state.");
            }
        }
    }
    
}
