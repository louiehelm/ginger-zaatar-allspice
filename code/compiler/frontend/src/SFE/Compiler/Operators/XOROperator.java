// PlusOperator.java.
// Copyright (C) 2004 Naom Nisan, Ziv Balshai, Amir Levy.
// See full copyright license terms in file ../GPL.txt

package SFE.Compiler.Operators;

import SFE.Compiler.AssignmentStatement;
import SFE.Compiler.BinaryOpExpression;
import SFE.Compiler.BlockStatement;
import SFE.Compiler.BooleanConstant;
import SFE.Compiler.BooleanExpressions;
import SFE.Compiler.BooleanType;
import SFE.Compiler.Expression;
import SFE.Compiler.Optimizer;
import SFE.Compiler.PolynomialExpression;
import SFE.Compiler.SLPTReduction;
import SFE.Compiler.StatementBuffer;
import SFE.Compiler.Type;
import SFE.Compiler.UnaryOpExpression;



/**
 * A class for representing binary | operator expressions that can be defined
 * in the program.
 */
public class XOROperator extends Operator implements SLPTReduction {
  //~ Methods ----------------------------------------------------------------

  /**
   * Returns a string representation of the object.
   */
  public String toString() {
    return "^";
  }

  public int arity() {
    return 2;
  }
  /**
   * Transforms this expression into an equivalent single level circuit of constraint-reducible gates
   */
  public BlockStatement toSLPTCircuit(Object obj) {
    //No conversion needed, plus gate is an arithmetic gate

    AssignmentStatement as     = ((AssignmentStatement) obj);
    BlockStatement      result = new BlockStatement();

    BinaryOpExpression rhs = (BinaryOpExpression)as.getRHS();

    Expression left = rhs.getLeft();
    Expression right = rhs.getRight();


    //For now, we require that the arguments are obviously booleans.
    /*
    result.addStatement(new AssignmentStatement(
    		as.getLHS(),
    		BooleanExpressions.xor(left, right)
    		).toSLPTCircuit(null));
    		*/
    result.addStatement(new AssignmentStatement(
                          as.getLHS().lvalBitAt(0),
                          new BinaryOpExpression(this, left.bitAt(0), right.bitAt(0))
                        ));

    return result;
  }

  public Expression inlineOp(StatementBuffer assignments, Expression ... args) {
    Expression left = args[0];
    Expression right = args[1];

    PolynomialExpression pleft = PolynomialExpression.toPolynomialExpression(left);
    PolynomialExpression pright = PolynomialExpression.toPolynomialExpression(right);
    if (pleft != null && pright != null) {
      boolean allowedXOR = false;
      switch(Optimizer.optimizationTarget) {
      case GINGER:
        allowedXOR = pleft.getDegree() + pright.getDegree() <= 2;
        break;
      case ZAATAR:
        allowedXOR = pleft.getDegree() <= 1 && pright.getDegree() <= 1;
        break;
      }
      if (allowedXOR) {
        return BooleanExpressions.xor(pleft, pright);
      }
    }

    return null;
  }
  
  public Expression resolve(Expression ... args) {
    Expression left = args[0];
    Expression right = args[1];

    BooleanConstant lc = BooleanConstant.toBooleanConstant(left);
    BooleanConstant rc = BooleanConstant.toBooleanConstant(right);
    if (lc != null && rc != null){
      return new BooleanConstant((lc.value() == 1) ^ (rc.value() == 1));
    }
    if (lc != null && lc.value() == 1){
      return new UnaryOpExpression(new NotOperator(), right);
    }
    if (lc != null && lc.value() == 0){
      return right;
    }
    if (rc != null && rc.value() == 1){
      return new UnaryOpExpression(new NotOperator(), left);
    }
    if (rc != null && rc.value() == 0){
      return left;
    }
    return null;
  }

  public Type getType(Object obj) {
    return new BooleanType();
  }

  /**
   * Returns an int that represents the priority of the operator
   * @return an int that represents the priority of the operator
   */
  public int priority() {
    return -1;
  }
}
