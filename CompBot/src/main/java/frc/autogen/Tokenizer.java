package frc.autogen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import frc.autogen.Token.SymbolKind;

class Tokenizer {
  private int line;
  private int start;
  private int curr;

  private String source;
  private List<Token> tokens;

  public Tokenizer(String source, int line) {
    this.line = line;
    start = 0;
    curr = 0;

    this.source = source;
    this.tokens = new ArrayList<>();
  }

  public List<Token> tokenize() {
    while (start < source.length()) {
      one_token();
      start = curr;
    }
    return tokens;
  }

  private void one_token() {
    char c = source.charAt(curr++);
    Optional<SymbolKind> symbol = SymbolKind.from(c);
    // handle symbols
    if (symbol.isPresent()) {
      tokens.add(new Token.Symbol(symbol.get()));
      return;
    }
    // Comments in auto_gen start with '#':
    if (c == '#') {
      // skip everything
      curr = source.length();
      return;
    }
    // Whitespace is ignored
    if (c == ' ' || c == '\t') {
      curr ++;
    }
    // By process of elimination, the current token MUST be a literal.
    while (curr < source.length() && SymbolKind.from(source.charAt(curr)).isEmpty()) {
      curr ++;
    }
    String literal = source.substring(start, curr).trim();
    tokens.add(new Token.Literal(literal));
  }
}
