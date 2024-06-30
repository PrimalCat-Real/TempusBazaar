package primalcat.tempusbazaar.category.atributes;

public class Price {
    private int min;
    private int current;
    private int max;

    // Геттеры и сеттеры
    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
}
