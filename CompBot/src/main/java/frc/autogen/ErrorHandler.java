package frc.autogen;

import java.util.List;
import edu.wpi.first.wpilibj.Alert;
import edu.wpi.first.wpilibj.Alert.AlertType;
import edu.wpi.first.wpilibj.DriverStation;

/**
 * A class which can handle errors in the autogen system.
 */
public abstract class ErrorHandler {
  private boolean failed;

  public final void error(ErrorInfo info) {
    failed = true;
    handleError(info);
  }

  public boolean succeeded() {
    return !failed;
  }

  protected abstract void handleError(ErrorInfo info);

  /** A class to hold error information */
  public record ErrorInfo(String message, int line) {
    public String fullMessage() {
      return (line > 0) ? "Error on line " + line + ": " + message : "Error: " + message;
    }
  }

  public static class MultiErrorHandler extends ErrorHandler {
    private final List<ErrorHandler> handlers;

    public MultiErrorHandler(ErrorHandler... handlers) {
      this.handlers = List.of(handlers);
    }

    protected void handleError(ErrorInfo info) {
      handlers.forEach(handler -> handler.handleError(info));
    }
  }

  /** An error handler class that logs errors using the DriverStation class. */
  public static class DSErrorHandler extends ErrorHandler {
    public void handleError(ErrorInfo info) {
      DriverStation.reportError(info.fullMessage(), false);
    }
  }

  /** An error handler that simply prints out errors to standard out */
  public static class SimpleErrorHandler extends ErrorHandler {
    protected void handleError(ErrorInfo info) {
      System.err.println(info.fullMessage());
    }
  }

  /** An error handler which writes to a {@link Alert} */
  public static class AlertErrorHandler extends ErrorHandler {
    private final Alert alert = new Alert("Autons", "", AlertType.kError);

    protected void handleError(ErrorInfo info) {
      alert.setText(alert.getText() + "\n" + info.fullMessage());
      alert.set(true);
    }
  }

  public void reset() {
    failed = false;
  }
}
