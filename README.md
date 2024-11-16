HITSZ_Compiler_2024
==========
## Current progress
- [x] lab1
- [x] lab2
- [x] lab3
- [x] lab4

> 这个仓库的代码绝大多数摘自往届学长学姐

# **《编译原理》实验报告**

[TOC]

## 1. 实验目的与方法

> *实验目的为本次实验的实验目的，方法为所使用的语言，软件环境等。*

### 1.1. 词法分析器

- 加深对词法分析程序的功能及实现方法的理解；
- 对类 C 语言的文法描述有更深的认识，理解有穷自动机、编码表和符号表在编译的整个过程中的应用；
- 设计并编程实现一个词法分析程序，对类 C 语言源程序段进行词法分析，加深对高级语言的认识。
- 使用`JAVA`，使用`IntelliJ IDEA 2023.3.5`编写

### 1.2. 语法分析

- 深入了解语法分析程序实现原理及方法。
- 理解 LR(1)分析法是严格的从左向右扫描和自底向上的语法分析方法。
- 使用`JAVA`，使用`IntelliJ IDEA 2023.3.5`编写

### 1.3. 典型语句的语义分析及中间代码生成

- 加深对自底向上语法制导翻译技术的理解，掌握声明语句、赋值语句和算术运
  算语句的翻译方法。
2. 巩固语义分析的基本功能和原理的认识，理解中间代码生产的作用。
- 使用`JAVA`，使用`IntelliJ IDEA 2023.3.5`编写

### 1.4. 目标代码生成

- 加深编译器总体结构的理解与掌握；
2. 掌握常见的 RISC-V 指令的使用方法；
3. 理解并掌握目标代码生成算法和寄存器选择算法。
- 使用`JAVA`，使用`IntelliJ IDEA 2023.3.5`编写

## 2. 实验内容及要求

> *每次实验室的实验内容和要求描述清楚。*

### 2.1. 词法分析器

- 编写一个词法分析程序，读取代码文件，对文件内自定义的类 C 语言程序段进行词法分析。 处理 C 语言源程序， 过滤掉无用符号， 分解出正确的单词， 以二元组形式存输出放在文件中。
- 词法分析程序输入：以文件形式存放自定义的类 C 语言程序段
- 词法分析程序输出：以文件形式存放的 Token串和简单符号表；
- 词法分析程序输入单词类型要求：输入的 C 语言程序段包含常见的关键字，标识符， 常数， 运算符和分界符等。

### 2.2. 语法分析

- 利用 LR(1)分析法，设计语法分析程序，对输入单词符号串进行语法分析；
- 输出推导过程中所用产生式序列并保存在输出文件中；
- 完成要求：实验模板代码中支持变量申明、变量赋值、基本算术运算的文法；实验一的输出作为实验二的输入。

### 2.3. 典型语句的语义分析及中间代码生成

- 采用实验二中的文法，为语法正确的单词串设计翻译方案，完成语法制导翻
  译。
2. 利用该翻译方案，对所给程序段进行分析，输出生成的中间代码序列和更新后
的符号表，并保存在相应文件中。
3. 实现声明语句、简单赋值语句、算术表达式的语义分析与中间代码生成。
4. 使用框架中的模拟器 `IREmulator` 验证生成的中间代码的正确性。

### 2.4. 目标代码生成

- 将实验三生成的中间代码转换为目标代码（汇编指令）；
2) 运行生成的目标代码，验证结果的正确性。

## 3. 实验总体流程与函数功能描述

### 3.1. 词法分析

#### 3.1.1. 编码表

- 词法分析器的输出是单词序列，在5种单词种类中，关键字、运算符、分界 符都是程序设计语言预先定义的，其数量是固定的。而标识符、常数则是由程序 设计人员根据具体的需要按照程序设计语言的规定自行定义的，其数量可以是无 穷多个。编译程序为了处理方便，通常需要按照一定的方式对单词进行分类和编 码，在此基础上，将单词表示成二元组的形式（类别编码，单词值）。 

  | **单词名称** | **类别编码** | **单词值** |
  | ------------ | ------------ | ---------- |
  | `int`        | 1            | –          |
  | `return`     | 2            | –          |
  | `=`          | 3            | –          |
  | `,`          | 4            | –          |
  | `Semicolon`  | 5            | –          |
  | `+`          | 6            | –          |
  | `-`          | 7            | –          |
  | `*`          | 8            | –          |
  | `/`          | 9            | –          |
  | `(`          | 10           | –          |
  | `)`          | 11           | –          |
  | `id`         | 51           | 内部字符串 |
  | `IntConst`   | 52           | 整数值     |
  | ......       | ......       | ......     |
  | 布尔常数     | 80           | 0 或 1     |
  | 字符串常数   | 81           | 内部字符串 |

- 上述编码表对应着data / in / coding_map.csv 文件中的内容。

#### 3.1.2. 正则文法

本实验的正则文法为：

**G = (V, T, P, S)**，其中：

- **V = {S, A, B, C, digit, no_0_digit, char}**
- **T = 任意符号**
- **P** 定义如下：

**约定**：

- **digit** 表示数字：`0, 1, 2, …, 9`
- **no_0_digit** 表示非零数字：`1, 2, …, 9`
- **letter** 表示字母：`A, B, …, Z, a, b, …, z, _`

**产生式规则**

- **标识符**：
  - `S → letter A`
  - `A → letter A | digit A | ε`
- **运算符、分隔符**：
  - `S → B`
  - `B → = | * | + | - | / | ( | ) | ;`
- **整数**：
  - `S → no_0_digit B`
  - `B → digit B | ε`
- **字符串常量**：
  - `S → "C"`
  - `S → 'D'`

#### 3.1.3. 状态转移图

- 具体要实现的词法规则如下:

| 类别        | 正则表达式           |
| ----------- | -------------------- |
| `int`       | `int`                |
| `return`    | `return`             |
| `=`         | `=`                  |
| `,`         | `,`                  |
| `Semicolon` | `;`                  |
| `+`         | `+`                  |
| `-`         | `-`                  |
| `*`         | `*`                  |
| `/`         | `/`                  |
| `(`         | `(`                  |
| `)`         | `)`                  |
| `id`        | `[a-zA-Z_][a-zA-Z]*` |
| `IntConst`  | `[0-9]+`             |

- 根据词法规则设计状态转移图如下图所示：

  ![状态转移图](.\状态转移图.png)

