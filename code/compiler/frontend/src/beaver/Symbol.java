/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * This file is part of Beaver Parser Generator.                       *
 * Copyright (C) 2003,2004 Alexander Demenchuk <alder@softanvil.com>.  *
 * All rights reserved.                                                *
 * See the file "LICENSE" for the terms and conditions for copying,    *
 * distribution and modification of Beaver.                            *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package beaver;

/**
 * Represents a symbol of a grammar.
 */
public class Symbol {
  static private final int COLUMN_FIELD_BITS = 12;
  static private final int COLUMN_FIELD_MASK = (1 << COLUMN_FIELD_BITS) - 1;

  /**
   * Packes symbol "coordinates" into a single number.
   */
  static public int makePosition(int line, int column) {
    return line << COLUMN_FIELD_BITS | column;
  }

  /**
   * Extracts line number from a packed position.
   */
  static public int getLine(int position) {
    return position >>> COLUMN_FIELD_BITS;
  }

  /**
   * Extracts column number from a packed position.
   */
  static public int getColumn(int position) {
    return position & COLUMN_FIELD_MASK;
  }

  /**
   * Value assigned to this symbol.
   */
  public final Object value;

  /**
   * Numeric symbol ID.
   */
  protected short id;

  /**
   * Line and column where this symbol begins.
   */
  protected int start;

  /**
   * Line and column where this symbol ends.
   */
  protected int end;

  public Symbol(short id) {
    this.id = id;
    this.value = null;
  }

  public Symbol(short id, Object value) {
    this.id    = id;
    this.value = value;
  }

  public Symbol(short id, int start, int end) {
    this.id    = id;
    this.value = null;
    this.start = start;
    this.end   = end;
  }

  public Symbol(short id, int left, int right, Object value) {
    this.id    = id;
    this.value = value;
    this.start = left;
    this.end   = right;
  }

  public Symbol(short id, int start_line, int start_column, int length) {
    this.id    = id;
    this.value = null;
    this.start = makePosition(start_line, start_column);
    this.end   = makePosition(start_line, start_column + length - 1);
  }

  public Symbol(short id, int start_line, int start_column, int length, Object value) {
    this.id    = id;
    this.value = value;
    this.start = makePosition(start_line, start_column);
    this.end   = makePosition(start_line, start_column + length - 1);
  }

  /**
   * Creates Symbol for non-symbolic results of action routines
   *
   * @param value attached Symbol's value
   */
  public Symbol(Object value) {
    if ((value+"").equals("null")){
      throw new RuntimeException("Null value");
    }
    this.value = value;
  }

  /**
   * Special case constructor that allows creation of explicitly Symbol-ized nonterminals.
   * <p/>
   * Used by classes descending from Symbol and which instances are returned by reduce actions.
   * In this case ID and symbol position will be assigned by the parser when reduce action
   * code returns this symbol.
   */
  protected Symbol() {
    this.value = this;
  }

  /**
   * Returns an ID of this symbol.
   * <p/>
   * This ID typically is, depending on a symbol type, either a terminal ID if a Symbol is a token
   * created and returned by a Scanner, or a nonterminal ID if a symbol was created by parser based
   * on that nonterminal definition. In the former case the ID is one of IDs generated by Beaver
   * for terminal symbols. The latter keeps IDs of nonterminal symbols.
   *
   * @return symbol's ID
   */
  public short getId() {
    return id;
  }

  /**
   * Returns a position in a source where this symbol starts.
   *
   * @return packed line and column numbers
   */
  public int getStart() {
    return start;
  }

  /**
   * Returns a position in a source where this symbol ends.
   *
   * @return packed line and column numbers
   */
  public int getEnd() {
    return end;
  }

  public String toString() {
    return ""+value;
  }
}
