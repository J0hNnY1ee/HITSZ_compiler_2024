package cn.edu.hitsz.compiler.lexer;

import cn.edu.hitsz.compiler.NotImplementedException;
import cn.edu.hitsz.compiler.symtab.SymbolTable;
import cn.edu.hitsz.compiler.utils.FileUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;
import java.util.logging.Logger;

/**
 * TODO: 实验一: 实现词法分析
 * <br>
 * 你可能需要参考的框架代码如下:
 *
 * @see Token 词法单元的实现
 * @see TokenKind 词法单元类型的实现
 */
public class LexicalAnalyzer {
    private static final Logger logger = Logger.getLogger(LexicalAnalyzer.class.getName());
    List<Token> tokens = new ArrayList<>();
    StringBuilder contends = new StringBuilder();

    public LexicalAnalyzer(SymbolTable symbolTable) {
    }


    /**
     * 从给予的路径中读取并加载文件内容
     *
     * @param path 路径
     */
    public void loadFile(String path) {
        // TODO: 词法分析前的缓冲区实现
        // 可自由实现各类缓冲区
        // 或直接采用完整读入方法
        List<String> buffer = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                buffer.add(line);
            }
        } catch (IOException e) {
            logger.severe("loadFile Failed: " + e.getMessage());
            throw new RuntimeException(e);
        }

        // 你可以在这里处理缓冲区数据
        // 例如，打印或进行词法分析
        for (String line : buffer) {
            contends.append(line).append('\n');
        }
    }

    /**
     * 执行词法分析, 准备好用于返回的 token 列表 <br>
     * 需要维护实验一所需的符号表条目, 而得在语法分析中才能确定的符号表条目的成员可以先设置为 null
     */
    public void run() {
        LexerState state = LexerState.START;
        tokens = new ArrayList<>();
        StringBuilder tmp = new StringBuilder();
        for (char c : contends.toString().toCharArray()) {
            switch (state) {
                case START -> {
                    if (Character.isWhitespace(c)) {
                        state = LexerState.START;
                    } else if (Character.isLetter(c)) {
                        state = LexerState.LETTER;
                    } else if (Character.isDigit(c)) {
                        state = LexerState.DIGIT;
                    } else if (c == '*') {
                        state = LexerState.MULTI;
                    } else if (c == '=') {
                        state = LexerState.EQ;
//                    } else if (c == '\"') {
//                        state = LexerState.QUOTE;
                    } else if (c == '(') {

                    } else if (c == ')') {

                    } else if (c == ':') {

                    } else if (c == '+') {

                    } else if (c == '-') {

                    } else if (c == '/') {

                    } else if (c == ',') {

                    } else if (c == ';') {

                    }
                }
            }
        }
    }


    /**
     * 获得词法分析的结果, 保证在调用了 run 方法之后调用
     *
     * @return Token 列表
     */
    public Iterable<Token> getTokens() {
        // TODO: 从词法分析过程中获取 Token 列表
        // 词法分析过程可以使用 Stream 或 Iterator 实现按需分析
        // 亦可以直接分析完整个文件
        // 总之实现过程能转化为一列表即可

        return null;
    }

    public void dumpTokens(String path) {
        FileUtils.writeLines(path, StreamSupport.stream(getTokens().spliterator(), false).map(Token::toString).toList());
    }


}