#### 3.1.4. 词法分析程序设计思路和算法描述

- 修改 SymbolTable.java 文件，完善与符号表相关的数据结构及操作方法。

  - 创建一个`HashMap`实现`String`和`SymbolTableEntry`的一一对应。

    ```java
    private final Map<String, SymbolTableEntry> symbolTable = new HashMap<>();
    ```

  - 填充待实现的方法

    - `has`方法，判断符号表中有无条目

      ```java
      public boolean has(String text) {
              return symbolTable.containsKey(text);
          }
      ```

    - `add`方法，在符号表中新增条目

      ```java
      public SymbolTableEntry add(String text) {
              SymbolTableEntry tmp  = new SymbolTableEntry(text);
              symbolTable.put(text, tmp);
              return tmp;
          }
      ```

    - `get`方法，获取符号表中已有的条目

      ```java
      public SymbolTableEntry get(String text) {
              return symbolTable.get(text);
          }
      ```

    - `getAllEntries`方法，返回符号表的所有条目

      ```java
      private Map<String, SymbolTableEntry> getAllEntries() {
              return symbolTable;
          }
      ```

- 修改`LexicalAnalyzer.java`文件，实现词法分析状态机代码的编写。

  - 创建相关的成员

    ```java
        List<Token> tokens = new ArrayList<>();
        StringBuilder contends = new StringBuilder();
        private final SymbolTable symbolTable;
    ```

  - 实现`loadFile`实现从文件中加载需要分析的代码

    ```java
    public void loadFile(String path) {
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
    ```

  - 定义状态名称，方便后续代码的编写

    ```java
        enum LexerState {
            START,
            LETTER,
            DIGIT,
        }
    ```

  - 实现`run`方法，这是词法分析器的核心函数，代码根据状态转移图实现，代码如下，已加上注释：

    ```java
    public void run() {
        // 初始化词法分析器的状态为 START（初始状态）
        LexerState state = LexerState.START;
        // 存储生成的 tokens（词法单元）
        tokens = new ArrayList<>();
        // 用于临时存储当前识别到的字符串
        StringBuilder tmp = new StringBuilder();
        
        // 遍历输入字符串 `contends` 的每一个字符
        for (int i = 0; i < contends.length(); i++) {
            // 获取当前字符
            char c = contends.charAt(i);
    
            // 根据当前的词法分析器状态进行处理
            switch (state) {
                case START -> {
                    // 在 START 状态下，根据不同字符类型跳转到相应的状态
                    if (Character.isWhitespace(c)) {
                        // 如果是空白字符（空格、换行等），保持在 START 状态
                        state = LexerState.START;
                    } else if (Character.isLetter(c)) { // 如果是字母
                        // 清空 `tmp`，准备存储新的标识符
                        tmp.setLength(0);
                        tmp.append(c);
                        // 切换到 LETTER 状态
                        state = LexerState.LETTER;
                    } else if (Character.isDigit(c)) { // 如果是数字
                        // 清空 `tmp`，准备存储新的数字常量
                        tmp.setLength(0);
                        tmp.append(c);
                        // 切换到 DIGIT 状态
                        state = LexerState.DIGIT;
                    } else if (c == ';') { // 如果是分号
                        // 直接将分号作为简单 token 添加到 `tokens` 列表
                        tokens.add(Token.simple("Semicolon"));
                    } else {
                        // 处理其他的特殊字符（如运算符和分隔符）
                        if (c == '*' || c == '=' || c == '(' || c == ')' ||
                            c == '+' || c == '-' || c == '/' || c == ',') {
                            // 将特殊字符作为简单 token 添加到 `tokens` 列表
                            tokens.add(Token.simple(String.valueOf(c)));
                        }
                    }
                }
    
                case LETTER -> {
                    // 在 LETTER 状态下，继续识别标识符或关键字
                    if (Character.isLetter(c) || Character.isDigit(c)) {
                        // 如果当前字符是字母或数字，则继续添加到 `tmp`
                        tmp.append(c);
                    } else {
                        // 遇到非字母或数字字符时，结束当前标识符的识别
                        state = LexerState.START;
                        // 回退 i 的值，以便重新处理当前字符
                        i--;
    
                        // 检查 `tmp` 是否为关键字
                        if (tmp.toString().equals("int")) {
                            tokens.add(Token.simple(tmp.toString())); // 关键字 "int"
                        } else if (tmp.toString().equals("return")) {
                            tokens.add(Token.simple(tmp.toString())); // 关键字 "return"
                        } else {
                            // 否则将其作为标识符处理，并加入符号表
                            tokens.add(Token.normal("id", tmp.toString()));
                            symbolTable.add(tmp.toString());
                        }
                        // 清空 `tmp`，以便下一次使用
                        tmp.setLength(0);
                    }
                }
    
                case DIGIT -> {
                    // 在 DIGIT 状态下，继续识别数字常量
                    if (Character.isDigit(c)) {
                        // 如果当前字符是数字，则继续添加到 `tmp`
                        tmp.append(c);
                    } else {
                        // 遇到非数字字符时，结束当前数字常量的识别
                        state = LexerState.START;
                        // 回退 i 的值，以便重新处理当前字符
                        i--;
                        // 将识别到的数字常量添加为 token
                        tokens.add(Token.normal("IntConst", tmp.toString()));
                        // 清空 `tmp`，以便下一次使用
                        tmp.setLength(0);
                    }
                }
    
                // 处理未预料到的状态，抛出异常
                default -> throw new IllegalArgumentException("Unexpected value: " + state);
            }
        }
    }
    
    ```

  - 实现`getTokens()`方法，用于获得分析的结果

    ```java
    public Iterable<Token> getTokens() {
    
            tokens.add(Token.simple("$"));
            return tokens;
     }
    ```

### 3.2. 语法分析

#### 3.2.1. 拓展文法

- 定义描述程序设计语言语法的文法，并编写拓广文法

  ```
  P -> S_list;
  S_list -> S Semicolon S_list;
  S_list -> S Semicolon;
  S -> D id;
  D -> int;
  S -> id = E;
  S -> return E;
  E -> E + A;
  E -> E - A;
  E -> A;
  A -> A * B;
  A -> B;
  B -> ( E );
  B -> id;
  B -> IntConst;
  ```

#### 3.2.2. LR1分析表

