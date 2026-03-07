package frc.autogen;

import java.util.List;
import java.util.Optional;
import frc.autogen.ErrorHandler.ErrorInfo;
import frc.autogen.Production.ProductionKind.CompositionKind;
import frc.autogen.Token.SymbolKind;

class Parser {
  private int curr;
  private List<Token> tokens;
  private int line;

  public Parser(List<Token> tokens, int line, ErrorHandler errorHandler) {
    this.tokens = tokens;
    this.line = line;
    curr = 0;
  }

  public Optional<Expr> expression() {
    try {
      return Optional.of(sequence());
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  private Expr sequence() throws Exception {
    Expr left = parallel();
    while (matches(SymbolKind.Plus)) {
      Expr right = parallel();
      left = new Expr.Composition(CompositionKind.Sequential, left, right);
    }
    return left;
  }

  private Expr parallel() throws Exception {
    Expr left = deadline();
    while (matches(SymbolKind.And)) {
      Expr right = deadline();
      left = new Expr.Composition(CompositionKind.Parallel, left, right);
    }
    return left;
  }

  private Expr deadline() throws Exception {
    Expr left = race();
    while (matches(SymbolKind.Question)) {
      Expr right = race();
      left = new Expr.Composition(CompositionKind.Dealine, left, right);
    }
    return left;
  }

  private Expr race() throws Exception {
    Expr left = primary();
    while (matches(SymbolKind.Star)) {
      Expr right = primary();
      left = new Expr.Composition(CompositionKind.Race, left, right);
    }
    return left;
  }

  private Expr primary() throws Exception {
    if (matches(SymbolKind.OpenParen)) {
      Expr inner = expression().get();
      if (!matches(SymbolKind.CloseParen)) {
        Autogen.errorHandler.error(new ErrorInfo("Unclosed parenthesis", line));
      }
      return new Expr.Grouping(inner);
    }
    Optional<Expr> expr = advance().expr();
    if (expr.isEmpty()) {
      Autogen.errorHandler.error(new ErrorInfo("Expected expression", line));
      throw new Exception();
    }
    return expr.get();
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
