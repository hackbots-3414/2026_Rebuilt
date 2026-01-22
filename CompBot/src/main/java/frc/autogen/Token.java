package frc.autogen;

import java.util.Optional;

interface Token {
  public enum TokenKind {
    Literal, Symbol;
  }

  public enum SymbolKind {
    Plus('+'), And('&'), OpenParen('('), CloseParen(')'), Star('*'), Question('?');

    public final char c;

    private SymbolKind(char c) {
      this.c = c;
    }

    public static Optional<SymbolKind> from(char c) {
      for (SymbolKind s : values()) {
        if (s.c == c) {
          return Optional.of(s);
        }
      }
      return Optional.empty();
    }

  }
  
  /** Returns the kind of token this token is */
  public TokenKind kind();

  /** Turns this token into an expression */
  public Optional<Expr> expr();

  public static class Literal implements Token {
    private final String literal;

    public Literal(String literal) {
      this.literal = literal;
    }

    public TokenKind kind() {
      return TokenKind.Literal;
    }

    public Optional<Expr> expr() {
      return Optional.of(new Expr.Literal(literal));
    }
  }

  public static class Symbol implements Token {
    public final SymbolKind symbol;

    public Symbol(SymbolKind symbol) {
      this.symbol = symbol;
    }

    public TokenKind kind() {
      return TokenKind.Symbol;
    }

    public Optional<Expr> expr() {
      return null;
    }

    @Override
    public int hashCode() {
      return symbol.hashCode();
    }

    @Override
    public boolean equals(Object other) {
      return hashCode() == other.hashCode();
    }
  }
}