- 在 Windows 环境下使用编译工作台生成LR(1)分析表，如下

  | 状态 |     ACTION      |          |                      |                      |                      |                      |         |         |         |          |                      |                                     | GOTO |        |      |      |      |      |
  | :--: | :-------------: | :------: | :------------------: | :------------------: | :------------------: | :------------------: | :-----: | :-----: | :-----: | :------: | :------------------: | :---------------------------------: | :--: | :----: | :--: | :--: | :--: | :--: |
  |      |       id        |    (     |          )           |          +           |          -           |          *           |    =    |   int   | return  | IntConst |      Semicolon       |                  $                  |  E   | S_list |  S   |  A   |  B   |  D   |
  |  0   |     shift 4     |          |                      |                      |                      |                      |         | shift 5 | shift 6 |          |                      |                                     |      |   1    |  2   |      |      |  3   |
  |  1   |                 |          |                      |                      |                      |                      |         |         |         |          |                      |               accept                |      |        |      |      |      |      |
  |  2   |                 |          |                      |                      |                      |                      |         |         |         |          |       shift 7        |                                     |      |        |      |      |      |      |
  |  3   |     shift 8     |          |                      |                      |                      |                      |         |         |         |          |                      |                                     |      |        |      |      |      |      |
  |  4   |                 |          |                      |                      |                      |                      | shift 9 |         |         |          |                      |                                     |      |        |      |      |      |      |
  |  5   | reduce D -> int |          |                      |                      |                      |                      |         |         |         |          |                      |                                     |      |        |      |      |      |      |
  |  6   |    shift 13     | shift 14 |                      |                      |                      |                      |         |         |         | shift 15 |                      |                                     |  10  |        |      |  11  |  12  |      |
  |  7   |     shift 4     |          |                      |                      |                      |                      |         | shift 5 | shift 6 |          |                      |    reduce S_list -> S Semicolon     |      |   16   |  2   |      |      |  3   |
  |  8   |                 |          |                      |                      |                      |                      |         |         |         |          |   reduce S -> D id   |                                     |      |        |      |      |      |      |
  |  9   |    shift 13     | shift 14 |                      |                      |                      |                      |         |         |         | shift 15 |                      |                                     |  17  |        |      |  11  |  12  |      |
  |  10  |                 |          |                      |       shift 18       |       shift 19       |                      |         |         |         |          | reduce S -> return E |                                     |      |        |      |      |      |      |
  |  11  |                 |          |                      |    reduce E -> A     |    reduce E -> A     |       shift 20       |         |         |         |          |    reduce E -> A     |                                     |      |        |      |      |      |      |
  |  12  |                 |          |                      |    reduce A -> B     |    reduce A -> B     |    reduce A -> B     |         |         |         |          |    reduce A -> B     |                                     |      |        |      |      |      |      |
  |  13  |                 |          |                      |    reduce B -> id    |    reduce B -> id    |    reduce B -> id    |         |         |         |          |    reduce B -> id    |                                     |      |        |      |      |      |      |
  |  14  |    shift 24     | shift 25 |                      |                      |                      |                      |         |         |         | shift 26 |                      |                                     |  21  |        |      |  22  |  23  |      |
  |  15  |                 |          |                      | reduce B -> IntConst | reduce B -> IntConst | reduce B -> IntConst |         |         |         |          | reduce B -> IntConst |                                     |      |        |      |      |      |      |
  |  16  |                 |          |                      |                      |                      |                      |         |         |         |          |                      | reduce S_list -> S Semicolon S_list |      |        |      |      |      |      |
  |  17  |                 |          |                      |       shift 18       |       shift 19       |                      |         |         |         |          |  reduce S -> id = E  |                                     |      |        |      |      |      |      |
  |  18  |    shift 13     | shift 14 |                      |                      |                      |                      |         |         |         | shift 15 |                      |                                     |      |        |      |  27  |  12  |      |
  |  19  |    shift 13     | shift 14 |                      |                      |                      |                      |         |         |         | shift 15 |                      |                                     |      |        |      |  28  |  12  |      |
  |  20  |    shift 13     | shift 14 |                      |                      |                      |                      |         |         |         | shift 15 |                      |                                     |      |        |      |      |  29  |      |
  |  21  |                 |          |       shift 30       |       shift 31       |       shift 32       |                      |         |         |         |          |                      |                                     |      |        |      |      |      |      |
  |  22  |                 |          |    reduce E -> A     |    reduce E -> A     |    reduce E -> A     |       shift 33       |         |         |         |          |                      |                                     |      |        |      |      |      |      |
  |  23  |                 |          |    reduce A -> B     |    reduce A -> B     |    reduce A -> B     |    reduce A -> B     |         |         |         |          |                      |                                     |      |        |      |      |      |      |
  |  24  |                 |          |    reduce B -> id    |    reduce B -> id    |    reduce B -> id    |    reduce B -> id    |         |         |         |          |                      |                                     |      |        |      |      |      |      |
  |  25  |    shift 24     | shift 25 |                      |                      |                      |                      |         |         |         | shift 26 |                      |                                     |  34  |        |      |  22  |  23  |      |
  |  26  |                 |          | reduce B -> IntConst | reduce B -> IntConst | reduce B -> IntConst | reduce B -> IntConst |         |         |         |          |                      |                                     |      |        |      |      |      |      |
  |  27  |                 |          |                      |  reduce E -> E + A   |  reduce E -> E + A   |       shift 20       |         |         |         |          |  reduce E -> E + A   |                                     |      |        |      |      |      |      |
  |  28  |                 |          |                      |  reduce E -> E - A   |  reduce E -> E - A   |       shift 20       |         |         |         |          |  reduce E -> E - A   |                                     |      |        |      |      |      |      |
  |  29  |                 |          |                      |  reduce A -> A * B   |  reduce A -> A * B   |  reduce A -> A * B   |         |         |         |          |  reduce A -> A * B   |                                     |      |        |      |      |      |      |
  |  30  |                 |          |                      |  reduce B -> ( E )   |  reduce B -> ( E )   |  reduce B -> ( E )   |         |         |         |          |  reduce B -> ( E )   |                                     |      |        |      |      |      |      |
  |  31  |    shift 24     | shift 25 |                      |                      |                      |                      |         |         |         | shift 26 |                      |                                     |      |        |      |  35  |  23  |      |
  |  32  |    shift 24     | shift 25 |                      |                      |                      |                      |         |         |         | shift 26 |                      |                                     |      |        |      |  36  |  23  |      |
  |  33  |    shift 24     | shift 25 |                      |                      |                      |                      |         |         |         | shift 26 |                      |                                     |      |        |      |      |  37  |      |
  |  34  |                 |          |       shift 38       |       shift 31       |       shift 32       |                      |         |         |         |          |                      |                                     |      |        |      |      |      |      |
  |  35  |                 |          |  reduce E -> E + A   |  reduce E -> E + A   |  reduce E -> E + A   |       shift 33       |         |         |         |          |                      |                                     |      |        |      |      |      |      |
  |  36  |                 |          |  reduce E -> E - A   |  reduce E -> E - A   |  reduce E -> E - A   |       shift 33       |         |         |         |          |                      |                                     |      |        |      |      |      |      |
  |  37  |                 |          |  reduce A -> A * B   |  reduce A -> A * B   |  reduce A -> A * B   |  reduce A -> A * B   |         |         |         |          |                      |                                     |      |        |      |      |      |      |
  |  38  |                 |          |  reduce B -> ( E )   |  reduce B -> ( E )   |  reduce B -> ( E )   |  reduce B -> ( E )   |         |         |         |          |                      |                                     |      |        |      |      |      |      |

