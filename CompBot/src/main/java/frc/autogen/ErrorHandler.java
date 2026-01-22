package frc.autogen;

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
      return "Error on line " + line + ": " + message;
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
      System.out.println(info.fullMessage());
    }
  }
}
