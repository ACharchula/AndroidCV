package pl.antonic.server.methods;

import java.util.List;

public class ImageProcessingMethodWrapper {

    private List<ImageProcessingMethod> methods;

    public List<ImageProcessingMethod> getMethods() {
        return methods;
    }

    public boolean contains(Methods method) {
        for (ImageProcessingMethod imageProcessingMethod : methods) {
            if (imageProcessingMethod.getId() == method)
                return true;
        }

        return false;
    }

    public void setMethods(List<ImageProcessingMethod> methods) {
        this.methods = methods;
    }
}