#### 3.2.3. 状态栈和符号栈的数据结构和设计思路

-  状态栈和符号栈的数据结构是栈，但为了方便实现，实验中采用线性表来模拟栈

  ```java
  private List<Status> statusStack = new ArrayList<>(); // 状态栈
  private List<Object> symbolStack = new ArrayList<>(); // 符号栈 (终结符/非终结符)
  ```

#### 3.2.4. LR驱动程序设计思路和算法描述

- 实现Tokens的加载，实现`loadTokens()`方法

  ```java
  public void loadTokens(Iterable<Token> tokens) {
      for (Token token : tokens) {
          tokenList.add(token);
      }
  }
  ```

- 实现LRTable的加载，实现`loadLRTable()`方法

  ```java
  public void loadLRTable(LRTable table) {
      // 将传入的 LRTable 存储起来
      this.lrTable = table;
      // 初始化状态为 LR 表中的初始状态
      this.currentStatus = lrTable.getInit();
  }
  ```

- 实现语法分析的核心函数`run()`，主要思想是遍历`tokens`，判断当前的状态和动作，更新状态栈和符号栈，代码如下，已经加上详细的注释

  ```java
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
                  if (!statusStack.isEmpty()) {
                      statusStack.remove(statusStack.size() - 1); // 删除最后一个元素
                  }
                  if (!symbolStack.isEmpty()) {
                      symbolStack.remove(symbolStack.size() - 1); // 删除最后一个元素
                  }
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
  ```

  

### 3.3. 语义分析和中间代码生成

#### 3.3.1. 翻译方案

- 采用S-属性定义的自底向上翻译方案。具体方案如下：
  - P → S_list {P.val = S_list.val}  
  - S_list  → S Semicolon S_list {S_list.val = S_list1.val}  
  - S_list  → S Semicolon; {S_list.val = S.val }  
  - S → D id {p=lookup(id.name); if p != nil then enter(id.name, D.type) else error}  
  - S → return E; {S.value = E.value}  
  - D → int {D.type = int;}  
  - S → id = E {gencode(id.val = E.val);}  
  - E → A {E.val = val;}  
  - A → B {A.val = B.val;}  
  - B → IntConst {B.val = IntConst.lexval;}  
  - E → E1 + A {E.val = newtemp(); gencode(E.val = E1.val + A.val);}  
  - E → E1 – A {E.val = newtemp(); gencode(E.val = E1.val - A.val);}  
  - A → A1 * B{A.val = newtemp();gencode(A.val = A1.val * B.val);}  
  - B → ( E ){B.val = E.val;}  
  - B → id { p = lookup(id.name); if p != nil then B.val = id.val else error}$$

#### 3.3.2. 语义分析和中间代码生成的数据结构

**同时使用线性表模拟栈和直接使用栈两个数据结构**

- `SemanticAnalyzer`与实验2相同使用线性表模拟栈

  ```java
      // 符号表，用于存储变量及其类型信息
      private SymbolTable symbolTable = null;
  
      // tokenStack 用于存储分析过程中遇到的 Token
      private final ArrayList<Token> tokenStack = new ArrayList<>();
  
      // dataStack 用于存储分析过程中推断出的类型信息
      private final ArrayList<SourceCodeType> dataStack = new ArrayList<>();
  ```

- `IRGenerator`使用java的栈

  ```java
      // 符号表，用于存储变量及其类型信息
      public SymbolTable symbolTable;
  
      // IRValueStack 用于存储符号分析时的中间结果
      // private final Stack<Symbol> IRValueStack = new Stack<>();
      private final Stack<IRValue> IRValueStack = new Stack<>();
  
      // instructionStack 用于存储生成的 IR 指令
      private final Stack<Instruction> instructionStack = new Stack<>();
  ```

#### 3.3.3. 语法分析程序设计思路和算法描述

**语义分析**

- 实现`setSymboltable()`方法，用于`symbolTable`的传入

  ```java
  public void setSymbolTable(SymbolTable table) {
      // 将传入的符号表存储在成员变量中，以便后续使用
      this.symbolTable = table;
  }
  ```

- 实现`whenShift()`方法，已加上注释

  ```java
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
  ```

- 实现`whenReduce()`方法，已加上注释

  ```java
  public void whenReduce(Status currentStatus, Production production) {
      switch (production.index()) {
          // 产生式 4: S -> D id
          case 4 -> {
              /*对于第4条S→D id产生式，分别取出id和D两个符号，并将id的type更新为
  			D的type，更新符号表中相应变量的type信息，最后压入空记录占位（D id被归
  			约为S，S不需要携带信息）。*/
              // 设置符号表中变量的类型为 int
              // 获取最后一个 token（即 id），并将其类型设为 int
              String identifier = tokenStack.get(tokenStack.size() - 1).getText();
              SourceCodeType type = dataStack.get(dataStack.size() - 2);
              symbolTable.get(identifier).setType(type);
          }
          // 产生式 5: D -> int
          case 5 -> {
              /*对于第5条D→int产生式，把int这个token的type类型赋值给D的type，并将
  			非终结符D压入语义分析栈。*/
              // 移除栈顶元素，因为 "int" 已经处理过了
              tokenStack.remove(tokenStack.size() - 1);
              dataStack.remove(dataStack.size() - 1);
              // 将占位符 null 压入 tokenStack，同时将类型设为 int
              tokenStack.add(null);
              dataStack.add(SourceCodeType.Int);
          }
          // 处理其他产生式
          /*对于其他产生式，由于不涉及到更新符号表，则仅将产生式右部弹出、左部压入
  		语义分析栈即可。在Symbol类中type字段已初始化为null，故无需额外处理。 
  		*/
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
  ```

  

