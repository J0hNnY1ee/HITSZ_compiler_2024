package cn.edu.hitsz.compiler.parser;

import cn.edu.hitsz.compiler.ir.IRImmediate;
import cn.edu.hitsz.compiler.ir.IRVariable;
import cn.edu.hitsz.compiler.ir.Instruction;
import cn.edu.hitsz.compiler.lexer.Token;
import cn.edu.hitsz.compiler.parser.table.Production;
import cn.edu.hitsz.compiler.parser.table.Status;
import cn.edu.hitsz.compiler.symtab.SymbolTable;
import cn.edu.hitsz.compiler.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * IRGenerator 类用于生成中间表示 (IR)。
 * 通过监听语法分析的移进和规约操作，生成相应的 IR 指令。
 */
public class bak_IRGenerator implements ActionObserver {

    // 符号表，用于存储变量及其类型信息
    public SymbolTable symbolTable;

    // symbolStack 用于存储符号分析时的中间结果
    private final Stack<bak_Symbol> symbolStack = new Stack<>();

    // instructionStack 用于存储生成的 IR 指令
    private final Stack<Instruction> instructionStack = new Stack<>();

    /**
     * 当发生移进操作时调用，将当前 Token 压入符号栈中。
     * 如果是数字常量，则将其解析为 IRImmediate，否则解析为 IRVariable。
     *
     * @param currentStatus 当前的状态
     * @param currentToken  当前移进的 Token
     */
    @Override
    public void whenShift(Status currentStatus, Token currentToken) {
        String numberPattern = "^[0-9]+$";
        bak_Symbol currentSymbol = new bak_Symbol(currentToken);

        // 判断当前 Token 是否为整数常量
        if (currentToken.getText().matches(numberPattern)) {
            // 如果是整数常量，解析为 IRImmediate
            currentSymbol.value = IRImmediate.of(Integer.parseInt(currentToken.getText()));
        } else {
            // 否则，解析为变量（IRVariable）
            currentSymbol.value = IRVariable.named(currentToken.getText());
        }
        // 将解析后的 Symbol 压入符号栈
        symbolStack.push(currentSymbol);
    }

    /**
     * 当发生规约操作时调用，根据产生式生成相应的 IR 指令。
     *
     * @param currentStatus 当前的状态
     * @param production    当前使用的产生式
     */
    @Override
    public void whenReduce(Status currentStatus, Production production) {
        bak_Symbol leftSymbol, rightSymbol;
        bak_Symbol nonTerminalSymbol = new bak_Symbol(production.head());
        IRVariable tempVariable;

        switch (production.index()) {
            case 6: // 产生式: S -> id = E;
                // 从栈中弹出 E，'='，id
                rightSymbol = symbolStack.pop();
                symbolStack.pop(); // 弹出 '='
                leftSymbol = symbolStack.pop();
                tempVariable = (IRVariable) leftSymbol.value;

                // 生成 MOV 指令，将右侧表达式的值赋给左侧变量
                instructionStack.push(Instruction.createMov(tempVariable, rightSymbol.value));

                // 规约后，将非终结符 S 压入栈
                nonTerminalSymbol.value = null;
                symbolStack.push(nonTerminalSymbol);
                break;

            case 7: // 产生式: S -> return E;
                // 从栈中弹出 E 和 'return'
                rightSymbol = symbolStack.pop();
                symbolStack.pop(); // 弹出 'return'

                // 生成 RET 指令
                instructionStack.push(Instruction.createRet(rightSymbol.value));

                // 规约后，将非终结符 S 压入栈
                nonTerminalSymbol.value = null;
                symbolStack.push(nonTerminalSymbol);
                break;

            case 8: // 产生式: E -> E + A;
                // 从栈中弹出 A，'+'，E
                rightSymbol = symbolStack.pop();
                symbolStack.pop(); // 弹出 '+'
                leftSymbol = symbolStack.pop();

                // 生成临时变量来存储结果
                tempVariable = IRVariable.temp();

                // 生成 ADD 指令
                instructionStack.push(Instruction.createAdd(tempVariable, leftSymbol.value, rightSymbol.value));

                // 将结果存入栈中
                nonTerminalSymbol.value = tempVariable;
                symbolStack.push(nonTerminalSymbol);
                break;

            case 9: // 产生式: E -> E - A;
                // 从栈中弹出 A，'-'，E
                rightSymbol = symbolStack.pop();
                symbolStack.pop(); // 弹出 '-'
                leftSymbol = symbolStack.pop();

                // 生成临时变量来存储结果
                tempVariable = IRVariable.temp();

                // 生成 SUB 指令
                instructionStack.push(Instruction.createSub(tempVariable, leftSymbol.value, rightSymbol.value));

                // 将结果存入栈中
                nonTerminalSymbol.value = tempVariable;
                symbolStack.push(nonTerminalSymbol);
                break;

            case 10: // 产生式: E -> A;
            case 12: // 产生式: A -> B;
            case 14: // 产生式: B -> id;
                // 直接将 A 或 B 的值传递给 E 或 A
                nonTerminalSymbol.value = symbolStack.pop().value;
                symbolStack.push(nonTerminalSymbol);
                break;

            case 11: // 产生式: A -> A * B;
                // 从栈中弹出 B，'*'，A
                rightSymbol = symbolStack.pop();
                symbolStack.pop(); // 弹出 '*'
                leftSymbol = symbolStack.pop();

                // 生成临时变量来存储结果
                tempVariable = IRVariable.temp();

                // 生成 MUL 指令
                instructionStack.push(Instruction.createMul(tempVariable, leftSymbol.value, rightSymbol.value));

                // 将结果存入栈中
                nonTerminalSymbol.value = tempVariable;
                symbolStack.push(nonTerminalSymbol);
                break;

            case 13: // 产生式: B -> ( E );
                // 从栈中弹出 E 和括号
                symbolStack.pop(); // 弹出 ')'
                rightSymbol = symbolStack.pop();
                symbolStack.pop(); // 弹出 '('

                // 传递 E 的值
                nonTerminalSymbol.value = rightSymbol.value;
                symbolStack.push(nonTerminalSymbol);
                break;

            case 15: // 产生式: B -> IntConst;
                // 直接将 IntConst 的值传递给 B
                rightSymbol = symbolStack.pop();
                nonTerminalSymbol.value = rightSymbol.value;
                symbolStack.push(nonTerminalSymbol);
                break;

            default:
                // 处理其他未定义的产生式，弹出产生式右部的所有符号
                for (int i = 0; i < production.body().size(); i++) {
                    symbolStack.pop();
                }
                // 将非终结符压入栈
                symbolStack.push(new bak_Symbol(production.head()));
        }
    }

    @Override
    public void whenAccept(Status currentStatus) {
        // 当分析器接受输入时，不需要额外操作
    }

    @Override
    public void setSymbolTable(SymbolTable symbolTable) {
        // 设置符号表，用于后续变量的类型检查和处理
        this.symbolTable = symbolTable;
    }

    /**
     * 获取生成的 IR 指令列表。
     *
     * @return IR 指令列表
     */
    public List<Instruction> getIR() {
        return new ArrayList<>(instructionStack);
    }

    /**
     * 将生成的 IR 指令输出到文件。
     *
     * @param path 文件路径
     */
    public void dumpIR(String path) {
        FileUtils.writeLines(path, getIR().stream().map(Instruction::toString).toList());
    }
}
