package cn.edu.hitsz.compiler.asm;

import cn.edu.hitsz.compiler.ir.IRImmediate;
import cn.edu.hitsz.compiler.ir.IRValue;
import cn.edu.hitsz.compiler.ir.IRVariable;
import cn.edu.hitsz.compiler.ir.Instruction;
import cn.edu.hitsz.compiler.ir.InstructionKind;
import cn.edu.hitsz.compiler.utils.FileUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * // TODO: 实验四: 实现汇编生成
 * <br>
 * 在编译器的整体框架中, 代码生成可以称作后端, 而前面的所有工作都可称为前端.
 * <br>
 * 在前端完成的所有工作中, 都是与目标平台无关的, 而后端的工作为将前端生成的目标平台无关信息
 * 根据目标平台生成汇编代码. 前后端的分离有利于实现编译器面向不同平台生成汇编代码. 由于前后
 * 端分离的原因, 有可能前端生成的中间代码并不符合目标平台的汇编代码特点. 具体到本项目你可以
 * 尝试加入一个方法将中间代码调整为更接近 risc-v 汇编的形式, 这样会有利于汇编代码的生成.
 * <br>
 * 为保证实现上的自由, 框架中并未对后端提供基建, 在具体实现时可自行设计相关数据结构.
 *
 * @see AssemblyGenerator#run() 代码生成与寄存器分配
 */
public class AssemblyGenerator {
    List<Instruction> instList_before = new LinkedList<>();
    List<Instruction> instList = new LinkedList<>();
    BMap<IRValue, Register> varMap = new BMap<>();
    // 汇编代码，初始化第一行为 ".text"
    List<String> sentences = new ArrayList<>(List.of(".text"));

    // 可以分配的寄存器号
    enum Register {
        t0, t1, t2, t3, t4, t5, t6
    }

    // 用来添加变量
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
        Set<Register> unusedRegs = Arrays.stream(Register.values()).collect(Collectors.toSet());
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

    /**
     * 加载前端提供的中间代码
     * <br>
     * 视具体实现而定, 在加载中或加载后会生成一些在代码生成中会用到的信息. 如变量的引用
     * 信息. 这些信息可以通过简单的映射维护, 或者自行增加记录信息的数据结构.
     *
     * @param originInstructions 前端提供的中间代码
     */

    public void loadIR(List<Instruction> originInstructions) {
        // // TODO: 读入前端提供的中间代码并生成所需要的信息
        instList_before = originInstructions;
        preprocessing();
    }

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

    /**
     * 执行代码生成.
     * <br>
     * 根据理论课的做法, 在代码生成时同时完成寄存器分配的工作. 若你觉得这样的做法不好,
     * 也可以将寄存器分配和代码生成分开进行.
     * <br>
     * 提示: 寄存器分配中需要的信息较多, 关于全局的与代码生成过程无关的信息建议在代码生
     * 成前完成建立, 与代码生成的过程相关的信息可自行设计数据结构进行记录并动态维护.
     */
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
     * 输出汇编代码到文件
     *
     * @param path 输出文件路径
     */
    public void dump(String path) {
        // // TODO: 输出汇编代码到文件
        FileUtils.writeLines(path,sentences.stream().toList());
    }
}