- 实现`whenAccept()`方法，当Accept的时候不需要进行任何操作，因此置空或者打印相关信息

  ```java
      public void whenAccept(Status currentStatus) {
          // 打印接受状态的提示信息
          System.out.println("Accept " + currentStatus.toString());
      }
  ```

**中间代码生成：**

- 实现`setSymboltable()`方法，用于`symbolTable`的传入

  ```java
      public void setSymbolTable(SymbolTable symbolTable) {
          // 设置符号表，用于后续变量的类型检查和处理
          this.symbolTable = symbolTable;
      }
  ```

与语义分析一致，需要实现三个观察者方法。

- 实现`whenShift()`方法，已加上注释

  ```java
  public void whenShift(Status currentStatus, Token currentToken) {
      String numberPattern = "^[0-9]+$";
      IRValue currentValue = null;
      // 判断当前 Token 是否为整数常量
      if (currentToken.getText().matches(numberPattern)) {
          // 如果是整数常量，解析为 IRImmediate
          currentValue = IRImmediate.of(Integer.parseInt(currentToken.getText()));
      } else {
          // 否则，解析为变量（IRVariable）
          currentValue = IRVariable.named(currentToken.getText());
      }
      // 将解析后的 Symbol 压入符号栈
      IRValueStack.push(currentValue);
  }
  ```

- 实现`whenReduce()`方法，已加上注释

  ```java
  public void whenReduce(Status currentStatus, Production production) {
      IRValue leftValue, rightValue;
      IRValue nonTerminalSymbol = null;
      IRVariable tempVariable;
      switch (production.index()) {
          case 6: // 产生式: S -> id = E;
              // 从栈中弹出 E，'='，id
              rightValue = IRValueStack.pop();
              IRValueStack.pop(); // 弹出 '='
              leftValue = IRValueStack.pop();
              tempVariable = (IRVariable) leftValue;
              // 生成 MOV 指令，将右侧表达式的值赋给左侧变量
              instructionStack.push(Instruction.createMov(tempVariable, rightValue));
              // 规约后，将非终结符 S 压入栈
              nonTerminalSymbol = null;
              IRValueStack.push(nonTerminalSymbol);
              break;
          case 7: // 产生式: S -> return E;
              // 从栈中弹出 E 和 'return'
              rightValue = IRValueStack.pop();
              IRValueStack.pop(); // 弹出 'return'
              // 生成 RET 指令
              instructionStack.push(Instruction.createRet(rightValue));
              // 规约后，将非终结符 S 压入栈
              nonTerminalSymbol = null;
              IRValueStack.push(nonTerminalSymbol);
              break;
          case 8: // 产生式: E -> E + A;
              // 从栈中弹出 A，'+'，E
              rightValue = IRValueStack.pop();
              IRValueStack.pop(); // 弹出 '+'
              leftValue = IRValueStack.pop();
              // 生成临时变量来存储结果
              tempVariable = IRVariable.temp();
              // 生成 ADD 指令
              instructionStack.push(Instruction.createAdd(tempVariable, leftValue, rightValue));
              // 将结果存入栈中
              nonTerminalSymbol = tempVariable;
              IRValueStack.push(nonTerminalSymbol);
              break;
          case 9: // 产生式: E -> E - A;
              // 从栈中弹出 A，'-'，E
              rightValue = IRValueStack.pop();
              IRValueStack.pop(); // 弹出 '-'
              leftValue = IRValueStack.pop();
              // 生成临时变量来存储结果
              tempVariable = IRVariable.temp();
              // 生成 SUB 指令
              instructionStack.push(Instruction.createSub(tempVariable, leftValue, rightValue));
              // 将结果存入栈中
              nonTerminalSymbol = tempVariable;
              IRValueStack.push(nonTerminalSymbol);
              break;
          case 10: // 产生式: E -> A;
          case 12: // 产生式: A -> B;
          case 14: // 产生式: B -> id;
              // 直接将 A 或 B 的值传递给 E 或 A
              nonTerminalSymbol = IRValueStack.pop();
              IRValueStack.push(nonTerminalSymbol);
              break;
          case 11: // 产生式: A -> A * B;
              // 从栈中弹出 B，'*'，A
              rightValue = IRValueStack.pop();
              IRValueStack.pop(); // 弹出 '*'
              leftValue = IRValueStack.pop();
              // 生成临时变量来存储结果
              tempVariable = IRVariable.temp();
              // 生成 MUL 指令
              instructionStack.push(Instruction.createMul(tempVariable, leftValue, rightValue));
              // 将结果存入栈中
              nonTerminalSymbol = tempVariable;
              IRValueStack.push(nonTerminalSymbol);
              break;
          case 13: // 产生式: B -> ( E );
              // 从栈中弹出 E 和括号
              IRValueStack.pop(); // 弹出 ')'
              rightValue = IRValueStack.pop();
              IRValueStack.pop(); // 弹出 '('
              // 传递 E 的值
              nonTerminalSymbol = rightValue;
              IRValueStack.push(nonTerminalSymbol);
              break;
          case 15: // 产生式: B -> IntConst;
              // 直接将 IntConst 的值传递给 B
              rightValue = IRValueStack.pop();
              nonTerminalSymbol = rightValue;
              IRValueStack.push(nonTerminalSymbol);
              break;
          default:
              // 处理其他未定义的产生式，弹出产生式右部的所有符号
              for (int i = 0; i < production.body().size(); i++) {
                  IRValueStack.pop();
              }
              // 将非终结符压入栈
              IRValueStack.push(null);
      }
  }
  ```

- 实现`whenAccept()`方法，当Accept的时候不需要进行任何操作，因此置空或者打印相关信息

  ```java
  public void whenAccept(Status currentStatus) {
      // 当分析器接受输入时，不需要额外操作
  }
  ```

### 3.4. 目标代码生成

#### 3.4.1 设计思路和算法描述

