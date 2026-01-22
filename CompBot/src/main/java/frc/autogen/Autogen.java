package frc.autogen;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.autogen.Production.ProductionKind.CompositionKind;

public class Autogen {
  protected static Map<String, Command> registered = new HashMap<>();

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
        Parser parser = new Parser(tokens);
        productions.add(parser.expression().produce());
      }
      Command command = new Production.Composition(CompositionKind.Sequential, productions).build();
      return command;
    } catch (FileNotFoundException e) {
      DriverStation.reportError("Invalid file for autogen: " + path, false);
    }
    return Commands.none();
  }
}
