public class Fuel {
    public enum Status {
        Stored, Readied
    }
    public Status status;
    public Fuel(Status status) {
        this.status = status; 
    }
}