- 实现一个双射，方便后续实现“给定寄存器号查找里面存储的变量”和“给定变量查找该变量存在的寄存器号”双向查找

  ```java
  public class BMap<K, V> {
      private final Map<K, V> KVmap = new HashMap<>();
      private final Map<V, K> VKmap = new HashMap<>();
  
      public void removeByKey(K key) {
          VKmap.remove(KVmap.remove(key));
      }
  
      public void removeByValue(V value) {
          KVmap.remove(VKmap.remove(value));
      }
  
      public boolean containsKey(K key) {
          return KVmap.containsKey(key);
      }
  
      public boolean containsValue(V value) {
          return VKmap.containsKey(value);
      }
  
      public void replace(K key, V value) {
          // 对于双射关系, 将会删除交叉项
          removeByKey(key);
          removeByValue(value);
          KVmap.put(key, value);
          VKmap.put(value, key);
      }
  
      public V getByKey(K key) {
          return KVmap.get(key);
      }
  
      public K getByValue(V value) {
          return VKmap.get(value);
      }
  }
  
  ```

- 定义数据结构，具体解释在注释

  ```java
      List<Instruction> instList_before = new LinkedList<>();// 预处理前的指令列表
      List<Instruction> instList = new LinkedList<>();// 预处理后的指令列表
      BMap<IRValue, Register> varMap = new BMap<>();// 寄存器与变量的双射
      // 汇编代码，初始化第一行为 ".text"
      List<String> sentences = new ArrayList<>(List.of(".text"));
  
      // 可以分配的寄存器号
      enum Register {
          t0, t1, t2, t3, t4, t5, t6
      }
  ```

- 实现`addVariable()`方法，用于给变量分配寄存器

  ```java
  public void addVariable(IRValue operand, int index) {
      // 如果是立即数或者已经存入变量，那么不需要分配寄存器
      if (operand.isImmediate() || varMap.containsKey(operand)) {
          return;
      }
      for (Register reg : Register.values()) {
          if (!varMap.containsValue(reg)) {
              varMap.replace(operand, reg); // 有空闲的寄存器
              return;
          }
      }
      // 若无空闲的寄存器
      Set<Register> unusedRegs = Arrays.stream(Register.values()).collect(Collect
      for (int i = index; i < instList.size(); i++) {
          Instruction inst = instList.get(i);
          for (IRValue irValue : inst.getOperands()) {
              Register reg = varMap.getByKey(irValue);
              unusedRegs.remove(reg); // 若有使用则删除
          }
      }
      if (!unusedRegs.isEmpty()) {
          varMap.replace(operand, unusedRegs.iterator().next());
      }
  }
  ```

- 实现`preprocessing()`方法，实现对指令的预处理

  - 对于 `BinaryOp`(两个操作数的指令)：
    - 将操作两个立即数的 `BinaryOp` 直接进行求值得到结果，然后替换成 MOV
      指令；
    - 将操作一个立即数的指令 (除了乘法和左立即数减法) 进行调整，使之满足
      `a := b op imm` 的格式
    - 将操作一个立即数的乘法和左立即数减法调整，前插一条 `MOV a， imm`，用
      a 替换原立即数，将指令调整为无立即数指令。
  - 对于 `UnaryOp`(一个操作数的指令)：
    - 当遇到 Ret 指令后直接舍弃后续指令。

  ```java
  public void preprocessing() {
      for (Instruction inst : instList_before) {
          switch (inst.getKind()) {
              case ADD, SUB, MUL -> {
                  boolean lhsIsImm = inst.getLHS().isImmediate();
                  boolean rhsIsImm = inst.getRHS().isImmediate();
                  if (lhsIsImm) {
                      if (rhsIsImm) {
                          InstructionKind kind = inst.getKind();
                          int l = ((IRImmediate) inst.getLHS()).getValue();
                          int r = ((IRImmediate) inst.getRHS()).getValue();
                          int res = switch (kind) {
                              case ADD -> l + r;
                              case SUB -> l - r;
                              case MUL -> l * r;
                              default -> 0;
                          };
                          IRImmediate tmp = IRImmediate.of(res);
                          instList.add(Instruction.createMov(inst.getResult(), tmp));
                      } else {
                          switch (inst.getKind()) {
                              case ADD ->
                                  instList.add(Instruction.createAdd(inst.getResult(), inst.getRHS(), inst.getLHS()));
                              case SUB -> {
                                  IRVariable tmp = IRVariable.temp();
                                  instList.add(Instruction.createMov(tmp, inst.getLHS()));
                                  instList.add(Instruction.createSub(inst.getResult(), tmp, inst.getRHS()));
                              }
                              case MUL -> {
                                  IRVariable tmp = IRVariable.temp();
                                  instList.add(Instruction.createMov(tmp, inst.getLHS()));
                                  instList.add(Instruction.createMul(inst.getResult(), tmp, inst.getLHS()));
                              }
                              default -> instList.add(inst);
                          }
                      }
                  }
                  else {
                      if (rhsIsImm) {
                          if (inst.getKind() == InstructionKind.MUL) {
                              IRVariable tmp = IRVariable.temp();
                              instList.add(Instruction.createMov(tmp, inst.getLHS()));
                              instList.add(Instruction.createMul(inst.getResult(), tmp, inst.getLHS()));
                          } else {
                              instList.add(inst);
                          }
                      } else {
                          instList.add(inst);
                      }
                  }
              }
              case RET -> {
                  instList.add(inst);
                  return;
              }
              default -> instList.add(inst);
          }
      }
  }
  ```

- 实现`run()`方法，用于最终代码的生成。

- 遍历`instructions `列表，对于其中预处理后的中间代码指令，判断指令的操作类型并作不 同处理。

  - 对于`ADD`指令：如果两个操作数都是变量，则根据寄存器分配方案为两个变量分别分 配寄存器，并生成相应的add指令；如果其中一个操作数是立即数，另一个操作数是变 量，则根据寄存器分配方案为其中的变量分配寄存器，并生成相应的`addi` 指令
  - 对于`SUB`指令：如果两个操作数都是变量，则根据寄存器分配方案为两个变量分别分 配寄存器，并生成相应的sub指令；如果其中一个操作数是立即数，另一个操作数是变 量，则根据寄存器分配方案为其中的变量分配寄存器，并生成相应的`subi `指令
  - 对于`MUL`指令：直接根据寄存器分配方案为两个变量分别分配寄存器，并生成相应的` mul `指令。
  - 对于`MOV`指令：由于只有两个操作数（源操作数和目的操作数），因此需要考虑其右 操作数form 是否为立即数。如果右操作数为变量，则生成相应的mv指令（即`mv rd,  rs1`）；如果右操作数为立即数，则生成相应的`li`指令（即`li rd, rs1`）。
  - 对于`RET`指令，其目的操作数固定存放在返回值寄存器 a0中，因此通过`getReturnValue() `方法获取返回值后直接生成相应的`mv`语句即可（即`mv a0, rs1`）。  

