package frc.autogen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import frc.autogen.Token.SymbolKind;

public class TokenizerTest {
  @Test
  public void equalityTest() {
    Token someSymbol = new Token.Symbol(SymbolKind.And);
    Token someOtherSymbol = new Token.Symbol(SymbolKind.And);
    assertEquals(someSymbol, someOtherSymbol);
  }
}
