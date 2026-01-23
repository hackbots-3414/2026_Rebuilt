package frc.autogen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.autogen.ErrorHandler.ErrorInfo;
import frc.autogen.Production.ProductionKind.CompositionKind;

public class Autogen {
  protected static Map<String, Command> registered = new HashMap<>();
  public static final ErrorHandler errorHandler = new ErrorHandler.SimpleErrorHandler();

  public static void registerCommand(String name, Command command) {
    registered.put(name, command);
  }

  public static Optional<Command> loadFromFile(String path) {
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
        return Optional.empty();
      }
      Command command = new Production.Composition(CompositionKind.Sequential, productions).build();
      return Optional.of(command);
    } catch (FileNotFoundException e) {
      errorHandler.error(new ErrorInfo("Could not find file at " + path, -1));
    }
    return Optional.empty();
  }

  private static Map<String, Command> loadCommands() {
    Path deployDirectory = Paths.get(Filesystem.getDeployDirectory().getPath());
    List<Path> files;
    try {
      files = Files.list(deployDirectory).filter(path -> path.toString().endsWith(".autogen"))
          .collect(Collectors.toList());
    } catch (IOException e) {
      errorHandler.error(new ErrorInfo("Could not read deploy directory", -1));
      return Map.of();
    }
    Map<String, Command> autons = new HashMap<>();
    for (Path path : files) {
      System.out.println("path=" + path);
      loadFromFile(path.toString()).ifPresent(
          command -> {
            autons.put(path.getFileName().toString(), command);
          });
    }
    return autons;
  }

  public static SendableChooser<Command> autoChooser() {
    SendableChooser<Command> chooser = new SendableChooser<>();
    chooser.setDefaultOption("None", Commands.none());
    for (Entry<String, Command> entry : loadCommands().entrySet()) {
      chooser.addOption(entry.getKey(), entry.getValue());
    }
    return chooser;
  }
}