- 最后，将汇编指令对应的中间代码作为注释输出，并将生成的汇编指令添加至 `asmInstructions `列表中。

  ```java
  public void run() {
      // 执行寄存器分配与代码生成
      for(int i=0;i<instList.size();i++) {
          Instruction inst = instList.get(i);
          String str = null;
          switch (inst.getKind()) {
              // 对ADD SUB MUL，为三个变量分配寄存器（若需要），并根据是否含立即数生成对应汇编代码
              case ADD -> {
                  IRValue res = inst.getResult();
                  IRValue lhs = inst.getLHS();
                  IRValue rhs = inst.getRHS();
                  addVariable(res,i);
                  addVariable(lhs,i);
                  addVariable(rhs,i);
                  Register resReg = varMap.getByKey(res);
                  Register lhsReg = varMap.getByKey(lhs);
                  Register rhsReg = varMap.getByKey(rhs);
                  if(rhs.isImmediate()){
                       str = "\taddi %s,%s,%s\t".formatted(resReg.toString(),lhsReg.toString(),rhs.toString());
                  } else {
                      str = "\tadd %s,%s,%s\t".formatted(resReg.toString(),lhsReg.toString(),rhsReg.toString());
                  }
              }
              case SUB -> {
                  IRValue res = inst.getResult();
                  IRValue lhs = inst.getLHS();
                  IRValue rhs = inst.getRHS();
                  addVariable(res,i);
                  addVariable(lhs,i);
                  addVariable(rhs,i);
                  Register resReg = varMap.getByKey(res);
                  Register lhsReg = varMap.getByKey(lhs);
                  Register rhsReg = varMap.getByKey(rhs);
                  if(rhs.isImmediate()){
                      str = "\tsubi %s,%s,%s\t".formatted(resReg.toString(),lhsReg.toString(),rhs.toString());
                  } else {
                      str = "\tsub %s,%s,%s\t".formatted(resReg.toString(),lhsReg.toString(),rhsReg.toString());
                  }
              }
              case MUL -> {
                  IRValue res = inst.getResult();
                  IRValue lhs = inst.getLHS();
                  IRValue rhs = inst.getRHS();
                  addVariable(res,i);
                  addVariable(lhs,i);
                  addVariable(rhs,i);
                  Register resReg = varMap.getByKey(res);
                  Register lhsReg = varMap.getByKey(lhs);
                  Register rhsReg = varMap.getByKey(rhs);
                  str = "\tmul %s,%s,%s\t".formatted(resReg.toString(),lhsReg.toString(),rhsReg.toString());
              }
              // 对MOV，为两个变量分配寄存器（若需要）
              // 若第二个操作数为立即数，生成汇编代码为 li（加载立即数），否则为 mv
              case MOV -> {
                  IRValue res = inst.getResult();
                  IRValue from = inst.getFrom();
                  addVariable(res,i);
                  addVariable(from,i);
                  Register resReg = varMap.getByKey(res);
                  Register fromReg = varMap.getByKey(from);
                  if (from.isImmediate()) {
                      str = "\tli %s,%s\t".formatted(resReg.toString(),from.toString());
                  } else {
                      str = "\tmv %s,%s\t".formatted(resReg.toString(),fromReg.toString());
                  }
              }
              // 对RET，生成汇编代码为 mv a0 __
              case RET -> str = "\tmv a0,\t" + varMap.getByKey(inst.getReturnValue()).toString();
          }
          // 添加注释，即对应中间代码
          str = str + "\t# %s".formatted(inst.toString());
          sentences.add(str);
          // 读取到RET指令后，直接舍弃后续指令
          if (inst.getKind() == InstructionKind.RET) {
              break;
          }
      }
      System.out.println("Assembly Generate over");
  }
  /**
  ```

  



## 4. 实验结果与分析

> *对实验的输入输出结果进行展示与分析。注意：要求给出编译器各阶段（词法分析、语法分析、中间代码生成、目标代码生成）的输入输出并进行分析说明。*

### 4.1 词法分析

**输入**：

- 码点文件`coding_map.csv`:

  ```
  1 int
  2 return
  3 =
  4 ,
  5 Semicolon
  6 +
  7 -
  8 *
  9 /
  10 (
  11 )
  51 id
  52 IntConst
  ```

- 输入的代码`input_code.txt`:

  ```c
  int result;
  int a;
  int b;
  int c;
  a = 8;
  b = 5;
  c = 3 - a;
  result = a * b - ( 3 + b ) * ( c - a );
  return result;
  ```

**输出：**

- 符号表`old_symbol_table.txt`符合预期，如下：

  ```
  (a, null)
  (b, null)
  (c, null)
  (result, null)
  ```

- 词法单元列表`token.txt`符合预期，如下：

  ```
  (int,)
  (id,result)
  (Semicolon,)
  (int,)
  (id,a)
  (Semicolon,)
  (int,)
  (id,b)
  (Semicolon,)
  (int,)
  (id,c)
  (Semicolon,)
  (id,a)
  (=,)
  (IntConst,8)
  (Semicolon,)
  (id,b)
  (=,)
  (IntConst,5)
  (Semicolon,)
  (id,c)
  (=,)
  (IntConst,3)
  (-,)
  (id,a)
  (Semicolon,)
  (id,result)
  (=,)
  (id,a)
  (*,)
  (id,b)
  (-,)
  ((,)
  (IntConst,3)
  (+,)
  (id,b)
  (),)
  (*,)
  ((,)
  (id,c)
  (-,)
  (id,a)
  (),)
  (Semicolon,)
  (return,)
  (id,result)
  (Semicolon,)
  ($,)
  ```

### 语法分析

**输入：**

- 码点文件`coding_map.csv`

- 输入的代码`input_code.txt`

