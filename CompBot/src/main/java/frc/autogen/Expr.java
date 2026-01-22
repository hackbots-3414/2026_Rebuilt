package frc.autogen;

import java.util.List;
import frc.autogen.Production.ProductionKind.CompositionKind;

interface Expr {
  public enum ExprKind {
    Literal, Grouping, Composition;
  }

  public ExprKind kind();

  public Production produce();

  public static class Literal implements Expr {
    private final String literal;

    public Literal(String literal) {
      this.literal = literal;
    }

    public ExprKind kind() {
      return ExprKind.Literal;
    }

    public Production produce() {
      return new Production.Named(literal);
    }
  }

  public static class Grouping implements Expr {
    private final Expr expr;

    public Grouping(Expr inner) {
      expr = inner;
    }

    public ExprKind kind() {
      return ExprKind.Grouping;
    }

    public Production produce() {
      return expr.produce();
    }
  }

  public static class Composition implements Expr {
    private final CompositionKind composition;
    private final Expr left;
    private final Expr right;

    public Composition(CompositionKind composition, Expr left, Expr right) {
      this.composition = composition;
      this.left = left;
      this.right = right;
    }

    public ExprKind kind() {
      return ExprKind.Composition;
    }

    public Production produce() {
      return new Production.Composition(composition, List.of(left.produce(), right.produce()));
    }
  }
}
