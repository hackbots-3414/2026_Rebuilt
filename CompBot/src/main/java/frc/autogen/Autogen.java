package frc.autogen;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.autogen.ErrorHandler.ErrorInfo;
import frc.autogen.Production.ProductionKind.CompositionKind;

public class Autogen {
  protected static Map<String, Command> registered = new HashMap<>();
  private static final ErrorHandler errorHandler = new ErrorHandler.SimpleErrorHandler();

  public static void registerCommand(String name, Command command) {
    registered.put(name, command);
  }

  public static Command loadFromFile(String path) {
    path = Filesystem.getDeployDirectory() + "/" + path;
    File file = new File(path);
    try (Scanner rdr = new Scanner(file)) {
      List<Production> productions = new ArrayList<>();
      int line = 1;
      while (rdr.hasNextLine()) {
        String input = rdr.nextLine();
        List<Token> tokens = new Tokenizer(input, line).tokenize();
        line++;
        if (tokens.isEmpty()) {
          continue;
        }
        Parser parser = new Parser(tokens, line, errorHandler);
        Optional<Expr> expr = parser.expression();
        if (expr.isEmpty()) {
          errorHandler.error(new ErrorInfo("Could not compile command from " + path, -1));
          break;
        }
        productions.add(expr.get().produce());
      }
      if (!errorHandler.succeeded()) {
        return Commands.none();
      }
      Command command = new Production.Composition(CompositionKind.Sequential, productions).build();
      return command;
    } catch (FileNotFoundException e) {
      errorHandler.error(new ErrorInfo("Could not find file at " + path, -1));
    }
    return Commands.none();
  }
}