- 语法文件`grammar.txt`：

  ```
  P -> S_list;
  S_list -> S Semicolon S_list;
  S_list -> S Semicolon;
  S -> D id;
  D -> int;
  S -> id = E;
  S -> return E;
  E -> E + A;
  E -> E - A;
  E -> A;
  A -> A * B;
  A -> B;
  B -> ( E );
  B -> id;
  B -> IntConst;
  ```

- IR分析表`LR1_table.csv`

**输出：**



- 规约过程的产生式列表`parser_list.txt`符合预期，如下：

  ```
  D -> int
  S -> D id
  D -> int
  S -> D id
  D -> int
  S -> D id
  D -> int
  S -> D id
  B -> IntConst
  A -> B
  E -> A
  S -> id = E
  B -> IntConst
  A -> B
  E -> A
  S -> id = E
  B -> IntConst
  A -> B
  E -> A
  B -> id
  A -> B
  E -> E - A
  S -> id = E
  B -> id
  A -> B
  B -> id
  A -> A * B
  E -> A
  B -> IntConst
  A -> B
  E -> A
  B -> id
  A -> B
  E -> E + A
  B -> ( E )
  A -> B
  B -> id
  A -> B
  E -> A
  B -> id
  A -> B
  E -> E - A
  B -> ( E )
  A -> A * B
  E -> E - A
  S -> id = E
  B -> id
  A -> B
  E -> A
  S -> return E
  S_list -> S Semicolon
  S_list -> S Semicolon S_list
  S_list -> S Semicolon S_list
  S_list -> S Semicolon S_list
  S_list -> S Semicolon S_list
  S_list -> S Semicolon S_list
  S_list -> S Semicolon S_list
  S_list -> S Semicolon S_list
  S_list -> S Semicolon S_list
  P -> S_list
  ```

### 语义分析及中间代码生成

**输入：**

- 码点文件`coding_map.csv`
- 输入的代码`input_code.txt`
- 语法文件`grammar.txt`
- IR分析表`LR1_table.csv`

**输出：**

- 语义分析后的符号表`new_symbol_table.txt`符合预期，如下：

  ```
  (a, Int)
  (b, Int)
  (c, Int)
  (result, Int)
  ```

- 中间表示的模拟执行的结果`ir_emulate_result.txt`符合预期，如下：

  ```
  144
  ```

- 中间代码`intermediate_code.txt`符合预期，如下：

  ```
  (MOV, a, 8)
  (MOV, b, 5)
  (SUB, $0, 3, a)
  (MOV, c, $0)
  (MUL, $1, a, b)
  (ADD, $2, 3, b)
  (SUB, $3, c, a)
  (MUL, $4, $2, $3)
  (SUB, $5, $1, $4)
  (MOV, result, $5)
  (RET, , result)
  ```

### 目标代码生成

**输入：**

- 码点文件`coding_map.csv`

- 输入的代码`input_code.txt`

- 语法文件`grammar.txt`

- IR分析表`LR1_table.csv`

- 用于测试寄存器分配的样例`reg-alloc.txt`，如下：

  ```c
  int f0;
  int f1;
  int f2;
  int f3;
  int f4;
  int f5;
  int f6;
  int f7;
  int f8;
  int f9;
  int f10;
  int f11;
  int f12;
  int f13;
  int f14;
  int f15;
  int f16;
  int f17;
  int f18;
  int f19;
  
  int s0;
  int s1;
  int s2;
  int s3;
  int s4;
  int s5;
  int s6;
  int s7;
  int s8;
  int s9;
  int s10;
  int s11;
  int s12;
  int s13;
  int s14;
  int s15;
  int s16;
  int s17;
  int s18;
  int s19;
  
  f0 = 0;
  f1 = 1;
  f2 = f1 + f0;
  f3 = f2 + f1;
  f4 = f3 + f2;
  f5 = f4 + f3;
  f6 = f5 + f4;
  f7 = f6 + f5;
  f8 = f7 + f6;
  f9 = f8 + f7;
  f10 = f9 + f8;
  f11 = f10 + f9;
  f12 = f11 + f10;
  f13 = f12 + f11;
  f14 = f13 + f12;
  f15 = f14 + f13;
  f16 = f15 + f14;
  f17 = f16 + f15;
  f18 = f17 + f16;
  f19 = f18 + f17;
  
  s0 = f0;
  s1 = s0 + f1;
  s2 = s1 + f2;
  s3 = s2 + f3;
  s4 = s3 + f4;
  s5 = s4 + f5;
  s6 = s5 + f6;
  s7 = s6 + f7;
  s8 = s7 + f8;
  s9 = s8 + f9;
  s10 = s9 + f10;
  s11 = s10 + f11;
  s12 = s11 + f12;
  s13 = s12 + f13;
  s14 = s13 + f14;
  s15 = s14 + f15;
  s16 = s15 + f16;
  s17 = s16 + f17;
  s18 = s17 + f18;
  s19 = s18 + f19;
  
  return s19;
  ```

**输出：**

- 终端打印

  ```
  Assembly Generate over
  ```

-  汇编代码`assembly_language.asm`，放入RARS中执行可以得到`a0`为`0x90`符合预期，如下：

  ```
  .text
  	li t0,8		# (MOV, a, 8)
  	li t1,5		# (MOV, b, 5)
  	li t2,3		# (MOV, $6, 3)
  	sub t3,t2,t0		# (SUB, $0, $6, a)
  	mv t4,t3		# (MOV, c, $0)
  	mul t5,t0,t1		# (MUL, $1, a, b)
  	addi t6,t1,3		# (ADD, $2, b, 3)
  	sub t3,t4,t0		# (SUB, $3, c, a)
  	mul t0,t6,t3		# (MUL, $4, $2, $3)
  	sub t3,t5,t0		# (SUB, $5, $1, $4)
  	mv t5,t3		# (MOV, result, $5)
  	mv a0,	t5	# (RET, , result)
  ```

  

## 实验中遇到的困难与解决办法

> *描述实验中遇到的困难与解决办法，对实验的意见与建议或收获。*

- 做实验的时候总感觉实验指导书晦涩难懂，读了几遍都不知道我到底需要干什么，最后在完成TODO的过程中才知道任务是什么
- 希望指导书能够更加简洁明了，有任务指导性
- 做了这个实验，我对编译器的五大部件有了深入的了解，对编译的工作原理有了深入的认识，对理论知识进一步加深了印象，可以说是收货颇丰。