package aa.timonin.service.enums;

public enum ServiceCommands {
    HELP("/help"), REGISTRATION("/registration"), CANCEL("/cancel"), START("/start");
    private String cmd;
    ServiceCommands(String cmd) {
        this.cmd = cmd;
    }

    @Override
    public String toString() {
        return cmd;
    }
    public boolean equals(String cmd){
        return this.toString().equals(cmd);
    }

}
