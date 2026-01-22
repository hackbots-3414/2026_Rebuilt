package frc.autogen;

import java.util.List;
import edu.wpi.first.wpilibj.DriverStation;
import frc.autogen.Production.ProductionKind.CompositionKind;
import frc.autogen.Token.SymbolKind;

class Parser {
  private int curr;
  private List<Token> tokens;

  public Parser(List<Token> tokens) {
    this.tokens = tokens;
    curr = 0;
  }

  public Expr expression() {
    return sequence();
  }

  private Expr sequence() {
    Expr left = parallel();
    while (matches(SymbolKind.Plus)) {
      Expr right = parallel();
      left = new Expr.Composition(CompositionKind.Sequential, left, right);
    }
    return left;
  }

  private Expr parallel() {
    Expr left = deadline();
    while (matches(SymbolKind.And)) {
      Expr right = deadline();
      left = new Expr.Composition(CompositionKind.Parallel, left, right);
    }
    return left;
  }

  private Expr deadline() {
    Expr left = race();
    while (matches(SymbolKind.Question)) {
      Expr right = race();
      left = new Expr.Composition(CompositionKind.Dealine, left, right);
    }
    return left;
  }

  private Expr race() {
    Expr left = primary();
    while (matches(SymbolKind.Star)) {
      Expr right = primary();
      left = new Expr.Composition(CompositionKind.Race, left, right);
    }
    return left;
  }

  private Expr primary() {
    if (matches(SymbolKind.OpenParen)) {
      Expr inner = expression();
      if (!matches(SymbolKind.CloseParen)) {
        DriverStation.reportError("unclosed paren", false);
        return null; // Erroring could happen here
      }
      return new Expr.Grouping(inner);
    }
    return advance().expr().get(); // TODO: add error reporting
  }

  private boolean matches(SymbolKind symbol) {
    if (check(symbol)) {
      advance();
      return true;
    }
    return false;
  }

  private boolean check(SymbolKind symbol) {
    if (at_end()) {
      return false;
    }
    return new Token.Symbol(symbol).equals(tokens.get(curr));
  }

  private boolean at_end() {
    return curr >= tokens.size();
  }

  private Token advance() {
    return tokens.get(curr++);
  }
}
