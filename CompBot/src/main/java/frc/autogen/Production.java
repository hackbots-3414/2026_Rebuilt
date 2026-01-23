package frc.autogen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.autogen.ErrorHandler.ErrorInfo;

interface Production {
  public enum ProductionKind {
    Named, Composition;

    public enum CompositionKind {
      Parallel, Sequential, Dealine, Race;
    }
  }

  /** Returns the kind of production this is (i.e. named command or command composition) */
  public ProductionKind kind();

  /**
   * Returns a list of "uncomposed" productions s.t. any production with the given kind is unwrapped
   * and its coontents are put into the list instead
   */
  public List<Production> uncompose(ProductionKind.CompositionKind kind);

  public Command build();

  public static class Named implements Production {
    private final String name;

    public Named(String name) {
      this.name = name;
    }

    public ProductionKind kind() {
      return ProductionKind.Named;
    }

    public List<Production> uncompose(ProductionKind.CompositionKind kind) {
      return List.of(this); // Named commands are never a composition.
    }

    public Command build() {
      if (!Autogen.registered.containsKey(name)) {
        Autogen.errorHandler.error(new ErrorInfo("Unregistered command: " + name, -1));
      }
      return Autogen.registered.getOrDefault(name, Commands.print("Unregistered command: " + name));
    }
  }

  public static class Composition implements Production {
    private final ProductionKind.CompositionKind composition;
    private final List<Production> group;

    public Composition(ProductionKind.CompositionKind kind, List<Production> group) {
      List<Production> finalProductions = new ArrayList<>();
      // Uncompose unnecessary compositions
      for (Production production : group) {
        finalProductions.addAll(production.uncompose(kind));
      }
      this.group = finalProductions;
      composition = kind;
    }

    public ProductionKind kind() {
      return ProductionKind.Composition;
    }

    public List<Production> uncompose(ProductionKind.CompositionKind kind) {
      if (composition.equals(kind)) {
        return group;
      }
      return List.of(this);
    }

    public Command build() {
      Command cmd = Commands.none();
      Command[] inner = group.stream().map(Production::build).collect(Collectors.toList()).toArray(new Command[0]);
      switch (composition) {
        case Sequential -> cmd = Commands.sequence(inner);
        case Parallel -> cmd = Commands.parallel(inner);
        case Dealine -> cmd = Commands.deadline(inner[0], Arrays.copyOfRange(inner, 1, inner.length));
        case Race -> cmd = Commands.race(inner);
      }
      return cmd;
    }
  }
}
