package pl.antonic.server.methods;

import java.util.List;

public class ImageProcessingMethod {

    private Methods id;
    private List<String> arguments;

    public Methods getId() {
        return id;
    }

    public void setId(String id) {
        this.id = Methods.valueOf(id);
    }

    public List<String> getArguments() {
        return arguments;
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }
}
